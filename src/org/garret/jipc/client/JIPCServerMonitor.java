package org.garret.jipc.client;

import org.garret.jipc.*;
import java.io.*;

/**
 * Server monitor class is used to send commands to the JIPC server spawned in daemon mode
 * (without user dialog). 
 * <PRE>
 *    Usage: 
 *        java org.garret.jipc.client.JIPCServerMonitor HOSTNAME PORT [command]
 *    Commands:
 *        SHUTDOWN            shutdown server
 *        INFO                dump information about server status
 * </PRE>
 * If command is not specified in command line, server monitor starts interactive dialog.
 * In this dialog, except SHTDOWN and INFO commands it also supports EXIT and HELP commands.
 * EXIT command is used to close dialog, HELP shows list of available commands.
 */
public class JIPCServerMonitor 
{ 
    /**
     * Server monitor main function. It should be called with 2 or 3 command line parameters.
     * Mandatory parameters are HOSTNAME and PORT. Optional third parameter specifies
     * command to be executed. If third parameter is absent, interactive dialog is started.
     * Otherwise monitor will exit after execution of the command.
     * @param args command line 
     */
    public static void main(String args[]) throws Exception 
    { 
	if (args.length < 2) { 
	    System.err.println("Usage: java org.garret.jipc.client.JIPCServerMonitor HOSTNAME PORT [info|exit]");
	    System.exit(1);
	}
	String hostname = args[0];
	int    port = Integer.parseInt(args[1], 10);
	JIPCServerMonitor monitor = new JIPCServerMonitor(hostname, port);
	boolean succeed = true;
	if (args.length > 2) { 
	    succeed = monitor.executeCommand(System.out, args[2]);
	} else { 
	    monitor.dialog();
	}	
	System.exit(succeed ? 0 : 1); 
    }
    
    /**
     * Server monitor constructor. This constructor creates clinet session and
     * establish connection with the server.
     * @param hostname address of the server
     * @param port server port
     */
    public JIPCServerMonitor(String hostname, int port) throws Exception
    { 
	JIPCFactory factory = JIPCClientFactory.getInstance();
	session = factory.create(hostname, port);
    }

    

    String input(String prompt) 
    {
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

    void dialog() throws Exception 
    {
	in = new BufferedReader(new InputStreamReader(System.in)); 
	while (true) {
	    String cmd = input("> ");
	    if (cmd.equalsIgnoreCase("exit")) { 
		break;
	    } else if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("?")) { 
		System.out.println("Commands: exit, shutdown, info, help");
	    } else { 
		executeCommand(System.out, cmd); 
	    }
	}
    }

    /**
     * Send command to the server
     * @param out print stream where result will be printed (for INFO command)
     * @param cmd command to be executed (INFO or SHUTDOWN)
     * @return <code>true</code> if command was succefully executed
     */
    public boolean executeCommand(PrintStream out, String cmd) throws Exception 
    {
	if (cmd.equalsIgnoreCase("info")) { 
	    session.showServerInfo(out);
	} else if (cmd.equalsIgnoreCase("shutdown")) { 
	    session.shutdownServer();
	} else { 
	    out.println("Unknown command: " + cmd);
	    out.println("Available commands: exit, shutdown, info, help");
	    return false;
	}
	return true;
    }

    JIPCSession    session;
    BufferedReader in;    
}
