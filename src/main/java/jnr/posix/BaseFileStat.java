/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * 
 *  
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
/**
 * $Id: $
 */

package jnr.posix;

import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.StructLayout;

/**
 *
 */
public abstract class BaseFileStat implements FileStat {
    protected final POSIX posix;
    protected final Pointer memory;

    protected BaseFileStat(NativePOSIX posix, StructLayout layout) {
        this.posix = posix;
        this.memory = Memory.allocate(posix.getRuntime(), layout.size());
    }
    
    public java.lang.String ftype() {
        if (isFile()) {
            return "file";
        } else if (isDirectory()) {
            return "directory";
        } else if (isCharDev()) {
            return "characterSpecial";
        } else if (isBlockDev()) {
            return "blockSpecial";
        } else if (isFifo()) {
            return "fifo";
        } else if (isSymlink()) {
            return "link";
        } else if (isSocket()) {
            return "socket";
        } 
          
        return "unknown";
    }

    public boolean groupMember(int gid) {
        if (posix.getgid() == gid || posix.getegid() == gid) {
            return true;
        }

        // FIXME: Though not Posix, windows has different mechanism for this.
        
        return false;
    }
    
    public boolean isBlockDev() {
        return (mode() & S_IFMT) == S_IFBLK;
    }
    
    public boolean isCharDev() {
        return (mode() & S_IFMT) == S_IFCHR;
    }

    public boolean isDirectory() {
        return (mode() & S_IFMT) == S_IFDIR;
    }
    
    public boolean isEmpty() {
        return st_size() == 0;
    }

    public boolean isExecutable() {
        if (posix.geteuid() == 0) return (mode() & S_IXUGO) != 0;
        if (isOwned()) return (mode() & S_IXUSR) != 0;
        if (isGroupOwned()) return (mode() & S_IXGRP) != 0;
        return (mode() & S_IXOTH) != 0;
    }
    
    public boolean isExecutableReal() {
        if (posix.getuid() == 0) return (mode() & S_IXUGO) != 0;
        if (isROwned()) return (mode() & S_IXUSR) != 0;
        if (groupMember(gid())) return (mode() & S_IXGRP) != 0;
        return (mode() & S_IXOTH) != 0;
    }
    
    public boolean isFile() {
        return (mode() & S_IFMT) == S_IFREG;
    }

    public boolean isFifo() {
        return (mode() & S_IFMT) == S_IFIFO;
    }
    
    public boolean isGroupOwned() {
        return groupMember(gid());
    }

    public boolean isIdentical(FileStat other) {
        return dev() == other.dev() && ino() == other.ino(); 
    }

    public boolean isNamedPipe() {
        return (mode() & S_IFIFO) != 0;
    }
    
    public boolean isOwned() {
        return posix.geteuid() == uid();
    }
    
    public boolean isROwned() {
        return posix.getuid() == uid();
    }
    
    public boolean isReadable() {
        if (posix.geteuid() == 0) return true;
        if (isOwned()) return (mode() & S_IRUSR) != 0;
        if (isGroupOwned()) return (mode() & S_IRGRP) != 0;
        return (mode() & S_IROTH) != 0;
    }
    
    public boolean isReadableReal() {
        if (posix.getuid() == 0) return true;
        if (isROwned()) return (mode() & S_IRUSR) != 0;
        if (groupMember(gid())) return (mode() & S_IRGRP) != 0;
        return (mode() & S_IROTH) != 0;
    }
    
    public boolean isSetgid() {
        return (mode() & S_ISGID) != 0;
    }

    public boolean isSetuid() {
        return (mode() & S_ISUID) != 0;
    }

    public boolean isSocket() {
        return (mode() & S_IFMT) == S_IFSOCK;
    }
    
    public boolean isSticky() {
        return (mode() & S_ISVTX) != 0;
    }

    public boolean isSymlink() {
        return (mode() & S_IFMT) == S_IFLNK;
    }

    public boolean isWritable() {
        if (posix.geteuid() == 0) return true;
        if (isOwned()) return (mode() & S_IWUSR) != 0;
        if (isGroupOwned()) return (mode() & S_IWGRP) != 0;
        return (mode() & S_IWOTH) != 0;
    }

    public boolean isWritableReal() {
        if (posix.getuid() == 0) return true;
        if (isROwned()) return (mode() & S_IWUSR) != 0;
        if (groupMember(gid())) return (mode() & S_IWGRP) != 0;
        return (mode() & S_IWOTH) != 0;
    }

    public int major(long dev) {
        return (int) (dev >> 24) & 0xff;
    }
    
    public int minor(long dev) {
        return (int) (dev & 0xffffff);
    }
}
