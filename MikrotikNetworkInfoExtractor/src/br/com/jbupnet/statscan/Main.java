package br.com.jbupnet.statscan;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main {

	//============== Funciona com o RouterOS v6.34.3 =======================

	public static void main(String[] args) throws Exception {
		
		if(args.length != 2){
			System.err.println("Execução incorreta! Esperados dois parâmetros.");
			System.err.println("Exemplo de uso:");
			System.err.println("MikrotikNetworkInfoExtractor.jar 192.168.88.1 relatorio.csv");
			return;
		}
		

//		final String targetRouterIP = "192.168.25.202";
//		final String csvFile = "resultado.csv";
		
		final String targetRouterIP = args[0];
		final String csvFile = args[1];

		BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));

		String[] urlsArray = ScanMainPage.getURLs(targetRouterIP);

		writer.write(ScanQueueStatisticsPage.getCSVHeader());

		for(String url : urlsArray){
			System.out.println(url);
			
			writer.write(ScanQueueStatisticsPage.getCSVLine(url));
		}
		
		System.out.println("Gerado arquivo " + args[1]);

		writer.flush();
		writer.close();

	}

}
