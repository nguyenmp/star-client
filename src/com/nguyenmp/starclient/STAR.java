package com.nguyenmp.starclient;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A facade that handles all the complicated HTTP calls involved with the STAR API.
 */
public class STAR {
	public static final String URL_LOGIN = "https://isis.sa.ucsb.edu/STAR/";
	public static final String URL_ROOT = "https://sso.my.ucsb.edu";
	private static final String ID_LOGIN_FORM = "ctl00";
	
	
	STAR(String username, String password) {
		
	}
	
	public static String login(String username, String password) throws IOException, XMLParser.XMLException {
		HttpContext context = new BasicHttpContext();
		HttpClient client = new DefaultHttpClient();
		
		HttpGet getRequest = new HttpGet(URL_LOGIN);
		HttpResponse response = client.execute(getRequest, context);
		String content = Utils.toString(response);
		
		Document document = XMLParser.getDocumentFromString(content);
		Element formElement = document.getElementById(ID_LOGIN_FORM);
		
		String postURL = URL_ROOT + formElement.getAttribute("action");
		
		
		Element viewStateElement = document.getElementById("__VIEWSTATE");
		String viewStateValue = viewStateElement.getAttribute("value");
		
		Element eventValidationElement = document.getElementById("__EVENTVALIDATION");
		String eventValidationValue = eventValidationElement.getAttribute("value");
		
		
		
		Element dbElement = (Element) XMLParser.getChildFromAttribute(formElement, "__db");
		String dbValue = dbElement.getAttribute("value");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("__VIEWSTATE", viewStateValue));
		nameValuePairs.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidationValue));
		nameValuePairs.add(new BasicNameValuePair("__db", dbValue));
		nameValuePairs.add(new BasicNameValuePair("ctl00$cphMain$tbUsername", username));
		nameValuePairs.add(new BasicNameValuePair("ctl00$cphMain$tbPassword:", password));
		nameValuePairs.add(new BasicNameValuePair("ctl00$cphMain$btnLogin:", "Sign In"));

		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);
		
		HttpPost post = new HttpPost(postURL);
		post.setEntity(formEntity);
		
		response = client.execute(post, context);
		
		return Utils.toString(response);
	}
	
	public static void main(String[] args) throws IOException, XMLParser.XMLException {
		STAR.login("asdfasdf", "asfsadf");
	}
}
