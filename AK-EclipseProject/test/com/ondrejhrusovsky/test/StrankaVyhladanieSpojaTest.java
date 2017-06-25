package com.ondrejhrusovsky.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ondrejhrusovsky.aplikacia.WebData;;

public class StrankaVyhladanieSpojaTest {
	@Test
	public void otvorStrankuVyhladavania() throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{	
		WebClient webClient = PomocneFunkcie.vytvorKlienta();
		webClient.getPage(WebData.LINK_VYHLADAVANIE);
	}
	
	@Test
	public void najdiFormularVyhladatSpojenie() throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		WebClient webClient = PomocneFunkcie.vytvorKlienta();
		HtmlPage strankaVyhladanieSpoja = webClient.getPage(WebData.LINK_VYHLADAVANIE);
		strankaVyhladanieSpoja.getFormByName(WebData.NAME_VYHLADAVANIE_FORM);
	}
}
