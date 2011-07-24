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
 * Copyright (C) 2008 Google Inc. All Rights Reserved.
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
package jnr.posix;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;

/**
 * <p>This file catches any SecurityExceptions raised when access
 * to a file is denied and responds as if the file didn't exist
 * instead.</p>
 *
 */
public class JavaSecuredFile extends File {
    public JavaSecuredFile(File parent, String child) {
        super(parent, child);
    }

    public JavaSecuredFile(String pathname) {
        super(pathname);
    }

    public JavaSecuredFile(String parent, String child) {
        super(parent, child);
    }

    public JavaSecuredFile(URI uri) {
        super(uri);
    }

    @Override
    public File getParentFile() {
        String parent = getParent();
        if (parent != null) {
            return new JavaSecuredFile(parent);
        } else {
            return null;
        }
    }

    @Override
    public File getAbsoluteFile() {
        String absolute = getAbsolutePath();
        if (absolute != null) {
            return new JavaSecuredFile(absolute);
        } else {
            return null;
        }
    }

    @Override
    public String getCanonicalPath() throws IOException {
        try {
            return super.getCanonicalPath();
        } catch (SecurityException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public File getCanonicalFile() throws IOException {
        String canonical = getCanonicalPath();
        if (canonical != null) {
            return new JavaSecuredFile(canonical);
        } else {
            return null;
        }
    }

    @Override
    public boolean canRead() {
        try {
            return super.canRead();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean canWrite() {
        try {
            return super.canWrite();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean exists() {
        try {
            return super.exists();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean isDirectory() {
        try {
            return super.isDirectory();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean isFile() {
        try {
            return super.isFile();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean isHidden() {
        try {
            return super.isHidden();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public long lastModified() {
        try {
            return super.lastModified();
        } catch (SecurityException ex) {
            return 0L;
        }
    }

    @Override
    public long length() {
        try {
            return super.length();
        } catch (SecurityException ex) {
            return 0L;
        }
    }

    @Override
    public boolean createNewFile() throws IOException {
        try {
            return super.createNewFile();
        } catch (SecurityException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public boolean delete() {
        try {
            return super.delete();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public String[] list() {
        try {
            return super.list();
        } catch (SecurityException ex) {
            return null;
        }
    }

    @Override
    public String[] list(FilenameFilter filter) {
        try {
            return super.list(filter);
        } catch (SecurityException ex) {
            return null;
        }
    }

    @Override
    public File[] listFiles() {
        try {
            return super.listFiles();
        } catch (SecurityException ex) {
            return null;
        }
    }

    @Override
    public File[] listFiles(FilenameFilter filter) {
        try {
            return super.listFiles(filter);
        } catch (SecurityException ex) {
            return null;
        }
    }

    @Override
    public File[] listFiles(FileFilter filter) {
        try {
            return super.listFiles(filter);
        } catch (SecurityException ex) {
            return null;
        }
    }

    @Override
    public boolean mkdir() {
        try {
            return super.mkdir();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean mkdirs() {
        try {
            return super.mkdirs();
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean renameTo(File dest) {
        try {
            return super.renameTo(dest);
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean setLastModified(long time) {
        try {
            return super.setLastModified(time);
        } catch (SecurityException ex) {
            return false;
        }
    }

    @Override
    public boolean setReadOnly() {
        try {
            return super.setReadOnly();
        } catch (SecurityException ex) {
            return false;
        }
    }
}

