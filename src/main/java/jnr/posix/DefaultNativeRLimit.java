package jnr.posix;

public class DefaultNativeRLimit extends RLimit {
    public final UnsignedLong rlim_cur = new UnsignedLong();
    public final UnsignedLong rlim_max = new UnsignedLong();

    protected DefaultNativeRLimit(jnr.ffi.Runtime runtime) {
        super(runtime);
    }

    @Override
    public void init(long rlimCur, long rlimMax) {
        rlim_cur.set(rlimCur);
        rlim_max.set(rlimMax);
    }

    @Override
    public long rlimCur() {
        return rlim_cur.get();
    }

    @Override
    public long rlimMax() {
        return rlim_max.get();
    }
}
