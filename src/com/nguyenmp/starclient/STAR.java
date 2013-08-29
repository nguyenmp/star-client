package com.nguyenmp.starclient;

import org.apache.commons.lang3.StringEscapeUtils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A facade that handles all the complicated HTTP calls involved with the STAR API.
 */
public class STAR {
	public static final String USERNAME = null, PASSWORD = null;
	public static final String URL_LOGIN = "https://isis.sa.ucsb.edu/STAR/";
	public static final String URL_ROOT = "https://sso.my.ucsb.edu";
	private static final String ID_LOGIN_FORM = "ctl00";
	
	
	STAR(String username, String password) {
		
	}
	
	public static String login(String username, String password) throws IOException, XMLParser.XMLException {
		HttpContext context = new BasicHttpContext();
		HttpClient client = new DefaultHttpClient();
		
		// Execute initial get request to get dynamic params from html
		HttpGet getRequest = new HttpGet(URL_LOGIN);
		HttpResponse response = client.execute(getRequest, context);
		String content = Utils.toString(response);
		
		// Read html as xml document
		Document document = XMLParser.getDocumentFromString(content);
		Element formElement = document.getElementById(ID_LOGIN_FORM);
		
		// Extract the url we are supposed tdo submit our login information to
		String postURL = URL_ROOT + StringEscapeUtils.unescapeHtml4(formElement.getAttribute("action"));
		
		// Extract the view state param 
		Element viewStateElement = document.getElementById("__VIEWSTATE");
		String viewStateValue = viewStateElement.getAttribute("value");
		
		// Extract the event validation param
		Element eventValidationElement = document.getElementById("__EVENTVALIDATION");
		String eventValidationValue = eventValidationElement.getAttribute("value");
		
		// Extract the database param
		Element dbElement = (Element) XMLParser.getChildFromAttribute(formElement, "name", "__db");
		String dbValue = dbElement.getAttribute("value");
		
		// Compile all the parameters into one form entity
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("__VIEWSTATE", viewStateValue));
		nameValuePairs.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidationValue));
		nameValuePairs.add(new BasicNameValuePair("__db", dbValue));
		nameValuePairs.add(new BasicNameValuePair("ctl00$cphMain$tbUsername", username));
		nameValuePairs.add(new BasicNameValuePair("ctl00$cphMain$tbPassword", password));
		nameValuePairs.add(new BasicNameValuePair("ctl00$cphMain$btnLogin", "Sign In"));
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);
		
		// Create a post request and attach the login form entity
		HttpPost post = new HttpPost(postURL);
		post.setEntity(formEntity);
		
		// Read the response which contains a redirect to the actual site
		HttpResponse postResponse = client.execute(post, context);
		post.abort();
		
		// Follow redirect
		getRequest = new HttpGet(URL_LOGIN);
		postResponse = client.execute(getRequest, context);
		
		String redirectHTML = Utils.toString(postResponse);
		
		
		
		return redirectHTML;
	}

	public static void main(String[] args) throws IOException, XMLParser.XMLException {
		System.out.println(STAR.login(USERNAME, PASSWORD));
	}
}
