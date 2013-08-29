package com.nguyenmp.starclient.test;

import com.nguyenmp.starclient.STAR;
import com.nguyenmp.starclient.XMLParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class STAR_Test {
	@Test
	public void login_Test() throws IOException, XMLParser.XMLException {
		Scanner scanner = new Scanner(new FileInputStream("credentials.conf"));
		String username = scanner.nextLine();
		String password = scanner.nextLine();
		
		//TODO: Write realistic test for logging in
		Assert.assertEquals(STAR.login(username, password), null);
	}
}
