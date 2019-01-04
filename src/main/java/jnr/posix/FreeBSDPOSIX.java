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
import jnr.ffi.Memory;
import jnr.ffi.mapper.FromNativeContext;
import jnr.ffi.Pointer;
import jnr.posix.util.MethodName;

import java.lang.Runtime;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.text.NumberFormat;
import java.text.ParsePosition;

final class FreeBSDPOSIX extends BaseNativePOSIX {
    private final int freebsdVersion;

    FreeBSDPOSIX(LibCProvider libc, POSIXHandler handler) {
        super(libc, handler);

        int parsed_version = 0;

        // FreeBSD 12 introduces a new stat structure. Until jffi gets dlvsym() support
        // to allow us to link explicitly to supported versions of functions, detect
        // the current userspace version and cross our fingers.
        try {
            Process p = Runtime.getRuntime().exec("/bin/freebsd-version -u");
            String version = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();

            if (p.waitFor() == 0 && version != null) {
                NumberFormat fmt = NumberFormat.getIntegerInstance();
                fmt.setGroupingUsed(false);

                parsed_version = fmt.parse(version, new ParsePosition(0)).intValue();
            }
        } catch (Exception e) { }

        freebsdVersion = parsed_version;
    }

    public FileStat allocateStat() {
        if (freebsdVersion >= 12) {
            return new FreeBSDFileStat12(this);
        } else {
            return new FreeBSDFileStat(this);
        }
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
            return arg != null ? new FreeBSDPasswd((Pointer) arg) : null;
        }
    };

    public Pointer allocatePosixSpawnFileActions() {
        return Memory.allocateDirect(getRuntime(), 8);
    }

    public Pointer allocatePosixSpawnattr() {
        return Memory.allocateDirect(getRuntime(), 8);
    }
}
