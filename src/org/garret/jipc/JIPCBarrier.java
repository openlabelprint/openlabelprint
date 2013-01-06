package org.garret.jipc;

import java.io.IOException;

/**
 * Barrier sycnhronization object.
 * This synchronization primitive is used to enhure that all processes
 * reach the same step. Session will be blocked on barrier until
 * all other sessions will also excute <code>waitFor</code> method
 * for this barrier (and also be blocked). Then, once all sessions, 
 * are blocked, all of them are released.<P>
 * Semantic of methods inherited from <code>JIPCPrimitive</code>:
 * <DL>
 * <DT><code>waitFor</code><DD>Wait until specified number of other sessions will 
 * reach this barrier (be blocked on this barrier)
 * <DT><code>reset</code><DD>Wakeup all blocked sessions
 * </DL>
 */
public interface JIPCBarrier extends JIPCPrimitive { 
}
