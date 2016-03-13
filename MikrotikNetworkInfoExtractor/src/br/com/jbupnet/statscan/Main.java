package br.com.jbupnet.statscan;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main {

	public static void main(String[] args) throws Exception {
		
		//Funciona com o RouterOS v6.34.3

		final String targetRouterIP = "192.168.25.202";
		final String csvFile = "resultado.csv";

		BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));

		String[] urlsArray = ScanMainPage.getURLs(targetRouterIP);

		writer.write(ScanQueueStatisticsPage.getCSVHeader());

		for(String url : urlsArray){
			System.out.println(url);
			
			writer.write(ScanQueueStatisticsPage.getCSVLine(url));
		}

		writer.flush();
		writer.close();

	}

}
