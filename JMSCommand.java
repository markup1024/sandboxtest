package com.timeinc.tcs.jms;

/**
 * Insert the type's description here.
 * Creation date: (10/19/01 2:35:30 PM)
 * @author: Administrator
 */
 import javax.jms.*;

import com.timeinc.tcs.Log;
public abstract class JMSCommand {

	private static final String QUEUE_CONNECTION_FACTORY="QUEUE_CONNECTION_FACTORY"; 
	protected static final String TRANSPORT_TYPE="TRANSPORT_TYPE";
	protected static final String CHANNEL="CHANNEL";
	protected static final String HOST_NAME="HOST_NAME";
	protected static final String PORT="PORT";
	protected static final String QUEUE_MANAGER="QUEUE_MANAGER";
	protected static final String TEMPORARY_MODEL="TEMPORARY_MODEL";
	protected static final String QUEUE_NAME="QUEUE_NAME";
	protected static final String MANAGED="MANAGED";
	protected static final String JNDI_FACTORY_NAME="JNDI_FACTORY_NAME";
	protected static final String JNDI_QUEUE_NAME="JNDI_QUEUE_NAME";
	protected static final String Q_EXPIRY ="Q_EXPIRY";
	protected static final String Q_PERSISTENCE="Q_PERSISTENCE";
	protected static final String USERID="USERID";

	static boolean debug = true;
	
/**
 * JMSCommand constructor comment.
 */
public JMSCommand() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/01 3:15:36 PM)
 * @return java.lang.String
 * @param text java.lang.String
 */
public abstract Message getMessage(String text) throws JMSException, Exception;
/**-+
 * Insert the method's description here.
 * Creation date: (10/19/01 3:15:36 PM)
 * @return java.lang.String
 * @param text java.lang.String
 */
public abstract String putMessage(String text, String messageId) throws JMSException, Exception;
	/**
	 * Returns the debug.
	 * @return boolean
	 */
	public static boolean isDebug() {
		return debug;
	}

	/**
	 * Sets the debug.
	 * @param debug The debug to set
	 */
	public static void setDebug(boolean debug) {
		JMSCommand.debug = debug;
		Log.setSystemOut(true);
		if( debug)
			Log.setLoglevel(3);
		else
			Log.setLoglevel(1);
	}

}
