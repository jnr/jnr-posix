package jnr.posix;

public interface SignalHandler {
    public void handle(int signal);
}
