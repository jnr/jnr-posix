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

import jnr.constants.platform.Sysconf;
import jnr.constants.platform.Fcntl;
import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.mapper.FromNativeContext;
import jnr.posix.util.MethodName;

import java.io.FileDescriptor;

final class AixPOSIX extends BaseNativePOSIX {
    // These should probably be put into jnr-constants instead eventually, but
    // they're here for now as a one-off to work around AIX flock issues
    private enum FlockFlags {
        LOCK_SH(1),
        LOCK_EX(2),
        LOCK_NB(4),
        LOCK_UN(8);
        private final int value;
        FlockFlags(int value) {
            this.value = value;
        }
        public final int intValue() {
            return value;
        }
    }

    AixPOSIX(LibCProvider libc, POSIXHandler handler) {
        super(libc, handler);
    }

    public FileStat allocateStat() { 
        return new AixFileStat(this); 
    }

    public MsgHdr allocateMsgHdr() {
        handler.unimplementedError(MethodName.getCallerMethodName());
        return null;
    }

    public SocketMacros socketMacros() {
        handler.unimplementedError(MethodName.getCallerMethodName());
        return null;
    }

    public long sysconf(Sysconf name) {
        return libc().sysconf(name);
    }

    public Times times() {
        return NativeTimes.times(this);
    }


    public static final PointerConverter PASSWD = new PointerConverter() {
        public Object fromNative(Object arg, FromNativeContext ctx) {
            return arg != null ? new AixPasswd((Pointer) arg) : null;
        }
    };

    public Pointer allocatePosixSpawnFileActions() {
        return Memory.allocateDirect(getRuntime(), 4);
    }

    public Pointer allocatePosixSpawnattr() {
        return Memory.allocateDirect(getRuntime(), 60);
    }

    // AIX flock lives in libbsd instead of libc.  AIX flock locks fully
    // interact with fcntl locks, so we can implement flock in terms of fcntl,
    // which is what we do here to avoid having to pull in that lib.
    @Override
    public int flock(int fd, int operation) {
        int cmd = Fcntl.F_SETLKW.intValue();
        short type = 0;

        // Map the flock call flags to a fcntl flock type flag
        if ((operation & FlockFlags.LOCK_SH.intValue()) != 0) {
            type = (short)Fcntl.F_RDLCK.intValue();
        } else if ((operation & FlockFlags.LOCK_EX.intValue()) != 0) {
            type = (short)Fcntl.F_WRLCK.intValue();
        } else if ((operation & FlockFlags.LOCK_UN.intValue()) != 0) {
            type = (short)Fcntl.F_UNLCK.intValue();
        }

        // Switch to the fcntl non-blocking command
        if ((operation & FlockFlags.LOCK_NB.intValue()) != 0) {
            cmd = Fcntl.F_SETLK.intValue();
        }

        Flock flock = allocateFlock();
        flock.type(type);
        flock.whence((short)0);
        flock.start(0);
        flock.len(0);
        return libc().fcntl(fd, cmd, flock);
    }

    @Override
    public Timeval allocateTimeval() { return new AixTimeval(getRuntime()); }

    // This isn't an override yet, because Flock would have to be implemented
    // for all platforms, or at least with a DefaultNative implementation.  This
    // is fine for now, because only AIX uses the flock structure
    public Flock allocateFlock() { return new AixFlock(getRuntime()); }
}
