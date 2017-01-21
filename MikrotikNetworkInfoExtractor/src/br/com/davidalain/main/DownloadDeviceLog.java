package br.com.davidalain.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import examples.Example;
import me.legrange.mikrotik.MikrotikApiException;

public class DownloadDeviceLog extends Example{

    public static void main(String... args) throws Exception {
    	
    	System.out.println("====* Download device log *====");
    	
    	DownloadDeviceLog ex = new DownloadDeviceLog();
        ex.connect();
        ex.test();
        ex.disconnect();
    }

    private void test() throws MikrotikApiException, InterruptedException, IOException {
    	
        List<Map<String, String>> result = con.execute("/log/print");
        FileOutputStream fos = new FileOutputStream(new File("mikrotik_log.csv"));
        
        //Imprime o cabeçalho
        if(result != null && result.size() > 0){
        	String str = toStringSemicoma(result.get(0).keySet(), true);
        	System.out.println(str);
        	fos.write((str+"\r\n").getBytes());
        }
        
        //Imprime os valores
        for (Map<String, String> map : result) {
        	String str = toStringSemicoma(map.values(), false);
            System.out.println(str);
            fos.write((str+"\r\n").getBytes());
        }
        
        fos.close();
        
        System.out.println();
        System.out.println("Finalizado!");
    }
    
    private static String toStringSemicoma(Collection<String> collection, boolean isHeader){
    	
    	String out = "";
    	String[] valuesStr = collection.toArray(new String[0]);
    	
    	for(int i = 0 ; i < valuesStr.length ; i++){
    		
    		if(i == 1)
    			continue;
    		
    		if(i == 2){
    			
    			String dateTime = isHeader ? valuesStr[i] : formatDateTime(valuesStr[i]);
    			out += dateTime;
    			
    		}else{
    			out += valuesStr[i];
    		}
    		
    		if(i < valuesStr.length - 1)
    			out += ";";
    	}
    	
    	return out;
    }
	
    private static String formatDateTime(String input){
    	
    	Calendar c = Calendar.getInstance();
    	
    	final String[] months = {"jan", "feb", "mar", "may", "apr", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
    	
    	int day = c.get(Calendar.DAY_OF_MONTH);
    	int month = c.get(Calendar.MONTH);
    	int year = c.get(Calendar.YEAR);
    	int hour = c.get(Calendar.HOUR_OF_DAY);
    	int min = c.get(Calendar.MINUTE);
    	int sec = c.get(Calendar.SECOND);
    	
    	String[] parts = input.split(" ");
    	
    	String[] timeFields = null;
    	
    	//formato: data hora
    	if(parts.length == 2){
    		
    		//formato: mmm/dd/yyyy ou mmm/dd
    		String[] dateFields = parts[0].split("/");
    		
    		//month
    		for(int i = 0 ; i < months.length; i++){
    			if(months[i].equals(dateFields[0])){
    				month = i;
    				break;
    			}
    		}
    		
    		//day
    		day = Integer.parseInt(dateFields[1]);
    		
    		//year
    		if(dateFields.length == 3){
    			year = Integer.parseInt(dateFields[2]);
    		}
    		
    	}
    	
    	//formato: hora
    	int timeIndex = parts.length - 1;
    	timeFields = parts[timeIndex].split(":");
    	
    	hour = Integer.parseInt(timeFields[0]);	//hour
    	min = Integer.parseInt(timeFields[1]);	//min
    	sec = Integer.parseInt(timeFields[2]);	//sec
    	
    	c.set(year, month, day, hour, min, sec);
    	
    	SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	
    	return format1.format(c.getTime());
    }
    
}
