package whatsapp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;

/**
 * Acts as a common utility class and stores entries that has to be updated to the master excel
 * @author vishal sundararajan
 *
 */
public class Global {
	static Logger exception= Logger.getLogger("exception");
	static Logger log= Logger.getLogger("log");
	static Logger excelLog= Logger.getLogger("excelLog");
	static String homePath = System.getProperty("homePath");
	static HashSet<String> contacts=new HashSet<String>();  
	static DataFormatter formatter = new DataFormatter();
	static Instant start = Instant.now();
	static HashSet<String> masterExcelContents=new HashSet<String>();  
	static Scanner user_input = new Scanner( System.in );
	
	/**
	 * This method will remove groups that contain regional language characters in it , will return true if the element does not contain any regional characters
	 * @param element
	 * @return
	 */
	static boolean checkRegionalLanguages(String element){
		try{
			for (char c: element.toCharArray()) {
			     if ((Character.UnicodeBlock.of(c) == Character.UnicodeBlock.DEVANAGARI) || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.TAMIL) 
			    		 || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.TELUGU)) {
			    	 return true;
			     }
			 }
		}
		catch(Exception e){
			Global.exception.error("Exception in checkRegionalLanguages", e);
		}
		return false;
	}
	
	/**
	 * This method will return true for contacts that does not contain Alphabets and _ character, 
	 * this is used to remove groups created with alphanumeric and already created groups that are in numbers but contains _ character in it
	 * @param contact
	 * @return
	 */
	static boolean patternMatcher(String contact){
		try{
			Pattern pattern = Pattern.compile(".*[a-zA-Z]+.*");
			Matcher matcher = pattern.matcher(contact);
			if((!matcher.matches()) && (!contact.contains("_"))){
				return true;
			}
		}
		catch(Exception e){
			Global.exception.error("Exception in patternMatcher", e);
		}
		return false;
	}
	
	/**
	 * This method will remove the brackets from the extracted phone number and trailing spaces and return the parsedString
	 * @param contact
	 * @return
	 */
	static String replaceString(String contact){
		try{
			String parsedString=contact.replace("(", "").replace(")", "").trim();
			return parsedString;
		}
		catch(Exception e){
			Global.exception.error("Exception in replaceString", e);
		}
		return contact;
	}
}
