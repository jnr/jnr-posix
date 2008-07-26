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

package org.jruby.ext.posix;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class OpenBSDPasswd extends NativePasswd implements Passwd {
    public String pw_name;   // user name
    public String pw_passwd; // password (encrypted)
    public int pw_uid;       // user id
    public int pw_gid;       // user id
    public NativeLong pw_change;    // password change time
    public String pw_class;  // user access class
    public String pw_gecos;  // login info
    public String pw_dir;    // home directory
    public String pw_shell;  // default shell
    public NativeLong pw_expire;    // account expiration

    OpenBSDPasswd(Pointer memory) {
        useMemory(memory);
        read();
    }

    public String getAccessClass() {
        return pw_class;
    }

    public String getGECOS() {
        return pw_gecos;
    }

    public long getGID() {
        return pw_gid;
    }

    public String getHome() {
        return pw_dir;
    }

    public String getLoginName() {
        return pw_name;
    }

    public int getPasswdChangeTime() {
        return pw_change.intValue();
    }

    public String getPassword() {
        return pw_passwd;
    }

    public String getShell() {
        return pw_shell;
    }

    public long getUID() {
        return pw_uid;
    }

    public int getExpire() {
        return pw_expire.intValue();
    }
}
