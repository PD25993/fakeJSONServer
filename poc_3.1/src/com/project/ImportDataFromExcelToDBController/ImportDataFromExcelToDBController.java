package com.project.ImportDataFromExcelToDBController;


/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;*/
import java.util.ArrayList;
import java.util.InputMismatchException;
//import java.util.Scanner;

import com.project.ImportDataFromExcelToDBService.ImportDataFromExcelToDBService;

public class ImportDataFromExcelToDBController {

	public ArrayList<String> ProcessData(String excelFile, String sheetNumberinString , String dbPropertiesFile) {

		//Scanner sc = new Scanner(System.in);
		//BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
		ImportDataFromExcelToDBService importDataFromExcelToDBService = new ImportDataFromExcelToDBService();
		ArrayList<String> msgToPrintInUI = new ArrayList<String>();
		try {


/*			System.out.print("Enter the file name \n");
			String fileName = bufferReader.readLine();

			System.out.print("Enter the sheet number \n");
			String sheetNumberinString = bufferReader.readLine();


			System.out.print("Enter database properties file name \n");
			String dbconnectionProperiesFileName = bufferReader.readLine();*/

			String fileName = excelFile;
			//String sheetNumberinString="0";
			String dbconnectionProperiesFileName = dbPropertiesFile;
			//String dbconnectionProperiesFileName = "I:/Poulami/dbconnection.properties";
			/*System.out.println("fileName in controller : " + fileName);
			System.out.println("dbconnectionProperiesFileName : " + dbconnectionProperiesFileName);
			System.out.println("sheetNumberinString : " + sheetNumberinString);*/
			/*String fileName = args[0];
			
			String sheetNumberinString = args[1];
			String dbconnectionProperiesFileName = args[2];*/
			
			Integer sheetNumber = Integer.parseInt(sheetNumberinString);
			

			/*System.out.println("sheetNumberinString : " + sheetNumberinString);
			
			System.out.println("sheetNumber : " + sheetNumber);*/
			
			if(fileName.isEmpty() || fileName.equals("") || sheetNumberinString.isEmpty() || sheetNumberinString.equals("") || dbconnectionProperiesFileName.isEmpty() || dbconnectionProperiesFileName.equals(""))
			{
				//System.out.println("Input field(s) is/are null or empty");
				msgToPrintInUI.add("Input field(s) is/are null or empty");
			}
			else
			{
				msgToPrintInUI=importDataFromExcelToDBService.getExcelData(fileName,sheetNumber,dbconnectionProperiesFileName);
			}


		}
		catch (InputMismatchException ie)
		{
			//System.out.println("Exception : Please provide valid input");
			msgToPrintInUI.add("Exception : Please provide valid input");
		}
		catch(NumberFormatException ne)
		{
			//System.out.println("Exception : Please provide proper sheet number");
			msgToPrintInUI.add("Exception : Please provide proper sheet number");
		}
		/*catch (IOException e) {
			
			System.out.println("Exception IOException: \n" +  e.getMessage());
		}
*/
		return msgToPrintInUI;

	}
}
