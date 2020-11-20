package com.project.ImportDataFromExcelToDBDAO;

import java.math.BigDecimal;
import java.sql.Connection;
//import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
/*import java.util.List;
import java.util.Map;*/
//import java.util.Properties;



import com.project.ImportDataFromExcelToDBConnection.ImportDataFromExcelToDBConnection;
import com.project.ImportDataFromExcelToDBUtility.ImportDataFromExcelToDBUtility;

public class ImportDataFromExcelToDBDAO 
{
	private static LinkedHashMap<String,Integer> getNotNullColumnDetail(String tableNameToInsert, Connection con , ArrayList<String> msgToPrint) 
	{
		tableNameToInsert = tableNameToInsert.toUpperCase();

		//System.out.println("Table name " + tableNameToInsert);

		LinkedHashMap<String,Integer> notNullColumnPositionDetails = new LinkedHashMap<String,Integer>();

		ResultSet res=null;

		if (null != con)
		{
			try {

				Statement stmt = con.createStatement();
				res = stmt.executeQuery("select * from " + tableNameToInsert + " where rownum<=2");
				ResultSetMetaData rsmd = res.getMetaData();

				for (int i=1;i<=rsmd.getColumnCount();i++)
				{

					//dataTypeDetails.add(rsmd.getColumnTypeName(i));

					int nullability = rsmd.isNullable(i);
					/*if(nullability == ResultSetMetaData.columnNullable)
					{
						//System.out.println(rsmd.getColumnName(i) + "Column have null value");
					}*/
					if(nullability == ResultSetMetaData.columnNoNulls)
					{
						//System.out.println(rsmd.getColumnName(i) + " Column have no null value");
						notNullColumnPositionDetails.put(rsmd.getColumnName(i),i);
					}
					/*else if(nullability == ResultSetMetaData.columnNullableUnknown)
					{
						//System.out.println(rsmd.getColumnName(i) + "Unknown");
					}*/

				}

				/*res = con.getMetaData().getColumns(null, null, tableNameToInsert, null);

				while(res.next())
				{

					String columnName = res.getString("COLUMN_NAME");
					//String uniqueColumn = res.getString("TYPE_NAME");
					String isNullable = res.getString("IS_NULLABLE");
					int position = res.getInt("ORDINAL_POSITION");
					String indivitualColumnType = res.getString("TYPE_NAME");

					//System.out.println(columnName + "===" +  "===" + isNullable);
					if (isNullable.equalsIgnoreCase("NO"))
					{
						if (( indivitualColumnType.equalsIgnoreCase("DATE")) || ( indivitualColumnType.equalsIgnoreCase("DATETIME")))
						{
							//System.out.println(columnName + " " + isNullable + " " + position + " " + indivitualColumnType);
							notNullColumnPositionDetails.put(columnName,position);
						}
						else
						{
							//System.out.println(columnName + " " + isNullable + " " + position + " " + indivitualColumnType);
							notNullColumnPositionDetails.put(columnName,position);
						}
					}
				}*/


			} catch (SQLException e) 
			{
				//System.out.println("SQL Exception : " + e.getErrorCode() + e.getMessage());
				//e.printStackTrace();
				msgToPrint.add("SQL Exception : " + e.getErrorCode() + e.getMessage());
			}
			catch(Exception e)
			{
				//System.out.println("Exception : " + e.getMessage());
				msgToPrint.add("Exception : " + e.getMessage());
				//e.printStackTrace();
			}
			finally
			{
				try 
				{
					res.close();
					//con.close();
				} catch (SQLException e) 
				{
					//System.out.println("SQL Exception : " + e.getErrorCode() + e.getMessage());
					msgToPrint.add("SQL Exception for closing connection : " + e.getErrorCode() + e.getMessage());
					//e.printStackTrace();
				}
			}
		}
		//System.out.println("notNullColumnPositionDetails : " + notNullColumnPositionDetails);
		return notNullColumnPositionDetails;

	}
	public static LinkedHashMap<String, String> fetchTableDetails(String tableNameFromExcel, String dbconnectionProperiesFileName, ArrayList<String> msgToPrint)
	{

		Connection con=null;

		LinkedHashMap<String,String> columnNameTypeDetails = new LinkedHashMap<String, String>();
		int tableFoundFlag=0;
		int tableFoundAsViewFlag = 0;
		//ResultSet res=null;
		try
		{
			con=ImportDataFromExcelToDBConnection.getConnection(dbconnectionProperiesFileName,msgToPrint);

			if (null != con)
			{

				//ResultSet res=con.getMetaData().getTables(null, null, null, new String[]{"TABLE"});

				ResultSet resView=con.getMetaData().getTables(null, null, null, new String[]{"VIEW"});
				String tableNameAsView="";
				while(resView.next())
				{
					tableNameAsView=resView.getString("TABLE_NAME");
					if(tableNameFromExcel.equalsIgnoreCase(tableNameAsView))
					{
						//System.out.println("Table (" + tableNameFromExcel +  ") is present as view in DB. No operation can be performed");
						msgToPrint.add("Table (" + tableNameFromExcel +  ") is present as view in DB. No operation can be performed. ");
						tableFoundAsViewFlag++;
					}

				}
				if(tableFoundAsViewFlag == 0 )
				{
					//ResultSet res=con.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
					ResultSet res=con.getMetaData().getTables(null, null, null, null);
					String tableName="";
					while(res.next())
					{

						tableName=res.getString("TABLE_NAME");

						if(tableNameFromExcel.equalsIgnoreCase(tableName))
						{

							//	System.out.println("tableName : " + tableName + " type " +res.getString(4));

							String DESC_TABLE= "select * from " + tableName +" where rownum<=2";

							Statement statement=con.createStatement();
							ResultSet rsDescTable = statement.executeQuery(DESC_TABLE);

							ResultSetMetaData rsmd = rsDescTable.getMetaData();
							for (int i=1;i<=rsmd.getColumnCount();i++)
							{

								//dataTypeDetails.add(rsmd.getColumnTypeName(i));

								String indivitualColumnType = rsmd.getColumnTypeName(i);
								String indivitualColumnName = rsmd.getColumnName(i);
								columnNameTypeDetails.put(indivitualColumnName,indivitualColumnType);

							}
							tableFoundFlag++;
						}

					}


					if (tableFoundFlag == 0)
					{
						//System.out.println("Table (" + tableNameFromExcel +  ") not found in DB");
						msgToPrint.add("Table (" + tableNameFromExcel +  ") not found in DB. ");
					}
					res.close();
				}

				resView.close();
				//System.out.println("Column name from DB: " + columnNameTypeDetails.keySet().toString());
				//System.out.println("Column data type from DB: " + columnNameTypeDetails.values().toString());
				//System.out.println("connection created successfully using properties file");

			}

			/*else 
			{
				System.out.println("Unable to create connection");
			}*/


		}catch (SQLException e) 
		{
			//System.out.println("SQL Exception : " + e.getErrorCode() + e.getMessage());
			msgToPrint.add("SQL Exception : " + e.getErrorCode() + e.getMessage());
			//e.printStackTrace();
		} 
		catch (Exception e) 
		{
			//System.out.println("Exception : " +  e.getMessage());
			msgToPrint.add("Exception : " +  e.getMessage());
			//e.printStackTrace();
		} 
		finally {

			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) 
			{
				//System.out.println("SQL Exception : problem in connection closing");
				msgToPrint.add("SQL Exception : problem in connection closing");
				//ex.printStackTrace();
			}
		}
		return columnNameTypeDetails;

	}

	private static LinkedHashMap<String,Object> checkDataExistInDB(String tableName,ArrayList<Object> insertQueryValuesDetails,LinkedHashMap<String, String> columnDetailFromDB, String dbconnectionProperiesFileName, ArrayList<String> msgToPrint) throws SQLException, ParseException, Exception
	{
		Connection con=null;
		PreparedStatement psStatement=null;
		Integer restultSetRowCount; 
		LinkedHashMap<String,Object> whereClauseList = new LinkedHashMap<String,Object>();
		ArrayList<String> prevColumnName = new ArrayList<String>();

		StringBuffer selectQuery = new StringBuffer("select * from ");
		tableName = tableName.toUpperCase();
		String andClause = "and " ;
		selectQuery.append(tableName);
		selectQuery.append(" where ( ");

		con=ImportDataFromExcelToDBConnection.getConnection(dbconnectionProperiesFileName,msgToPrint);
		LinkedHashMap<String,Integer> notNullColumnPositionDetailsForTable = getNotNullColumnDetail(tableName,con,msgToPrint);
		//LinkedHashMap<String, String> columnDetailFromDB=fetchTableDetails(tableName);				


		for (String notNullColumnValue : notNullColumnPositionDetailsForTable.keySet())
		{
			int counterForPSStatement = 1;

			if ( selectQuery.lastIndexOf(")") > 0)
			{
				selectQuery.deleteCharAt(selectQuery.lastIndexOf(")"));
				selectQuery.append(andClause);
			}
			selectQuery.append(notNullColumnValue);
			selectQuery.append(" = ? ");
			selectQuery.append(andClause);


			selectQuery.delete(selectQuery.lastIndexOf(andClause), selectQuery.length());
			selectQuery.append(")");
			//System.out.println("Final Select Query : " + selectQuery);

			if (con != null) 
			{

				psStatement = con.prepareStatement(selectQuery.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				for ( int counter =0 ; counter < prevColumnName.size() ; counter++)
				{

					String notNullColumnValuePrev = (String) notNullColumnPositionDetailsForTable.keySet().toArray()[counter];
					int columnNumber = notNullColumnPositionDetailsForTable.get(notNullColumnValuePrev);

					String coulmnDataType = columnDetailFromDB.get(notNullColumnValuePrev);
					if ( columnNumber > 0)
					{
						columnNumber = columnNumber - 1 ;
					}
					Object obj =  insertQueryValuesDetails.get(columnNumber);
					String setObjectInstance = ImportDataFromExcelToDBUtility.objectToDataInstanceConvert(obj);
					//System.out.println("notNullColumnValue : " + notNullColumnValuePrev + " columnNumber : " + columnNumber + " coulmnDataType : " + coulmnDataType + " counterForPSStatement : " + counterForPSStatement + " setObjectInstance : " + setObjectInstance);
					setValuesForPreparedStatement(coulmnDataType, setObjectInstance, psStatement, counterForPSStatement, obj);
					counterForPSStatement++;
				}
				int columnNumber = notNullColumnPositionDetailsForTable.get(notNullColumnValue);

				String coulmnDataType = columnDetailFromDB.get(notNullColumnValue);
				if ( columnNumber > 0)
				{
					columnNumber = columnNumber - 1 ;
				}
				Object obj =  insertQueryValuesDetails.get(columnNumber);
				String setObjectInstance = ImportDataFromExcelToDBUtility.objectToDataInstanceConvert(obj);
				//System.out.println("notNullColumnValue : " + notNullColumnValue + " columnNumber : " + columnNumber + " coulmnDataType : " + coulmnDataType + " counterForPSStatement : " + counterForPSStatement + " setObjectInstance : " + setObjectInstance);
				setValuesForPreparedStatement(coulmnDataType, setObjectInstance, psStatement, counterForPSStatement, obj);

				ResultSet rsForSQLQuery = psStatement.executeQuery();

				rsForSQLQuery.last();
				restultSetRowCount= rsForSQLQuery.getRow();
				rsForSQLQuery.beforeFirst();
				//System.out.println("Resultset row: " + restultSetRowCount);
				Object lastNotNullColumnName= notNullColumnPositionDetailsForTable.keySet().toArray()[notNullColumnPositionDetailsForTable.size() - 1];
				if ((restultSetRowCount > 1) && ( ! notNullColumnValue.equalsIgnoreCase(lastNotNullColumnName.toString())))
				{

					//System.out.println("Record found in DB and current column name is not match with last not null column (" + lastNotNullColumnName.toString() + ")");

					whereClauseList.put(notNullColumnValue,obj);
					prevColumnName.add(notNullColumnValue);

				}
				else if ((restultSetRowCount > 1) && ( notNullColumnValue.equalsIgnoreCase(lastNotNullColumnName.toString())))
				{
					//System.out.println("Record found in DB and current column name is match with last not null column (" + lastNotNullColumnName.toString() + ")");
					whereClauseList=new LinkedHashMap<String,Object>();
					break;
				}
				else if (restultSetRowCount == 1)
				{

					whereClauseList.put(notNullColumnValue,obj);

					break;

				}

				psStatement.close();


			}
		}


		return whereClauseList;
	}


	public static String insertRecord(String tableName, LinkedHashMap<String, String> columnDetailsFromDB, ArrayList<Object> insertQueryValuesDetails, ArrayList<String> missingColumnNameInExcel, String dbconnectionProperiesFileName, ArrayList<String> msgToPrint)
	{
		Connection con=null;
		String setObjectInstance = null;
		String returnExceptionFlag = "N";
		StringBuffer insertSqlQuery=new StringBuffer("Insert into ");
		insertSqlQuery.append(tableName);
		insertSqlQuery.append("( ");
		StringBuffer placeholder=new StringBuffer();
		PreparedStatement psStatement = null;
		int counterForInsertQueryValue=0;
		Object objTemp="";

		for(Iterator<String> iterateKeySetFromDB=columnDetailsFromDB.keySet().iterator();iterateKeySetFromDB.hasNext();)
		{
			insertSqlQuery.append(iterateKeySetFromDB.next());
			placeholder.append("?");
			if(iterateKeySetFromDB.hasNext())
			{
				insertSqlQuery.append(",");

				placeholder.append(",");
			}

		}
		insertSqlQuery.append(") VALUES (").append(placeholder).append(")");

		//System.out.println(columnDetailsFromDB.size());
		//System.out.println("Final Insert Query : " + insertSqlQuery);
		try
		{
			con=ImportDataFromExcelToDBConnection.getConnection(dbconnectionProperiesFileName,msgToPrint);

			if (null != con) 
			{
				psStatement = con.prepareStatement(insertSqlQuery.toString());

				int counterForPSStatement = 1;

				for(int counterForRow= 0; counterForRow < columnDetailsFromDB.size(); counterForRow++)
				{
					//System.out.println("counterForRow " + counterForRow + " columnDetailsFromDB.size() " + columnDetailsFromDB.size() + " insertQueryValuesDetails.size()" + insertQueryValuesDetails.size());

					String indColumnNameFromDB = (String) columnDetailsFromDB.keySet().toArray()[counterForRow];
					String currentDataTypeValue = (String) columnDetailsFromDB.values().toArray()[counterForRow];

					//System.out.println("indColumnNameFromDB : " + indColumnNameFromDB);

					//if(counterForRow <= (insertQueryValuesDetails.size() - 1))
					if( ! missingColumnNameInExcel.contains(indColumnNameFromDB))
					{

						objTemp = insertQueryValuesDetails.get(counterForInsertQueryValue);

						//String currentDataTypeValue = iterateValueSetFromDB.next();

						setObjectInstance = ImportDataFromExcelToDBUtility.objectToDataInstanceConvert(objTemp);
						setValuesForPreparedStatement(currentDataTypeValue, setObjectInstance, psStatement, counterForPSStatement, objTemp);

						//System.out.println("counterForRow : " + counterForRow + " counterForPSStatement : " + counterForPSStatement + " currentDataTypeValue : " + currentDataTypeValue);
						counterForPSStatement++;
						counterForInsertQueryValue++;

					}
					else 
					{
						//System.out.println("Record missing in Excel.");

						LinkedHashMap<String, Integer> notNullColumnDetail = getNotNullColumnDetail(tableName.toUpperCase(), con, msgToPrint);

						if(notNullColumnDetail.keySet().contains(indColumnNameFromDB))
						{
							//System.out.println(indColumnNameFromDB + " is not null column and objTemp will change");
							if((currentDataTypeValue.equalsIgnoreCase("VARCHAR2") || currentDataTypeValue.equalsIgnoreCase("LONGVARCHAR") || currentDataTypeValue.equalsIgnoreCase("CHAR")))objTemp=" ";							
							if(currentDataTypeValue.equals("DATE")|| currentDataTypeValue.equalsIgnoreCase("DATETIME") || currentDataTypeValue.equalsIgnoreCase("TIMESTAMP"))objTemp=ImportDataFromExcelToDBUtility.getCurrentDateTime();						
							if(currentDataTypeValue.equals("NUMBER") || currentDataTypeValue.equalsIgnoreCase("INTEGER"))objTemp=0;

							setObjectInstance="Y";
						}
						else
						{
							//System.out.println(indColumnNameFromDB + " is null column and objTemp will change");
							objTemp = null;
							setObjectInstance = ImportDataFromExcelToDBUtility.objectToDataInstanceConvert(objTemp);
						}

						setValuesForPreparedStatement(currentDataTypeValue, setObjectInstance, psStatement, counterForPSStatement, objTemp);

						//System.out.println("Else : counterForRow : " + counterForRow + " counterForPSStatement : " + counterForPSStatement + " currentDataTypeValue : " + currentDataTypeValue);
						counterForPSStatement++;
					}

				}
				int row = psStatement.executeUpdate();
				if (row > 0 )
				{
					returnExceptionFlag = "N";
					//System.out.println("Record " + curRowNum + " inserted Successfully");
				}
				else
				{
					returnExceptionFlag = "Y";
					//System.out.println("no record found. update");
				}

			}
		}
		catch (SQLIntegrityConstraintViolationException sqlInConViolationException)
		{
			if(sqlInConViolationException.getErrorCode() == 1) //&& errorMessage.contains("violated"))
			{
				returnExceptionFlag = "Y";

			}
			else
			{
				returnExceptionFlag = "E";

			}
			//System.out.println("Error SQLIntegrityConstraintViolationException: " + sqlInConViolationException.getMessage());
			//sqlInConViolationException.printStackTrace();

		}
		catch (SQLException e) 
		{
			//e.printStackTrace();

			String errorMessage = e.getMessage();

			if((errorMessage.contains("unique constraint")) && errorMessage.contains("violated"))
			{
				returnExceptionFlag = "Y";

			}
			else
			{
				returnExceptionFlag = "E";			
				//System.out.println("Error exception : " + e.getMessage());
			}

		} 
		catch (Exception e) 
		{
			returnExceptionFlag = "E";
			//e.printStackTrace();

		} 
		finally {


			try {
				con.close();
				psStatement.close();
			} catch (SQLException e) {
				//returnExceptionFlag = "E";
				//System.out.println("SQL Exception : DB connection closing failed.");
				msgToPrint.add("SQL Exception while performing Insert : DB connection closing failed.");
				//System.out.println("Excel line number  (" + curRowNum + ") can not be inserted.  \n");
			}


		}

		return returnExceptionFlag;
	}


	private static void setValuesForPreparedStatement(String currentDataTypeValue, String setObjectInstance, PreparedStatement psStatement, int counterForPSStatement, Object objTemp) throws SQLException, ParseException 
	{

		if(currentDataTypeValue.equalsIgnoreCase("VARCHAR2") || currentDataTypeValue.equalsIgnoreCase("LONGVARCHAR") || currentDataTypeValue.equalsIgnoreCase("CHAR"))
		{
			if (setObjectInstance.equals("Y"))
			{
				psStatement.setString(counterForPSStatement, objTemp.toString());
			}
			else if (setObjectInstance.equals("N"))
			{
				psStatement.setNull(counterForPSStatement, Types.VARCHAR);
			}
		}
		else if (currentDataTypeValue.equalsIgnoreCase("BIT"))
		{
			Boolean tempValue;
			if (setObjectInstance.equals("Y"))
			{

				tempValue = Boolean.valueOf(objTemp.toString());	
				//((Boolean) objTemp).booleanValue();
				psStatement.setBoolean(counterForPSStatement, tempValue);
			}
			else if (setObjectInstance.equals("N"))
			{
				psStatement.setNull(counterForPSStatement, Types.BOOLEAN);
			}
		}
		else if (currentDataTypeValue.equalsIgnoreCase("NUMBER"))
		{
			if (setObjectInstance.equals("Y"))
			{
				BigDecimal tempBigDecimalValue;
				if(objTemp.toString().matches("[0-9]+"))
				{
					tempBigDecimalValue = new BigDecimal(objTemp.toString());	
				}
				else
				{
					String formatedObjTempValue= objTemp.toString().replaceAll("[^\\d.-]", "");
					//String formatedObjTempValue= objTemp.toString().replace(",", "");
					//System.out.println("Number in String : " + formatedObjTempValue);
					tempBigDecimalValue = new BigDecimal(formatedObjTempValue);
				}
				//System.out.println("Number : " + tempBigDecimalValue);
				psStatement.setBigDecimal(counterForPSStatement, tempBigDecimalValue);
			}
			else if (setObjectInstance.equals("N"))
			{
				psStatement.setNull(counterForPSStatement, Types.LONGVARCHAR);
			}
		}
		else if (currentDataTypeValue.equalsIgnoreCase("INTEGER"))
		{
			Integer tempValue;
			if (setObjectInstance.equals("Y"))
			{
				if(objTemp.toString().matches("[0-9]+"))
				{
					tempValue = Integer.valueOf(objTemp.toString());	
				}
				else
				{
					String formatedObjTempValue= objTemp.toString().replaceAll("[^\\d.-]", "");
					//String formatedObjTempValue= objTemp.toString().replace(",", "");
					tempValue = Integer.valueOf(formatedObjTempValue);
				}
				psStatement.setInt(counterForPSStatement, tempValue);
			}
			else if (setObjectInstance.equals("N"))
			{
				psStatement.setNull(counterForPSStatement, Types.INTEGER);
			}
		}
		else if (currentDataTypeValue.equalsIgnoreCase("DATE") || currentDataTypeValue.equalsIgnoreCase("DATETIME") || currentDataTypeValue.equalsIgnoreCase("TIMESTAMP"))
		{
			if (setObjectInstance.equals("Y"))
			{
				//String formatedObjTempValue= objTemp.toString().replace(",", "");
				@SuppressWarnings("deprecation")
				Date tempDateConvertFromString = new Date(objTemp.toString());
				SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss" );
				String dateFormatString = sdf.format(tempDateConvertFromString);
				Date tempDate = sdf.parse(dateFormatString);
				java.sql.Timestamp tempDateTimeStampValue = new java.sql.Timestamp(tempDate.getTime());
				psStatement.setTimestamp(counterForPSStatement, tempDateTimeStampValue);
			}
			else if (setObjectInstance.equals("N"))
			{
				psStatement.setNull(counterForPSStatement, Types.TIMESTAMP);
			}
		}

	}

	public static Integer updateRecordInDB(String tableNameToInsert, LinkedHashMap<String, String> columnDetailFromDB, ArrayList<Object> updateRowValuesDetailsList, ArrayList<String> missingColumnNameInExcel, String dbconnectionProperiesFileName, ArrayList<String> msgToPrint) 
	{
		//System.out.println("In update method");
		Connection con=null;
		String separator = ", " ;
		String andClause = " and " ;
		String setObjectInstance = null;
		PreparedStatement psStatement = null;

		Integer updateRowCount = 0;
		int counterForUpdateQueryValue=0;
		Object objTemp="";

		StringBuffer updateSQLQuery=new StringBuffer("Update ");
		updateSQLQuery.append(tableNameToInsert);
		updateSQLQuery.append(" set ");

		LinkedHashMap<String, Object> whereClauseList = new LinkedHashMap<String, Object>();


		try {
			con=ImportDataFromExcelToDBConnection.getConnection(dbconnectionProperiesFileName,msgToPrint);
			whereClauseList = checkDataExistInDB(tableNameToInsert,updateRowValuesDetailsList,columnDetailFromDB,dbconnectionProperiesFileName,msgToPrint);

			if( ! whereClauseList.isEmpty())
			{
				for (String columnNameForUpdateSet : columnDetailFromDB.keySet())
				{
					updateSQLQuery.append(columnNameForUpdateSet);
					updateSQLQuery.append(" = ? ");
					updateSQLQuery.append(separator);

				}
				updateSQLQuery.delete(updateSQLQuery.lastIndexOf(separator), updateSQLQuery.length());
				updateSQLQuery.append(" where ");

				//int counter = 0;
				for (String whereClauseIndKey: whereClauseList.keySet()) 
				{
					updateSQLQuery.append(whereClauseIndKey);
					updateSQLQuery.append(" = ? ");
					//updateSQLQuery.append(" = '");
					//updateSQLQuery.append(whereClauseList.values().toArray()[counter]);
					//updateSQLQuery.append("'");
					updateSQLQuery.append(andClause);
					//counter++ ;
				}
				updateSQLQuery.delete(updateSQLQuery.lastIndexOf(andClause), updateSQLQuery.length());
				//System.out.println("Update Query : " + updateSQLQuery);

				//			try{

				if (null != con) 
				{

					psStatement = con.prepareStatement(updateSQLQuery.toString());

					int counterForPSStatementColumn = 1;


					for(int counterForRow= 0; counterForRow< columnDetailFromDB.size(); counterForRow++)
					{
						String indColumnNameFromDB = (String) columnDetailFromDB.keySet().toArray()[counterForRow];
						String currentDataTypeValue = (String) columnDetailFromDB.values().toArray()[counterForRow];

						//if(counterForRow <= (insertQueryValuesDetails.size() - 1))
						if( ! missingColumnNameInExcel.contains(indColumnNameFromDB))
						{

							objTemp = updateRowValuesDetailsList.get(counterForUpdateQueryValue);

							setObjectInstance = ImportDataFromExcelToDBUtility.objectToDataInstanceConvert(objTemp);
							setValuesForPreparedStatement(currentDataTypeValue, setObjectInstance, psStatement, counterForPSStatementColumn, objTemp);

							//System.out.println("counterForRow : " + counterForRow + " counterForPSStatement : " + counterForPSStatement + " currentDataTypeValue : " + currentDataTypeValue);
							counterForPSStatementColumn++;
							counterForUpdateQueryValue++;
						}
						else 
						{
							//System.out.println("Record missing in Excel.");

							LinkedHashMap<String, Integer> notNullColumnDetail = getNotNullColumnDetail(tableNameToInsert.toUpperCase(), con, msgToPrint);

							if(notNullColumnDetail.keySet().contains(indColumnNameFromDB))
							{
								//System.out.println(indColumnNameFromDB + " is not null column and objTemp will change");
								if((currentDataTypeValue.equalsIgnoreCase("VARCHAR2") || currentDataTypeValue.equalsIgnoreCase("LONGVARCHAR") || currentDataTypeValue.equalsIgnoreCase("CHAR")))objTemp=" ";							
								if(currentDataTypeValue.equals("DATE")|| currentDataTypeValue.equalsIgnoreCase("DATETIME") || currentDataTypeValue.equalsIgnoreCase("TIMESTAMP"))objTemp=ImportDataFromExcelToDBUtility.getCurrentDateTime();						
								if(currentDataTypeValue.equals("NUMBER") || currentDataTypeValue.equalsIgnoreCase("INTEGER"))objTemp=0;

								setObjectInstance="Y";
							}
							else
							{
								//System.out.println(indColumnNameFromDB + " is null column and objTemp will change");
								objTemp = null;
								setObjectInstance = ImportDataFromExcelToDBUtility.objectToDataInstanceConvert(objTemp);
							}

							setValuesForPreparedStatement(currentDataTypeValue, setObjectInstance, psStatement, counterForPSStatementColumn, objTemp);
							counterForPSStatementColumn++;
						}

						/*if(counterForRow == (updateRowValuesDetailsList.size() - 1))
						{
							System.out.println("counterForPSStatementColumn : "+counterForPSStatementColumn + "updateRowValuesDetailsList.size " + updateRowValuesDetailsList.size() + " counterForRow " + counterForRow );
						}*/
					}

					int counter = 0;
					for (String whereClauseIndKey: whereClauseList.keySet()) 
					{
						Object whereClauseIndValue=whereClauseList.values().toArray()[counter];
						String currentDataTypeValue=columnDetailFromDB.get(whereClauseIndKey);
						//String currentDataTypeValue = (String) columnDetailFromDB.values().toArray()[counterForRow];
						setObjectInstance = ImportDataFromExcelToDBUtility.objectToDataInstanceConvert(whereClauseIndValue);
						//System.out.println("currentDataTypeValue "+currentDataTypeValue+" whereClauseIndValue : " + whereClauseIndValue + " setObjectInstance : " + setObjectInstance+ " whereClauseIndKey : "+ whereClauseIndKey + " counterForPSStatementColumn : " + counterForPSStatementColumn);
						setValuesForPreparedStatement(currentDataTypeValue, setObjectInstance, psStatement, counterForPSStatementColumn, whereClauseIndValue);
						counter++;
						counterForPSStatementColumn++;
					}
					int row = psStatement.executeUpdate();
					if (row > 0 )
					{
						updateRowCount=1;
						//System.out.println("Record " + curRowNum + " updated Successfully");
					}
					else
					{
						updateRowCount=2;

					}
				}
			}
		}
		catch (SQLException e) 
		{
			updateRowCount=2;
			//e.printStackTrace();

		} 
		catch (ParseException e)
		{
			updateRowCount=2;
		}
		catch (Exception e) 
		{
			updateRowCount=2;

		} 
		finally {


			try {
				con.close();
				psStatement.close();
				//psStatementForUpdateWhereClause.close();
			} catch (SQLException e) 
			{
				//updateRowCount=2;
				//System.out.println("SQL Exception : DB connection closing failed.");
				msgToPrint.add("SQL Exception while performing Update : DB connection closing failed.");
			}
		}

		return updateRowCount;
	}


}
