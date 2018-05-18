package jnr.posix;

public final class AixFlock extends Flock {
    public final Signed16 l_type = new Signed16();
    public final Signed16 l_whence = new Signed16();
    public final Unsigned32 l_sysid = new Unsigned32();
    public final Signed32 l_pid = new Signed32();
    public final Signed32 l_vfs = new Signed32();
    public final SignedLong l_start = new SignedLong();
    public final SignedLong l_len = new SignedLong();

    public AixFlock(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    public void type(short type) {
        this.l_type.set(type);
    }

    public void whence(short whence) {
        this.l_whence.set(whence);
    }

    public void start(long start) {
        this.l_start.set(start);
    }

    public void len(long len) {
        this.l_len.set(len);
    }

    public void pid(int pid) {
        this.l_pid.set(pid);
    }

    public short type() {
        return this.l_type.get();
    }

    public short whence() {
        return this.l_whence.get();
    }

    public long start() {
        return this.l_start.get();
    }

    public long len() {
        return this.l_len.get();
    }

    public int pid() {
        return this.l_pid.get();
    }
}
