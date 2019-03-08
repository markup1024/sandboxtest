package com.timeinc.tcs.jms;

/**
 * @author yunm
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import java.util.Enumeration;
import javax.jms.*;
import javax.naming.InitialContext;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.timeinc.tcs.Log;

public class BrowseQCommand extends JMSQCommand {

	public BrowseQCommand(String propertyFile) {
		super(propertyFile);
	}


	public BrowseQCommand(String jndiQCF, String jndiQ) {
		super(jndiQCF,jndiQ);
		
	
	}	


  public String browseMessage(String messageID, int timeout, boolean lastMessage) 
 	throws JMSException, Exception  {
		QueueSession session = null;
		QueueBrowser browser = null;
		QueueConnection jmsCon = null;
		String messagetext = "";
		String body = null;
		Message inMessage = null;
		String pdID = propQ + ">>>";
		int i = 0;

		String selector = "JMSCorrelationID = '" + messageID + "'";
		
			Message lastMsg = null;
						
			Log.setTrace(
				"JMSQCommand.browseMessage:" + pdID + " Q getting message");

			java.util.Vector list = new java.util.Vector();

			try {
			jmsCon = getQueueConn();
			session = getQueueSession(jmsCon);
		
				if(messageID == null) {
					browser = session.createBrowser(getQueue());
				} else {
					browser = session.createBrowser(getQueue(), selector);
				}				

				java.util.Enumeration enumb = browser.getEnumeration();
				com.ibm.mq.jms.MQQueueEnumeration enum1 = (com.ibm.mq.jms.MQQueueEnumeration)enumb;



				while(enum1.hasMoreElements()){
				Object obtest = enum1.nextElement();	
				String tpe = "";	
				if(obtest != null){
					tpe = obtest.getClass().getName();
					
				}
				
					if(tpe.equalsIgnoreCase("com.ibm.msg.client.jms.internal.JmsTextMessageImpl")){
					
						com.ibm.msg.client.jms.internal.JmsTextMessageImpl mes = (com.ibm.msg.client.jms.internal.JmsTextMessageImpl)obtest;					
						Log.setError("com.ibm.msg.client.jms.internal.JmsTextMessageImpl!!!" + pdID + " Q getting message");
						
						//	com.ibm.jms.JMSMessage mes = (com.ibm.jms.JMSMessage)enum1.nextElement();
						MessageDataBean msgBean = new MessageDataBean();
						msgBean.setCorrelationID(mes.getJMSCorrelationID());
						msgBean.setMessageID(mes.getJMSMessageID());
						Class mesClass = mes.getClass();
						msgBean.setMessage(mesClass.getName());
						if (mes instanceof TextMessage){
							msgBean.setMessage(((TextMessage)mes).getText());
							messagetext = msgBean.getMessage();
						}
					
					}else{
						
						com.ibm.jms.JMSTextMessage mes = (com.ibm.jms.JMSTextMessage)obtest;							
						Log.setError("com.ibm.jms.JMSTextMessage!!!" + pdID + " Q getting message");
						
						//	com.ibm.jms.JMSMessage mes = (com.ibm.jms.JMSMessage)enum1.nextElement();
						MessageDataBean msgBean = new MessageDataBean();
						msgBean.setCorrelationID(mes.getJMSCorrelationID());
						msgBean.setMessageID(mes.getJMSMessageID());
						Class mesClass = mes.getClass();
						msgBean.setMessage(mesClass.getName());
						if (mes instanceof TextMessage){
							msgBean.setMessage(((TextMessage)mes).getText());
							messagetext = msgBean.getMessage();
						}
					}
				
				}

	    errorct=0;

		} catch (JMSException je) {
			handleMQError(je);
			throw je;

		} catch (Exception e) {
			Log.setTrace(
				"JMSQCommand.browseMessage:" + pdID + " Q failed with " + e);

			// pass exception back to client
			throw e;
		}finally {
			
			if( browser != null){
				browser.close();
				browser = null;
			}
			
			close(session, jmsCon);
			
		}

		return messagetext;   
 }

 public int msgCount(String messageID, int timeout, boolean lastMessage) 
 	throws JMSException, Exception  {
		QueueSession session = null;
		QueueBrowser queueBrowser = null;
		QueueConnection jmsCon = null;
		String body = null;
		Message inMessage = null;
		String pdID = propQ + ">>>";
		int count = 0;

		String selector = null;
		if( messageID != null)
			selector = "JMSCorrelationID = '" + messageID + "'";
		
		try {
			jmsCon = getQueueConn();
			session = getQueueSession(jmsCon);
			
			if( selector != null)
				queueBrowser = session.createBrowser(getQueue());
			else
				queueBrowser = session.createBrowser(getQueue());
			
			Message lastMsg = null;
						
			Log.setTrace(
				"JMSQCommand.msgCount:" + pdID + " Q getting message");

			Enumeration e = queueBrowser.getEnumeration();
			if( e != null){
				while( e.hasMoreElements()){
					Object o = e.nextElement();
					if( o != null)
						count++;
				}
			}
			
			errorct=0; 

		} catch (JMSException je) {
			handleMQError(je);
			throw je;

		} catch (Exception e) {
			e.printStackTrace();
			Log.setTrace(
				"JMSQCommand.msgCount:" + pdID + " Q failed with " + e);

			// pass exception back to client
			throw e;
		}finally {
			
			if( queueBrowser != null){
				queueBrowser.close();
				queueBrowser = null;
			}
			close(session, jmsCon);
		}

		return count;   
 }

}
