package com.ondrejhrusovsky.aplikacia;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.ondrejhrusovsky.formulare.FormularInformaciaOTrase;
import com.ondrejhrusovsky.formulare.FormularNakup;
import com.ondrejhrusovsky.aplikacia.WebData;

public class Spoj
{
	private Vyhladavanie vyhladavanie; // Vyhladavanie, ktoreho sucastou je tento spoj
	
	private ArrayList<Vlak> vlaky;
	private int trvanieHod; // dlzka cestovania (pocet hodin)
	private int trvanieMin; // dlzka cestovania (pocet minut - bez hodin)
	
	private ArrayList<Usek> volneUseky; // Naplnene po volani funkcie nacitajUsekySVolnymKontingentom()
	private ArrayList<Usek> chybneUseky;
	
	private HtmlPage strankaSpoje;
	private int idSpoja;
	
	public Spoj(Vyhladavanie vyhladavanie, DomElement elementSpoja, int idSpoja) throws IOException, InaChybaException, ChybajuciElementException, ZlyFormatElementuException
	{
		this.vyhladavanie = vyhladavanie;
		this.idSpoja = idSpoja;
			
		// Nacitaj formular informacie o trase
		strankaSpoje = elementSpoja.getHtmlPageOrNull();
		
		if(strankaSpoje == null)
		{
			throw new InaChybaException("Spoju sa nepodarilo ziskat stranku spoje");
		}
		
		String nazovFormInfo = WebData.NAME_SPOJE_FORM_INFO_PREFIX + idSpoja + WebData.NAME_SPOJE_FORM_INFO_SUFFIX;
		
		FormularInformaciaOTrase formularInformaciaOTrase = null;
		
		try
		{
			formularInformaciaOTrase = new FormularInformaciaOTrase(strankaSpoje.getFormByName(nazovFormInfo));
			
		}
		catch (ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Element form info o trase (" + nazovFormInfo + ") sa nenasiel");
		}
		
		HtmlPage strankaInformacieOTrase = formularInformaciaOTrase.posli();
		
		// Nacitaj vlaky
		List<Object> h2Elementy = elementSpoja.getByXPath(WebData.XPATH_SPOJE_NAZVYVLAKOV);
		vlaky = new ArrayList<Vlak>();
		
		if(h2Elementy.size() > 0)
		{
			for(Object e : h2Elementy)
			{
				int idxNovehoVlaku = vlaky.size();
				
				List<Object> elementInfo = strankaInformacieOTrase.getByXPath(WebData.XPATH_INFO_BLOKZOZNAMZASTAVOK);
				
				if(elementInfo.size() > idxNovehoVlaku)
				{
					vlaky.add(new Vlak(this, elementSpoja, (DomElement) elementInfo.get(idxNovehoVlaku), idxNovehoVlaku));
				}
				else
				{
					throw new ChybajuciElementException("Nenasiel sa zoznam zastavok (" + WebData.XPATH_INFO_BLOKZOZNAMZASTAVOK + ") pre vlak s indexom " + idxNovehoVlaku);
				}
			}
		}
		else
		{
			throw new ChybajuciElementException("Nenasli sa h2 (" + WebData.XPATH_SPOJE_NAZVYVLAKOV + ") elementy (vlaky)");
		}

		// Nacitaj trvanie
		DomElement trvanieElem = (DomElement) elementSpoja.getFirstByXPath(WebData.XPATH_SPOJE_TRVANIE);			
		
		if(trvanieElem == null)
		{
			throw new ChybajuciElementException("Element trvanie (" + WebData.XPATH_SPOJE_TRVANIE + ") sa nenasiel");
		}
		
		String[] minHod = trvanieElem.asText().replace("min", "").split("hod");

		if(trvanieElem.asText().contains("hod"))
		{
			trvanieHod = Integer.parseInt(minHod[0].trim());
		}
		else
		{
			trvanieHod = 0;
		}
		
		if(trvanieElem.asText().contains("min"))
		{
			if(trvanieElem.asText().contains("hod"))
			{
				trvanieMin = Integer.parseInt(minHod[1].trim());
			}
			else
			{
				trvanieMin = Integer.parseInt(minHod[0].trim());
			}			
		}
		else
		{
			trvanieMin = 0;
		}
		
		if((trvanieHod == 0 && trvanieMin == 0) || minHod.length == 0)
		{
			throw new ZlyFormatElementuException("Trvanie je 0 min a 0 hod alebo minHod ma dlzku 0");
		}
	}
	
	public void spustiHladanieVolnehoKontingentu() throws ZlyFormatElementuException, InaChybaException
	{		
		ArrayList<Zastavka> vsetkyZastavky = ziskajVsetkyZastavky();
		ArrayList<KontingentThread> kontingentThready = new ArrayList<KontingentThread>();
		volneUseky = new ArrayList<Usek>();
		chybneUseky = new ArrayList<Usek>();
		
		if(ziskajVsetkyZastavky().size() == 0)
		{
			throw new InaChybaException("Nenasli sa zastavky");
		}
		
		int poslednyOdchodHodina = 0;
		int poslednyOdchodMinuta = 0;
		
		String datumOdchodu = ziskajVyhladavanie().ziskajDatum();
		
		for(int i = 0; i < vsetkyZastavky.size() - 1; i++)
		{		
			String cas = vsetkyZastavky.get(i).ziskajCasOdchodu();
			
			if(!cas.contains(":"))
			{
				throw new ZlyFormatElementuException("Cas neobsahuje \":\"");
			}
			
			final int odchodHodina = Integer.parseInt(cas.split(":")[0]);
			final int odchodMinuta = Integer.parseInt(cas.split(":")[1]);
			
			if(!datumOdchodu.contains("."))
			{
				throw new ZlyFormatElementuException("Datum neobsahuje \"\\.\"");
			}
			
			final int odchodRok = Integer.parseInt(datumOdchodu.split("\\.")[2]);
			final int odchodMesiac = Integer.parseInt(datumOdchodu.split("\\.")[1]);
			final int odchodDen = Integer.parseInt(datumOdchodu.split("\\.")[0]);	
			
			Calendar odchodDatum = new GregorianCalendar(odchodRok, odchodMesiac, odchodDen);			
			// Kontrola prechodu cez polnoc
			// Problem by bol, ak by vlak nezastavil v ziadnej stanici behom 24 hodin a presiel tak za jeden usek cez polnoc 2x
			if(poslednyOdchodHodina * 60 + poslednyOdchodMinuta > odchodHodina * 60 + odchodMinuta)
			{
				odchodDatum.add(Calendar.DAY_OF_MONTH, 1);
			}
				
			poslednyOdchodHodina = odchodHodina;
			poslednyOdchodMinuta = odchodMinuta;
			
			datumOdchodu = odchodDatum.get(Calendar.DAY_OF_MONTH) + "." + odchodDatum.get(Calendar.MONTH) + "." + odchodDatum.get(Calendar.YEAR);
			
			// @todo Optimalizovat: pre osobaky nemusime thread vobec vytvarat
			KontingentThread kt = new KontingentThread(this, vsetkyZastavky.get(i), vsetkyZastavky.get(i+1), "", datumOdchodu, cas);
			kontingentThready.add(kt);
			kt.start();				
		}
											
		for(KontingentThread kt : kontingentThready)
		{
			try {
				kt.join();
			} catch (InterruptedException e) {
				System.out.println("Vlakno " + kt.toString() + " - " + kt.toString() + " interrupted");
				pridajChybnyUsek(new Usek(kt.ziskajOdkial(), kt.ziskajKam()), kt);				
			}
		}
	}
	
	public boolean jeKontingentVycerpany() throws NemoznoZakupitListokException, IOException, ChybajuciElementException
	{
		// Ak je tento spoj zlozeny len z jedneho osobaku, tak urcite ma volny kontingent
		if(ziskajVlaky().size() == 1 && ziskajVlaky().get(0).ziskajTriedu().equals("Os"))
		{
			return false;
		}
		
		strankaSpoje.refresh();
		
		FormularNakup formularNakup = null;
				
		// Nacitaj formular informacie o trase
		String nazovFormNakup = WebData.NAME_SPOJE_FORM_NAKUP_PREFIX + idSpoja + WebData.NAME_SPOJE_FORM_NAKUP_SUFFIX;
		try
		{
			formularNakup = new FormularNakup(strankaSpoje.getFormByName(nazovFormNakup));
		}
		catch(ElementNotFoundException e)
		{
			throw new NemoznoZakupitListokException("Nebol najdeny formular Nakup (spoj zrejme nema tlacidlo Nakup)");
		}		
		
		//Budeme potrebovat javascript pre zobrazenie "Bezplatne" checkboxu
		ziskajVyhladavanie().ziskajWebClienta().getOptions().setJavaScriptEnabled(true);
		
		HtmlPage strankaNakup = formularNakup.posli();		
		HtmlSelect rezervaciaElement = null;
		HtmlOption listokOption = null;
		
		// Nechceme zakupit aj miestenku
				
		try
		{
			rezervaciaElement = (HtmlSelect) strankaNakup.getElementByName(WebData.NAME_NAKUP_SELECT_REZERVACIA);
		}
		catch(ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Element rezervacia (" + WebData.NAME_NAKUP_SELECT_REZERVACIA + ") sa nenasiel");
		}
		
		try
		{
			listokOption = rezervaciaElement.getOptionByValue(WebData.NAME_NAKUP_OPTION_LISTOK);
		}
		catch (ElementNotFoundException e)
		{
			throw new NemoznoZakupitListokException("V ponuke dostupnych moznosti zakupenia sa nenachadza option (" + WebData.NAME_NAKUP_OPTION_LISTOK + ") Listok");		
		}
		
		strankaNakup = rezervaciaElement.setSelectedAttribute(listokOption, true);
		
		ziskajVyhladavanie().ziskajWebClienta().waitForBackgroundJavaScript(10000); 
		
		HtmlSelect typCestujucehoElement = null;
		HtmlOption studentOption = null;
		
		// Nastavime studenta
		try
		{
			typCestujucehoElement = (HtmlSelect) strankaNakup.getElementByName(WebData.NAME_NAKUP_SELECT_TYPCESTUJUCEHO);
		}
		catch(ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Element typ cestujuceho (" + WebData.NAME_NAKUP_SELECT_TYPCESTUJUCEHO + ") sa nenasiel");
		}
		
		try
		{
			studentOption = typCestujucehoElement.getOptionByValue(WebData.NAME_NAKUP_OPTION_STUDENT);
		}
		catch(ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Element option student (" + WebData.NAME_NAKUP_OPTION_STUDENT + ") sa nenasiel");
		}
		
		strankaNakup = typCestujucehoElement.setSelectedAttribute(studentOption, true);

		ziskajVyhladavanie().ziskajWebClienta().waitForBackgroundJavaScript(10000);   
		
		// Nasledujuca stranka, po odoslani formulara
		
		HtmlForm nakupForm = null;
		
		try
		{
			nakupForm = strankaNakup.getFormByName(WebData.NAME_NAKUP_FORM_NAKUP);
		}
		catch(ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Element form nakup (" + WebData.NAME_NAKUP_OPTION_STUDENT + ") sa nenasiel");
		}	
		
        HtmlPage vysledok = null;
        
        try
        {
        	vysledok = nakupForm.getInputByName(WebData.NAME_NAKUP_FORM_NAKUP_SUBMIT).click();
        }
        catch(ElementNotFoundException e)
        {
        	throw new ChybajuciElementException("Element potvrd nakup (" + WebData.NAME_NAKUP_FORM_NAKUP_SUBMIT + ") sa nenasiel");
        }
        
        // Nepodarilo sa zakupit listok z nezisteneho dovodu (divoka kombinacia spojov rozneho typu a podobne)
        if(!vysledok.asText().contains("Registraèné èíslo") && !vysledok.asText().contains("Vyèerpaný kontingent!"))
        {
        	throw new NemoznoZakupitListokException("Pri pokuse o zakupenie listka sme dostali neocakavany vysledok");
        }
		
        final boolean bVycerpany = vysledok.asText().contains("Vyèerpaný kontingent!");
        
        if(!bVycerpany)
        {
        	// Slusne zrusime nakup (ak by sme to nespravili, zrejme by sme blokovali listok az po nejaky timeout)
        	HtmlForm zrusNakupForm = null;
        	
        	try
        	{
        		zrusNakupForm = vysledok.getFormByName(WebData.NAME_POKLADNA_FORM_ZRUSNAKUP);
        	}
        	catch(ElementNotFoundException e)
        	{
        		throw new ChybajuciElementException("Element zrus nakup form (" + WebData.NAME_POKLADNA_FORM_ZRUSNAKUP + ") sa nenasiel");
        	}
        	
        	try
        	{
        		zrusNakupForm.getInputByName(WebData.NAME_POKLADNA_FORM_ZRUSNAKUP_SUBMIT).click();
        	}
        	catch(ElementNotFoundException e)
        	{
        		throw new ChybajuciElementException("Element submit zrus nakup (" + WebData.NAME_POKLADNA_FORM_ZRUSNAKUP_SUBMIT + ") sa nenasiel");
        	}
        }
        
        // Uz javascript nepotrebujeme
        ziskajVyhladavanie().ziskajWebClienta().getOptions().setJavaScriptEnabled(false);
		
		return bVycerpany;
	}
	
	public Vyhladavanie ziskajVyhladavanie()
	{
		return vyhladavanie;
	}

	public ArrayList<Vlak> ziskajVlaky()
	{
		return vlaky;
	}

	public int ziskajTrvanieHod()
	{
		return trvanieHod;
	}

	public int ziskajTrvanieMin()
	{
		return trvanieMin;
	}
	
	public ArrayList<Zastavka> ziskajVsetkyZastavky()
	{
		ArrayList<Zastavka> vsetkyZastavky = new ArrayList<>();		
		
		for(Vlak v : vlaky)
		{
			ArrayList<Zastavka> dalsieZastavky = v.ziskajZastavky();
			if(vsetkyZastavky.size() > 0)
			{
				Zastavka posledna = vsetkyZastavky.get(vsetkyZastavky.size() - 1);
				Zastavka dalsia = dalsieZastavky.get(0);
				
				if(posledna.ziskajNazov().equals(dalsia.ziskajNazov()))
				{
					posledna.nastavCasOdchodu(dalsia.ziskajCasOdchodu());
					dalsieZastavky.remove(0);
				}
			}
			
			vsetkyZastavky.addAll(dalsieZastavky);
		}
		
		return vsetkyZastavky;
	}
	
	public synchronized void pridajVolnyUsek(Usek usek, KontingentThread thread)
	{
		volneUseky.add(usek);
		volneUseky = spojUseky(volneUseky);
	}
	
	public synchronized void pridajChybnyUsek(Usek usek, KontingentThread thread)
	{
		chybneUseky.add(usek);
		chybneUseky = spojUseky(chybneUseky);
	}
	
	public ArrayList<Usek> ziskajVolneUseky()
	{
		return volneUseky;
	}
	
	public ArrayList<Usek> ziskajChybneUseky()
	{
		return volneUseky;
	}
	
	public ArrayList<Usek> spojUseky(ArrayList<Usek> usekyNaSpojenie)
	{
		if(usekyNaSpojenie.size() == 0 || usekyNaSpojenie.size() == 1)
		{
            return usekyNaSpojenie;
		}
		
		ArrayList<Zastavka> vsetkyZastavky = ziskajVsetkyZastavky();

        Collections.sort(usekyNaSpojenie, new UsekComparator(vsetkyZastavky));

        Usek first = usekyNaSpojenie.get(0);
        Zastavka start = first.zac;
        Zastavka end = first.kon;

        ArrayList<Usek> result = new ArrayList<Usek>();

        for (int i = 1; i < usekyNaSpojenie.size(); i++)
        {
            Usek current = usekyNaSpojenie.get(i);
            if (vsetkyZastavky.indexOf(current.zac) <= vsetkyZastavky.indexOf(end))
            {
                end = vsetkyZastavky.get(Math.max(vsetkyZastavky.indexOf(current.kon), vsetkyZastavky.indexOf(end)));
            } 
            else
            {
                result.add(new Usek(start, end));
                start = current.zac;
                end = current.kon;
            }
        }

        result.add(new Usek(start, end));
        return result;
	}
}

class UsekComparator implements Comparator<Usek>
{
	private ArrayList<Zastavka> vsetkyZastavky;
	
	public UsekComparator(ArrayList<Zastavka> vsetkyZastavky)
	{
		this.vsetkyZastavky = vsetkyZastavky;
	}
	
    public int compare(Usek u1, Usek u2)
    {
        return vsetkyZastavky.indexOf(u1.zac) - vsetkyZastavky.indexOf(u2.zac);
    }
}
