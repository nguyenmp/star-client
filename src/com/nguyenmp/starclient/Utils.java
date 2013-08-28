package com.nguyenmp.starclient;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {
	
	public static String toString(HttpResponse response) throws IOException {
		// Create reader and writer objects
		StringBuilder builder = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
		char[] buffer = new char[1024];
		int charsRead;
		
		//Read all of the content and put it into a string
		while ((charsRead = reader.read(buffer, 0, buffer.length)) != -1) {
			builder.append(buffer, 0, charsRead);
		}
		
		//Return the read characters as as string
		return builder.toString();
	}
}
