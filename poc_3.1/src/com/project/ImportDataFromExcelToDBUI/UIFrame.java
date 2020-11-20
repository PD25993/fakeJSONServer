package com.project.ImportDataFromExcelToDBUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
//import javax.swing.JTextField;


import com.project.ImportDataFromExcelToDBController.ImportDataFromExcelToDBController;

public class UIFrame extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFileChooser fileChooser;
	JFileChooser fileChooser1;
	public File file;
	public String path="";
	public String path1="";
	public String SheetNumber="";

	public UIFrame(String title){
		super(title);

		//Set Layout manager
		setLayout(new BorderLayout());

		//Create Swing component
		final JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setPreferredSize(new Dimension(300, 5));
		textArea.setBounds(0, 0, 300 , 5);
		textArea.setBackground(getBackground());

		final JButton  buttonForExcel = new JButton("Browse Excel File");
		final JButton  buttonForImport = new JButton("Import");
		JButton  buttonForExit = new JButton("Exit");		
		final JButton  buttonForDBProperties = new JButton("Browse DB Properties File");
		final JButton  buttonForReturnMenu = new JButton("Home");
		
		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser1 = new JFileChooser();
		//fileChooser1.setMultiSelectionEnabled(true);
		JPanel buttonPaneForExcel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel buttonPaneForExit = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel buttonPane4 = new JPanel();

		//use FlowLayout
		buttonPaneForExcel.add(buttonForExcel);
		buttonPaneForExit.add(buttonForImport);
		buttonPaneForExit.add(buttonForExit);
		buttonPaneForExcel.add(buttonForDBProperties);
		buttonPaneForExit.add(buttonForReturnMenu);

		final JPanel main = new JPanel();
		final JLabel notifyline = new JLabel("PLEASE CHOOSE REQUIRED FILE(S)");
		//notifyline1.setText("PLEASE CHOOSE EXCEL FILE");
		//notifyline1.setBounds(10, 10, 10, 10);
		main.add(notifyline);
		final JLabel notifyline1 = new JLabel("EXCEL FILE CHOOSEN :");
		final JLabel notifyline2 = new JLabel("DB CONFIGURATION FILE CHOOSEN :");

		//Add Swing component to content pane
		final Container container = getContentPane();
		container.add(buttonPaneForExcel, BorderLayout.PAGE_START);
		container.add(buttonPaneForExit, BorderLayout.PAGE_END);
		container.add(buttonPane4, BorderLayout.WEST);

		container.add(textArea,BorderLayout.LINE_END);
		container.add(main,BorderLayout.CENTER);

		buttonForImport.setEnabled(false);
		buttonForDBProperties.setEnabled(false);

		buttonForReturnMenu.setEnabled(true);

		textArea.setEditable(false);

		//Add behaviour
		buttonForExcel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				textArea.setText("");
				int returnVal = fileChooser.showOpenDialog(UIFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) 
				{
					file = fileChooser.getSelectedFile();
					File[] fileN = fileChooser.getSelectedFiles();
					
					for (File f : fileN)
					{
						System.out.println("Excel Files : " + f.getName());
					}
					//This is where our application will process the Excel Sheet
					String fileName = file.getName();
					String filePath = file.getAbsolutePath().replace("\\", "\\\\");
					
					filePath = filePath.substring(0, filePath.lastIndexOf("\\\\")+2); //+2 to incorporate the '\\' characters
					path=file.getAbsolutePath();
					path = path.replace("\\", "//");
					textArea.append("Excel File : " + fileName + "\nFile Path:" + path);

					
					buttonForDBProperties.setEnabled(true);
					buttonForExcel.setEnabled(false);

					main.remove(notifyline);
					//main.setEnabled(false);
					main.remove(notifyline2);
					//main.setEnabled(false);
					main.setVisible(false);

					//notifyline.setText("EXCEL FILE CHOOSEN : ");
					//notifyline1.setBounds(10, 10, 10, 10);

					main.add(notifyline1);

					container.add(main,BorderLayout.CENTER);
					main.setVisible(true);

				} else 
				{
					textArea.append("Open command cancelled by user.\n");
				}
				textArea.setCaretPosition(textArea.getDocument().getLength());
			}


		});

		buttonForExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}         

		});

		buttonForImport.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				
				ImportDataFromExcelToDBController bobj = new ImportDataFromExcelToDBController();
				String SheetNumber="0";

				ArrayList<String> msgFromDB=bobj.ProcessData(path,SheetNumber,path1);
				
				msgbox(msgFromDB);
				buttonForReturnMenu.setEnabled(true);
				buttonForImport.setEnabled(false);
			}          

		});


		buttonForDBProperties.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				textArea.setText("");
				main.remove(notifyline1);
				//main.setEnabled(false);
				main.remove(notifyline);
				//main.setEnabled(false);
				main.setVisible(false);		
				main.add(notifyline2);

				container.add(main,BorderLayout.CENTER);
				main.setVisible(true);
				
				int returnVal = fileChooser1.showOpenDialog(UIFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fileChooser1.getSelectedFile();
					//This is where our application will process the Excel Sheet
					String fileName = file.getName();
					String filePath = file.getAbsolutePath().replace("\\", "\\\\");
					filePath = filePath.substring(0, filePath.lastIndexOf("\\\\")+2); //+2 to incorporate the '\\' characters

					path1=file.getAbsolutePath();
					path1 = path1.replace("\\", "//");

					textArea.append("DB Configuration File: " + fileName + "\nFile Path:" + path1);
					
					buttonForDBProperties.setEnabled(false);
					buttonForImport.setEnabled(true);
					
				} else 
				{
					textArea.append("Open command cancelled by user.\n");
				}
				textArea.setCaretPosition(textArea.getDocument().getLength());
			}


		});


		buttonForReturnMenu.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {

				textArea.setText("");
				
				buttonForExcel.setEnabled(true);
				buttonForImport.setEnabled(false);
				buttonForDBProperties.setEnabled(false);

				main.remove(notifyline2);
				//main.setEnabled(false);
				main.remove(notifyline1);
				//main.setEnabled(false);
				main.setVisible(false);
				notifyline.setBounds(10, 10, 10, 10);

				main.add(notifyline);

				container.add(main,BorderLayout.CENTER);
				main.setVisible(true);
			}

		});
	}

	public void msgbox(ArrayList<String> msgToPrint)
	{

		JPanel panel = new JPanel();

		JTextArea msgToPrintTextArea = new JTextArea();
		msgToPrintTextArea.setBackground(getBackground());
		panel.add(msgToPrintTextArea);
		for (String msg : msgToPrint)
		{
			msgToPrintTextArea.append(msg);
		}
		msgToPrintTextArea.setEditable(false);
		JOptionPane.showMessageDialog(null, panel,"Final Details", JOptionPane.INFORMATION_MESSAGE);
	}
}
