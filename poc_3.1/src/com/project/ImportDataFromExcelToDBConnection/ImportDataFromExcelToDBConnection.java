package com.project.ImportDataFromExcelToDBConnection;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;



public class ImportDataFromExcelToDBConnection 
{
	public static Properties loadPropertiesFile(String dbconnectionProperiesFileName,ArrayList<String> msgToPrint)
	{

		Properties properties=new Properties();
		try
		{
			FileReader fileReader=new FileReader(dbconnectionProperiesFileName);
			properties.load(fileReader);
			fileReader.close();

		}
		catch(FileNotFoundException e)
		{
			//e.printStackTrace();
			//System.out.println("Exception : DB connection properties file is not found. Please check the file name/path entered.");
			msgToPrint.add("Exception : DB connection properties file is not found. Please check the file name/path entered.");
		} catch (IOException e) {

			//e.printStackTrace();
			//System.out.println("Exception : DB connection properties loading issue.");
			msgToPrint.add("Exception : DB connection properties loading issue.");
		}

		return properties;
	}
	public static Connection getConnection(String dbconnectionProperiesFileName, ArrayList<String> msgToPrint)
	{
		Connection con = null;



		Properties prop = loadPropertiesFile(dbconnectionProperiesFileName,msgToPrint);

		String driverClass = prop.getProperty("driver");
		String url = prop.getProperty("url");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		try 
		{
			//if(driverClass.isEmpty() || driverClass.equals("") || url.isEmpty() || url.equals("") || username.isEmpty() || username.equals("") || password.isEmpty() || password.equals(""))
			if(driverClass != null && (! driverClass.equals("")) && url != null && (! url.equals("")) && username != null && (! username.equals("")) && password != null && (! password.equals("")))
			{

				Class.forName(driverClass);
				con = DriverManager.getConnection(url, username, password);
			}else
			{
				//System.out.println("All DB connection property parameter not mentioned properly in " + dbconnectionProperiesFileName);
				msgToPrint.add("All DB connection property parameter not mentioned properly in " + dbconnectionProperiesFileName);
			}

		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();s
			//System.out.println("SQL Exception : DB connection failed.");
			msgToPrint.add("SQL Exception : DB connection failed.");
		}
		catch (ClassNotFoundException e) 
		{
			//e.printStackTrace();
			//System.out.println("Exception : DB connection loading class is missing.");
			msgToPrint.add("Exception : DB connection loading class is missing.");
		}
		return con;
	}



	/*	public static void main(String[] args) 
	{
		System.out.println("start");
		dbOperation("ms_order_dtl");

	}*/
}