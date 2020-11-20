package com.project.ImportDataFromExcelToDBUI;

import java.awt.EventQueue;

import javax.swing.JFrame;


public class ImportDataFromExcelToDBSUI {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame jFrame = new UIFrame("Import Data to DB Tool");
					jFrame.setSize(550, 350);
					jFrame.setResizable(false);
					jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					jFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}
		});
	}

}
