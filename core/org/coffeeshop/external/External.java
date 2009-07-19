package org.coffeeshop.external;

import java.io.IOException;

import org.coffeeshop.io.Streams;

public class External {

	public static int execute(String command) {
		try {
			
			Process p = Runtime.getRuntime().exec(command);
		
			return p.waitFor();
			
		} catch (IOException e) {
			return -1;
		} catch (InterruptedException e) {
			return -1;
		}
	}
	
	public static String gather(String command) {
		
		try {
			
			Process p = Runtime.getRuntime().exec(command);
		
			return new String(Streams.getStreamAsByteArray(p.getInputStream()));
			
		} catch (IOException e) {
			return null;
		} 
		
	}
	
}
