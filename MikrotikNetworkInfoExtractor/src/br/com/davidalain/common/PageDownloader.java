package br.com.davidalain.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PageDownloader {

	/**
	 * Este método baixa a página de Queue Statistics de uma determinada Simple Queue e corrige os problemas de compatibilidade das tags.
	 * 
	 * Estes problemas ocorrem porque existem tags HTML que não seguem o padrão do XML sobre abertura e fechamento de tags.
	 * 
	 * @param urlQueueStatistics	Caminho da página da Queue Statistics.
	 * 
	 * 
	 * 		Exemplos de formato:
	 * 			"http://ROUTER_OS_ADDRESS/graphs/"
	 * 			"http://ROUTER_OS_ADDRESS/graphs/queue/QUEUE_NAME/"
	 * 	
	 * 			Em que:
	 * 				ROUTER_OS_ADDRESS é o endereço IP (com ou sem a porta), por exemplo "192.168.88.1" ou "192.168.88.1:80"
	 * 				QUEUE_NAME é o nome da Simple Queue  
	 * 
	 * @param targetFilePath
	 * 		Local
	 */
	public static void downloadPageAndFixBrokenTags(String urlQueueStatistics, String targetFilePath){

		try{

			URL url = new URL(urlQueueStatistics);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())); //Cria o leitor do conteúdo da página (conteúdo original)
			BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath)); //Cria o escritor do conteúdo da página (conteúdo corrigido)
			String readLine = null; //Linha lida

			while ((readLine = reader.readLine()) != null){

				//Procura pelas linhas que contém as tags <li> e adiciona a tag de fechamento </li>
				if((readLine.indexOf("<li>") >= 0) && (readLine.indexOf("</li>") < 0)){
					readLine = readLine.concat("</li>");
				}

				//Procura pelas tags <br> e as substitui por <br/>
				if((readLine.indexOf("<br>") >= 0)){
					readLine = readLine.replace("<br>", "<br/>");
				}

				//Procura pela tag que contém "&nbsp;" e o remove, substituindo-o por string vazia
				if((readLine.indexOf("&nbsp;") >= 0)){
					readLine = readLine.replace("&nbsp;", "");
				}

				//Procura pela linha que contém o conteúdo abaixo e substitui poelo texto que contém a tag de fechamento </p> no fim
				if((readLine.indexOf("<p><a href=\"/graphs/\">Main page</a>") >= 0)){
					readLine = readLine.replace("<p><a href=\"/graphs/\">Main page</a>", "<p><a href=\"/graphs/\">Main page</a></p>");
				}

				//Escreve no arquivo o conteúdo corrigido
				writer.write(readLine);
			}
			
			//Fecha o leitor e escritor
			reader.close();
			writer.close();

		}catch(IOException io){
			io.printStackTrace();
		}

	}
	
}
