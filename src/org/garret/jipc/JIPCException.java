package org.garret.jipc;

/**
 * Base class for all JIPC exceptions
 */
abstract public class JIPCException extends Exception {
    abstract public int getResponseCode();
}
