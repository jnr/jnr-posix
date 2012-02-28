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

public final class OpenBSDHeapFileStat extends BaseHeapFileStat {
    public final class time_t extends Signed32 {}
    public final class dev_t extends Signed32 {}

    public final dev_t  st_dev = new dev_t();
    public final Unsigned32  st_ino = new Unsigned32();
    public final Unsigned32  st_mode = new Unsigned32();
    public final Unsigned32  st_nlink = new Unsigned32();
    public final Unsigned32  st_uid = new Unsigned32();
    public final Unsigned32  st_gid = new Unsigned32();
    public final dev_t  st_rdev = new dev_t();
    public final Signed32  st_lspare0 = new Signed32();
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
    public final Signed32  st_lspare1 = new Signed32();
    public final time_t st_birthtime = new time_t();
    public final SignedLong   st_birthtimensec = new SignedLong();
    public final Signed64  st_qspare0 = new Signed64();
    public final Signed64  st_qspare1 = new Signed64();
    
    public OpenBSDHeapFileStat(NativePOSIX posix) {
        super(posix);
    }
    public long atime() {
        return st_atime.get();
    }

    public long blocks() {
        return st_blocks.get();
    }

    public long blockSize() {
        return st_blksize.get();
    }

    public long ctime() {
        return st_ctime.get();
    }

    public long dev() {
        return st_dev.get();
    }

    public int gid() {
        return (int)st_gid.get();
    }

    public long ino() {
        return st_ino.get();
    }

    public int mode() {
        return (int)(st_mode.get() & 0xffff);
    }

    public long mtime() {
        return st_mtime.get();
    }

    public int nlink() {
        return (int)st_nlink.get();
    }

    public long rdev() {
        return st_rdev.get();
    }

    public long st_size() {
        return st_size.get();
    }

    public int uid() {
        return (int)st_uid.get();
    }
}
