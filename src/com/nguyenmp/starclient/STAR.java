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
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A facade that handles all the complicated HTTP calls involved with the STAR API.
 */
public class STAR {
	public static final String URL_LOGIN = "https://isis.sa.ucsb.edu/STAR/";
	public static final String URL_ROOT = "https://sso.my.ucsb.edu";
	private static final String ID_LOGIN_FORM = "ctl00";
	
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
		String redirectHTML = Utils.toString(postResponse);
		String secondRedirectHTML = followSSORedirect(redirectHTML,client, context);
		
		// Test client
		HttpGet get = new HttpGet(URL_LOGIN);
		response = client.execute(get, context);
		
		// Follow second redirect
		return Utils.toString(response);
	}
	
	public static String followSSORedirect(String redirectHTML, HttpClient client, HttpContext context) throws XMLParser.XMLException, IOException {
		// Get redirect url
		int redirectURLStart = redirectHTML.indexOf("<a href=\"") + "<a href=\"".length();
		int redirectURLEnd = redirectHTML.indexOf("\">", redirectURLStart);
		String redirectURL = URL_ROOT + StringEscapeUtils.unescapeHtml4(redirectHTML.substring(redirectURLStart, redirectURLEnd));

		// Follow redirect
		HttpGet getRequest = new HttpGet(redirectURL);
		HttpResponse postResponse = client.execute(getRequest, context);
		redirectHTML = Utils.toString(postResponse);
		
		Document redirectDocument = XMLParser.getDocumentFromString(redirectHTML);
		Node bodyNode = XMLParser.getChildFromName(redirectDocument.getDocumentElement(), "body");
		Node redirectFormNode = XMLParser.getChildFromName((Element) bodyNode, "form");
		String redirectAction = StringEscapeUtils.unescapeHtml4(((Element) redirectFormNode).getAttribute("action"));
		String redirectWaValue = StringEscapeUtils.unescapeHtml4(XMLParser.getChildFromAttribute((Element) redirectFormNode, "name", "wa").getAttribute("value"));
		String redirectWresultValue = StringEscapeUtils.unescapeHtml4(XMLParser.getChildFromAttribute((Element) redirectFormNode, "name", "wresult").getAttribute("value"));
		String redirectWctxValue = StringEscapeUtils.unescapeHtml4(XMLParser.getChildFromAttribute((Element) redirectFormNode, "name", "wctx").getAttribute("value"));

		List<NameValuePair> redirectNameValuePairs = new ArrayList<NameValuePair>();
		redirectNameValuePairs.add(new BasicNameValuePair("wa", redirectWaValue));
		redirectNameValuePairs.add(new BasicNameValuePair("wresult", redirectWresultValue));
		redirectNameValuePairs.add(new BasicNameValuePair("wctx", redirectWctxValue));
		UrlEncodedFormEntity redirectEntity = new UrlEncodedFormEntity(redirectNameValuePairs);

		HttpPost redirectPost = new HttpPost(redirectAction);
		redirectPost.setEntity(redirectEntity);
		
		HttpResponse redirectResponse = client.execute(redirectPost, context);
		return Utils.toString(redirectResponse);
	}
	
	public static void main(String[] args) throws IOException, XMLParser.XMLException {
		System.out.println(new File("credentials.conf").getAbsolutePath());
		Scanner scanner = new Scanner(new FileInputStream("credentials.conf"));
		String username = scanner.nextLine();
		String password = scanner.nextLine();

		//TODO: Write realistic test for logging in
		Assert.assertEquals(STAR.login(username, password), null);
	}
}
