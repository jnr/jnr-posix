package jnr.posix.util;

public class MethodName {
    private static final int CLIENT_CODE_STACK_INDEX;

    static {
        // Finds out the index of "this code" in the returned stack trace - funny but it differs in JDK 1.5 and 1.6
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            i++;
            if (ste.getClassName().equals(MethodName.class.getName())) {
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
    }

    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName();
    }

    public static String getCallerMethodName() {
        return Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX + 1].getMethodName();
    }
}
