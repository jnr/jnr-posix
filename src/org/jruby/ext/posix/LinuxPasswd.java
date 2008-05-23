/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.ext.posix;

import com.sun.jna.Pointer;

/**
 *
 */
public class LinuxPasswd extends NativePasswd implements Passwd {
    public String pw_name;   // user name
    public int pw_uid;       // user id
    public int pw_gid;       // user id
    public String pw_dir;    // home directory
    public String pw_shell;  // default shell
    public String pw_passwd; // password (encrypted)
    public String pw_gecos;  // login info
    
    LinuxPasswd(Pointer memory) {
        useMemory(memory);
        read();
    }
    
    public String getAccessClass() {
        return "";
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
    public String getPassword() {
        return pw_passwd;
    }
    public String getShell() {
        return pw_shell;
    }
    public long getUID() {
        return pw_uid;
    }
    public int getPasswdChangeTime() {
        return 0;
    }
    public int getExpire() {
        return Integer.MAX_VALUE;
    }
}
