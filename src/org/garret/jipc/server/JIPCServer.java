package org.garret.jipc.server;

import org.garret.jipc.*;
import org.garret.jipc.protocol.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Factory for local session. These session can be used to provide
 * synchornization and communitcation between threads in the same process.
 * It is possible to share tghe single session between all threads.
 * Also this class is used as server for remove sessions.
 * Server should be launched by
 * "<code>java org.garret.jipc.server.JIPCServer [-d] PORT</code>" command.
 * The following system proiperties can be soecified:
 * <DL>
 * <DT><code>jipc.debug</code><DD>specifies debugging level:
 * <UL>
 * <LI><code>0</code> no debugging
 * <LI><code>1</code> trace all exceptions
 * <LI><code>2</code> trace session open/close requests
 * <LI><code>3</code> trace all requests
 * </UL>
 * <DT><code>jipc.linger</code><DD>linger time in seconds (default 10)
 * </DL> 
 */
public class JIPCServer extends Thread implements JIPCFactory {
    /**
     * Get instance of the server 
     */
    public static JIPCFactory getInstance() { 
	return theServer;
    }
    
    static JIPCServer theServer = new JIPCServer();

    public JIPCSession create(String address, int port) { 
	return getSession();
    }
    
    /**
     * Dump information about state of the server:
     * all active sessions and all primitives opened by these sessions
     * @param out output stream
     */
    public synchronized void dump(PrintStream out) throws JIPCException {
	out.println("<<<-------- Sessions --------->>>");
	Enumeration enumuration = sessions.elements();
	Iterator iter;
	while (enumuration.hasMoreElements()) { 
	    ((JIPCSessionImpl)enumuration.nextElement()).dump(out);
	}
	out.println("<<<-------- Primitives --------->>>");
	iter = primitives.values().iterator();
	while (iter.hasNext()) {
	    ((JIPCPrimitiveImpl)iter.next()).dump(out);
	}
    }

    String input(String prompt) {
	while (true) { 
	    try { 
		System.out.print(prompt);
		String answer = in.readLine().trim();
		if (answer.length() != 0) {
		    return answer;
		}
	    } catch (IOException x) {}
	}
    }

    /**
     * Server main function.
     * <PRE>
     *    Command syntax: 
     *        java org.garret.jipc.server.JIPCServer [-d] PORT
     *    Options:
     *        -d         start program in daemon mode without interactive dialog
     *    Parameters:
     *        PORT       port at which server will accept client connections
     * </PRE>
     * Whithout "-d" option this functions starts interactive dialog. 
     * The following commands are supported:
     * <DL>
     * <DT><code>info</code><DD>Dump information about all sessions and
     * all primitives
     * <DT><code>exit</code><DD>Shutdown server
     * <DT><code>help</code><DD>Print list of accepted commands
     * </DL><P>
     * When server is started in daemon mode, commands to the server can be sent
     * using <code>org.garret.jipc.client.JIPCServerMonitor</code> utility
     * @see org.garret.jipc.client.JIPCServerMonitor
     */
    public static void main(String args[]) throws Exception 
    { 
	if (args.length < 1 || (args[0].startsWith("-d") && args.length != 2)) {
	    System.err.println("Syntax: java org.garret.jipc.server.JIPCServer [-d] PORT");
	    System.exit(1);
	}
	boolean isDaemon = false;
	int port;
	if (args[0].startsWith("-d")) { 
	    port = Integer.parseInt(args[1], 10);
	    isDaemon = true;
	} else { 
	    port = Integer.parseInt(args[0], 10);
	}
	JIPCServer server = new JIPCServer(port);
	if (isDaemon) { 
	    server.run();
	} else {
	    server.start();
	    server.dialog();
	}
    }

    void dialog() throws JIPCException { 
	in = new BufferedReader(new InputStreamReader(System.in)); 
	while (true) {
	    String cmd = input("> ");
	    if (cmd.equalsIgnoreCase("exit")) { 
		shutdown();	 
		break;
	    } else if (cmd.equalsIgnoreCase("info")) { 
		dump(System.out);
	    } else if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("?")) { 
		System.out.println("Commands: exit, info, help");
	    } else { 
		System.out.println("Invalid command " + cmd);
		System.out.println("Commands: exit, info, help");
	    }
	}
    }
    
    /**
     * Constructor server for remote connections. 
     * This constructor creates server socket at which client connections
     * will be accepted
     * @param port port number at which client connections will be accepted
     */
    public JIPCServer(int port) throws IOException 
    { 
	lingerTime = Integer.parseInt(System.getProperty("jipc.linger", "10"));
	debug = Integer.parseInt(System.getProperty("jipc.debug", "1"));
	socket = new ServerSocket(port);
	this.port = port;
	running = true;
    }

    JIPCServer() {
	debug = Integer.parseInt(System.getProperty("jipc.debug", "1"));
    }

    /**
     * This method is executed by thread accepting client connections 
     */
    public void run() 
    { 
	try { 
	    while (running) { 
		Socket s = socket.accept();			
		if (!running) { 
		    break;
		}
		try { 
		    s.setTcpNoDelay(true);
		} catch (NoSuchMethodError er) {}
		if (lingerTime != 0) { 
		    try {
			s.setSoLinger(true, lingerTime);
		    } catch (NoSuchMethodError er) {}
		}
		startSession(s);
	    }
	} catch(Exception x) {
	    x.printStackTrace();
	}		 
    }
    
    /**
     * Shutdown the server
     */
    public synchronized void shutdown() 
    { 
	running = false;
	try { 
	    Socket s = new Socket("localhost", port); // wakeup accept thread
	    s.close();
	} catch (IOException x) {}
    }

    protected synchronized JIPCPrimitiveImpl getPrimitive(int oid) throws JIPCException { 
	JIPCPrimitiveImpl prim = (JIPCPrimitiveImpl)primitives.get(new Integer(oid));
	if (prim == null) { 
	    throw new JIPCNotFoundException();
	}
	return prim;
    }

    protected JIPCEventImpl getEvent(int oid) throws JIPCException {
	JIPCPrimitiveImpl prim = getPrimitive(oid);
	if (!(prim instanceof JIPCEventImpl)) { 
	    throw new JIPCNotFoundException();
	}
	return (JIPCEventImpl)prim;
    }

    protected JIPCMutexImpl getMutex(int oid) throws JIPCException {
	JIPCPrimitiveImpl prim = getPrimitive(oid);
	if (!(prim instanceof JIPCMutexImpl)) { 
	    throw new JIPCNotFoundException();
	}
	return (JIPCMutexImpl)prim;
    }

    protected JIPCSemaphoreImpl getSemaphore(int oid) throws JIPCException {
	JIPCPrimitiveImpl prim = getPrimitive(oid);
	if (!(prim instanceof JIPCSemaphoreImpl)) { 
	    throw new JIPCNotFoundException();
	}
	return (JIPCSemaphoreImpl)prim;
    }

    protected JIPCQueueImpl getQueue(int oid) throws JIPCException {
	JIPCPrimitiveImpl prim = getPrimitive(oid);
	if (!(prim instanceof JIPCQueueImpl)) { 
	    throw new JIPCNotFoundException();
	}
	return (JIPCQueueImpl)prim;
    }

    protected JIPCSharedMemoryImpl getSharedMemory(int oid) throws JIPCException {
	JIPCPrimitiveImpl prim = getPrimitive(oid);
	if (!(prim instanceof JIPCSharedMemoryImpl)) { 
	    throw new JIPCNotFoundException();
	}
	return (JIPCSharedMemoryImpl)prim;
    }

    protected JIPCLockImpl getLock(int oid) throws JIPCException {
	JIPCPrimitiveImpl prim = getPrimitive(oid);
	if (!(prim instanceof JIPCLockImpl)) { 
	    throw new JIPCNotFoundException();
	}
	return (JIPCLockImpl)prim;
    }

    protected synchronized JIPCEventImpl createEvent(JIPCSessionImpl session, String name, boolean signaled, boolean manualReset) 
      throws JIPCException
    {
	JIPCEventImpl event = (JIPCEventImpl)events.get(name);
	if (event != null) { 
	    session.exists = true;
	} else { 
	    event = new JIPCEventImpl(this, name, signaled, manualReset);
	    events.put(name, event);
	    session.exists = false;
	}
	session.beginAccess(event);
	return event;
    }    

    protected synchronized JIPCMutexImpl createMutex(JIPCSessionImpl session, String name, boolean locked) 
      throws JIPCException
    {
	JIPCMutexImpl mutex = (JIPCMutexImpl)mutexes.get(name);
	if (mutex != null) { 
	    session.exists = true;
	} else { 
	    mutex = new JIPCMutexImpl(this, name, locked);
	    mutexes.put(name, mutex);
	    session.exists = false;
	}
	session.beginAccess(mutex);
	return mutex;
    }    

    protected synchronized JIPCSemaphoreImpl createSemaphore(JIPCSessionImpl session, String name,int initCount) 
      throws JIPCException
    {
	JIPCSemaphoreImpl semaphore = (JIPCSemaphoreImpl)semaphores.get(name);
	if (semaphore != null) { 
	    session.exists = true;
	} else { 
	    semaphore = new JIPCSemaphoreImpl(this, name, initCount);
	    semaphores.put(name, semaphore);
	    session.exists = false;
	}
	session.beginAccess(semaphore);
	return semaphore;
    }    

    protected synchronized JIPCLockImpl createLock(JIPCSessionImpl session, String name) 
      throws JIPCException
    {
	JIPCLockImpl lock = (JIPCLockImpl)locks.get(name);
	if (lock != null) { 
	    session.exists = true;
	} else { 
	    lock = new JIPCLockImpl(this, name);
	    locks.put(name, lock);
	    session.exists = false;
	}
	session.beginAccess(lock);
	return lock;
    }    

    protected synchronized JIPCBarrierImpl createBarrier(JIPCSessionImpl session, String name, int nSessions) 
      throws JIPCException
    {
	JIPCBarrierImpl barrier = (JIPCBarrierImpl)barriers.get(name);
	if (barrier != null) { 
	    session.exists = true;
	} else { 
	    barrier = new JIPCBarrierImpl(this, name, nSessions);
	    barriers.put(name, barrier);
	    session.exists = false;
	}
	session.beginAccess(barrier);
	return barrier;
    }    

    protected synchronized JIPCQueueImpl createQueue(JIPCSessionImpl session, String name) 
      throws JIPCException
    {
	JIPCQueueImpl queue = (JIPCQueueImpl)queues.get(name);
	if (queue != null) { 
	    session.exists = true;
	} else { 
	    queue = new JIPCQueueImpl(this, name);
	    queues.put(name, queue);
	    session.exists = false;
	}
	session.beginAccess(queue);
	return queue;
    }    

    protected synchronized JIPCSharedMemoryImpl createSharedMemory(JIPCSessionImpl session,
								   String name, Serializable obj)
      throws JIPCException
    {
	JIPCSharedMemoryImpl shmem = (JIPCSharedMemoryImpl)shmems.get(name);
	if (shmem != null) { 
	    session.exists = true;
	} else { 
	    shmem = new JIPCSharedMemoryImpl(this, name, obj);
	    shmems.put(name, shmem);
	    session.exists = false;
	}
	session.beginAccess(shmem);
	return shmem;
    }    

    protected synchronized JIPCEventImpl openEvent(JIPCSessionImpl session, String name) 
      throws JIPCException
    {
	JIPCEventImpl event = (JIPCEventImpl)events.get(name);
	if (event != null) { 
	    session.beginAccess(event);
	}
	return event;
    }    

    protected synchronized JIPCMutexImpl openMutex(JIPCSessionImpl session, String name) 
      throws JIPCException
    {
	JIPCMutexImpl mutex = (JIPCMutexImpl)mutexes.get(name);
	if (mutex != null) { 
	    session.beginAccess(mutex);
	}
	return mutex;
    }    

    protected synchronized JIPCQueueImpl openQueue(JIPCSessionImpl session, String name) 
      throws JIPCException
    {
	JIPCQueueImpl queue = (JIPCQueueImpl)queues.get(name);
	if (queue != null) { 
	    session.beginAccess(queue);
	}
	return queue;
    }    

    protected synchronized JIPCSemaphoreImpl openSemaphore(JIPCSessionImpl session, String name) 
      throws JIPCException
    {
	JIPCSemaphoreImpl semaphore = (JIPCSemaphoreImpl)semaphores.get(name);
	if (semaphore != null) { 
	    session.beginAccess(semaphore);
	}
	return semaphore;
    }    

    protected synchronized JIPCLockImpl openLock(JIPCSessionImpl session, String name) 
      throws JIPCException
    {
	JIPCLockImpl lock = (JIPCLockImpl)locks.get(name);
	if (lock != null) { 
	    session.beginAccess(lock);
	}
	return lock;
    }    

    protected synchronized JIPCBarrierImpl openBarrier(JIPCSessionImpl session, String name) 
      throws JIPCException
    {
	JIPCBarrierImpl barrier = (JIPCBarrierImpl)barriers.get(name);
	if (barrier != null) { 
	    session.beginAccess(barrier);
	}
	return barrier;
    }    

    protected synchronized JIPCSharedMemoryImpl openSharedMemory(JIPCSessionImpl session, String name)
      throws JIPCException
    {
	JIPCSharedMemoryImpl shmem = (JIPCSharedMemoryImpl)shmems.get(name);
	if (shmem != null) { 
	    session.beginAccess(shmem);
	}
	return shmem;
    }    

    protected void deleteEvent(JIPCEventImpl event) { 
	events.remove(event.name);
	primitives.remove(new Integer(event.id));
    }

    protected void deleteMutex(JIPCMutexImpl mutex) { 
	mutexes.remove(mutex.name);
	primitives.remove(new Integer(mutex.id));
    }

    protected void deleteSemaphore(JIPCSemaphoreImpl semaphore) { 
	semaphores.remove(semaphore.name);
	primitives.remove(new Integer(semaphore.id));
    }

    protected void deleteQueue(JIPCQueueImpl queue) { 
	queues.remove(queue.name);
	primitives.remove(new Integer(queue.id));
    }

    protected void deleteSharedMemory(JIPCSharedMemoryImpl shmem) { 
	shmems.remove(shmem.name);
	primitives.remove(new Integer(shmem.id));
    }

    protected void deleteLock(JIPCLockImpl lock) { 
	locks.remove(lock.name);
	primitives.remove(new Integer(lock.id));
    }

    protected void deleteBarrier(JIPCBarrierImpl barrier) { 
	barriers.remove(barrier.name);
	primitives.remove(new Integer(barrier.id));
    }

    protected void assignId(JIPCPrimitiveImpl prim) { 
	prim.id = ++lastPrimId;
	primitives.put(new Integer(prim.id), prim);
    }

    protected JIPCResponse handleRequest(JIPCSessionImpl session, JIPCRequest req)
      throws JIPCException
    { 
	JIPCResponse resp = new JIPCResponse();
	try { 
	    resp.statusCode = JIPCResponse.OK;
	    if (debug >= DEBUG_REQUESTS) { 
		JIPCPrimitiveImpl prim = req.objectId != 0 
		    ? (JIPCPrimitiveImpl)primitives.get(new Integer(req.objectId)) : null;
		System.out.println("Session " + session + " receive request " + req + " " 
				   + (prim != null ? prim.toString() : ("name=" + req.name)));
	    }
	    switch (req.opCode) { 
	      case JIPCRequest.LOGIN:
		break;
	      case JIPCRequest.WAIT:
		getPrimitive(req.objectId).priorityWait(req.rank);
		break;
	      case JIPCRequest.TIMED_WAIT:
		if (!getPrimitive(req.objectId).priorityWait(req.rank, req.value)) { 
		    resp.statusCode = JIPCResponse.TIMEOUT_EXPIRED;
		}
		break;
	      case JIPCRequest.RESET:
		getPrimitive(req.objectId).reset();
		break;
	      case JIPCRequest.SIGNAL_EVENT:
		getEvent(req.objectId).signal();
		break;
	      case JIPCRequest.SIGNAL_SEMAPHORE:
		getSemaphore(req.objectId).signal();
		break;
	      case JIPCRequest.PULSE:
		getEvent(req.objectId).pulse();
		break;
	      case JIPCRequest.LOCK_MUTEX:
		getMutex(req.objectId).priorityLock(req.rank);
		break;
	      case JIPCRequest.TIMED_LOCK_MUTEX:
		if (!getMutex(req.objectId).priorityLock(req.rank, req.value)) { 
		    resp.statusCode = JIPCResponse.TIMEOUT_EXPIRED;
		}
		break;
	      case JIPCRequest.UNLOCK_MUTEX:
		getMutex(req.objectId).unlock();
		break;
	      case JIPCRequest.SHARED_LOCK:
		getLock(req.objectId).prioritySharedLock(req.rank);
		break;
	      case JIPCRequest.TIMED_SHARED_LOCK:
		if (!getLock(req.objectId).prioritySharedLock(req.rank, req.value)) { 
		    resp.statusCode = JIPCResponse.TIMEOUT_EXPIRED;
		}
		break;
	      case JIPCRequest.EXCLUSIVE_LOCK:
		getLock(req.objectId).priorityExclusiveLock(req.rank);
		break;
	      case JIPCRequest.TIMED_EXCLUSIVE_LOCK:
		if (!getLock(req.objectId).priorityExclusiveLock(req.rank, req.value)) { 
		    resp.statusCode = JIPCResponse.TIMEOUT_EXPIRED;
		}
		break;
	      case JIPCRequest.UNLOCK:
		getLock(req.objectId).unlock();
		break;
	      case JIPCRequest.ENQUEUE:
		getQueue(req.objectId).put(req.data);
		break;
	      case JIPCRequest.BROADCAST:
		getQueue(req.objectId).broadcast(req.data);
		break;
	      case JIPCRequest.TIMED_DEQUEUE:
		resp.data = getQueue(req.objectId).priorityGet(req.rank, req.value);
		break;
	      case JIPCRequest.DEQUEUE:
		resp.data = getQueue(req.objectId).priorityGet(req.rank);
		break;
              case JIPCRequest.QUEUE_SIZE:
                resp.data = new Integer(getQueue(req.objectId).size());
                break;
	      case JIPCRequest.GET_OBJECT:
		resp.data = getSharedMemory(req.objectId).get();
		break;
	      case JIPCRequest.SET_OBJECT:
		getSharedMemory(req.objectId).set(req.data);
		break;
	      case JIPCRequest.CREATE_SEMAPHORE:
		resp.objectId = createSemaphore(session, req.name, (int)req.value).id;
		if (session.exists) { 
		    resp.statusCode = JIPCResponse.ALREADY_EXISTS;
		}
		break;
  	      case JIPCRequest.CREATE_EVENT:
		resp.objectId = createEvent(session, req.name, req.value != 0, true).id;
		if (session.exists) { 
		    resp.statusCode = JIPCResponse.ALREADY_EXISTS;
		}
		break;
	      case JIPCRequest.CREATE_AUTO_EVENT:
		resp.objectId = createEvent(session, req.name, req.value != 0, false).id;
		if (session.exists) { 
		    resp.statusCode = JIPCResponse.ALREADY_EXISTS;
		}
		break;
	      case JIPCRequest.CREATE_MUTEX:
		resp.objectId = createMutex(session, req.name, req.value != 0).id;
		if (session.exists) { 
		    resp.statusCode = JIPCResponse.ALREADY_EXISTS;
		}
		break;
	      case JIPCRequest.CREATE_QUEUE:
		resp.objectId = createQueue(session, req.name).id;
		if (session.exists) { 
		    resp.statusCode = JIPCResponse.ALREADY_EXISTS;
		}
		break;
	      case JIPCRequest.CREATE_SHMEM:
		resp.objectId = createSharedMemory(session, req.name, req.data).id;
		if (session.exists) { 
		    resp.statusCode = JIPCResponse.ALREADY_EXISTS;
		}
		break;
	      case JIPCRequest.CREATE_LOCK:
		resp.objectId = createLock(session, req.name).id;
		if (session.exists) { 
		    resp.statusCode = JIPCResponse.ALREADY_EXISTS;
		}
		break;
	      case JIPCRequest.CREATE_BARRIER:
		resp.objectId = createBarrier(session, req.name, (int)req.value).id;
		if (session.exists) { 
		    resp.statusCode = JIPCResponse.ALREADY_EXISTS;
		}
		break;
	      case JIPCRequest.OPEN_SEMAPHORE:
		JIPCSemaphoreImpl semaphore = openSemaphore(session, req.name);
		if (semaphore == null) {
		    resp.statusCode = JIPCResponse.NOT_FOUND;
		} else { 
		    resp.objectId = semaphore.id;
		}
		break;
	      case JIPCRequest.OPEN_EVENT:
		JIPCEventImpl event = openEvent(session, req.name);
		if (event == null) {
		    resp.statusCode = JIPCResponse.NOT_FOUND;
		} else { 
		    resp.objectId = event.id;
		}
		break;
	      case JIPCRequest.OPEN_MUTEX:
		JIPCMutexImpl mutex = openMutex(session, req.name);
		if (mutex == null) {
		    resp.statusCode = JIPCResponse.NOT_FOUND;
		} else { 
		    resp.objectId = mutex.id;
		}
		break;
	      case JIPCRequest.OPEN_QUEUE:
		JIPCQueueImpl queue = openQueue(session, req.name);
		if (queue == null) {
		    resp.statusCode = JIPCResponse.NOT_FOUND;
		} else { 
		    resp.objectId = queue.id;
		}
		break;
	      case JIPCRequest.OPEN_SHMEM:
		JIPCSharedMemoryImpl shmem = openSharedMemory(session, req.name);
		if (shmem == null) {
		    resp.statusCode = JIPCResponse.NOT_FOUND;
		} else { 
		    resp.objectId = shmem.id;
		}
		break;
	      case JIPCRequest.OPEN_LOCK:
		JIPCLockImpl lock = openLock(session, req.name);
		if (lock == null) {
		    resp.statusCode = JIPCResponse.NOT_FOUND;
		} else { 
		    resp.objectId = lock.id;
		}
		break;
	      case JIPCRequest.OPEN_BARRIER:
		JIPCBarrierImpl barrier = openBarrier(session, req.name);
		if (barrier == null) {
		    resp.statusCode = JIPCResponse.NOT_FOUND;
		} else { 
		    resp.objectId = barrier.id;
		}
		break;
	      case JIPCRequest.CLOSE_PRIMITIVE:
		session.endAccess(getPrimitive(req.objectId)); 
		break;
	      case JIPCRequest.CLOSE_SESSION:		    
		return null;
	      case JIPCRequest.SHOW_INFO:
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bas);
		dump(out);
		out.close();
		resp.data = new String(bas.toByteArray());
		break;
	      case JIPCRequest.SHUTDOWN:
		shutdown();
		return null;
	      default:
		resp.statusCode = JIPCResponse.INTERNAL_ERROR;		
	    }
	    if (debug >= DEBUG_REQUESTS) { 
		System.out.println("Session " + session + " comlete request " + req);
	    }
	} catch (JIPCException x) { 
	    if (debug >= DEBUG_EXCEPTIONS) { 
		x.printStackTrace();
	    }
	    resp.statusCode = x.getResponseCode();
	} catch (Throwable x) { 
	    if (debug >= DEBUG_EXCEPTIONS) { 
		x.printStackTrace();
	    }
	    resp.statusCode = JIPCResponse.INTERNAL_ERROR;
	}
	return resp;
    }

    protected JIPCSessionImpl getSession() { 
	Thread t = Thread.currentThread();
	if (t instanceof JIPCSessionImpl) { 
	    return (JIPCSessionImpl)t;
	}
	JIPCSessionImpl s = (JIPCSessionImpl)sessions.get(t);
	if (s == null) { 
	    s = new JIPCSessionImpl(this);
	    sessions.put(t, s);
	}
	return s;
    }

	
    protected void startSession(Socket s) throws IOException {
	JIPCSessionImpl session = new JIPCSessionImpl(this, s);
	if (debug >= DEBUG_SESSIONS) {
	    System.out.println("Start session " + session);
	}
	sessions.put(session, session);
	session.start();
    }
    
    protected void stopSession(JIPCSessionImpl session) {
	if (debug >= DEBUG_SESSIONS) {
	    System.out.println("Session " + session + " is terminated");
	}
	sessions.remove(session);
    }

    static final int LOCKED_SERVER = 0x1000;
    static final int TIMED_WAIT    = 0x2000;


    protected JIPCWaitObject waitNotification(JIPCPrimitiveImpl prim, int flags, int rank) 
      throws JIPCException 
    { 
	JIPCSessionImpl session = getSession();
	JIPCWaitObject wob = new JIPCWaitObject(session, prim, flags, rank);
	prim.addWaitObject(wob);
	session.waitFor = wob;
	if ((flags & LOCKED_SERVER) != 0) { 
	    cs.leave();
	}
	wob.waitNotification();
    	return wob;	
    }
				   
    protected JIPCWaitObject waitNotificationWithTimeout(JIPCPrimitiveImpl prim, long timeout, int flags, int rank) 
      throws JIPCException 
    { 
	JIPCSessionImpl session = getSession();
	JIPCWaitObject wob = new JIPCWaitObject(session, prim, flags, rank);
	if (timeout != 0) { 
	    cs.enter();
	    wob.flags |= TIMED_WAIT;
	    prim.addWaitObject(wob);
	    session.waitFor = wob;
	    cs.leave();
	    wob.waitNotificationWithTimeout(timeout);
	    if (!wob.signaled) { 
		cs.enter();
		wob.session.waitFor = null;
                wob.unlink();
		cs.leave();
	    }
	}
	return wob;
    }
	
    /**
     * Debug levels
     */
    public static final int DEBUG_NONE       = 0;
    public static final int DEBUG_EXCEPTIONS = 1;
    public static final int DEBUG_SESSIONS   = 2;
    public static final int DEBUG_REQUESTS   = 3;
    
		   
    boolean             running;
    int                 port;
    int                 lingerTime;
    int                 debug;
    ServerSocket        socket;
    int                 lastPrimId;
    BufferedReader      in;

    Hashtable           sessions = new Hashtable();

    HashMap             primitives = new HashMap();
    HashMap             semaphores = new HashMap();
    HashMap             barriers = new HashMap();    
    HashMap             mutexes = new HashMap();
    HashMap             events = new HashMap();
    HashMap             queues = new HashMap();    
    HashMap             shmems = new HashMap();    
    HashMap             locks = new HashMap();    

    JIPCCriticalSection cs = new JIPCCriticalSection();
}
