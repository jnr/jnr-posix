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

public final class OpenBSDFileStat extends BaseFileStat implements NanosecondFileStat{
    private static final class Layout extends StructLayout {

        private Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public final class time_t extends Signed64 {}
        public final class dev_t extends Signed32 {}

        public final Unsigned32  st_mode = new Unsigned32();
        public final dev_t  st_dev = new dev_t();
        public final Unsigned64  st_ino = new Unsigned64();
        public final Unsigned32  st_nlink = new Unsigned32();
        public final Unsigned32  st_uid = new Unsigned32();
        public final Unsigned32  st_gid = new Unsigned32();
        public final dev_t  st_rdev = new dev_t();
        public final time_t st_atime = new time_t();
        public final SignedLong   st_atimensec = new SignedLong();
        public final time_t st_mtime = new time_t();
        public final SignedLong   st_mtimensec = new SignedLong();
        public final time_t st_ctime = new time_t();
        public final SignedLong   st_ctimensec = new SignedLong();
        public final Signed64  st_size = new Signed64();
        public final Signed64  st_blocks = new Signed64();
        public final Unsigned32  st_blksize = new Unsigned32();
        public final Unsigned32  st_flags = new Unsigned32();
        public final Unsigned32  st_gen = new Unsigned32();
        public final time_t st_birthtime = new time_t();
        public final SignedLong   st_birthtimensec = new SignedLong();
    }
    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());
    public OpenBSDFileStat(NativePOSIX posix) {
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
        return (int) layout.st_gid.get(memory);
    }

    public long ino() {
        return layout.st_ino.get(memory);
    }

    public int mode() {
        return (int)(layout.st_mode.get(memory) & 0xffff);
    }

    public long mtime() {
        return layout.st_mtime.get(memory);
    }

    public int nlink() {
        return (int)layout.st_nlink.get(memory);
    }

    public long rdev() {
        return layout.st_rdev.get(memory);
    }

    public long st_size() {
        return layout.st_size.get(memory);
    }

    public int uid() {
        return (int)layout.st_uid.get(memory);
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
