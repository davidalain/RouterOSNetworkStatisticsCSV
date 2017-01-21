package br.com.davidalain.common;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ScanMainPage {

	public static String[] getURLs(String targetRouterIP){

		String[] result = null;

		try{

			final String targetURL = makeMainPageURL(targetRouterIP);

			//Baixa a página principal (Main Page) e corrige as TAGs HTML que não estão no padrão XML
			PageDownloader.downloadPageAndFixBrokenTags(targetURL, StatScanConfig.HTML_TEMP_FILE);

			//Lê o arquivo temporário baixado e gera um documento com todas as tags para serem percorridas 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(StatScanConfig.HTML_TEMP_FILE));

			//Pega as tags contidas na tag body
			Element htmlTag = doc.getDocumentElement();
			Element bodyTag = (Element) htmlTag.getElementsByTagName("body").item(0);
			NodeList nodes =  bodyTag.getChildNodes();


			String textQueueHeader = nodes.item(8).getTextContent();
			int simpleQueuesQuantity = Integer.parseInt(textQueueHeader.substring(textQueueHeader.indexOf("You have access to ") + "You have access to ".length(), textQueueHeader.indexOf(" queues:")));
			int indexNodeStart = 10; //Primeiro índice que contém os valores com os nomes das simples queues. Para revalidar descomente o for abaixo.

			if(nodes.item(indexNodeStart).getTextContent().equals("hs-<hotspot_server>")){
				simpleQueuesQuantity -= 1; //Esta queue não deve ser contabilizada
				indexNodeStart += 2; //Pula a queue do hotspot. Pula dois nodes pq cada queue está em um node e o próximo é vazio. Para revalidar descomente o for abaixo.
			}

			//// Usado para mostrar os valores contidos nos Nodes para validar os índices utilizados
			//			for(int i = 0 ; i < nodes.getLength() ; i++){
			//				System.out.println("-----------------------------------------------------------------------");
			//				System.out.println("i=" + i);
			//				System.out.println("_"+simpleQueueName+"_");
			//				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			//			}

			result = new String[simpleQueuesQuantity]; //Cria o resultado com array de URLs das simple queues

			System.out.println("simpleQueuesQuantity = " + simpleQueuesQuantity);

			//Itera todas as simple queues pegando os nomes e gerando as URLs
			for(int i = indexNodeStart , index = 0 ; (i < nodes.getLength()) && (index < simpleQueuesQuantity) ; i+= 2 , index++){

				String simpleQueueName = nodes.item(i).getTextContent();

				result[index] = makeQueueStatisticsURL(targetRouterIP, simpleQueueName);

				//				System.out.println("-----------------------------------------------------------------------");
				//				System.out.println("i=" + i);
				//				System.out.println("_"+simpleQueueName+"_");
				//				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}
	
	public static String makeMainPageURL(String routerOSAddress){
		return "http://" + routerOSAddress + "/graphs/";
	}
	
	public static String makeQueueStatisticsURL(String routerOSAddress, String queueName){
		return "http://" + routerOSAddress + "/graphs/queue/" + queueName;
	}


//	private static void downloadMainPageAndFixBrokenTags(String urlQueueStatistics, String targetFilePath){
//
//		try{
//
//			URL url = new URL(urlQueueStatistics);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
//			BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath));
//			String inputLine;
//
//			while ((inputLine = reader.readLine()) != null){
//
//				if((inputLine.indexOf("<li>") >= 0) && (inputLine.indexOf("</li>") < 0)){
//					inputLine = inputLine.concat("</li>");
//				}
//
//				if((inputLine.indexOf("<br>") >= 0)){
//					inputLine = inputLine.replace("<br>", "<br/>");
//				}
//
//				if((inputLine.indexOf("&nbsp;") >= 0)){
//					inputLine = inputLine.replace("&nbsp;", "");
//				}
//
//				if((inputLine.indexOf("<p><a href=\"/graphs/\">Main page</a>") >= 0)){
//					inputLine = inputLine.replace("<p><a href=\"/graphs/\">Main page</a>", "<p><a href=\"/graphs/\">Main page</a></p>");
//				}
//
//				//				System.out.println(inputLine);
//				writer.write(inputLine);
//			}
//
//			reader.close();
//			writer.close();
//
//		}catch(IOException io){
//			io.printStackTrace();
//		}
//
//	}


}
