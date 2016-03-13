package br.com.jbupnet.statscan;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
//
//import com.sun.org.apache.xpath.internal.XPathAPI;

public class ScanQueueStatisticsPage {

	private static final String HTML_TEMP_FILE = "htmlQueueStatTempFile.html";

	/*private static void downloadPage(String urlToDownload, String filePath){

		try{

			//Baixa a página
			BufferedInputStream bin = null;
			FileOutputStream fout = null;
			try {
				bin = new BufferedInputStream(new URL(urlToDownload).openStream());
				fout = new FileOutputStream(filePath);

				final byte data[] = new byte[1024];
				int count;
				while ((count = bin.read(data, 0, 1024)) != -1) {
					fout.write(data, 0, count);
				}
			} finally {
				if (bin != null) {
					bin.close();
				}
				if (fout != null) {
					fout.close();
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	private static void replaceBrokenTags(String filePath){

		try{

			Path path = Paths.get(filePath);
			Charset charset = StandardCharsets.UTF_8;

			String content = new String(Files.readAllBytes(path), charset);

			content = content.replaceAll("charset=windows-1252\">", "charset=windows-1252\"/>"); //erro da tag meta
			content = content.replaceAll("<br>", "<br/>"); //erro da tag br
			content = content.replaceAll("Graph\">", "Graph\"/>"); //Erro da tag img
			content = content.replaceAll("&nbsp;", ""); //Erro da entidade &nbsp;

			System.out.println("Imprimindo conteúdo");
			System.out.println("#################################################################################################################################");
			System.out.println(content);
			System.out.println("#################################################################################################################################");

			Files.write(path, content.getBytes(charset));

		}catch(Exception e){
			e.printStackTrace();
		}

	}*/

	private static void downloadPageAndFixBrokenTags(String urlQueueStatistics, String targetFilePath){

		try{

			URL url = new URL(urlQueueStatistics);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath));
			String inputLine;

			while ((inputLine = reader.readLine()) != null){

				if((inputLine.indexOf("<li>") >= 0) && (inputLine.indexOf("</li>") < 0)){
					inputLine = inputLine.concat("</li>");
				}

				if((inputLine.indexOf("<br>") >= 0)){
					inputLine = inputLine.replace("<br>", "<br/>");
				}

				if((inputLine.indexOf("&nbsp;") >= 0)){
					inputLine = inputLine.replace("&nbsp;", "");
				}

				if((inputLine.indexOf("<p><a href=\"/graphs/\">Main page</a>") >= 0)){
					inputLine = inputLine.replace("<p><a href=\"/graphs/\">Main page</a>", "<p><a href=\"/graphs/\">Main page</a></p>");
				}

				//				System.out.println(inputLine);
				writer.write(inputLine);
			}

			reader.close();
			writer.close();

		}catch(IOException io){
			io.printStackTrace();
		}

	}

	public static final String getCSVHeader(){

		final String lineOut = "QueueName"+";"+"IP"+";"+

				"Avg. In Daily (Kbps)"+";"+
				"Avg. In Weekly (Kbps)"+";"+
				"Avg. In Monthly (Kbps)"+";"+
				"Avg. In Yearly (Kbps)"+";"+
				"Bigger Avg. In (Kbps)"+";"+

				"Avg. Out Daily (Kbps)"+";"+
				"Avg. Out Weekly (Kbps)"+";"+
				"Avg. Out Monthly (Kbps)"+";"+
				"Avg. Out Yearly (Kbps)"+";"+
				"Bigger Avg. Out (Kbps)"+";"+

				"\r\n";

		//		System.out.println();
		//		System.out.println("Cabeçalho do arquivo:");
		//		System.out.println(lineOut);

		return lineOut;

	}

	public static String getCSVLine(String urlToDownload) throws MalformedURLException, IOException{

		try{

			//Baixa a página com as estatísticas da Queue e corrige as TAGs HTML que não estão no padrão XML
			downloadPageAndFixBrokenTags(urlToDownload, HTML_TEMP_FILE);

			//Lê o arquivo temporário baixado e gera um documento com todas as tags para serem percorridas
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(HTML_TEMP_FILE));

			// mudando o valor de 'title'
			Element htmlTag = doc.getDocumentElement();
			Element bodyTag = (Element) htmlTag.getElementsByTagName("body").item(0);
			NodeList nodes =  bodyTag.getChildNodes();

			//			//=====================================================================
			//			for(int i = 0 ; i < nodes.getLength() ; i++){
			//				System.out.println("-----------------------------------------------------------------------");
			//				System.out.println("i=" + i);
			//				System.out.println(nodes.item(i).getTextContent());
			//				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			//			}
			//			//=====================================================================

			String queueInfoText = nodes.item(0).getTextContent();
			String queueName = queueInfoText.substring(queueInfoText.indexOf("Queue <") + "Queue <".length(), queueInfoText.indexOf("> Statistics"));
			//			System.out.println("QueueName: " + queueName);

			String ipInfoText = nodes.item(1).getTextContent();
			String ip = ipInfoText.substring(ipInfoText.indexOf("Source-addresses: ") + "Source-addresses: ".length(), ipInfoText.indexOf("Destination-address:") - 3);
			//			System.out.println("IP: " + ip);

			String[] full = new String[4];
			String[] in = new String[4];
			String[] out = new String[4];

			ArrayList<Tuple<Double, String>> inValueUnitTuples = new ArrayList<Tuple<Double, String>>();
			ArrayList<Tuple<Double, String>> outValueUnitTuples = new ArrayList<Tuple<Double, String>>();

			full[0] = nodes.item(2).getTextContent(); //daily
			full[1] = nodes.item(3).getTextContent(); //weekly
			full[2] = nodes.item(4).getTextContent(); //monthly
			full[3] = nodes.item(5).getTextContent(); //yearly

			//Extrai os textos das médias (Average)
			final String matchInIni = "; Average In: ";
			final String matchInEnd = "; Current In: ";
			final String matchOutIni = "; Average Out:";
			final String matchOutEnd = "; Current Out:";

			for(int i = 0 ; i < full.length ; i++){
				int inIndexIni = full[i].indexOf(matchInIni) + matchInIni.length();
				int inIndexEnd = full[i].indexOf(matchInEnd);

				int outIndexIni = full[i].indexOf(matchOutIni) + matchOutIni.length();
				int outIndexEnd = full[i].indexOf(matchOutEnd);

				in[i] = full[i].substring(inIndexIni, inIndexEnd);
				out[i] = full[i].substring(outIndexIni, outIndexEnd);
			}

			//Remove o texto do percentual, (se houver): Exemplo "(0.0%)" e também os espaços em branco desnecessários
			for(int i = 0 ; i < full.length ; i++){

				int iIn = in[i].indexOf("(");
				int iOut = out[i].indexOf("(");

				if(iIn >= 0) in[i] = in[i].substring(0, iIn - 1);
				if(iOut >= 0) out[i] = out[i].substring(0, iOut - 1);

				in[i] = in[i].trim();
				out[i] = out[i].trim();
			}

			//Separa os valores em tuplas e adiciona no arraylist de tuplas
			for(int i = 0 ; i < full.length ; i++){
				inValueUnitTuples.add(Util.createTupleValueUnit(in[i]));
				outValueUnitTuples.add(Util.createTupleValueUnit(out[i]));
			}

			//Descobre o maior valor dentre as médias (daily, weekly, monthly, yearly)
			int inBiggest = 0;
			int outBiggest = 0;

			for(int i = 1 ; i < in.length ; i++){

				if(Util.getTotalValueInBits(inValueUnitTuples.get(i)) > Util.getTotalValueInBits(inValueUnitTuples.get(inBiggest))){
					inBiggest = i;
				}
				if(Util.getTotalValueInBits(outValueUnitTuples.get(i)) > Util.getTotalValueInBits(outValueUnitTuples.get(outBiggest))){
					outBiggest = i;
				}
			}

			//Adiciona o maior valor como o índice 4 do arraylist de tuplas
			inValueUnitTuples.add(inValueUnitTuples.get(inBiggest));
			outValueUnitTuples.add(outValueUnitTuples.get(outBiggest));

			//Cria a linha no padrão CSV
			String csvLine = queueName+";"+ip+";";

			double kbits;
			for(Tuple<Double, String> t : inValueUnitTuples){
				kbits = Util.getTotalValueInBits(t)/Util.Kbit; //Pega o valor total em bits e converte para kbits
				csvLine += Double.toString(kbits).replace(".", ",")+";"; //Troca o '.' por ',', porque o excel em português reconhece a vírgula como separador decimal
			}
			for(Tuple<Double, String> t : outValueUnitTuples){
				kbits = Util.getTotalValueInBits(t)/Util.Kbit; //Pega o valor total em bits e converte para kbits
				csvLine += Double.toString(kbits).replace(".", ",")+";"; //Troca o '.' por ',', porque o excel em português reconhece a vírgula como separador decimal
			}
			csvLine += "\r\n";
			return csvLine;

		}catch(Exception e){
			e.printStackTrace();
		}

		return null;

	}



}

//Leia mais em:
//Manipulando arquivos
//XML em
//Java http:// www.devmedia.com.br/manipulando-arquivos-xml-em-java/3245#ixzz42elMP6U3