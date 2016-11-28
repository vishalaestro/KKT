package whatsapp;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;


/**
 * This class will start the automation process and adds the extracted and parsed contacts into the contact HashSet
 * @author vishal sundararajan
 *
 */
public class Automate {
	
	
	/**
	 * open the specified URL
	 * @throws Exception
	 */
	public void initiateProcess() throws Exception{
		try{
			System.setProperty("webdriver.firefox.marionette",Global.homePath+File.separator+"Driver"+File.separator+"geckodriver.exe");
			WebDriver driver = new FirefoxDriver();
			driver.get("https://web.whatsapp.com/");
			driver.manage().window().maximize();
			getContacts(driver);
		}
		catch(Exception e){
			Global.exception.error("Exception occurred in method initiateProcess", e);
		}
	}
	
	/**
	 * This method waits for the user to enter the specified input to ensure whether the contacts have started to display on the computer screen , on success the automation process will be started
	 * @param driver
	 * @throws Exception
	 */
	private void getContacts(WebDriver driver) throws Exception{
		try{
			System.out.println("opening Browser,scan the QR code and wait for the messages to display then enter continue for further operation ");
			String key = Global.user_input.next( );
  			if(key.trim().equalsIgnoreCase("continue")){
  				Global.log.info("starting a new session at : "+new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(new Date()));
  				maniplateDOMElments(driver);
  			} 
  			else{
  				System.out.println("wrong input, Try again !");
  				getContacts(driver);
  			}
		}
		catch(Exception e){
			Global.exception.error("Exception occurred in method getContacts", e);
		}
	
	}

	/**
	 * Constantly look for contacts in WhatsApp for the specified time period and delete them from WhatsApp as they are stored in the contacts HashSet 
	 * After the set Interval Time is over ,the stored contacts are updated to the master Excel excluding duplicates.
	 * @param driver
	 * @throws Exception
	 */
	void maniplateDOMElments(WebDriver driver) throws Exception{
		try{
				while(true){
					Properties prop = new Properties();
					InputStream input = null;
					input = new FileInputStream(Global.homePath+File.separator+"config.properties");
					prop.load(input);
					List<WebElement> list = driver.findElements(By.xpath("//*[@id=\"pane-side\"]/div/div/div/div/div/div/div[2]/div[1]/div[1]/span"));
					Thread.sleep(2000);
					if(list!=null && !list.isEmpty()){ //check whether there is any contact to be added else stop the automation process and start updating the master excel with the available contacts in HashSet
						WebElement child=list.get(0);
						String name=child.getText();
						if((name!=null) && (!name.isEmpty())){
							if( (Global.patternMatcher(name)) && (Global.checkRegionalLanguages(name)) ){
								String parsedContact=Global.replaceString(name).trim();
								if(parsedContact!=null && !parsedContact.isEmpty()){
									Global.log.info("parsedContact :"+parsedContact);
									Global.contacts.add(parsedContact);
								}
							}
							Actions action= new Actions(driver);
							action.contextClick(child).build().perform();//right click the contact to be deleted
							Thread.sleep(2000);
							if(name.length()>100){
								driver.findElement(By.xpath("//*[@id=\"app\"]/div/span[4]/div/ul/li[2]/a")).click();//click the delete chat button for numbers
							}
							else{
								driver.findElement(By.xpath("//*[@id=\"app\"]/div/span[4]/div/ul/li[3]/a")).click();//click exit group button if its a group 
							}
					
							Thread.sleep(2000);
							driver.findElement(By.xpath("//*[@id=\"app\"]/div/span[3]/div/div/div/div[2]/button[2]")).click();// click the confirm button
							Thread.sleep(4000);
							
							Instant end = Instant.now();
							Duration dur = Duration.between(Global.start, end);// check whether the specified time interval is over by comparing with the startTime
							Integer timeInterval=Integer.parseInt(prop.getProperty("Interval"));
							Integer elapsedMinutes=(int) dur.toMinutes();
							Global.log.info("elapsedMinutes : "+elapsedMinutes);
								if(elapsedMinutes>timeInterval){ // The automation process is ended if the set time is reached .
									break;
								}
							}
							
						}
						else{
							break;
						}
					
					}
				
				new MasterExcel().processMasterExcel();
			}
			catch(Exception e){
				maniplateDOMElments(driver);
			}
			
		}

	}
