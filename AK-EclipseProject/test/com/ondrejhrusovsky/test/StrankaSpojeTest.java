package com.ondrejhrusovsky.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.exceptions.NepodariloSaNajstSpojeException;
import com.ondrejhrusovsky.formulare.FormularInformaciaOTrase;
import com.ondrejhrusovsky.formulare.FormularVyhladatSpojenie;
import com.ondrejhrusovsky.formulare.FormularVyhladatSpojenieAutocorrect;
import com.ondrejhrusovsky.ikvcAPI.Config;
import com.ondrejhrusovsky.ikvcAPI.Spoj;

public class StrankaSpojeTest {
	
	private static FormularVyhladatSpojenie formularVyhladatSpojenie;
	
	private static String vychodziaStanica = "BRATISLAVA";
	private static String cielovaStanica = "Košice";
	
	private static String odkial = "Bratislava";
	private static String kam = "Kosice";
	private static String cez = "Zilina";
	private static String datum = "20.10.2017";
	private static String cas = "13:00";
	
	private static HtmlPage strankaSpoje;
	
	@BeforeClass
	public static void pripravTesty() throws FailingHttpStatusCodeException, MalformedURLException, IOException, ElementNotFoundException, ChybajuciElementException
	{
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setJavaScriptEnabled(false);	
		
		HtmlPage strankaVyhladanieSpoja = webClient.getPage(Config.LINK_VYHLADAVANIE);
		
		formularVyhladatSpojenie = new FormularVyhladatSpojenie(strankaVyhladanieSpoja.getFormByName(Config.NAME_VYHLADAVANIE_FORM));
		formularVyhladatSpojenie.nastavOdkial(odkial);
		formularVyhladatSpojenie.nastavKam(kam);
		formularVyhladatSpojenie.nastavCez(cez);
		formularVyhladatSpojenie.nastavDatum(datum);
		formularVyhladatSpojenie.nastavCas(cas);
		
		HtmlPage strankaVyhladanieSpoja2 = formularVyhladatSpojenie.posli();
		FormularVyhladatSpojenie formularVyhladatSpojenie2 = new FormularVyhladatSpojenieAutocorrect(strankaVyhladanieSpoja2.getFormByName("connectionParam")); 						
		strankaSpoje = formularVyhladatSpojenie2.posli();	
	}
	
	@Test
	public void vyhladajSpoje() throws NepodariloSaNajstSpojeException
	{
		if(!strankaSpoje.asText().contains("Nasledujúce"))
		{
			throw new NepodariloSaNajstSpojeException();
		}
	}
	
	@Test
	public void najdiStartovnuACielovuStanicu()
	{
		DomElement zaciatokKoniecElem = (DomElement) strankaSpoje.getFirstByXPath(Config.ELEM_SPOJE_ZACIATOKKONIEC);
		
		String[] zaciatokKoniec = zaciatokKoniecElem.asText().split("/");
		String vStanica = zaciatokKoniec[0].trim();
		String cStanica = zaciatokKoniec[1].trim();
		
		assertEquals(vychodziaStanica, vStanica);
		assertEquals(cielovaStanica, cStanica);
	}
	
	@Test
	public void najdiSpoje()
	{
		List<Object> elementySpojov = strankaSpoje.getByXPath(Config.ELEM_SPOJE_SPOJ);		
		assertNotEquals(elementySpojov.size(), 0);
	}
	
	@Test
	public void najdiFormularInformacieOTrase()
	{
		List<Object> elementySpojov = strankaSpoje.getByXPath(Config.ELEM_SPOJE_SPOJ);		
		List<Spoj> spoje = new ArrayList<Spoj>();
		
		HtmlForm formular = strankaSpoje.getFormByName(Config.NAME_SPOJE_FORM_INFO_PREFIX + 0 + Config.NAME_SPOJE_FORM_INFO_SUFFIX);
	}
}