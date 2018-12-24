/***** BEGIN LICENSE BLOCK *****
 * Version: EPL 2.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Eclipse Public
 * License Version 2.0 (the "License"); you may not use this file
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

public final class DragonFlyFileStat extends BaseFileStat implements NanosecondFileStat {
    private static final class Layout extends StructLayout {

        private Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public final class time_t extends SignedLong {}
        public final class dev_t extends Unsigned32 {}

        public final Signed64    st_ino = new Signed64();
        public final Signed32    st_nlink = new Signed32();
        public final dev_t       st_dev = new dev_t();
        public final Unsigned16  st_mode = new Unsigned16();
        public final Unsigned16  st_padding1 = new Unsigned16();
        public final Signed32    st_uid = new Signed32();
        public final Signed32    st_gid = new Signed32();
        public final dev_t       st_rdev = new dev_t();
        public final time_t      st_atim = new time_t();
        public final time_t      st_atimnsec = new time_t();
        public final time_t      st_mtim = new time_t();
        public final time_t      st_mtimnsec = new time_t();
        public final time_t      st_ctim = new time_t();
        public final time_t      st_ctimnsec = new time_t();
        public final Signed32    st_size = new Signed32();
        public final Signed32    st_blocks = new Signed32();
        public final Signed32    st_blksize = new Signed32();
        public final Signed32    st_flags = new Signed32();
        public final Signed32    st_gen = new Signed32();
        public final Signed32    st_lspare = new Signed32();
        public final Signed64    st_qspare1 = new Signed64();
        public final Signed64    st_qspare2 = new Signed64();
    }
    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    public DragonFlyFileStat(NativePOSIX posix) {
        super(posix, layout);
    }

    public long atime() {
        return layout.st_atim.get(memory);
    }

    public long blocks() {
        return layout.st_blocks.get(memory);
    }

    public long blockSize() {
        return layout.st_blksize.get(memory);
    }

    public long ctime() {
        return layout.st_ctim.get(memory);
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
        return layout.st_mtim.get(memory);
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
        return layout.st_atimnsec.get(memory);
    }

    @Override
    public long cTimeNanoSecs() {
        return layout.st_ctimnsec.get(memory);
    }

    @Override
    public long mTimeNanoSecs() {
        return layout.st_mtimnsec.get(memory);
    }
}
