package br.com.davidalain.common;

import java.util.Scanner;

public class InputKeyboardUtil {

	private static Scanner keyboard = new Scanner(System.in);
	
	public static final String DEFAULT_HOST = "(PUT YOUR DEFAULT HOST HERE: InputHostUserPass.DEFAULT_HOST)";
	public static final String DEFAULT_HOST_HTTP_PORT = "(PUT YOUR DEFAULT HOST WITH HTTP PORT HERE: InputHostUserPass.DEFAULT_HOST_HTTP_PORT)";
	
	public static final String getHost(){
    	System.out.print("target host: ");
    	return keyboard.next();
    }
	
	public static final String getHostPort(){
    	System.out.print("target host with port: ");
    	return keyboard.next();
    }
    
    public static final String getUserName(){
    	System.out.print("username: ");
    	return keyboard.next();
    }
    
    public static final String getPassword(){
    	System.out.print("password: ");
    	return keyboard.next();
    }
    
    public static final String getFilename(){
    	System.out.print("CSV filename: ");
    	return keyboard.next();
    }
    
    
	
}
