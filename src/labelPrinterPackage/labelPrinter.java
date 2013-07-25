package labelPrinterPackage;

import java.io.Console;
import java.util.Scanner;

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


public class labelPrinter {

	private static String url = "http://admin.touchofmodern.local:3000/shipments/scan_label";

	public static void main(String[] args) throws Exception{
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a username: ");
		String username = scan.nextLine();
		System.out.print("Enter a password: ");
		String password = scan.nextLine();
		while(true){
			System.out.println("Enter shipping numbers to print labels, or an empty line to close the application");
			System.out.print("Enter a shipping number: ");
			String shippingNumber = scan.nextLine();
			if(shippingNumber == ""){
				scan.close();
				break;
			}
			httpPost post = new httpPost(username,password, url,shippingNumber);
			if(post.wasRedirected()){
				System.out.println("Request was redirected, try reenetering username and password");
				System.out.print("Enter a username: ");
				username = scan.nextLine();
				System.out.print("Enter a password: ");
				password = scan.nextLine();
			}else if(post.labelFailed()){
				System.out.println("Failed to generate label, try reentering shipping number");
			}else{
				String command = post.getDecoded();
				System.out.println(command);
//				printLabel(command);
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


