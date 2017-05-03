package com.ondrejhrusovsky.aplikacia;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ondrejhrusovsky.formulare.FormularVyhladatSpojenie;

public class Vyhladavanie
{
	private String odkial;
	private String kam;
	private String cez;
	private String datum;
	private String cas;	
	
	private String vychodziaStanica;
	private String cielovaStanica;
		
	private FormularVyhladatSpojenie formularVyhladatSpojenie;
	private HtmlPage strankaSpoje;
	private List<Spoj> spoje;
	
	private WebClient webClient;
	
	public Vyhladavanie(String odkial, String kam, String cez, String datum, String cas) throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		this.odkial = odkial;
		this.kam = kam;
		this.cez = cez;
		this.datum = datum;
		this.cas = cas;
		
		// Specialny pripad, bratislava hl.st. moze byt nazvana aj bratislava
		if(this.odkial.toLowerCase().equals("bratislava"))
		{
			this.odkial = "Bratislava hl.st.";
		}
				
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setJavaScriptEnabled(false);	
		HtmlPage strankaVyhladanieSpoja = webClient.getPage("https://ikvc.slovakrail.sk/mobile-sales-web/pages/connection/searchParam.xhtml");
		formularVyhladatSpojenie = new FormularVyhladatSpojenie(strankaVyhladanieSpoja.getFormByName("connectionParam"));
		formularVyhladatSpojenie.nastavOdkial(this.odkial);
		formularVyhladatSpojenie.nastavKam(this.kam);
		formularVyhladatSpojenie.nastavCas(this.cez);
		formularVyhladatSpojenie.nastavDatum(this.datum);
		formularVyhladatSpojenie.nastavCas(this.cas);
		
		nacitajStrankuSpoje();
	}
	
	public void nacitajStrankuSpoje() throws IOException
	{
		HtmlPage strankaVyhladanieSpoja2 = formularVyhladatSpojenie.posli();
		
		try 
		{
			// Predpokladame, ze stranka skusila spravit autocorrect nepresneho nazvu stanice
			FormularVyhladatSpojenie formularVyhladatSpojenie2 = new FormularVyhladatSpojenie(strankaVyhladanieSpoja2.getFormByName("connectionParam")); 						
			strankaSpoje = formularVyhladatSpojenie2.posli();	
			
		}
		catch (ElementNotFoundException e)
		{
			// Nazov bol presny
			strankaSpoje = strankaVyhladanieSpoja2;
		}
			
		parsujSpoje();
	}
	
	private void parsujSpoje() throws IOException
	{		
		// Nacitaj vychodziu a cielovu stanicu
		DomElement zaciatokKoniecElem = (DomElement) strankaSpoje.getFirstByXPath("//div[@class='searched_connections']/div[@class='spoj']");
		
		//System.out.println(strankaSpoje.asText());
		
		String[] zaciatokKoniec = zaciatokKoniecElem.asText().split("/");
		vychodziaStanica = zaciatokKoniec[0].trim();
		cielovaStanica = zaciatokKoniec[1].trim();
		
		// Nacitaj spoje
		List<Object> elementySpojov = strankaSpoje.getByXPath("//div[@class='searched_connections']/div[@class='i']");
		spoje = new ArrayList<Spoj>();
				
		for(Object ch : elementySpojov)
		{
			spoje.add(new Spoj(this, (DomElement) ch, spoje.size()));
		}		
	}
	
	public List<Spoj> ziskajSpoje()
	{
		return spoje;
	}

	public String ziskajOdkial()
	{
		return odkial;
	}

	public String ziskajKam()
	{
		return kam;
	}

	public String ziskajCez()
	{
		return cez;
	}

	public String ziskajDatum()
	{
		return datum;
	}

	public String ziskajCas()
	{
		return cas;
	}
	
	public String ziskajVychodziaStanica()
	{
		return vychodziaStanica;
	}
	
	public String ziskajCielovaStanica()
	{
		return cielovaStanica;
	}
	
	public WebClient ziskajWebClienta()
	{
		return webClient;
	}
}
