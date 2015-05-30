package jnr.posix;

public class DefaultNativeRLimit extends RLimit {
    public final UnsignedLong rlim_cur = new UnsignedLong();
    public final UnsignedLong rlim_max = new UnsignedLong();

    protected DefaultNativeRLimit(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    @Override
    public void init(long current, long max) {
        rlim_cur.set(current);
        rlim_max.set(max);
    }

    @Override
    public long getCurrent() {
        return rlim_cur.get();
    }

    @Override
    public long getMax() {
        return rlim_max.get();
    }
}
