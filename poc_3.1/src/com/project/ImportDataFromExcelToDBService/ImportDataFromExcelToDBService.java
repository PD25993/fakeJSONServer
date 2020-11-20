package com.project.ImportDataFromExcelToDBService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;



import java.util.Map.Entry;



/*import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;*/
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
//import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.project.ImportDataFromExcelToDBDAO.ImportDataFromExcelToDBDAO;
import com.project.ImportDataFromExcelToDBUtility.ImportDataFromExcelToDBUtility;

public class ImportDataFromExcelToDBService 
{

	public void readsheet(String fileName, Integer sheetCount) {
		XSSFRow row;

		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet spreadsheet = workbook.getSheetAt(sheetCount);
			Iterator<Row> rowIterator = spreadsheet.iterator();

			while (rowIterator.hasNext()) {
				row = (XSSFRow) rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print(cell.getNumericCellValue() + " \t\t ");
						break;

					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + " \t\t ");
						break;
					}
				}
				System.out.println();
			}
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found. Please check the file name entered exists");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<String> getExcelData(String fileName, Integer sheetCount, String dbconnectionProperiesFileName) 
	{
		
		XSSFRow rowValue;
		//List<MSOrderHDRBean> excelDataBeanList = new ArrayList<MSOrderHDRBean>();
		ArrayList<String> columNameFromExcel=new ArrayList<String>();
		ArrayList<Object> valuesDetailsToInsertList = new ArrayList<Object>();
		LinkedHashMap<String, String> columnDetailFromDB= new LinkedHashMap<String, String>();
		ArrayList<Integer> insertDetailRowCount = new ArrayList<Integer>();
		ArrayList<Integer> updeteDetailRowCount = new ArrayList<Integer>();
		ArrayList<Integer> exceptionDetailRowCount = new ArrayList<Integer>();
		ArrayList<String> missingColumnNameInExcel= new ArrayList<String>();
		ArrayList<String> msgToPrint = new ArrayList<String>();

		String tableNameToInsert=new String();
		FileInputStream fis;
		int columnLengthForValues=0;
		int lastColumnNumber=0;
		//int currrentRowNum = 1;
		String errorFoundFlag = "N";

		try {
			fis = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet spreadsheet = workbook.getSheetAt(sheetCount);

			//System.out.println("Excel row count : " + spreadsheet.getLastRowNum());
			for(int rowNumber = 0; rowNumber <= spreadsheet.getLastRowNum(); rowNumber++) 
			{
				rowValue = spreadsheet.getRow(rowNumber);
				valuesDetailsToInsertList = new ArrayList<Object>();
				if ( rowValue != null ) 
				{
					if (rowNumber > 1)
					{
						lastColumnNumber  =  columnLengthForValues;
						//System.out.println("1 Column cell no : " + rowValue.getLastCellNum() + " rowNumber : " + rowNumber);
					}
					else
					{
						lastColumnNumber  = rowValue.getLastCellNum();
						//System.out.println("Column cell no : " + rowValue.getLastCellNum() + " rowNumber : " + rowNumber);
					}

					
					for(int columnNumber = 0; columnNumber < lastColumnNumber; columnNumber++) 
					{
						XSSFCell cellValue = rowValue.getCell(columnNumber);


						if(cellValue != null) 
						{
							// do something with the cell
							//System.out.println("rowNumber : " + rowNumber);
							switch (rowNumber){
							case 0 : 
								if (rowValue.getLastCellNum() == 1)
								{
									tableNameToInsert = cellValue.toString();
									//System.out.println("Table name from excel sheet : " + tableNameToInsert);
									msgToPrint.add("Table name from excel sheet : " + tableNameToInsert +" .\n");
									columnDetailFromDB=ImportDataFromExcelToDBDAO.fetchTableDetails(tableNameToInsert,dbconnectionProperiesFileName,msgToPrint);
								}
								break;
							case 1 :
								String columnNameToInsert = null;
								columnNameToInsert= cellValue.getStringCellValue();
								//System.out.println("Column value : " + columnNameToInsert);
								columNameFromExcel.add(columnNameToInsert);
								columnLengthForValues = rowValue.getLastCellNum();
								break;
							default:
								//System.out.println("Cell value : " + cellValue);
								switch (cellValue.getCellType()) 
								{
								case Cell.CELL_TYPE_NUMERIC:
									//System.out.print(cellValue.getNumericCellValue() + " \t\t ");
									DataFormatter dm = new DataFormatter(); 
									String numericValueFromCell = dm.formatCellValue(cellValue);
									valuesDetailsToInsertList.add(numericValueFromCell);
									//valuesDetailsToInsertList.add((int)Math.round(cellValue.getNumericCellValue()));

									break;

								case Cell.CELL_TYPE_STRING:
									//System.out.print(cellValue.getStringCellValue() + " \t\t ");
									String stringValueFromCell = cellValue.getStringCellValue();
									valuesDetailsToInsertList.add(stringValueFromCell);
									break;
								case Cell.CELL_TYPE_BOOLEAN:
									//System.out.print(cellValue.getBooleanCellValue() + " \t\t ");
									Boolean booleanValueFromCell = cellValue.getBooleanCellValue();
									String booleanValueDetailsToInsert = new String(booleanValueFromCell.toString());
									valuesDetailsToInsertList.add(booleanValueDetailsToInsert);
									break;

								case Cell.CELL_TYPE_BLANK:
									Cell customNullCell = rowValue.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
									valuesDetailsToInsertList.add(columnNumber, customNullCell);	
									//System.out.println("Cell type blank 1");
									break;
								default:
									Cell customNullCellForDefault = rowValue.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
									valuesDetailsToInsertList.add(columnNumber, customNullCellForDefault);	
									break;

								}
								break;
							}
						}
						else
						{

							if(rowNumber > 1)
							{
								Cell customNullCell = rowValue.getCell(columnNumber, Row.RETURN_BLANK_AS_NULL);
								valuesDetailsToInsertList.add(columnNumber, customNullCell);
								//System.out.println("Cell type blank");
							}
							/*else 
							{
								System.out.println("Cell value is null");
							}*/
						}
					}	
				}
				else if ((rowNumber == 1) && (rowValue == null))
				{
					int printRowNumber=1;
					printRowNumber=printRowNumber+rowNumber;
					//System.out.println("Column Name (row no "+ printRowNumber +") is blank. Please provide valid excel. ");
					msgToPrint.add("Column Name (row no "+ printRowNumber +") is blank. Please provide valid excel. \n");
					break;
				}
				else if ((rowNumber == 0) && (rowValue == null))
				{
					int printRowNumber=1;
					printRowNumber=printRowNumber+rowNumber;
					//System.out.println("Table name (row no "+ printRowNumber +") is blank. Please provide valid excel. ");
					msgToPrint.add("Table name (row no "+ printRowNumber +") is blank. Please provide valid excel. \n");
					break;
				}


				if ( ! tableNameToInsert.isEmpty())
				{
					if (( ! columnDetailFromDB.isEmpty()) && ( ! columNameFromExcel.isEmpty()) && (rowNumber > 1))
					{
						//System.out.println("columnDetailFromDB : " + columnDetailFromDB.toString() + " columNameFromExcel : " + columNameFromExcel.toString());

						LinkedHashMap<String,String> columnValueMatchOffStatus = ImportDataFromExcelToDBUtility.columnValueMatchOff(columNameFromExcel, columnDetailFromDB, msgToPrint);


						//System.out.println("columnValueMatchOffStatus : " + columnValueMatchOffStatus.values().toString());
						int currrentRowNum=1;
						if ( ! valuesDetailsToInsertList.isEmpty()) 
						{
							if ( ! columnValueMatchOffStatus.values().toString().contains("N") )
							{
								currrentRowNum = currrentRowNum + rowNumber ;

								if(currrentRowNum == 3)
								{
									//System.out.println("Column name(s) present in excel sheet match with DB column details.");
									msgToPrint.add("Column name(s) present in excel sheet match with DB column details. \n");

									missingColumnNameInExcel = ImportDataFromExcelToDBUtility.findMissingColumnNameInExcel(columnDetailFromDB,columNameFromExcel,msgToPrint);
								}

								String flag = ImportDataFromExcelToDBDAO.insertRecord(tableNameToInsert, columnDetailFromDB,valuesDetailsToInsertList, missingColumnNameInExcel,dbconnectionProperiesFileName,msgToPrint);
								if (flag.equals("Y"))
								{
									//System.out.println("Line number " + currrentRowNum + " found in DB. Performing Update for the same.");
									Integer updatedRowFlag = ImportDataFromExcelToDBDAO.updateRecordInDB(tableNameToInsert, columnDetailFromDB, valuesDetailsToInsertList, missingColumnNameInExcel,dbconnectionProperiesFileName,msgToPrint);
									if( updatedRowFlag == 1 )
									{
										updeteDetailRowCount.add(currrentRowNum);
									}
									else if( updatedRowFlag == 2 )
									{
										exceptionDetailRowCount.add(currrentRowNum);
										//System.out.println("Exception record : " + valuesDetailsToInsertList.toString());
									}
								}
								else if (flag.equals("N"))
								{
									insertDetailRowCount.add(currrentRowNum);
								}
								else if (flag.equals("E"))
								{
									//System.out.println("Exception record : " + valuesDetailsToInsertList.toString());
									exceptionDetailRowCount.add(currrentRowNum);
								}


							}
							else if ( ! errorFoundFlag.equalsIgnoreCase("P"))
							{
								ArrayList<String> incorrectColumnName = new ArrayList<String>();
								for (Entry<String,String> entry : columnValueMatchOffStatus.entrySet())
								{
									if(entry.getValue().equalsIgnoreCase("N"))
									{
										incorrectColumnName.add(entry.getKey());
									}
								}
								//System.out.println("Column name(s) " + incorrectColumnName.toString() + " mentioned in excel is/are not matched with DB column name. ");
								msgToPrint.add("Column name(s) " + incorrectColumnName.toString() + " mentioned in excel is/are not matched with DB column name. \n");
								errorFoundFlag="P";
							}						
						}
					}
					else if (( columnDetailFromDB.isEmpty()) || ( columNameFromExcel.isEmpty()) && (rowNumber > 1))
					{
						break;
						//System.out.println("All necessary details not found from Excel/DB");
					}
				}
			}

			//System.out.println((! insertDetailRowCount.isEmpty()) ? msgToPrint.add("Excel line number(s) " + insertDetailRowCount.toString() + " inserted successfully in DB \n") : msgToPrint.add("No record(s) inserted \n"));
			@SuppressWarnings("unused")
			Object a =(! insertDetailRowCount.isEmpty()) ? msgToPrint.add("Excel line number(s) " + insertDetailRowCount.toString() + " inserted successfully in DB. \n") : msgToPrint.add("No record(s) inserted. \n");
			//System.out.println((! updeteDetailRowCount.isEmpty()) ? msgToPrint.add("Excel line number(s) " + updeteDetailRowCount.toString() + " updated successfully in DB \n"): msgToPrint.add("No record(s) updated \n"));
			a =(! updeteDetailRowCount.isEmpty()) ? msgToPrint.add("Excel line number(s) " + updeteDetailRowCount.toString() + " updated successfully in DB. \n"): msgToPrint.add("No record(s) updated. \n");
			//System.out.println(( ! exceptionDetailRowCount.isEmpty()) ? "Excel line number(s) " + exceptionDetailRowCount.toString() + msgToPrint.add(" can not be inserted/updated in DB \n") : "");
			a = ( ! exceptionDetailRowCount.isEmpty()) ? msgToPrint.add("Excel line number(s) " + exceptionDetailRowCount.toString() + " can not be inserted/updated in DB. \n") : "";


			fis.close();

		} catch (FileNotFoundException e) {
			//System.out.println("Exception : Excel File not found. Please check the file name/path entered.");
			msgToPrint.add("Exception : Excel File not found. Please check the file name/path entered. \n");
		} catch (IOException e) {
			//System.out.println("Exception : Please provide proper input");
			msgToPrint.add("Exception : Please provide proper input. \n");
		} catch (NullPointerException e){
			//e.printStackTrace();
			//System.out.println("Exception : Null Pointer Exception");
			msgToPrint.add("Exception : Null Pointer Exception. \n");

		}
		return msgToPrint;

	}
}