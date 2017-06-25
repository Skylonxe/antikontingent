package com.ondrejhrusovsky.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ondrejhrusovsky.aplikacia.WebData;

public class FormularVyhladatSpojenieTest {
	
	private static HtmlForm formular;
	
	@BeforeClass
	public static void pripravTesty() throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		WebClient webClient = PomocneFunkcie.vytvorKlienta();
		HtmlPage strankaVyhladanieSpoja = webClient.getPage(WebData.LINK_VYHLADAVANIE);
		formular = strankaVyhladanieSpoja.getFormByName(WebData.NAME_VYHLADAVANIE_FORM);
	}
	
	@Test
	public void najdiTextInputOdkial() throws ElementNotFoundException
	{
		formular.getInputByName(WebData.NAME_VYHLADAVANIE_TEXTINPUT_ODKIAL);
	}
	
	@Test
	public void najdiTextInputKam() throws ElementNotFoundException
	{
		formular.getInputByName(WebData.NAME_VYHLADAVANIE_TEXTINPUT_KAM);
	}
	
	@Test
	public void najdiTextInputCez() throws ElementNotFoundException
	{
		formular.getInputByName(WebData.NAME_VYHLADAVANIE_TEXTINPUT_CEZ);
	}
	
	@Test
	public void najdiTextInputDatum() throws ElementNotFoundException
	{
		formular.getInputByName(WebData.NAME_VYHLADAVANIE_TEXTINPUT_DATUM);
	}
	
	@Test
	public void najdiTextInputCas() throws ElementNotFoundException
	{
		formular.getInputByName(WebData.NAME_VYHLADAVANIE_TEXTINPUT_CAS);
	}

	@Test
	public void najdiSubmitInputVyhladat() throws ElementNotFoundException
	{
		formular.getInputByName(WebData.NAME_VYHLADAVANIE_SUBMITINPUT_VYHLADAT);
	}
}
