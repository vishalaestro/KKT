package whatsapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Takes care of operations such as extracting existing contacts from masterExcel ,removing duplicates and updating them to the master excel
 * @author vishal sundararajan
 *
 */
public class MasterExcel {

	
	private HashSet<String> contacts=new HashSet<String>();  
	private DataFormatter formatter = new DataFormatter();
	/**
	 * Gets the entries from the master excel and remove duplicates in the contacts HashSet by comparing with the master excel
	 */
	void processMasterExcel(){
		try{
			processRawContacts();
			createExcel();
		/*	List<String> notNeededContacts=new ArrayList<String>();
			Global.log.info("Master Excel update process started on : "+new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(new Date()));
			System.out.println("please wait while the application compares with the master sheet");
			HashSet<String> masterContacts=new HashSet<String>();  
			File f = new File(Global.homePath+File.separator+"Contacts"+File.separator+"master.xlsx");
			if(f.exists() && !f.isDirectory()) { 
				Integer emptyRowlast=null;
				FileInputStream file = new FileInputStream(f);	
				XSSFWorkbook existingWorkBook = new XSSFWorkbook(file);
				 XSSFSheet firstSheet = existingWorkBook.getSheetAt(0);
				 for(int i=0;i<=firstSheet.getLastRowNum();i++){
					   Row row=firstSheet.getRow(i);
					   if(row!=null){
					   if(isRowEmpty(row)){
						   emptyRowlast=row.getRowNum();
						   break;
					   }
					   }
				   }
				 Global.excelLog.info("total number of rows in master sheet from 0th Row : "+emptyRowlast);
				 for(int i =1;i<=emptyRowlast;i++){
					 if(firstSheet.getRow(i)!=null){
						 Cell cell = firstSheet.getRow(i).getCell(1);//represents the cell where the phone number exists
						 if(cell!=null){
							 String cellValue=formatter.formatCellValue(cell);
							 if(cellValue!=null && !cellValue.isEmpty()){
								 masterContacts.add(cellValue.trim());
							 }
						 }
					 }
				 }
					for(String duplicate:contacts){
						if(masterContacts.contains(duplicate)){
							notNeededContacts.add(duplicate);
						}
					}
					contacts.removeAll(notNeededContacts);
					
					Global.excelLog.info("Contacts to be removed as duplicates :"+notNeededContacts);
					System.out.println("Completed removing duplicates from Master sheet");
					Global.excelLog.info("Total no of contacts to be updated :"+contacts.size());
					System.out.println("Total no of contacts to be updated :"+contacts.size());
					updateExcelFile((emptyRowlast),firstSheet,existingWorkBook,Global.homePath+File.separator+"Contacts"+File.separator+"master.xlsx");
			}
			else{
				Global.exception.error("Master file not found under contacts folder");
			}*/
		
		}
		catch(Exception e){
			Global.exception.error("Error occurred in processMasterExcel :", e);
		}
		
	}
	private void createExcel(){
		try{
		
			Date date = new Date() ;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss") ;
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Sample sheet");
			int i=0;
			for(String contact:contacts){
				Row row = sheet.createRow(i);
				Cell cell = row.createCell(0);
				cell.setCellValue(contact);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				i++;
			}
			FileOutputStream out = 
					new FileOutputStream(new File(Global.homePath+File.separator+"Contacts"+File.separator+dateFormat.format(date)+".xlsx"));
			workbook.write(out);
			workbook.close();
			out.close();
			System.out.println("Excel written successfully..");
			
			
		}
		catch(Exception e){
			Global.exception.error("Exception occurred while creating excel ", e);
		}
	}
	/**
	 * This method will update the existing excel file name with the specified sheet 
	 * @param rowNumber
	 * @param sheet
	 * @param workBook
	 * @param excelFileName
	 */
	@SuppressWarnings("unused")
	private void updateExcelFile(Integer rowNumber,XSSFSheet sheet,XSSFWorkbook workBook,String excelFileName){
		try{
			int i=0;
			Global.excelLog.info("Intiating master Excel update process for excelFileName : "+excelFileName +"from row number : "+rowNumber);
			System.out.println("updating master excel");
			for(String contact : contacts){
				if(!contact.isEmpty()){
				Row row = sheet.createRow(rowNumber+i);//specifies the row number , initially the row number will be the next row of the last row of the existing master excel
				Cell cell = row.createCell(1);//specifies the cell position on where the contact should be inserted for a particular row , cell 20 is the cell where contacts are entered .
				cell.setCellValue(contact);
				i++;
				}
			}
			FileOutputStream out = new FileOutputStream(new File(excelFileName));
			workBook.write(out);
			out.close();
			workBook.close();
			System.out.println("process complete...bye bye....");
			Global.excelLog.info("completed master Excel update process for excelFileName : "+excelFileName);
		}
		catch(Exception e){
			Global.exception.error("Error occurred in updateExcelFile :", e);
		}
	}
	@SuppressWarnings("unused")
	private static boolean isRowEmpty(Row row) {
	    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
	        Cell cell = row.getCell(c);
	        if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
	            return false;
	    }
	    return true;
	}
	private void processRawContacts(){
		try{
			System.out.println("Processing raw contacts");
			Pattern pattern = Pattern.compile(".*[a-zA-Z]+.*");
			
			for(String raw:Global.rawContacts){
				Matcher matcher = pattern.matcher(raw);
				if((!matcher.matches()) && (!raw.contains("_"))){
					if(checkRegionalLanguages(raw)){
						String rawParsed=raw.replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("-", "").replaceAll(" ", "").trim();
						contacts.add(rawParsed);
					}
				}
			}
			Global.excelLog.info("contents after parsing raw contacts : "+contacts);
		}
		catch(Exception e){
			Global.exception.error("Exception occurred while processing raw contacts ", e);
		}
	}
	/**
	 * This method will remove groups that contain regional language characters in it , will return true if the element does not contain any regional characters
	 * @param element
	 * @return
	 */
	private boolean checkRegionalLanguages(String element){
		try{
			for (char c: element.toCharArray()) {
			     if ((Character.UnicodeBlock.of(c) == Character.UnicodeBlock.DEVANAGARI) || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.TAMIL) 
			    		 || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.TELUGU)) {
			    	 return false;
			     }
			 }
		}
		catch(Exception e){
			Global.exception.error("Exception in checkRegionalLanguages", e);
		}
		return true;
	}

}
