package br.com.davidalain.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ScanQueueStatisticsPage {


	private static final String Kbps = "(Kbps)";
	private static final String Mbps = "(Mbps)";

	public static final String getCSVHeader(){
		
		final String lineOut = "QueueName"+";"+"IP"+";"+
		
				"Max In Daily (Kbps)"+";"+
				"Max In Weekly (Kbps)"+";"+
				"Max In Monthly (Kbps)"+";"+
				"Max In Yearly (Kbps)"+";"+
				"Bigger Max In (Kbps)"+";"+

				"Max Out Daily (Kbps)"+";"+
				"Max Out Weekly (Kbps)"+";"+
				"Max Out Monthly (Kbps)"+";"+
				"Max Out Yearly (Kbps)"+";"+
				"Bigger Max Out (Kbps)"+";"+
				

				"Average In Daily (Kbps)"+";"+
				"Average In Weekly (Kbps)"+";"+
				"Average In Monthly (Kbps)"+";"+
				"Average In Yearly (Kbps)"+";"+
				"Bigger Average In (Kbps)"+";"+

				"Average Out Daily (Kbps)"+";"+
				"Average Out Weekly (Kbps)"+";"+
				"Average Out Monthly (Kbps)"+";"+
				"Average Out Yearly (Kbps)"+";"+
				"Bigger Average Out (Kbps)"+";"+
				
				
				"Current In Daily (Kbps)"+";"+
				"Current In Weekly (Kbps)"+";"+
				"Current In Monthly (Kbps)"+";"+
				"Current In Yearly (Kbps)"+";"+
				"Bigger Current In (Kbps)"+";"+

				"Current Out Daily (Kbps)"+";"+
				"Current Out Weekly (Kbps)"+";"+
				"Current Out Monthly (Kbps)"+";"+
				"Current Out Yearly (Kbps)"+";"+
				"Bigger Current Out (Kbps)"+";"+

				"\r\n";

		//		System.out.println();
		//		System.out.println("Cabeçalho do arquivo:");
		//		System.out.println(lineOut);

		return lineOut;

	}

	public static String getCSVLine(String urlToDownload) throws MalformedURLException, IOException{

		try{

			//Baixa a página com as estatísticas da Queue e corrige as TAGs HTML que não estão no padrão XML
			PageDownloader.downloadPageAndFixBrokenTags(urlToDownload, StatScanConfig.HTML_TEMP_FILE);

			//Lê o arquivo temporário baixado e gera um documento com todas as tags para serem percorridas
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(StatScanConfig.HTML_TEMP_FILE));

			// mudando o valor de 'title'
			Element htmlTag = doc.getDocumentElement();
			Element bodyTag = (Element) htmlTag.getElementsByTagName("body").item(0);
			NodeList nodes =  bodyTag.getChildNodes();

			/*
			//=====================================================================
			for(int i = 0 ; i < nodes.getLength() ; i++){
				System.out.println("-----------------------------------------------------------------------");
				System.out.println("i=" + i);
				System.out.println(">>>"+nodes.item(i).getTextContent()+"<<<");
				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			}
			//=====================================================================
			 */

			final String queueInfoText = nodes.item(0).getTextContent();
			String queueName = queueInfoText.substring(queueInfoText.indexOf("Queue <") + "Queue <".length(), queueInfoText.indexOf("> Statistics"));
			//			System.out.println("QueueName: " + queueName);

			String ipInfoText = nodes.item(1).getTextContent();
			String ip = ipInfoText.substring(ipInfoText.indexOf("Source-addresses: ") + "Source-addresses: ".length(), ipInfoText.indexOf("Destination-address:") - 2);
			//			System.out.println("IP: " + ip);

			String[] graphData = new String[4];
			String[] maxInStr = new String[4];
			String[] maxOutStr = new String[4];
			String[] averageInStr = new String[4];
			String[] averageOutStr = new String[4];
			String[] currentInStr = new String[4];
			String[] currentOutStr = new String[4];

			ArrayList<Tuple<Double, String>> maxInValueUnitTuples = new ArrayList<Tuple<Double, String>>();
			ArrayList<Tuple<Double, String>> maxOutValueUnitTuples = new ArrayList<Tuple<Double, String>>();
			ArrayList<Tuple<Double, String>> averageInValueUnitTuples = new ArrayList<Tuple<Double, String>>();
			ArrayList<Tuple<Double, String>> averageOutValueUnitTuples = new ArrayList<Tuple<Double, String>>();
			ArrayList<Tuple<Double, String>> currentInValueUnitTuples = new ArrayList<Tuple<Double, String>>();
			ArrayList<Tuple<Double, String>> currentOutValueUnitTuples = new ArrayList<Tuple<Double, String>>();

			graphData[0] = nodes.item(2).getTextContent(); //daily
			graphData[1] = nodes.item(3).getTextContent(); //weekly
			graphData[2] = nodes.item(4).getTextContent(); //monthly
			graphData[3] = nodes.item(5).getTextContent(); //yearly

			//			//Extrai os textos das médias (Average)
			//			final String averageInIni = "; Average In: ";
			//			final String averageInEnd = "; Current In: ";
			//			final String averageOutIni = "; Average Out:";
			//			final String averageOutEnd = "; Current Out:";
			//
			//			//Extrai os textos dos valores atuais (Current)
			//			final String currentInIni = "; Current In: ";
			//			final String currentInEnd = "; Max Out: ";
			//			final String currentOutIni = "; Current Out: ";
			//			final String currentOutEnd = ";   #####";

			for(int i = 0 ; i < graphData.length ; i++){

				//				System.out.println("full: _"+graphData[i].toString()+"_");

				String[] values = graphData[i].split(";");
				//				int c = 0;
				//				for(String s : values){
				//					System.out.println("c="+ c++ +"______"+s+"________");
				//				}

				maxInStr[i] = values[0].substring(values[0].indexOf("Max In: ") + "Max In: ".length(), values[0].indexOf("b")+1);
				maxOutStr[i] = values[3].substring(values[3].indexOf("Max Out: ") + "Max Out: ".length(), values[3].indexOf("b")+1);

				averageInStr[i] = values[1].substring(values[1].indexOf("Average In: ") + "Average In: ".length(), values[1].indexOf("b")+1);
				averageOutStr[i] = values[4].substring(values[4].indexOf("Average Out: ") + "Average Out: ".length(), values[4].indexOf("b")+1);

				currentInStr[i] = values[2].substring(values[2].indexOf("Current In: ") + "Current In: ".length(), values[2].indexOf("b")+1);
				currentOutStr[i] = values[5].substring(values[5].indexOf("Current Out: ") + "Current Out: ".length(), values[5].indexOf("b")+1);


				//				//average
				//				int avgInIndexIni = graphData[i].indexOf(averageInIni) + averageInIni.length();
				//				int avgInIndexEnd = graphData[i].indexOf(averageInEnd);
				//
				//				int avgOutIndexIni = graphData[i].indexOf(averageOutIni) + averageOutIni.length();
				//				int avgOutIndexEnd = graphData[i].indexOf(averageOutEnd);
				//
				//				averageInStr[i] = graphData[i].substring(avgInIndexIni, avgInIndexEnd);
				//				averageOutStr[i] = graphData[i].substring(avgOutIndexIni, avgOutIndexEnd);
				//				
				//				//current
				//				int curInIndexIni = graphData[i].indexOf(currentInIni) + currentInIni.length();
				//				int curInIndexEnd = graphData[i].indexOf(currentInEnd);
				//
				//				int curOutIndexIni = graphData[i].indexOf(currentOutIni) + currentOutIni.length();
				//				int curOutIndexEnd = graphData[i].indexOf(currentOutEnd);
				//
				//				currentInStr[i] = graphData[i].substring(curInIndexIni, curInIndexEnd);
				//				currentOutStr[i] = graphData[i].substring(curOutIndexIni, curOutIndexEnd);

			}

			//Remove o texto do percentual, (se houver): Exemplo "(0.0%)" e também os espaços em branco desnecessários
			//			for(int i = 0 ; i < graphData.length ; i++){
			//
			//				//average
			//				int indexIn = averageInStr[i].indexOf("(");
			//				int indexOut = averageOutStr[i].indexOf("(");
			//
			//				if(indexIn >= 0) averageInStr[i] = averageInStr[i].substring(0, indexIn - 1);
			//				if(indexOut >= 0) averageOutStr[i] = averageOutStr[i].substring(0, indexOut - 1);
			//
			//				averageInStr[i] = averageInStr[i].trim();
			//				averageOutStr[i] = averageOutStr[i].trim();
			//				
			//				//current
			//				indexIn = currentInStr[i].indexOf("(");
			//				indexOut = currentOutStr[i].indexOf("(");
			//
			//				if(indexIn >= 0) currentInStr[i] = currentInStr[i].substring(0, indexIn - 1);
			//				if(indexOut >= 0) currentOutStr[i] = currentOutStr[i].substring(0, indexOut - 1);
			//
			//				currentInStr[i] = currentInStr[i].trim();
			//				currentOutStr[i] = currentOutStr[i].trim();
			//			}

			//Separa os valores em tuplas e adiciona no arraylist de tuplas
			for(int i = 0 ; i < graphData.length ; i++){

				maxInValueUnitTuples.add(BitUtil.parseValueUnit(maxInStr[i]));
				maxOutValueUnitTuples.add(BitUtil.parseValueUnit(maxOutStr[i]));

				averageInValueUnitTuples.add(BitUtil.parseValueUnit(averageInStr[i]));
				averageOutValueUnitTuples.add(BitUtil.parseValueUnit(averageOutStr[i]));

				currentInValueUnitTuples.add(BitUtil.parseValueUnit(currentInStr[i]));
				currentOutValueUnitTuples.add(BitUtil.parseValueUnit(currentOutStr[i]));
			}

//			//Descobre o maior valor dentre as médias (daily, weekly, monthly, yearly)
//			int inBiggest = 0;
//			int outBiggest = 0;
//
//			for(int i = 1 ; i < averageInStr.length ; i++){
//				
//				if(Util.getTotalValueInBits(currentInValueUnitTuples.get(i)) > Util.getTotalValueInBits(currentInValueUnitTuples.get(inBiggest))){
//					inBiggest = i;
//				}
//				if(Util.getTotalValueInBits(currentOutValueUnitTuples.get(i)) > Util.getTotalValueInBits(currentOutValueUnitTuples.get(outBiggest))){
//					outBiggest = i;
//				}
//			}
//
//			//Adiciona o maior valor como o índice 4 do arraylist de tuplas
//			currentInValueUnitTuples.add(currentInValueUnitTuples.get(inBiggest));
//			currentOutValueUnitTuples.add(currentOutValueUnitTuples.get(outBiggest));
			
			//Adiciona o maior valor como o índice 4 do arraylist de tuplas
			maxInValueUnitTuples.add(getMax(maxInValueUnitTuples));
			maxOutValueUnitTuples.add(getMax(maxOutValueUnitTuples));
			
			//Adiciona o maior valor como o índice 4 do arraylist de tuplas
			averageInValueUnitTuples.add(getMax(averageInValueUnitTuples));
			averageOutValueUnitTuples.add(getMax(averageOutValueUnitTuples));
			
			//Adiciona o maior valor como o índice 4 do arraylist de tuplas
			currentInValueUnitTuples.add(getMax(currentInValueUnitTuples));
			currentOutValueUnitTuples.add(getMax(currentOutValueUnitTuples));

			//Cria a linha no padrão CSV
			String csvLine = queueName+";"+ip+";";

			double kbits;
			
			for(Tuple<Double, String> t : maxInValueUnitTuples){
				kbits = BitUtil.getTotalValueInBits(t)/BitUtil.Kbit; //Pega o valor total em bits e converte para kbits
				csvLine += Double.toString(kbits).replace(".", ",")+";"; //Troca o '.' por ',', porque o excel em português reconhece a vírgula como separador decimal
			}
			for(Tuple<Double, String> t : maxOutValueUnitTuples){
				kbits = BitUtil.getTotalValueInBits(t)/BitUtil.Kbit; //Pega o valor total em bits e converte para kbits
				csvLine += Double.toString(kbits).replace(".", ",")+";"; //Troca o '.' por ',', porque o excel em português reconhece a vírgula como separador decimal
			}
			
			for(Tuple<Double, String> t : averageInValueUnitTuples){
				kbits = BitUtil.getTotalValueInBits(t)/BitUtil.Kbit; //Pega o valor total em bits e converte para kbits
				csvLine += Double.toString(kbits).replace(".", ",")+";"; //Troca o '.' por ',', porque o excel em português reconhece a vírgula como separador decimal
			}
			for(Tuple<Double, String> t : averageOutValueUnitTuples){
				kbits = BitUtil.getTotalValueInBits(t)/BitUtil.Kbit; //Pega o valor total em bits e converte para kbits
				csvLine += Double.toString(kbits).replace(".", ",")+";"; //Troca o '.' por ',', porque o excel em português reconhece a vírgula como separador decimal
			}
			
			for(Tuple<Double, String> t : currentInValueUnitTuples){
				kbits = BitUtil.getTotalValueInBits(t)/BitUtil.Kbit; //Pega o valor total em bits e converte para kbits
				csvLine += Double.toString(kbits).replace(".", ",")+";"; //Troca o '.' por ',', porque o excel em português reconhece a vírgula como separador decimal
			}
			for(Tuple<Double, String> t : currentOutValueUnitTuples){
				kbits = BitUtil.getTotalValueInBits(t)/BitUtil.Kbit; //Pega o valor total em bits e converte para kbits
				csvLine += Double.toString(kbits).replace(".", ",")+";"; //Troca o '.' por ',', porque o excel em português reconhece a vírgula como separador decimal
			}
			
			csvLine += "\r\n";
			return csvLine;

		}catch(Exception e){
			e.printStackTrace();
		}

		return null;

	}
	
	
	private static Tuple<Double, String> getMax(List<Tuple<Double, String>> list) throws Exception{
		
		//Descobre o maior valor dentre as médias (daily, weekly, monthly, yearly)
		int biggest = 0;

		for(int i = 1 ; i < list.size() ; i++){
			if(BitUtil.getTotalValueInBits(list.get(i)) > BitUtil.getTotalValueInBits(list.get(biggest))){
				biggest = i;
			}
		}

		//Adiciona o maior valor como o índice 4 do arraylist de tuplas
		return list.get(biggest);
		
	}


}

