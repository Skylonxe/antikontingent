package com.ondrejhrusovsky.ikvcAPI;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.exceptions.InaChybaException;
import com.ondrejhrusovsky.exceptions.NepodariloSaNajstSpojeException;
import com.ondrejhrusovsky.exceptions.ZlyFormatElementuException;
import com.ondrejhrusovsky.formulare.FormularVyhladatSpojenie;
import com.ondrejhrusovsky.formulare.FormularVyhladatSpojenieAutocorrect;

public class Vyhladavanie
{
	static final TreeMap<String, String> SYNONYMA_NAZVOV_STANIC = vytvorSynonymaNazvovStanic();
	
	private static TreeMap<String, String> vytvorSynonymaNazvovStanic()
	{
		TreeMap<String, String> mapa = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		
		mapa.put("Bratislava", "Bratislava hl.st");
		mapa.put("Poprad", "Poprad-Tatry");
		mapa.put("Zvolen", "Zvolen os.st.");
		mapa.put("Praha", "Praha hl.n.");
		mapa.put("Berlin", "Berlin HBF (Tief)");
		mapa.put("Wien", "Wien Hbf");
		
		// Tento zoznam nie je kompletny
		// Slusi hlavne na to, aby si vlak vedel spravne najst vystupnu a nastupnu stanicu
		// V pripade, ze sa mu to nepodari, tak sa pokusi tento problem vyriesit
		
		return mapa;
	}
	
	public String odkial;
	public String kam;
	public String cez;
	public String datum;
	public String cas;	
	
	public String vychodziaStanica;
	public String cielovaStanica;
		
	public ArrayList<Spoj> spoje;
	
	private WebClient webClient;

	public Vyhladavanie(String odkial, String kam, String cez, String datum, String cas) throws FailingHttpStatusCodeException, MalformedURLException, IOException, NepodariloSaNajstSpojeException, ChybajuciElementException, ZlyFormatElementuException, InaChybaException
	{
		if(SYNONYMA_NAZVOV_STANIC.containsKey(odkial))
		{
			odkial = SYNONYMA_NAZVOV_STANIC.get(odkial);
		}
		if(SYNONYMA_NAZVOV_STANIC.containsKey(kam))
		{
			kam = SYNONYMA_NAZVOV_STANIC.get(kam);
		}
		if(SYNONYMA_NAZVOV_STANIC.containsKey(cez))
		{
			cez = SYNONYMA_NAZVOV_STANIC.get(cez);
		}
		
		this.odkial = odkial;
		this.kam = kam;
		this.cez = cez;
		this.datum = datum;
		this.cas = cas;	
		
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		webClient = new WebClient(Config.VerziaPrehliadaca);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
			
		FormularVyhladatSpojenie form = nacitajFormularVyhladatSpojenie();
		HtmlPage strankaSpoje = nacitajStrankuSpoje(form);
		parsujSpoje(strankaSpoje);
	}
	
	public FormularVyhladatSpojenie nacitajFormularVyhladatSpojenie() throws FailingHttpStatusCodeException, MalformedURLException, IOException, ChybajuciElementException
	{
		HtmlPage strankaVyhladanieSpoja = webClient.getPage(Config.LINK_VYHLADAVANIE);
		HtmlForm formularVyhladatSpojenieElem = null;
		
		try
		{
			formularVyhladatSpojenieElem = strankaVyhladanieSpoja.getFormByName(Config.NAME_VYHLADAVANIE_FORM);
		}
		catch(ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Element formularVyhladatSpojenie (" + Config.NAME_VYHLADAVANIE_FORM + ") sa nenasiel");
		}
		
		FormularVyhladatSpojenie formularVyhladatSpojenie = null;
		
		formularVyhladatSpojenie = new FormularVyhladatSpojenie(formularVyhladatSpojenieElem);		
		formularVyhladatSpojenie.nastavOdkial(this.odkial);
		formularVyhladatSpojenie.nastavKam(this.kam);
		formularVyhladatSpojenie.nastavCez(this.cez);
		formularVyhladatSpojenie.nastavDatum(this.datum);
		formularVyhladatSpojenie.nastavCas(this.cas);
		
		return formularVyhladatSpojenie;
	}

	public HtmlPage nacitajStrankuSpoje(FormularVyhladatSpojenie formularVyhladatSpojenie) throws IOException, NepodariloSaNajstSpojeException, ChybajuciElementException
	{
		HtmlPage nasledujucaStranka = formularVyhladatSpojenie.posli();

		try 
		{
			// Predpokladame, ze stranka skusila spravit autocorrect nepresneho nazvu stanice
			HtmlForm form = nasledujucaStranka.getFormByName(Config.NAME_VYHLADAVANIE_FORM);	
			FormularVyhladatSpojenieAutocorrect vyplnenyForm = new FormularVyhladatSpojenieAutocorrect(form);
			nasledujucaStranka = vyplnenyForm.posli();	
		}
		catch (ElementNotFoundException e)
		{
			// Je to ok, formular tam nemusi byt, ak udaje boli spravne
		}

		if(!nasledujucaStranka.asText().contains("Nasledujúce"))
		{
			throw new NepodariloSaNajstSpojeException();
		}
		
		return nasledujucaStranka;
	}
	
	private void parsujSpoje(HtmlPage strankaSpoje) throws IOException, ChybajuciElementException, ZlyFormatElementuException, InaChybaException
	{		
		// Nacitaj vychodziu a cielovu stanicu
		DomElement zaciatokKoniecElem = (DomElement) strankaSpoje.getFirstByXPath(Config.ELEM_SPOJE_ZACIATOKKONIEC);
		
		if(zaciatokKoniecElem != null)
		{		
			if(zaciatokKoniecElem.asText().contains("/"))
			{
				String[] zaciatokKoniec = zaciatokKoniecElem.asText().split("/");
				
				if(zaciatokKoniec.length == 2)
				{
					vychodziaStanica = zaciatokKoniec[0].trim();
					cielovaStanica = zaciatokKoniec[1].trim();
				}
				else
				{
					throw new ZlyFormatElementuException("Element zaciatok/koniec (" + Config.ELEM_SPOJE_ZACIATOKKONIEC + ") po rozdeleni nema dva prvky");
				}
			}
			else
			{
				throw new ZlyFormatElementuException("Element zaciatok/koniec (" + Config.ELEM_SPOJE_ZACIATOKKONIEC + ") neobsahuje znak \"/\"");
			}		
		}
		else
		{
			throw new ChybajuciElementException("Element zaciatok/koniec (" + Config.ELEM_SPOJE_ZACIATOKKONIEC + ") sa nenasiel");
		}	
		
		// Nacitaj spoje
		List<Object> elementySpojov = strankaSpoje.getByXPath(Config.ELEM_SPOJE_SPOJ);
		spoje = new ArrayList<Spoj>();
		
		if(elementySpojov.size() > 0)
		{
			for(Object ch : elementySpojov)
			{
				spoje.add(new Spoj(this, (DomElement) ch, spoje.size()));
			}
		}
		else
		{
			throw new ChybajuciElementException("Elementy spojov (" + Config.ELEM_SPOJE_SPOJ + ") sa nenasli");
		}	
	}
	
	public WebClient ziskajWebClienta()
	{
		return webClient;
	}
}
