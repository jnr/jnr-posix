package jnr.posix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class JavaFileStat extends AbstractJavaFileStat {
    short st_mode;

    BasicFileAttributes attrs;
    PosixFileAttributes posixAttrs;
    DosFileAttributes dosAttrs;
    
    public JavaFileStat(POSIX posix, POSIXHandler handler) {
        super(posix, handler);
    }
    
    public void setup(String filePath) {
        File file = new JavaSecuredFile(filePath);
        Path path = file.toPath();

        try {
            try {
                // try POSIX
                posixAttrs = Files.readAttributes(path, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                attrs = posixAttrs;
            } catch (UnsupportedOperationException uoe) {
                try {
                    // try DOS
                    dosAttrs = Files.readAttributes(path, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                    attrs = dosAttrs;
                } catch (UnsupportedOperationException uoe2) {
                    attrs = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                }
            }
        } catch (IOException ioe) {
            // fall back on pre-NIO2 logic
            attrs = new PreNIO2FileAttributes(file);
        }

        // Simulated mode value
        st_mode = calculateMode(file, (short) 0);
    }

    private class PreNIO2FileAttributes implements BasicFileAttributes {
        final long st_size;
        final int st_ctime;
        final int st_mtime;
        final boolean regularFile;
        final boolean directory;

        PreNIO2FileAttributes(File file) {
            st_size = file.length();

            st_mtime = (int) (file.lastModified() / 1000);

            // Parent file last modified will only represent when something was added or removed.
            // This is not correct, but it is better than nothing and does work in one common use
            // case.
            if (file.getParentFile() != null) {
                st_ctime = (int) (file.getParentFile().lastModified() / 1000);
            } else {
                st_ctime = st_mtime;
            }

            regularFile = file.isFile();
            directory = file.isDirectory();
        }

        @Override
        public FileTime lastModifiedTime() {
            return FileTime.fromMillis(st_mtime);
        }

        @Override
        public FileTime lastAccessTime() {
            return lastModifiedTime();
        }

        @Override
        public FileTime creationTime() {
            return FileTime.fromMillis(st_mtime);
        }

        @Override
        public boolean isRegularFile() {
            return (st_mode & S_IFREG) != 0;
        }

        @Override
        public boolean isDirectory() {
            return (st_mode & S_IFDIR) != 0;
        }

        @Override
        public boolean isSymbolicLink() {
            return (st_mode & S_IFLNK) != 0;
        }

        @Override
        public boolean isOther() {
            return !(isRegularFile() || isDirectory() || isSymbolicLink());
        }

        @Override
        public long size() {
            return st_size;
        }

        @Override
        public Object fileKey() {
            return null;
        }
    }

    private short calculateMode(File file, short st_mode) {
        // implementation to lowest common denominator...
        // Windows has no file mode, but C ruby returns either 0100444 or 0100644

        if (file.canRead()) {
            st_mode |= ALL_READ;
        }

        if (file.canWrite()) {
            st_mode |= ALL_WRITE;
            st_mode &= ~(S_IWGRP | S_IWOTH);
        }

        if (file.isDirectory()) {
            st_mode |= S_IFDIR;
        } else if (file.isFile()) {
            st_mode |= S_IFREG;
        }

        if (posixAttrs != null && posixAttrs.isSymbolicLink()) {
            st_mode |= S_IFLNK;
        } else {
            try {
                st_mode = calculateSymlink(file, st_mode);
            } catch (IOException e) {
                // Not sure we can do much in this case...
            }
        }

        return st_mode;
    }

    private static short calculateSymlink(File file, short st_mode) throws IOException {
        if (file.getAbsoluteFile().getParentFile() == null) {
            return st_mode;
        }

        File absoluteParent = file.getAbsoluteFile().getParentFile();
        File canonicalParent = absoluteParent.getCanonicalFile();

        if (canonicalParent.getAbsolutePath().equals(absoluteParent.getAbsolutePath())) {
            // parent doesn't change when canonicalized, compare absolute and canonical file directly
            if (!file.getAbsolutePath().equalsIgnoreCase(file.getCanonicalPath())) {
                st_mode |= S_IFLNK;
                return st_mode;
            }
        }

        // directory itself has symlinks (canonical != absolute), so build new path with canonical parent and compare
        file = new JavaSecuredFile(canonicalParent.getAbsolutePath() + "/" + file.getName());
        if (!file.getAbsolutePath().equalsIgnoreCase(file.getCanonicalPath())) {
            st_mode |= S_IFLNK;
        }

        return st_mode;
    }

    /**
     * Limitation: Java has no access time support, so we return mtime as the next best thing.
     */
    public long atime() {
        return (int) (attrs.lastAccessTime().toMillis() / 1000);
    }

    public long ctime() {
        return (int) (attrs.creationTime().toMillis() / 1000);
    }

    public boolean isDirectory() {
        return attrs.isDirectory();
    }

    public boolean isEmpty() {
        return attrs.size() == 0;
    }

    public boolean isExecutable() {
        if (posixAttrs != null) {
            Set<PosixFilePermission> permissions = posixAttrs.permissions();

            return permissions.contains(PosixFilePermission.OWNER_EXECUTE) ||
                    permissions.contains(PosixFilePermission.GROUP_EXECUTE) ||
                    permissions.contains(PosixFilePermission.OTHERS_EXECUTE);
        }

        // silently return false, since it's likely an unusual filesystem
        return false;
    }

    public boolean isExecutableReal() {
        return isExecutable();
    }

    public boolean isFile() {
        return attrs.isRegularFile();
    }
    
    public boolean isGroupOwned() {
        return groupMember(gid());
    }

    public boolean isIdentical(FileStat other) {
        // if attrs supports file keys, we can compare them
        Object key = attrs.fileKey();

        if (key != null && other instanceof JavaFileStat) {
            JavaFileStat otherStat = (JavaFileStat) other;

            return key.equals(otherStat.attrs.fileKey());
        }

        handler.unimplementedError("identical file detection");
        
        return false;
    }

    public boolean isOwned() {
        return posix.geteuid() == uid();
    }

    public boolean isROwned() {
        return posix.getuid() == uid();
    }

    public boolean isReadable() {
        if (posixAttrs != null) {
            Set<PosixFilePermission> permissions = posixAttrs.permissions();

            return permissions.contains(PosixFilePermission.OWNER_READ) ||
                    permissions.contains(PosixFilePermission.GROUP_READ) ||
                    permissions.contains(PosixFilePermission.OTHERS_READ);
        }

        int mode = mode();
        
        if ((mode & S_IRUSR) != 0) return true;
        if ((mode & S_IRGRP) != 0) return true;
        if ((mode & S_IROTH) != 0) return true;
        
        return false;
    }

    // We do both readable and readable_real through the same method because
    public boolean isReadableReal() {
        return isReadable();
    }

    public boolean isSymlink() {
        if (posixAttrs != null) {
            return posixAttrs.isSymbolicLink();
        }

        return (mode() & S_IFLNK) == S_IFLNK;
    }
    
    public boolean isWritable() {
        if (posixAttrs != null) {
            Set<PosixFilePermission> permissions = posixAttrs.permissions();

            return permissions.contains(PosixFilePermission.OWNER_WRITE) ||
                    permissions.contains(PosixFilePermission.GROUP_WRITE) ||
                    permissions.contains(PosixFilePermission.OTHERS_WRITE);
        } else if (dosAttrs != null) {
            return !dosAttrs.isReadOnly();
        }

        int mode = mode();
        
        if ((mode & S_IWUSR) != 0) return true;
        if ((mode & S_IWGRP) != 0) return true;
        if ((mode & S_IWOTH) != 0) return true;

        return false;
    }
    
    // We do both readable and readable_real through the same method because
    // in our java process effective and real userid will always be the same.
    public boolean isWritableReal() {
        return isWritable();
    }

    public int mode() {
        return st_mode & 0xffff;
    }

    public long mtime() {
        return (int) (attrs.lastModifiedTime().toMillis() / 1000);
    }

    public long st_size() {
        return attrs.size();
    }

}
