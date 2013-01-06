package org.garret.jipc.protocol;

import java.io.Serializable;

public class JIPCRequest implements Serializable { 
    public int          objectId;
    public int          opCode;
    public int          rank;
    public long         value;
    public long         sequenceNo;
    public String       name;
    public Serializable data;

    public static final int LOGIN                = 0;
    public static final int WAIT                 = 1;
    public static final int TIMED_WAIT           = 2;
    public static final int RESET                = 3;
    public static final int SIGNAL_EVENT         = 4;
    public static final int SIGNAL_SEMAPHORE     = 5;
    public static final int PULSE                = 6;
    public static final int LOCK_MUTEX           = 7;
    public static final int TIMED_LOCK_MUTEX     = 8;
    public static final int UNLOCK_MUTEX         = 9;
    public static final int ENQUEUE              = 10;
    public static final int BROADCAST            = 11;
    public static final int DEQUEUE              = 12;  
    public static final int TIMED_DEQUEUE        = 13;  
    public static final int SET_OBJECT           = 14;  
    public static final int GET_OBJECT           = 15;  
    public static final int EXCLUSIVE_LOCK       = 16;  
    public static final int SHARED_LOCK          = 17;  
    public static final int TIMED_EXCLUSIVE_LOCK = 18;  
    public static final int TIMED_SHARED_LOCK    = 19;  
    public static final int UNLOCK               = 20;  
    public static final int CREATE_SEMAPHORE     = 21;
    public static final int CREATE_EVENT         = 22;
    public static final int CREATE_AUTO_EVENT    = 23;
    public static final int CREATE_MUTEX         = 24;
    public static final int CREATE_QUEUE         = 25;
    public static final int CREATE_SHMEM         = 26;
    public static final int CREATE_LOCK          = 27;
    public static final int CREATE_BARRIER       = 28;
    public static final int OPEN_SEMAPHORE       = 29;
    public static final int OPEN_EVENT           = 30;
    public static final int OPEN_MUTEX           = 31;
    public static final int OPEN_QUEUE           = 32;
    public static final int OPEN_SHMEM           = 33;
    public static final int OPEN_LOCK            = 34;
    public static final int OPEN_BARRIER         = 35;
    public static final int CLOSE_PRIMITIVE      = 36;
    public static final int CLOSE_SESSION        = 37;
    public static final int SHOW_INFO            = 38;
    public static final int SHUTDOWN             = 39;
    public static final int QUEUE_SIZE           = 40;

    public String toString() {
	switch (opCode) { 
	  case LOGIN: return "LOGIN";
	  case WAIT: return "WAIT";
	  case TIMED_WAIT: return "TIMED_WAIT";
	  case RESET: return "RESET";
	  case SIGNAL_EVENT: return "SIGNAL_EVENT";
	  case SIGNAL_SEMAPHORE: return "SIGNAL_SEMAPHORE";
	  case PULSE: return "PULSE";
	  case LOCK_MUTEX: return "LOCK_MUTEX";
	  case TIMED_LOCK_MUTEX: return "TIMED_LOCK_MUTEX";
	  case UNLOCK_MUTEX: return "UNLOCK_MUTEX";
	  case ENQUEUE: return "ENQUEUE";
	  case DEQUEUE: return "DEQUEUE";
	  case BROADCAST: return "BROADCAST";
	  case TIMED_DEQUEUE: return "TIMED_DEQUEUE";
	  case SET_OBJECT: return "SET_OBJECT";
	  case GET_OBJECT: return "GET_OBJECT";
	  case EXCLUSIVE_LOCK: return "EXCLUSIVE_LOCK";
	  case SHARED_LOCK: return "SHARED_LOCK";
	  case TIMED_EXCLUSIVE_LOCK: return "TIMED_EXCLUSIVE_LOCK";
	  case TIMED_SHARED_LOCK: return "TIMED_SHARED_LOCK";
	  case UNLOCK: return "UNLOCK";
	  case CREATE_SEMAPHORE: return "CREATE_SEMAPHORE";
	  case CREATE_EVENT: return "CREATE_EVENT";
	  case CREATE_AUTO_EVENT: return "CREATE_AUTO_EVENT";
	  case CREATE_MUTEX: return "CREATE_MUTEX";
	  case CREATE_QUEUE: return "CREATE_QUEUE";
	  case CREATE_SHMEM: return "CREATE_SHMEM";
	  case CREATE_LOCK: return "CREATE_LOCK";
	  case CREATE_BARRIER: return "CREATE_BARRIER";
	  case OPEN_SEMAPHORE: return "OPEN_SEMAPHORE";
	  case OPEN_EVENT: return "OPEN_EVENT";
	  case OPEN_MUTEX: return "OPEN_MUTEX";
	  case OPEN_QUEUE: return "OPEN_QUEUE";
	  case OPEN_SHMEM: return "OPEN_SHMEM";
	  case OPEN_LOCK: return "OPEN_LOCK";
	  case OPEN_BARRIER: return "OPEN_BARRIER";
	  case CLOSE_PRIMITIVE: return "CLOSE_PRIMITIVE";
	  case CLOSE_SESSION: return "CLOSE_SESSION";
	  case SHOW_INFO: return "SHOW_INFO";
	  case SHUTDOWN: return "SHUTDOWN";
	  case QUEUE_SIZE: return "QUEUE_SIZE";
	}
	return "???";
    }
}




