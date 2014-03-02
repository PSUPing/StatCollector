package edu.drexel.StatCollector.sensors;

/**
 * Created by Matt on 2/8/14.
 */
public class SystemCalls {

    static {
        System.loadLibrary("systemcalls");
    }

    /**
     * Adds two integers, returning their sum
     */
    public native int add( int v1, int v2 );

    /**
     * Returns Hello World string
     */
    public native String hello();

    public native String getSystemCalls(int pid);
}