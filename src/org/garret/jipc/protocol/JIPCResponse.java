package org.garret.jipc.protocol;

import java.io.Serializable;

public class JIPCResponse implements Serializable { 
    public int          objectId;
    public int          statusCode;
    public long         sequenceNo;
    public Serializable data;

    public static final int OK                = 0;
    public static final int ALREADY_EXISTS    = 1;
    public static final int TIMEOUT_EXPIRED   = 2;
    public static final int NOT_FOUND         = 3;
    public static final int NOT_OWNER         = 4;
    public static final int DEADLOCK          = 5;
    public static final int INTERRUPTED       = 6;
    public static final int INVALID_PARAMETER = 7;
    public static final int INTERNAL_ERROR    = 8;
}
