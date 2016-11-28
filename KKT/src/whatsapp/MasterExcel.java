package whatsapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Takes care of operations such as extracting existing contacts from masterExcel ,removing duplicates and updating them to the master excel
 * @author vishal sundararajan
 *
 */
public class MasterExcel {

	/**
	 * Gets the entries from the master excel and remove duplicates in the contacts HashSet by comparing with the master excel
	 */
	void processMasterExcel(){
		try{
			List<String> notNeededContacts=new ArrayList<String>();
			Global.log.info("Master Excel update process started on : "+new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(new Date()));
			System.out.println("please wait while the application compares with the master sheet");
			HashSet<String> masterContacts=new HashSet<String>();  
			File f = new File(Global.homePath+File.separator+"Contacts"+File.separator+"master.xlsx");
			if(f.exists() && !f.isDirectory()) { 
				FileInputStream file = new FileInputStream(f);	
				XSSFWorkbook existingWorkBook = new XSSFWorkbook(file);
				 XSSFSheet firstSheet = existingWorkBook.getSheetAt(0);
				 Integer lastRowNum=firstSheet.getLastRowNum();
				 Global.excelLog.info("total number of rows in master sheet from 0th Row : "+lastRowNum);
				 for(int i =0;i<lastRowNum;i++){
					 if(firstSheet.getRow(i)!=null){
						 Cell cell = firstSheet.getRow(i).getCell(20);//represents the cell where the phone number exists
						 if(cell!=null){
							 String cellValue=Global.formatter.formatCellValue(cell).trim();
							 if(cellValue!=null && !cellValue.isEmpty()){
								 masterContacts.add(cellValue);
							 }
						 }
					 }
				 }
				 Global.excelLog.info("Contacts in master Sheet :"+notNeededContacts);
					for(String duplicate:Global.contacts){
						if(masterContacts.contains(duplicate.trim())){
							notNeededContacts.add(duplicate.trim());
						}
					}
					Global.contacts.removeAll(notNeededContacts);
					Global.contacts.remove(null);
					
					Global.excelLog.info("Contacts to be removed as duplicates :"+notNeededContacts);
					System.out.println("Completed removing duplicates from Master sheet");
					Global.excelLog.info("Total no of contacts to be updated :"+Global.contacts.size());
					System.out.println("Total no of contacts to be updated :"+Global.contacts.size());
					updateExcelFile(lastRowNum+1,firstSheet,existingWorkBook,Global.homePath+File.separator+"Contacts"+File.separator+"master.xlsx");
			}
			else{
				Global.exception.error("Master file not found under contacts folder");
			}
		
		}
		catch(Exception e){
			Global.exception.error("Error occurred in processMasterExcel :", e);
		}
		
	}
	/**
	 * This method will update the existing excel file name with the specified sheet 
	 * @param rowNumber
	 * @param sheet
	 * @param workBook
	 * @param excelFileName
	 */
	private void updateExcelFile(Integer rowNumber,XSSFSheet sheet,XSSFWorkbook workBook,String excelFileName){
		try{
			Global.excelLog.info("Intiating master Excel update process for excelFileName : "+excelFileName);
			System.out.println("updating master excel");
			int i=0;
			for(String contact : Global.contacts){
				Row row = sheet.createRow(rowNumber+i);//specifies the row number , initially the row number will be the next row of the last row of the existing master excel
				Cell cell = row.createCell(20);//specifies the cell position on where the contact should be inserted for a particular row , cell 20 is the cell where contacts are entered .
				cell.setCellValue(contact);
				Global.excelLog.info("contact to be inserted in Excel :"+contact);
				i++;
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

}
