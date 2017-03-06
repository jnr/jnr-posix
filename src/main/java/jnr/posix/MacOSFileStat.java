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

import jnr.ffi.StructLayout;

public final class MacOSFileStat extends BaseFileStat implements NanosecondFileStat {
    public static class Layout extends StructLayout {

        public Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public final class time_t extends SignedLong {
        }
        public final Signed32 st_dev = new Signed32();
        public final Signed32 st_ino = new Signed32();
        public final Signed16 st_mode = new Signed16();
        public final Signed16 st_nlink = new Signed16();
        public final Signed32 st_uid = new Signed32();
        public final Signed32 st_gid = new Signed32();
        public final Signed32 st_rdev = new Signed32();
        public final time_t st_atime = new time_t();
        public final SignedLong st_atimensec = new SignedLong();
        public final time_t st_mtime = new time_t();
        public final SignedLong st_mtimensec = new SignedLong();
        public final time_t st_ctime = new time_t();
        public final SignedLong st_ctimensec = new SignedLong();
        public final Signed64 st_size = new Signed64();
        public final Signed64 st_blocks = new Signed64();
        public final Signed32 st_blksize = new Signed32();
        public final Signed32 st_flags = new Signed32();
        public final Signed32 st_gen = new Signed32();
        public final Signed32 st_lspare = new Signed32();
        public final Signed64 st_qspare0 = new Signed64();
        public final Signed64 st_qspare1 = new Signed64();
    }
    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    public MacOSFileStat(MacOSPOSIX posix) {
        super(posix, layout);
    }

    public long atime() {
        return layout.st_atime.get(memory);
    }

    public long blocks() {
        return layout.st_blocks.get(memory);
    }

    public long blockSize() {
        return layout.st_blksize.get(memory);
    }

    public long ctime() {
        return layout.st_ctime.get(memory);
    }

    public long dev() {
        return layout.st_dev.get(memory);
    }

    public int gid() {
        return layout.st_gid.get(memory);
    }

    public long ino() {
        return layout.st_ino.get(memory);
    }

    public int mode() {
        return layout.st_mode.get(memory) & 0xffff;
    }

    public long mtime() {
        return layout.st_mtime.get(memory);
    }

    public int nlink() {
        return layout.st_nlink.get(memory);
    }

    public long rdev() {
        return layout.st_rdev.get(memory);
    }

    public long st_size() {
        return layout.st_size.get(memory);
    }

    public int uid() {
        return layout.st_uid.get(memory);
    }

    @Override
    public long aTimeNanoSecs() {
        return layout.st_atimensec.get(memory);
    }

    @Override
    public long cTimeNanoSecs() {
        return layout.st_ctimensec.get(memory);
    }

    @Override
    public long mTimeNanoSecs() {
        return layout.st_mtimensec.get(memory);
    }
}
