package org.garret.jipc.server;

import org.garret.jipc.*;
import java.io.*;

class JIPCQueueImpl extends JIPCPrimitiveImpl implements JIPCQueue { 
    JIPCQueueImpl(JIPCServer server, String name) {
	super(server, name);
    }
 
    static class QueueElement { 
	QueueElement next;
	Serializable value;
	Receipt      receipts;
	int          nReceipts;
	boolean      isBroadcast;

	void dump(PrintStream out) { 
	    out.println("    Object = " + value);
	    if (isBroadcast && receipts != null) { 
		out.println("      Broadcasted by: " + receipts.session);
		if (receipts.next != null) { 
		    out.println("      Already received by:");
		    for (Receipt rc = receipts.next; rc != null; rc = rc.next) { 
			out.println("        " + rc.session);
		    }
		}
	    }
	}

	QueueElement(Serializable obj) { 
	    value = obj;
	}
	QueueElement(Serializable obj, JIPCSessionImpl session) { 
	    value = obj;
	    receipts = new Receipt(session, null);
	    nReceipts = 1;
	}
    };

    static class Receipt { 
	Receipt         next;
	JIPCSessionImpl session;

	Receipt(JIPCSessionImpl session, Receipt list) { 
	    next = list;
	    this.session = session;
	}
    }

    static final int CHECK_ONLY = 1;

    boolean nextElementAvailable() { 
	if (messageList == null) { 
	    return false;
	}
	if (!messageList.isBroadcast) { 
	    return true;
	}
	JIPCSessionImpl session = server.getSession();
	for (Receipt rc = messageList.receipts; rc != null; rc = rc.next) { 
	    if (rc.session == session) { 
		return false;
	    }
	}
	return true;
    }

    boolean nextElementAvailable(QueueElement element, JIPCWaitObject wob) { 
	if (messageList == null) { 
	    return false;
	}
	if (!messageList.isBroadcast) { 
	    return true;
	}
	for (Receipt rc = element.receipts; rc != null; rc = rc.next) { 
	    if (rc.session == wob.session) { 
		return false;
	    }
	}
	return true;
    }

    public void priorityWait(int rank) throws JIPCException { 
	cs.enter();
	if (!nextElementAvailable()) { 
	    server.waitNotification(this, CHECK_ONLY, rank);
	}	
	cs.leave();
    }

    public boolean priorityWait(int rank, long timeout) throws JIPCException { 
	boolean result = true;
	cs.enter();
	if (!nextElementAvailable()) { 
	    JIPCWaitObject wob = server.waitNotificationWithTimeout(this, timeout, CHECK_ONLY, rank);
	    if (!wob.signaled) { 
		result = false;
	    }
	}	
	cs.leave();
	return result;
    }

    public Serializable get() throws JIPCException {
        return priorityGet(DEFAULT_RANK);
    }

    public Serializable priorityGet(int rank) throws JIPCException {
	cs.enter();
	Serializable obj;
	if (nextElementAvailable()) { 
	    obj = messageList.value;
	    if (messageList.isBroadcast) { 
		if (++messageList.nReceipts == accessCount) {
		    messageList = messageList.next;
		    while (messageList != null && send(messageList)) { 
			messageList = messageList.next;
                        nElems -= 1;
		    }
		} else { 
		    messageList.receipts = new Receipt(server.getSession(), messageList.receipts);
		}
	    } else { 
		messageList = messageList.next;
                nElems -= 1;
	    }
	} else { 
	    //System.out.println("!!!!!!!!!!!!! Session " + server.getSession() + " is blocked");
	    // server.dump(System.out);
	    JIPCWaitObject wob = server.waitNotification(this, 0, rank);
	    obj = (Serializable)wob.data;
	}	
	cs.leave();
	return obj;
    }

    public Serializable get(long timeout) throws JIPCException {
        return priorityGet(DEFAULT_RANK, timeout);
    }

    public Serializable priorityGet(int rank, long timeout) throws JIPCException {
	cs.enter();
	Serializable obj = null;
	if (nextElementAvailable()) { 
	    obj = messageList.value;
	    if (messageList.isBroadcast) { 
		if (++messageList.nReceipts == accessCount) { 
		    messageList = messageList.next;
		    while (messageList != null && send(messageList)) { 
			messageList = messageList.next;
                        nElems -= 1;
		    }
		} else { 
		    messageList.receipts = new Receipt(server.getSession(), messageList.receipts);
		}
	    } else { 
		messageList = messageList.next;
                nElems -= 1;
	    }
	} else { 
	    JIPCWaitObject wob = server.waitNotificationWithTimeout(this, timeout, 0, rank);
	    if (wob.signaled) { 
		obj = (Serializable)wob.data;
	    }
	}	
	cs.leave();
	return obj;
    }

    boolean send(QueueElement element) throws JIPCException {
	JIPCWaitObject head = wobList;
        JIPCWaitObject wob = head;
        while ((wob = wob.next) != head) { 
	    if (nextElementAvailable(element, wob)) { 
                wob.unlink();
		if ((wob.flags & CHECK_ONLY) != 0) { 
		    wob.sendNotification();
		} else { 
		    wob.sendNotification(element.value);
		    if (element.isBroadcast) { 
			if (++element.nReceipts == accessCount) {
			    return true;
			} else { 
			    element.receipts = new Receipt(wob.session, element.receipts);
			}
		    } else { 
			return true;
		    }
		}
	    }
	}
	return false;
    }

    public void put(Serializable obj) throws JIPCException {
	cs.enter();
	QueueElement newElem = new QueueElement(obj);
	if (!send(newElem)) { 
	    if (messageList == null) { 	    
		messageList = newElem;
	    } else { 
		lastElement.next = newElem;
	    }
	    lastElement = newElem;
            nElems += 1;
	}
	cs.leave();
    }

     public void broadcast(Serializable obj) throws JIPCException {
	cs.enter();
	QueueElement newElem = new QueueElement(obj, server.getSession());
	if (!send(newElem)) { 
	    if (messageList == null) { 	    
		messageList = newElem;
	    } else { 
		lastElement.next = newElem;
	    }
	    lastElement = newElem;
            nElems += 1;
	}
	cs.leave();
    }

    public void reset() throws JIPCException {
	cs.enter();
	messageList = null;
        nElems = 0;
	cs.leave();
    }
    
    protected void endAccess(JIPCSessionImpl session) throws JIPCException, IOException { 
	cs.enter();
	QueueElement elem = messageList, prevElem = null;
	boolean newTopElem = false; 
	while (elem != null) { 
	    if (elem.isBroadcast) { 
		Receipt rp, prev; 
		for (rp = elem.receipts, prev = null; rp != null && rp.session != session; rp = rp.next);
		if (rp != null) {
		    if (prev != null) { 
			prev.next = rp.next;
		    } else { 
			elem.receipts = rp.next;
		    }
		    elem.nReceipts -= 1;
		} else if (elem.nReceipts == accessCount-1) { 
		    QueueElement next = elem.next;
		    if (prevElem == null) { 
			newTopElem = true;
			messageList = next;
		    } else { 
			prevElem.next = next;
		    }
		    if (lastElement == elem) {
			lastElement = prevElem;
		    }		 
		    elem = next;
                    nElems -= 1;
		    continue;
		}
	    }
	    prevElem = elem;
	    elem = elem.next;
	}
	if (newTopElem) { 
	    while ((elem = messageList) != null && send(elem)) { 
		messageList = elem.next;
                nElems -= 1;
	    }
	}
	cs.leave();
	super.endAccess(session);
    }

    public int size() { 
        return nElems;
    }

    public void deletePrimitive() { 
	server.deleteQueue(this);
    }

    public void dump(PrintStream out) throws JIPCException { 
	super.dump(out);
	cs.enter();
	out.println("  Messages (" + nElems + "):");
	for (QueueElement msg = messageList; msg != null; msg = msg.next) { 
	    msg.dump(out);
	}
	cs.leave();
    }

    QueueElement messageList;
    QueueElement lastElement;
    int          nElems;
}




