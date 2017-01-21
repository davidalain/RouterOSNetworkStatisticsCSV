package br.com.davidalain.common;

public class BitUtil {
	
	//Contantes com os valores dos multiplicadores
	public static final double bit 	= 1.0;
	public static final double Kbit = 1024.0;
	public static final double Mbit = 1024.0 * 1024.0;
	public static final double Gbit = 1024.0 * 1024.0 * 1024.0;

	/**
	 * Faz o parsing da String recebida e cria um Tuple com valor e unidade
	 * 
	 * @param valueUnit
	 * @return
	 */
	public static Tuple<Double, String> parseValueUnit(String valueUnit){
		
			//Pega o maior indice dentre as Strings: "Gb", "Mb" e "Kb", caso índice seja < 0, então não contém a String, então o valor está em bytes "b". 
			int index = Math.max( Math.max(valueUnit.indexOf("Gb"), valueUnit.indexOf("Mb")), valueUnit.indexOf("Kb"));
			if(index < 0) index = valueUnit.indexOf("b");
			
			//Pega o valor e a unidade em variáveis separadas
			double value = Double.parseDouble(valueUnit.substring(0, index));
			String unit = valueUnit.substring(index, valueUnit.length());
			
			//Cria e retorna a tupla
			return new Tuple<Double, String>(value, unit);
	}
	
	public static double getMultiplierValue(String multiplierUnit) throws Exception{
		
		if(multiplierUnit.equals("b"))
			return BitUtil.bit;
		if(multiplierUnit.equals("Kb"))
			return BitUtil.Kbit;
		if(multiplierUnit.equals("Mb"))
			return BitUtil.Mbit;
		if(multiplierUnit.equals("Gb"))
			return BitUtil.Gbit;
		
		throw new Exception("Multiplicador desconhecido");
	}
	
	public static double getTotalValueInBits(Tuple<Double, String> tuple) throws Exception{
		return tuple.x * getMultiplierValue(tuple.y);
	}
	
}
