package com.timeinc.tcs.jms;

/**
* Test Properties loading in the different environments
*
*/ 
// Imports

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.Enumeration;
import com.timeinc.tcs.Log;

public class JMSProperties implements java.io.Serializable  {

	static boolean debug = true;
	final static String prefix = "JMSProperties :";
	private String propName="propjms.properties";
 	private static final String propBundleName="propjms";
 	private static final String INITIAL_CONTEXT_FACTORY = "INITIAL_CONTEXT_FACTORY";
 	private static final String JNDI_SESSION_NAME = "JNDI_SESSION_NAME";
	private static final String PROVIDER_URL="PROVIDER_URL";
	private static final String QUEUE_CONNECTION_FACTORY="QUEUE_CONNECTION_FACTORY"; 
	private static final String TRANSPORT_TYPE="TRANSPORT_TYPE";
	private static final String CHANNEL="CHANNEL";
	private static final String HOST_NAME="HOST_NAME";
	private static final String PORT="PORT";
	private static final String QUEUE_MANAGER="QUEUE_MANAGER";
	private static final String TEMPORARY_MODEL="TEMPORARY_MODEL";
	private static final String QUEUE_NAME="QUEUE_NAME";
	private static final String MANAGED="MANAGED";
	private static final String JNDI_FACTORY_NAME="JNDI_FACTORY_NAME";
	private static final String JNDI_QUEUE_NAME="JNDI_QUEUE_NAME";
	private static final String Q_EXPIRY ="Q_EXPIRY";
	private static final String Q_PERSISTENCE="Q_PERSISTENCE";
	private static final String USERID="USERID";

//	private InputStream propIn = null;
	private Properties properties = null;
	
// report causes search of report_en_US.properties	
	private PropertyResourceBundle propertyBundle = null;
//---------------------------------------------
// Notice the size of the class file when the 
// final static is true, versus its size when
// it is false....
//--------------------------------------------- 
/*private static void debugTrace(String s) {
	
	if( debug == true)
		System.out.println(prefix + s);
} */

//--------------------------------------------
// dump properties to the stream
//--------------------------------------------
private void dumpProperties(Properties property, PrintWriter out) {

	try {
		Enumeration props = property.propertyNames();
		while(props.hasMoreElements())
		{
			String propKey = (String)props.nextElement();
			out.print("<h3>"+propKey+" = ");
			out.print(property.getProperty(propKey));
			out.println("</h3>");
		}

	}
	catch (Exception e) {
		Log.setError("Error.. dumping properties" + e.getMessage());
	}

}
//--------------------------------------------
// dump properties to the stream
//--------------------------------------------
private void dumpProperties(PropertyResourceBundle propBundle, PrintWriter out) {

	try {
		Enumeration props = propBundle.getKeys();
		while(props.hasMoreElements())
		{
			String propKey = (String)props.nextElement();
			out.print("<h3>"+propKey+" = ");
			out.print(propBundle.getString(propKey));
			out.println("</h3>");
		}		
	}
	catch (Exception e) {
		Log.setReport("Error.. dumping properties" + e.getMessage());
	}

}
public String getProperty(String key) {

		return properties.getProperty(key);
}

public void setProperty(String key, String value) {

	properties.setProperty(key, value);
}

//--------------------------------------------
// load properties from the properties file
//--------------------------------------------
private boolean loadProperties(String aPropName, Locale locale) {

	try {
		// report causes search of report_en_US.properties
		if( locale == null)
			propertyBundle = (PropertyResourceBundle)PropertyResourceBundle.getBundle(aPropName);
		else 
			propertyBundle = (PropertyResourceBundle)PropertyResourceBundle.getBundle(aPropName, locale);
			
		Log.setError("Successfully created PropertyResourceBundle:" + aPropName);
		Enumeration e = propertyBundle.getKeys();
		properties = new Properties();
		
		try{
			while( e.hasMoreElements()){
				String key =(String) e.nextElement();	
				String value = propertyBundle.getString(key);
				properties.put(key, value);
			}
		}catch(java.util.MissingResourceException ee){}

		
		propertyBundle = null;
			
		return true;
	}
	catch (Exception e) {
		Log.setError("getBundle...method is wrong" + e.getMessage());
		Log.setError("Not able to load the properties file: " + aPropName);
	}

	FileInputStream fis = null;
	
	try {
		if (aPropName != null)
		{
			properties = new Properties();
			fis = new FileInputStream(aPropName);
			properties.load(fis);
		}
		else {
			Log.setError("Can't find the properties file - " + aPropName);
			return false;		
		}
	}
	catch (Exception e) {
			Log.setError("Something is wrong in the input stream while loading properties:"+ aPropName+" " + e.getMessage());
			return false;
	}finally{
		if( fis != null){
		
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
				
	}
	return true;
}


//--------------------------------------------
// load properties first from property bundy, 
// then tries file system
//--------------------------------------------
public void processProperties(Locale locale) {

	
	Log.setError("Loading property file:"+ propName);

	if(!loadProperties(propName, locale)){
		Log.setError("Error Loaing property file:"+propName);
		return;
	}

		Log.setError("JMSProperties.processProperties:INITIAL_CONTEXT_FACTORY ="+ getProperty(INITIAL_CONTEXT_FACTORY));
		Log.setError("JMSProperties.processProperties:JNDI_SESSION_NAME ="+ getProperty(JNDI_SESSION_NAME));	
		Log.setError("JMSProperties.processProperties:PROVIDER_URL ="+getProperty(PROVIDER_URL));
		Log.setError("JMSProperties.processProperties:QUEUE_CONNECTION_FACTORY ="+getProperty(QUEUE_CONNECTION_FACTORY)); 
		Log.setError("JMSProperties.processProperties:TRANSPORT_TYPE ="+getProperty(TRANSPORT_TYPE));
		Log.setError("JMSProperties.processProperties:CHANNEL ="+getProperty(CHANNEL));
		Log.setError("JMSProperties.processProperties:HOST_NAME ="+getProperty(HOST_NAME));
		Log.setError("JMSProperties.processProperties:PORT ="+getProperty(PORT));
		Log.setError("JMSProperties.processProperties:QUEUE_MANAGER ="+getProperty(QUEUE_MANAGER));
		Log.setError("JMSProperties.processProperties:TEMPORARY_MODEL ="+getProperty(TEMPORARY_MODEL));
		Log.setError("JMSProperties.processProperties:QUEUE_NAME ="+getProperty(QUEUE_NAME));
		Log.setError("JMSProperties.processProperties:MANAGED ="+getProperty(MANAGED));
		Log.setError("JMSProperties.processProperties:JNDI_FACTORY_NAME ="+getProperty(JNDI_FACTORY_NAME));
		Log.setError("JMSProperties.processProperties:JNDI_QUEUE_NAME ="+getProperty(JNDI_QUEUE_NAME));
		Log.setError("JMSProperties.processProperties:Q_EXPIRY ="+getProperty(Q_EXPIRY));
		Log.setError("JMSProperties.processProperties:Q_PERSISTENCE ="+getProperty(Q_PERSISTENCE));
		Log.setError("JMSProperties.processProperties:USERID ="+getProperty(USERID));
	return;
}
//--------------------------------------------
// load properties from the properties file
//--------------------------------------------
public void setPropertyFileName(String aName) {

	propName=aName;

	return;
}

/**
 * Returns the properties.
 * @return Properties
 */
public Properties getProperties() {
	return properties;
}

/**
 * Sets the properties.
 * @param properties The properties to set
 */
public void setProperties(Properties properties) {
	this.properties = properties;
}

}
