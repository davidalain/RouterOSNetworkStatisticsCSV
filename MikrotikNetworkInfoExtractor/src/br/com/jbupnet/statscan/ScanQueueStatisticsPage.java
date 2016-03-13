package br.com.jbupnet.statscan;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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

	public static String getCSVHeader(){

		String lineOut = ""+ 
				"QueueName"+";"+
				"IP"+";"+
				"Avg. In Daily Value"+";"+"Avg. In. Daily Unit"+";"+
				"Avg. In Weekly Value"+";"+"Avg. In. Weekly Unit"+";"+
				"Avg. In Monthly Value"+";"+"Avg. In. Monthly Unit"+";"+
				"Avg. In Yearly Value"+";"+"Avg. In. Yearly Unit"+";"+
				"Bigger Avg. In. Value"+";"+"Bigger Avg. In. Unit"+";"+
				"Avg. Out Daily Value"+";"+"Avg. Out Daily Unit"+";"+
				"Avg. Out Weekly Value"+";"+"Avg. Out Weekly Unit"+";"+
				"Avg. Out Monthly Value"+";"+"Avg. Out Monthly Unit"+";"+
				"Avg. Out Yearly Value"+";"+"Avg. Out Yearly Unit"+";"+
				"Bigger Avg. Out Value"+";"+"Bigger Avg. Out Unit"+"\r\n";

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

			double[] inValues = new double[4];
			double[] outValues = new double[4];
			String[] inUnit = new String[4];
			String[] outUnit = new String[4];

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

			//Remove o texto do percentual: Exemplo "(0.0%)" e também os espaços em branco desnecessários
			for(int i = 0 ; i < full.length ; i++){
				in[i] = in[i].substring(0, in[i].indexOf("(") - 1).trim();
				out[i] = out[i].substring(0, out[i].indexOf("(") - 1).trim();
			}

			//Separa os valores e unidades (b ou Kb ou Mb ou Gb)
			for(int i = 0 ; i < full.length ; i++){
				
				int indexIn = Math.max( Math.max(in[i].indexOf("Gb"), in[i].indexOf("Mb")), in[i].indexOf("Kb"));
				if(indexIn < 0) indexIn = in[i].indexOf("b");
				
				int indexOut = Math.max( Math.max(out[i].indexOf("Gb"), out[i].indexOf("Mb")), out[i].indexOf("Kb"));
				if(indexOut < 0) indexOut = out[i].indexOf("b");
				
				try{
					inValues[i] = Double.parseDouble(in[i].substring(0, indexIn));
					outValues[i] = Double.parseDouble(out[i].substring(0, indexOut));

					inUnit[i] = in[i].substring(indexIn, in[i].length());
					outUnit[i] = out[i].substring(indexOut, out[i].length());

				}catch(Exception e1){
					e1.printStackTrace();
				}
			}

			//Descobre o maior valor dentre as médias (daily, weekly, monthly, yearly)
			int inBiggest = 0;
			int outBiggest = 0;
			for(int i = 0 ; i < in.length ; i++){

				long multIn = inUnit[i].equals("Kb")? 1024L : (inUnit[i].equals("Mb") ? 1024L*1024L : (inUnit[i].equals("Gb")? 1024L*1024L*1024L : 1L));
				long multOut = outUnit[i].equals("Kb")? 1024L : (outUnit[i].equals("Mb")? 1024L*1024L : (outUnit[i].equals("Gb")? 1024L*1024L*1024L : 1L));

				if(((long)inValues[i] * multIn) > ((long)inValues[inBiggest] * multIn)){
					inBiggest = i;
				}

				if(((long)outValues[i] * multOut) > ((long)outValues[inBiggest] * multOut)){
					outBiggest = i;
				}
			}

			//			for(int i = 0 ; i < in.length ; i++){
			//				System.out.println(titles[i]);
			//				System.out.println("in:" + inValues[i] + " " + inUnit[i]);
			//				System.out.println("out:" + outValues[i] + " " + outUnit[i]);
			//				System.out.println();
			//			}
			//			System.out.println("Biggest in:" + inValues[inBiggest] + " " + inUnit[inBiggest]);
			//			System.out.println("Biggest out:" + outValues[outBiggest] + " " + outUnit[outBiggest]);


			String lineOut = ""+
					queueName+";"+
					ip+";"+
					inValues[0]+";"+inUnit[0]+";"+
					inValues[1]+";"+inUnit[1]+";"+
					inValues[2]+";"+inUnit[2]+";"+
					inValues[3]+";"+inUnit[3]+";"+
					inValues[inBiggest]+";"+inUnit[inBiggest]+";"+
					outValues[0]+";"+outUnit[0]+";"+
					outValues[1]+";"+outUnit[1]+";"+
					outValues[2]+";"+outUnit[2]+";"+
					outValues[3]+";"+outUnit[3]+";"+
					outValues[outBiggest]+";"+outUnit[outBiggest]+"\r\n";
			//			System.out.println();
			//			System.out.println("Linha do arquivo:");
			//			System.out.println(lineOut);

			return lineOut;

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