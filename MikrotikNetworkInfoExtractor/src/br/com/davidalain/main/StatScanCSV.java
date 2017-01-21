package br.com.davidalain.main;

import java.io.BufferedWriter;
import java.io.FileWriter;

import br.com.davidalain.common.InputKeyboardUtil;
import br.com.davidalain.common.ScanMainPage;
import br.com.davidalain.common.ScanQueueStatisticsPage;
import br.com.davidalain.common.StatScanConfig;

public class StatScanCSV {

	//============== Funciona com o RouterOS v6.34.3 =======================

	public static void main(String[] args) throws Exception {

		//		if(args.length != 2){
		//			System.err.println("Erro! Esperados dois parâmetros.");
		//			System.err.println("Exemplo de uso:");
		//			System.err.println("MikrotikNetworkInfoExtractor.jar TARGET_ROUTEROS_ADDRESS CSV_FILE_PATH");
		//			
		//			System.err.println();
		//			System.err.println("Executando na configuração padrão:");
		//			System.err.println("MikrotikNetworkInfoExtractor.jar "+StatScanConfig.DEFAULT_ADDRESS+" "+StatScanConfig.DEFAULT_CSV_FILE);
		//		}

		//		final String targetRouterIP = (args.length == 2) ? args[0] : StatScanConfig.DEFAULT_ADDRESS;
		//		final String csvFile = (args.length == 2) ? args[1] : StatScanConfig.DEFAULT_CSV_FILE;

		System.out.println("====* Mikrotik Queues Info Extractor *====");
		System.out.println();
		System.out.println("Default target host with HTTP port is " + InputKeyboardUtil.DEFAULT_HOST_HTTP_PORT);
    	System.out.println();
		
		String targetRouterAddress = InputKeyboardUtil.getHostPort();
		String csvFilename = InputKeyboardUtil.getFilename();
		
		if(!csvFilename.endsWith(".csv")) csvFilename += ".csv";

		BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilename));

		writer.write(ScanQueueStatisticsPage.getCSVHeader()); //Cabeçalho do CSV

		for(String url : ScanMainPage.getURLs(targetRouterAddress)){ //Linhas do CSV
			System.out.println(url);

			writer.write(ScanQueueStatisticsPage.getCSVLine(url));
		}

		writer.flush();
		writer.close();

		System.out.println("Gerado arquivo " + csvFilename);

	}

}
