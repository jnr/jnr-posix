

package org.jruby.ext.posix;


/**
 *
 */
public final class MacOSPasswd extends NativePasswd implements Passwd {
    public final UTF8StringRef pw_name = new UTF8StringRef();   // user name
    public final UTF8StringRef pw_passwd = new UTF8StringRef(); // password (encrypted)
    public final Signed32 pw_uid = new Signed32();       // user id
    public final Signed32 pw_gid = new Signed32();       // user id
    public final SignedLong pw_change = new SignedLong();    // password change time
    public final UTF8StringRef pw_class = new UTF8StringRef();  // user access class
    public final UTF8StringRef pw_gecos = new UTF8StringRef();  // login info
    public final UTF8StringRef pw_dir = new UTF8StringRef();    // home directory
    public final UTF8StringRef pw_shell = new UTF8StringRef();  // default shell
    public final SignedLong pw_expire = new SignedLong();    // account expiration
    
    MacOSPasswd(jnr.ffi.Pointer memory) {
        useMemory(memory);
    }
    
    public java.lang.String getAccessClass() {
        return pw_class.get();
    }
    
    public java.lang.String getGECOS() {
        return pw_gecos.get();
    }
    
    public long getGID() {
        return pw_gid.get();
    }
    
    public java.lang.String getHome() {
        return pw_dir.get();
    }
    
    public java.lang.String getLoginName() {
        return pw_name.get();
    }
    
    public int getPasswdChangeTime() {
        return pw_change.intValue();
    }
    
    public java.lang.String getPassword() {
        return pw_passwd.get();
    }
    
    public java.lang.String getShell() {
        return pw_shell.get();
    }
    
    public long getUID() {
        return pw_uid.get();
    }
    
    public int getExpire() {
        return pw_expire.intValue();
    }
}
