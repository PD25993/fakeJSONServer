package com.project.ImportDataFromExcelToDBUtility;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;



public class ImportDataFromExcelToDBUtility 
{
	public static LinkedHashMap<String,String>  columnValueMatchOff(List<String> columNameFromExcel, LinkedHashMap<String, String> columnDetailsFromDB, ArrayList<String> msgToPrint)
	{
		LinkedHashMap<String,String> columnValueMatchOffStatusWithColumnName = new LinkedHashMap<String, String>();

		//Iterator<String> iterateKeySetFromDB=columnDetailsFromDB.keySet().iterator();

		if(columnDetailsFromDB.isEmpty())
		{
			//System.out.println("Column details not found from DB.");
			msgToPrint.add("Column details not found from DB.");

		}
		if(columNameFromExcel.isEmpty())
		{
			//System.out.println("Column details not found from Excel.");
			msgToPrint.add("Column details not found from Excel.");

		}
		else if (( ! columnDetailsFromDB.isEmpty()) && ( ! columNameFromExcel.isEmpty()))
		{
			for(int counter = 0 ; counter<columNameFromExcel.size();counter++)
			{
				
				String indivitualColumnNameFromExcel = columNameFromExcel.get(counter);
				String indivitualColumnNameFromDB = columnDetailsFromDB.get(indivitualColumnNameFromExcel);
				
				//System.out.println("indivitualColumnNameFromExcel : "+indivitualColumnNameFromExcel+" indivitualColumnNameFromDB : "+ indivitualColumnNameFromDB);
				if ( indivitualColumnNameFromDB == null)
				{
					columnValueMatchOffStatusWithColumnName.put(indivitualColumnNameFromExcel, "N");

				}
				else
				{
					columnValueMatchOffStatusWithColumnName.put(indivitualColumnNameFromExcel, "Y");
					//System.out.println("Column name present in excel and database is not same");
				}
			}
		}
		return columnValueMatchOffStatusWithColumnName ; 
	}


	public static String objectToDataInstanceConvert(Object objTypeForInstance)
	{
		String  setObjectInstance = null;

		if(objTypeForInstance instanceof String || objTypeForInstance instanceof Integer || objTypeForInstance instanceof Date || objTypeForInstance instanceof Number || objTypeForInstance instanceof Character || objTypeForInstance instanceof Boolean || objTypeForInstance instanceof BigDecimal)
		{
			//	System.out.println("Values Details from excel "+ insertQueryValuesDetails.get(counterForRow).get(counterForColumn).toString());

			String objTempInString = objTypeForInstance.toString();
			if (objTempInString.trim().length() > 0)
			{
				//System.out.println("Datatype for " + insertQueryValuesDetails.get(counterForRow).get(counterForColumn).toString() + " : " +currentDataTypeValue);
				//System.out.println("Data : " + objTempInString);
				setObjectInstance = "Y" ;
			}
			else
			{
				setObjectInstance = "N" ;
				//System.out.println("Values from excel set as Null");
			}
		}
		else
		{
			//System.out.println("Values from excel is Null and Datatype ");
			setObjectInstance = "N" ;
		}
		return setObjectInstance;
	}
	
	public static ArrayList<String> findMissingColumnNameInExcel(LinkedHashMap<String, String> columnDetailsFromDB, ArrayList<String> columnDetailsFromExcel, ArrayList<String> msgToPrint) 
	{
		ArrayList<String> missingColumnName = new ArrayList<String>(); 
		
		for (String indCloumnNameFromDB : columnDetailsFromDB.keySet())
		{
			if( ! columnDetailsFromExcel.contains(indCloumnNameFromDB))
			{
				//System.out.println("Column present in DB but not found in excel : " + indCloumnNameFromDB);
				missingColumnName.add(indCloumnNameFromDB);
			}
		}
		if( ! missingColumnName.isEmpty())msgToPrint.add("Missing column name in excel but present in DB : " + missingColumnName.toString());
		return missingColumnName;
		
	}
	
	public static String getCurrentDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		String sDate= sdf.format(date);
		return sDate;
	}
}


