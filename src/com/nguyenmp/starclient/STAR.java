package com.nguyenmp.starclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 8/7/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class STAR {
	public static final String LOGIN_URL = "https://isis.sa.ucsb.edu/STAR/";
	
	
	STAR(String username, String password) {
		
	}
	
	public static String login(String username, String password) throws IOException {
		HttpClient client = new DefaultHttpClient();
		
		HttpGet getRequest = new HttpGet(LOGIN_URL);
		HttpResponse response = client.execute(getRequest);
		String content = Utils.toString(response);
		return content;
	}
}
