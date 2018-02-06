package com.medibox.auto.mapping.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SimpleExcelWriter {
	
	
	
	FileOutputStream outputStream;
	XSSFWorkbook workbook;
    XSSFSheet sheet; 
    
    public SimpleExcelWriter() throws FileNotFoundException {
    	
    	workbook = new XSSFWorkbook();
    	sheet = workbook.createSheet(Constants.DIST_TO_MASTER_WORKSHEET_NAME);
        outputStream = new FileOutputStream(Constants.MAPPING_RESULTS_XLSX_FILE);
        
    }
    
	public void writeToExcel(String distributor, String master, int rowcount) {
		
         
        Object[][] bookData = {
                {distributor, master},
        };
 
        int rowCount = rowcount;
         
        for (Object[] aBook : bookData) {
            Row row = sheet.createRow(++rowCount);
             
            int columnCount = 0;
             
            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
             
        }
         
         
        try {
            outputStream = new FileOutputStream(Constants.MAPPING_RESULTS_XLSX_FILE);
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
