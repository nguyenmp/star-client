package com.nguyenmp.starclient.test;

import com.nguyenmp.starclient.STAR;
import com.nguyenmp.starclient.XMLParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class STAR_Test {
	@Test
	public void login_Test() throws IOException, XMLParser.XMLException {
		//TODO: Write realistic test for logging in
		Assert.assertEquals(STAR.login("mpnasf8", "asoidfu"), null);
	}
}
