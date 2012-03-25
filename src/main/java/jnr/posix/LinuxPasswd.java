
package jnr.posix;

import jnr.ffi.StructLayout;

public final class LinuxPasswd extends NativePasswd implements Passwd {
    private static final class Layout extends StructLayout {

        private Layout(jnr.ffi.Runtime runtime) {
            super(runtime);
        }

        public final UTF8StringRef pw_name = new UTF8StringRef();   // user name
        public final UTF8StringRef pw_passwd = new UTF8StringRef(); // password (encrypted)
        public final Signed32 pw_uid = new Signed32();       // user id
        public final Signed32 pw_gid = new Signed32();       // user id
        public final UTF8StringRef pw_gecos = new UTF8StringRef();  // login info
        public final UTF8StringRef pw_dir = new UTF8StringRef();    // home directory
        public final UTF8StringRef pw_shell = new UTF8StringRef();  // default shell
    }
    private static final Layout layout = new Layout(jnr.ffi.Runtime.getSystemRuntime());

    LinuxPasswd(jnr.ffi.Pointer memory) {
        super(memory);
    }
    
    public java.lang.String getAccessClass() {
        return "";
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
    public java.lang.String getPassword() {
        return layout.pw_passwd.get(memory);
    }
    public java.lang.String getShell() {
        return layout.pw_shell.get(memory);
    }
    public long getUID() {
        return layout.pw_uid.get(memory);
    }
    public int getPasswdChangeTime() {
        return 0;
    }
    public int getExpire() {
        return Integer.MAX_VALUE;
    }
}
