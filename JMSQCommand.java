package com.timeinc.tcs.jms;

/**
 * Insert the type's description here.
 * Creation date: (10/19/01 2:20:34 PM)
 * @author: Administrator
 */
import java.util.Locale;

import javax.jms.*;
import javax.naming.InitialContext;
import com.timeinc.tcs.*;
public class JMSQCommand extends JMSCommand {

	protected String propQ=null;
	private String propF= null;
	private QueueConnectionFactory qcf=null;
	private Queue ioqueue=null;
	private JMSProperties properties;
	private String jndiQCF, jndiQ;
	private QueueConnection ioqConn;
	protected QueueSession qSession = null;
	private boolean managed = true;
	private Locale locale = null;
	
	// WebSphere is multi-threaded environment so session cannot be reused.
	// For Screen Pop it's usually reuse session.	
//	protected boolean reuseSession = false;
	protected int errorct = 0;
	protected boolean email_notify = true;	
	
public JMSQCommand(String jndiQCF, String jndiQ) {
	super();
	this.jndiQ = jndiQ;
	this.jndiQCF = jndiQCF;
	this.propQ = jndiQCF+"|"+jndiQ;
	managed = true;
}	
	
/**
 * JMSQCommand constructor comment.
 */
public JMSQCommand(String propertyFile) {
	super();
	propF = propertyFile;
	managed = false;
}

/**
 * JMSQCommand constructor comment.
 */
public JMSQCommand(String propertyFile, Locale loc) {
	this(propertyFile);
	locale = loc;
}

  public Message getMessage()
    throws JMSException, Exception
  {
    return getMessage(null, -1);

  }
/*************************************************************
/* timeout in milliseconds
/*************************************************************
 */ 
  public Message getMessage(int timeout)
    throws JMSException, Exception
  {

    return getMessage(null, timeout);

  }

  public Message getMessage(String messageID)
    throws JMSException, Exception
  {

    return getMessage(messageID, -1);

  }
  
  public Message getMessage(String messageID, int timeout)
    throws JMSException, Exception
  {
  	return getMessage(messageID, timeout, false);
  }
  
	/*************************************************************
	*  timeout in milliseconds
	* Usually Screen Pop uses lastMessage...
	************************************************************* 
	*/
	public Message getMessage(String messageID, int timeout, boolean lastMessage)
		throws JMSException, Exception {
		QueueSession session = null;
		QueueReceiver queueReceiver = null;
		QueueConnection jmsCon = null;
		String body = null;
		Message inMessage = null;
		String pdID = propQ + ">>>";
		int i = 0;
		try {
			jmsCon = getQueueConn();
			session = getQueueSession(jmsCon);
			if (messageID != null) {
				String selector = "JMSCorrelationID = '" + messageID + "'";
				Log.setTrace(
					"JMSQCommand.getMessage:"
						+ pdID
						+ " Q Command selector for get = "
						+ selector);
				queueReceiver = session.createReceiver(getQueue(), selector);
			} else {
				Log.setTrace("JMSQCommand.getMessage:" + pdID + " Q Command");
				queueReceiver = session.createReceiver(getQueue());
			}
			
			Message lastMsg = null;
			while(true){
						
				Log.setTrace(
					"JMSQCommand.getMessage:" + pdID + " Q getting message");
	
				//*** After the first get, do a receive no wait
				if (timeout > 0 && inMessage == null) {
					inMessage = queueReceiver.receive(timeout);
				} else {
					inMessage = queueReceiver.receiveNoWait();
					//inMessage = queueReceiver.receive();
				}
				
				if( lastMessage == true && inMessage != null){
					lastMsg = inMessage;
				} else{
					break;
				}
				i++;	
			}
			
			if( i > 1){
				Log.setTrace(pdID + " Read Queue:" + i);
			}
			
			if (inMessage == null) {
				Log.setTrace(
					"JMSQCommand.getMessage:" + pdID + " Q no message found");
			} else {
				if (inMessage instanceof TextMessage) {
					body = ((TextMessage) inMessage).getText();
					Log.setTrace(inMessage.getJMSCorrelationID());
					Log.setTrace(
						"JMSQCommand.getMessage:"
							+ pdID
							+ " Q body is >"
							+ body
							+ "<");
				} else {
					Log.setTrace(
						"JMSQCommand.getMessage:"
							+ pdID
							+ " Q message not a TextMessage");
				}
			}
			errorct=0;

		} catch (JMSException je) {
			handleMQError(je);
			throw je;

		} catch (Exception e) {
			Log.setError(
				"JMSQCommand.getMessage:" + pdID + " Q failed with " + e);

			// pass exception back to client
			throw e;
		}finally {
			if(queueReceiver!= null){
				queueReceiver.close();
				queueReceiver = null;
			}
			close(session, jmsCon);
		}

		return inMessage;

	}  
public String getProperty(String aKey) 
  {
  	if( propF == null){
  		return null;
  	}
    if(properties == null)
    {	
 		properties = new JMSProperties();
 		
		properties.setPropertyFileName(propF);
		properties.processProperties(locale);
		
    }
    return properties.getProperty(aKey);
}
  
 public void setProperty(String aKey, String value) 
  {
  	if( propF == null){
  		return;
  	}
    
    if(properties == null)
    {
		properties = new JMSProperties();
		properties.setPropertyFileName(propF);
		properties.processProperties(locale);		
    }
    properties.setProperty(aKey, value);

  }
   
public QueueConnectionFactory getQCF() throws JMSException
  {
    if(qcf != null) {
    	return qcf;
    }
 
   try{
	    //com.ibm.mq.MQEnvironment.enableTracing(5, System.out);
	    
	}catch(Throwable t){
	    Log.setError(t.getMessage());
    }
    		
    if(propF == null || getProperty(MANAGED).equals("YES"))
    {
	    try {
		Log.setError("QCF is MANAGED..about to look it up");
		InitialContext ic = new InitialContext();
		Log.setError("GETTING JNDI QCF:" + jndiQCF);
		qcf = (QueueConnectionFactory) ic.lookup(jndiQCF);
		if(qcf != null)
			{Log.setError("Got QCF " +qcf.toString());
			}
		else
			{Log.setError("Got a null qcf");
			}
	    }
	    catch(Throwable e)
	    {e.printStackTrace();
		 qcf = null;
	    }
    }
    else
    {	
		Log.setError("QCF is NON-MANAGED.." + propF);
		propQ = getProperty(QUEUE_MANAGER)+"." + getProperty(QUEUE_NAME);
		com.ibm.mq.jms.MQQueueConnectionFactory myQCF = new com.ibm.mq.jms.MQQueueConnectionFactory();
		String binding = getProperty(TRANSPORT_TYPE);
		int intBind = new Integer(binding).intValue();
		myQCF.setTransportType(intBind);
		myQCF.setChannel(getProperty(CHANNEL));
		myQCF.setHostName(getProperty(HOST_NAME));
		String hostPort = getProperty(PORT);
		int intPort = new Integer(hostPort).intValue();
		myQCF.setPort(intPort);
		myQCF.setQueueManager(getProperty(QUEUE_MANAGER));
		myQCF.setTemporaryModel(getProperty(TEMPORARY_MODEL));
//		myQCF.setCCSID(37);
		qcf = (QueueConnectionFactory) myQCF;
    }
    return qcf;

  }
public Queue getQueue() throws JMSException {
    if( ioqueue != null){
//	    Log.setTrace("Queue already initialized!");
    	return ioqueue;
    }
    
 	if(propF == null || getProperty(MANAGED).equals("YES")) {
	    try {
			Log.setError("Queue is MANAGED..about to look it up");
			InitialContext ic = new InitialContext();
			Log.setError("GETTING JNDI Q:" + jndiQ);
			ioqueue = (Queue)ic.lookup(jndiQ);
			if(ioqueue != null) {
				Log.setError("Got a queue");

				((com.ibm.mq.jms.MQQueue)ioqueue).setTargetClient(com.ibm.mq.jms.JMSC.MQJMS_CLIENT_NONJMS_MQ);
	 		} else {
		 		Log.setError("Got a null queue");
			}
	    }
	    catch(Throwable e) {
		    e.printStackTrace();
		 	ioqueue = null;
	    }
    } else {
		propQ = getProperty(QUEUE_MANAGER)+"." + getProperty(QUEUE_NAME);
	    Log.setError("Initializing the NON-MANAGED Queue..." + propQ);
		com.ibm.mq.jms.MQQueue myIOQueue = new com.ibm.mq.jms.MQQueue();
		myIOQueue.setTargetClient(com.ibm.mq.jms.JMSC.MQJMS_CLIENT_NONJMS_MQ);
		myIOQueue.setBaseQueueName(getProperty(QUEUE_NAME));

		int tmpint = 0;
		String tmpprop = getProperty(Q_EXPIRY);
		if( tmpprop != null){
			tmpint = new Integer(tmpprop).intValue();
			myIOQueue.setExpiry(tmpint);
		}
			
		tmpprop = getProperty(Q_PERSISTENCE);
		if( tmpprop != null){
			tmpint = new Integer(tmpprop).intValue();
			myIOQueue.setPersistence(tmpint);
		}

		ioqueue = (Queue) myIOQueue;
    }
       Log.setTrace(propQ+"  Getting Queue Connection");

    return ioqueue;

  }
 
	public QueueConnection getQueueConn()throws JMSException{
		try{	
			if( (!managed) && ioqConn != null){
				ioqConn.start();
				return ioqConn;
			}
				
			QueueConnection thisIOqConn = getQCF().createQueueConnection();
			if( managed)
				Log.setTrace(propQ + ">>>Acquired Queue Connection!");
			else
				Log.setReport(propQ + ">>>Acquired Queue Connection!");
				
			thisIOqConn.start();
			
			if( !managed)
				ioqConn = thisIOqConn;
				
			return thisIOqConn;
		}catch(JMSException je){
			handleMQError(je);
			return ioqConn;
		}
	}
  
 	public QueueSession getQueueSession(QueueConnection queueConnection) throws JMSException {
	
		if( (!managed) && qSession != null)
			return qSession;
		
		QueueSession thisQSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		if( managed)
			Log.setTrace(propQ + ">>>Acquired Session!");
		else
			Log.setReport(propQ + ">>>Acquired Session!");
		
		if( !managed)
			qSession = thisQSession;
			
		return thisQSession;
	}
 
  public String putMessage(byte[] inputdata, String correlationID)
    throws JMSException, Exception
  {
    return putMessage(inputdata, correlationID, null);

  }
  public String putMessage(byte[] inputdata, String correlationID, Queue replyQ)
    throws JMSException, Exception
  {
    String result = null;      
    QueueSession session = null;
    QueueSender sender = null;
    QueueConnection jmsCon = null;
    String pdID = propQ + ">>>";
    Log.setTrace("JMSQCommand.putMessage:" + pdID+" Q request to put message '"+new String(inputdata,"037") +"'");
    try {
		jmsCon = getQueueConn();
		session = getQueueSession(jmsCon);
    	
		Log.setTrace("JMSQCommand.putMessage:" + pdID+" Q creating queue sender");
		sender = session.createSender(getQueue());
		BytesMessage message = session.createBytesMessage();
		message.setIntProperty("Length", inputdata.length);		
		message.writeBytes(inputdata);
		message.setStringProperty(com.ibm.mq.jms.JMSC.CHARSET_PROPERTY, "037");
		Log.setTrace("JMSQCommand.putMessage:" + pdID+" Q sending message");
		message.setJMSCorrelationID(correlationID);
		if( replyQ != null)
			message.setJMSReplyTo(replyQ);
		sender.send(message);
// store the message id for the return value
		result = message.getJMSMessageID();
		errorct = 0;
		Log.setTrace("JMSQCommand.putMessage:" + pdID+" Q done");
    }catch (JMSException je) {
		handleMQError(je);
		throw je;
	}catch (Exception e) {
		Log.setError("JMSQCommand.putMessage:" + pdID+" Q failed with "+e);
		throw e;
	}finally {
      Log.setTrace(pdID+" Q closing sender");		      
      if( sender != null)
	      sender.close();
	  sender = null;
 	  close(session, jmsCon);
 
    }

    return result;

  }
  
  
  
  public String putMessage(String text, String messageID)
    throws JMSException, Exception {
    String result = null;      
    QueueSession session = null;
    QueueSender sender = null;
    QueueConnection jmsCon = null;
    String pdID = propQ + ">>>";
    Log.setTrace("JMSQCommand.putMessage:" + pdID+" Q request to put message '"+text+"'");
    try {
		jmsCon = getQueueConn();
		session = getQueueSession(jmsCon);

		Log.setTrace("JMSQCommand.putMessage:" + pdID+" Q creating queue sender");
		sender = session.createSender(getQueue());
		TextMessage message = session.createTextMessage(text);
		message.setIntProperty("Length", text.length());
		Log.setTrace("JMSQCommand.putMessage: " + pdID+"Q sending message");
		message.setStringProperty(com.ibm.mq.jms.JMSC.CHARSET_PROPERTY, "037");	
		if( message != null)
			message.setJMSCorrelationID(messageID);
		sender.send(message);
		
		// store the message id for the return value
		result = message.getJMSMessageID();
		Log.setTrace("correlationID=" + message.getJMSCorrelationID());
		
		Log.setTrace("JMSQCommand.putMessage:" + pdID+" Q done");
		errorct = 0;
    }
	catch (JMSException je) {
		handleMQError(je);
		throw je;
	}
	catch (Exception e) {
		Log.setError("JMSQCommand.putMessage:" + pdID+" Q failed with "+e);
		// Pass the exception back to the caller
		throw e;
	}finally {
		// Ensure that the Connection always gets closed
		// Close the connection (close calls will cascade to other objects)
		
      Log.setTrace(pdID+" Q closing sender");
      if( sender != null)
	      sender.close();	      
	  sender = null;	
	  close(session, jmsCon);
	    
    }

    return result;

  }

public void close(){
	
	Log.setTrace(propQ+"  Closing Connection");	

	try {
		if( qSession != null){
			qSession.close();
			qSession = null;
		}
			
			
		if( ioqConn != null){
			//ioqConn.stop();
			ioqConn.close();
		}
		ioqConn = null;
		
	} catch (JMSException e1) {
		
	}	
}

protected void handleMQError(JMSException e)throws JMSException{
	
	errorct++;
	
	close();
	StringBuffer sb = new StringBuffer();
	sb.append(propQ+">>>Exception " + e + ":" 
			+ e.getMessage() + "|" 
			+ e.getLocalizedMessage());
	Exception le = e.getLinkedException();
	if (le != null)
		sb.append("\n" + propQ+"linked exception " + le + ":" 
			+ le.getMessage() + "|" 
			+ le.getLocalizedMessage());
	
	Log.setError(sb.toString());		
	throw new JMSCommException(sb.toString());
}	

	public boolean isEmail_notify() {
		return email_notify;
	}

	public void setEmail_notify(boolean email_notify) {
		this.email_notify = email_notify;
	}

  
	/**
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	public void close(Session thissession, QueueConnection thisQConn){

		if(!managed)
			return;
		
		Log.setTrace(propQ+"  Closing Connection");	

		try {
			if( thissession != null){
				thissession.close();
				thissession = null;
			}
				
			if( thisQConn != null){
				//thisQConn.stop();
				thisQConn.close();
			}
			thisQConn = null;
			
		} catch (JMSException e1) {
			Log.setTrace(e1.getMessage());
		}	
	}
}
