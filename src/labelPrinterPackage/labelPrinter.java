package labelPrinterPackage;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.PrinterName;

public class labelPrinter {

	private static String url = "http://admin.touchofmodern.local:3000/shipments/scan_label";

	public static void main(String[] args) throws Exception{
		BlockingQueue<Post> queue = new ArrayBlockingQueue<>(20);
		PostGenerator generator = new PostGenerator(queue);
		Printer printer = new Printer(queue);
		
		//Start generator to produce jobs in the queue
		new Thread(generator).start();
		//Start printer to consume jobs
		new Thread(printer).start();
		System.out.println("Both threads started");
		

	}	
}

