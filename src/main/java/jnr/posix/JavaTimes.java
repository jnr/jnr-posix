package jnr.posix;

final class JavaTimes implements Times {
    private static final long startTime = System.currentTimeMillis();
    static final long HZ = 1000;

    public long utime() {
        return Math.max(System.currentTimeMillis() - startTime, 1);
    }

    public long stime() {
        return 0;
    }

    public long cutime() {
        return 0;
    }

    public long cstime() {
        return 0;
    }
}
