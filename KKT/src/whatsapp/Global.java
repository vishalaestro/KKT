package whatsapp;


import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * Acts as a common utility class and stores entries that has to be updated to the master excel
 * @author vishal sundararajan
 *
 */
public class Global {
	static Logger exception= Logger.getLogger("exception");
	static Logger log= Logger.getLogger("log");
	static Logger excelLog= Logger.getLogger("excelLog");
	static Logger emerygencyContacts= Logger.getLogger("emerygencyContacts");
	static String homePath = System.getProperty("homePath");
	static HashSet<String> rawContacts=new HashSet<String>();
	

	
	

}
