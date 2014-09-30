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

public class OpenBSDPasswd extends NativePasswd implements Passwd {
    private static final class Layout extends StructLayout {

        private Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public final UTF8StringRef pw_name = new UTF8StringRef();   // user name
        public final UTF8StringRef pw_passwd = new UTF8StringRef(); // password (encrypted)
        public final Unsigned32 pw_uid = new Unsigned32();       // user id
        public final Unsigned32 pw_gid = new Unsigned32();       // user id
        public final Signed64 pw_change = new Signed64();    // password change time
        public final UTF8StringRef pw_class = new UTF8StringRef();  // user access class
        public final UTF8StringRef pw_gecos = new UTF8StringRef();  // login info
        public final UTF8StringRef pw_dir = new UTF8StringRef();    // home directory
        public final UTF8StringRef pw_shell = new UTF8StringRef();  // default shell
        public final Signed64 pw_expire = new Signed64();    // account expiration
    }
    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    OpenBSDPasswd(jnr.ffi.Pointer memory) {
        super(memory);
    }

    public java.lang.String getAccessClass() {
        return layout.pw_class.get(memory);
    }

    public java.lang.String getGECOS() {
        return layout.pw_gecos.get(memory);
    }

    public long getGID() {
        return layout.pw_gid.get(memory);
    }

    public java.lang.String getHome() {
        return layout.pw_dir.get(memory);
    }

    public java.lang.String getLoginName() {
        return layout.pw_name.get(memory);
    }

    public int getPasswdChangeTime() {
        return layout.pw_change.intValue(memory);
    }

    public java.lang.String getPassword() {
        return layout.pw_passwd.get(memory);
    }

    public java.lang.String getShell() {
        return layout.pw_shell.get(memory);
    }

    public long getUID() {
        return layout.pw_uid.get(memory);
    }

    public int getExpire() {
        return layout.pw_expire.intValue(memory);
    }
}
