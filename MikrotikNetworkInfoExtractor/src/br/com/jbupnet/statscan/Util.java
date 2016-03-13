package br.com.jbupnet.statscan;

public class Util {
	
	//Contantes com os valores dos multiplicadores
	public static final double bit 	= 1.0;
	public static final double Kbit = 1024.0;
	public static final double Mbit = 1024.0 * 1024.0;
	public static final double Gbit = 1024.0 * 1024.0 * 1024.0;

	public static Tuple<Double, String> createTupleValueUnit(String valueUnit){
		
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
			return Util.bit;
		if(multiplierUnit.equals("Kb"))
			return Util.Kbit;
		if(multiplierUnit.equals("Mb"))
			return Util.Mbit;
		if(multiplierUnit.equals("Gb"))
			return Util.Gbit;
		
		throw new Exception("Multiplicador desconhecido");
	}
	
	public static double getTotalValueInBits(Tuple<Double, String> tuple) throws Exception{
		return tuple.x * getMultiplierValue(tuple.y);
	}
	
}
