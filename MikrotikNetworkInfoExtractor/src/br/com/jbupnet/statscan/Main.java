package br.com.jbupnet.statscan;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main {

	//============== Funciona com o RouterOS v6.34.3 =======================

	public static final String DEFAULT_IP_ADDRESS = "192.168.25.202";
	public static final String DEFAULT_CSV_FILE = "resultado.csv";

	public static void main(String[] args) throws Exception {

		if(args.length != 2){
			System.err.println("Erro: esperados dois parâmetros.");
			System.err.println("Exemplo de uso:");
			System.err.println("MikrotikNetworkInfoExtractor.jar TARGET_ROUTEROS_ADDRESS CSV_FILE_PATH");
			
			System.err.println();
			System.err.println("Executando na configuração padrão:");
			System.err.println("MikrotikNetworkInfoExtractor.jar "+DEFAULT_IP_ADDRESS+" "+DEFAULT_CSV_FILE);
		}

		final String targetRouterIP = (args.length == 2) ? args[0] : DEFAULT_IP_ADDRESS;
		final String csvFile = (args.length == 2) ? args[1] : DEFAULT_CSV_FILE;

		BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));

		writer.write(ScanQueueStatisticsPage.getCSVHeader()); //Cabeçalho do CSV

		for(String url : ScanMainPage.getURLs(targetRouterIP)){ //Linhas do CSV
			System.out.println(url);

			writer.write(ScanQueueStatisticsPage.getCSVLine(url));
		}

		writer.flush();
		writer.close();
		
		if(args.length == 2)
			System.out.println("Gerado arquivo " + args[1]);
		else
			System.out.println("Gerado arquivo " + DEFAULT_CSV_FILE);

	}

}
