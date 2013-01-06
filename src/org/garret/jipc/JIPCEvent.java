package org.garret.jipc;

import java.io.IOException;

/**
 * Event synchronization object. It provides the same functionality as
 * event object in Win32. Event can be either with <i>manual reset</i>
 * or with <i>automatic reset</i>. In first case evet remains
 * in signaled state until been explcitely reset by user.
 * Evet with <i>automatic reset</i> is switched to non-signled state
 * after first wait request.<P>
 * Semantic of methods inherited from <code>JIPCPrimitive</code>:
 * <DL>
 * <DT><code>waitFor</code><DD>Wait until event is set to signaled state. If
 * event is already in signaled state then method immediatly returns
 * and event with <i>automatic reset</i> is switched to non-signaled state
 * <DT><code>reset</code><DD>Switch event to non-signaled state
 * </DL>
 */
public interface JIPCEvent extends JIPCPrimitive { 
    /**
     * Set event to the signaled state.
     * <UL>
     * <LI>For event with <I>manual reset</I> policy, this method
     * wakes up all waiting sessions and switch event to the signaled state
     * <LI>For even with <I>automatic reset</I> policy, this method
     * invokes exactly one waiting session if such exists, otherwise
     * switcheds event to the signled state
     * </UL>
     */
    public void signal() throws JIPCException, IOException;    

    /**
     * Wakeup one or all waiting sessions.
     * <UL>
     * <LI>For event with <I>manual reset</I> policy, this method
     * wakes up all waiting sessions.
     * <LI>For even with <I>automatic reset</I> policy, this method
     * invokes exactly one waiting session if such exists
     * </UL>
     */ 
    public void pulse() throws JIPCException, IOException;
}



