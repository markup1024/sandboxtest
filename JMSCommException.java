package com.timeinc.tcs.jms;

import javax.jms.JMSException;

/**
 * @author yunm
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JMSCommException extends JMSException {
	 public JMSCommException(String msg){
	 	super(msg);
	 }
}
