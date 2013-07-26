package labelPrinterPackage;

import java.io.*;
import java.awt.*;
import java.util.*;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;



import javax.swing.*;

import bsh.util.GUIConsoleInterface;
import bsh.util.JConsole;


public class labelPrinter {

	private static String url = "http://admin.touchofmodern.local:3000/shipments/scan_label";
	private static Queue<String> queue = new LinkedList<String>();

	public static void main(String[] args) throws Exception{
		
		JTextField unField = new JTextField();
		JTextField pwField = new JTextField();
		JTextField textField = new JTextField();
		
		Object[] message = {
				"Login Credentials for ToMo Admin",
			    "Username:", unField,
			    "Password:", pwField,
		};
		JTextField snField = new JTextField();
		
		Object[] labelMessage = {
			"Shipping number",
			snField
		};
		
		Object[] options = {"OK"};
		JOptionPane.showOptionDialog(null, message, "Login", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		
		String username = unField.getText();
		String password = pwField.getText();
		
		
		
//		Scanner scan = new Scanner(System.in);
//		System.out.print("Enter a username: ");
//		String username = scan.nextLine();
//		System.out.print("Enter a password: ");
//		String password = scan.nextLine();
		String shippingNumber;
		while(true){
//			System.out.println("Enter shipping numbers to print labels, or an empty line to close the application");
//			System.out.print("Enter a shipping number: ");
//			String shippingNumber = scan.nextLine();
			
			/*Get Shipping number*/
			JOptionPane.showOptionDialog(null, labelMessage, "Enter Shipping Number", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			shippingNumber = snField.getText();
			if(shippingNumber == ""){
//				scan.close();
				break;
			}
			httpPost post = new httpPost(username,password, url,shippingNumber);
			if(post.wasRedirected()){
				message[0] = "Request redirected, retry login";
				
				JOptionPane.showOptionDialog(null, message, "Login", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				username = unField.getText();
				password = pwField.getText();
				
//				System.out.println("Request was redirected, try reenetering username and password");
//				System.out.print("Enter a username: ");
//				username = JOptionPane.showInputDialog(null,"Page was redirected, try reentering username and password");
//				username = scan.nextLine();
//				System.out.print("Enter a password: ");
//				password = scan.nextLine();
				
			}else if(post.labelFailed()){
//				System.out.println("Failed to generate label, try reentering shipping number");
				JOptionPane.showMessageDialog(null, "Failed to generate label, try reentering shipping number");
				
			}else{
				String command = post.getDecoded();
				System.out.println(command);
				printLabel(command);
			}

		}

	}

	private static boolean printLabel(String command){
		try{
			PrintService psZebra = null;
			String sPrinterName = null;
			PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

			for (int i = 0; i < services.length; i++) {

				PrintServiceAttribute attr = services[i].getAttribute(PrinterName.class);
				sPrinterName = ((PrinterName) attr).getValue();

				if (sPrinterName.toLowerCase().indexOf("zebralabel") >= 0) {
					psZebra = services[i];
					break;
				}
			}

			if (psZebra == null) {
				System.out.println("Zebra printer is not found.");
				return false;
			}
			DocPrintJob job = psZebra.createPrintJob();

			byte[] by = command.getBytes();
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			Doc doc = new SimpleDoc(by, flavor, null);
			job.print(doc, null);


		} catch (PrintException e) {
			e.printStackTrace();
		} 
		return true;
	}
	
}


