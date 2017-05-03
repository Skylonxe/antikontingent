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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.ondrejhrusovsky.formulare.FormularInformaciaOTrase;
import com.ondrejhrusovsky.formulare.FormularNakup;

public class Spoj
{
	private Vyhladavanie vyhladavanie; // Vyhladavanie, ktoreho sucastou je tento spoj
	
	private ArrayList<Vlak> vlaky;
	private int trvanieHod; // dlzka cestovania (pocet hodin)
	private int trvanieMin; // dlzka cestovania (pocet minut - bez hodin)
	
	private FormularInformaciaOTrase formularInformaciaOTrase;
	private FormularNakup formularNakup; // Moze byt null, ak chyba tlacidlo Nakup a teda listky sa  na tento spoj nedaju zakupit
	
	private ArrayList<Usek> volneUseky; // Naplnene po volani funkcie nacitajUsekySVolnymKontingentom()
	
	public Spoj(Vyhladavanie vyhladavanie, DomElement elementSpoja, int idSpoja) throws IOException
	{
		this.vyhladavanie = vyhladavanie;
		
		// Nacitaj formular informacie o trase
		HtmlPage strankaSpoje = elementSpoja.getHtmlPageOrNull();
		formularInformaciaOTrase = new FormularInformaciaOTrase(strankaSpoje.getFormByName("j_idt89:" + idSpoja + ":j_idt131"));
		
		try
		{
			formularNakup = new FormularNakup(strankaSpoje.getFormByName("j_idt89:" + idSpoja + ":j_idt144"));
		}
		catch(ElementNotFoundException e)
		{
			formularNakup = null;
			// Na tento spoj sa neda zakupit cestovny listok (chyba tlacidlo Nakup)
		}	
		
		HtmlPage strankaInformacieOTrase = formularInformaciaOTrase.posli();
		
		// Nacitaj vlaky
		List<Object> h2Elementy = elementSpoja.getByXPath("h2");
		vlaky = new ArrayList<Vlak>();

		for(Object e : h2Elementy)
		{
			int idxNovehoVlaku = vlaky.size();
			
			DomElement elementInfo = (DomElement) strankaInformacieOTrase.getByXPath("//div[@class='block']").get(idxNovehoVlaku);
			vlaky.add(new Vlak(this, elementSpoja, elementInfo, idxNovehoVlaku));
		}
		
		// Nacitaj trvanie
		DomElement trvanieElem = (DomElement) elementSpoja.getFirstByXPath("div[@class='info']/p/strong");			
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
	}
	
	public void spustiHladanieVolnehoKontingentu()
	{		
		ArrayList<Zastavka> vsetkyZastavky = ziskajVsetkyZastavky();
		ArrayList<KontingentThread> kontingentThready = new ArrayList<KontingentThread>();
		volneUseky = new ArrayList<Usek>();
		
		int poslednyOdchodHodina = 0;
		int poslednyOdchodMinuta = 0;
		
		String datumOdchodu = ziskajVyhladavanie().ziskajDatum();
		
		for(int i = 0; i < vsetkyZastavky.size() - 1; i++)
		{		
			String cas = vsetkyZastavky.get(i).ziskajCasOdchodu();
			
			final int odchodHodina = Integer.parseInt(cas.split(":")[0]);
			final int odchodMinuta = Integer.parseInt(cas.split(":")[1]);
			
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
			}
		}
	}
	
	public boolean jeKontingentVycerpany() throws NemoznoZakupitListokException, IOException, ElementNotFoundException
	{
		ziskajVyhladavanie().ziskajWebClienta().getOptions().setJavaScriptEnabled(true);
		
		if(ziskajFormularNakup() == null)
		{
			throw new NemoznoZakupitListokException();
		}
		
		HtmlPage strankaNakup = ziskajFormularNakup().posli();
		
		// Nechceme zakupit aj miestenku
		HtmlSelect rezervaciaElement = (HtmlSelect) strankaNakup.getElementByName("j_idt92:j_idt94");
		strankaNakup = rezervaciaElement.setSelectedAttribute(rezervaciaElement.getOptionByValue("1"), true);
		
		ziskajVyhladavanie().ziskajWebClienta().waitForBackgroundJavaScript(10000); 
		
		// Nastavime studenta
		HtmlSelect typCestujucehoElement = (HtmlSelect) strankaNakup.getElementById("ticketParam:j_idt112:1:j_idt114");
		strankaNakup = typCestujucehoElement.setSelectedAttribute(typCestujucehoElement.getOptionByValue("2"), true);

		ziskajVyhladavanie().ziskajWebClienta().waitForBackgroundJavaScript(10000);   
		
		// Nasledujuca stranka, po odoslani formulara
        HtmlPage vysledok = strankaNakup.getFormByName("ticketParam").getInputByName("ticketParam:j_idt333").click();
		
        final boolean bVycerpany = vysledok.asText().contains("Vyèerpaný kontingent!");
        
        if(vysledok.asText().contains("Registraèné èíslo"))
        {
        	// Slusne zrusime nakup
			vysledok = vysledok.getFormByName("cancelShoppingCartForm").getInputByName("cancelShoppingCartForm:submit").click();
        }
        
        ziskajVyhladavanie().ziskajWebClienta().getOptions().setJavaScriptEnabled(false);
		
		return bVycerpany;
	}
	
	public Vyhladavanie ziskajVyhladavanie()
	{
		return vyhladavanie;
	}

	public FormularInformaciaOTrase ziskajFormularInformaciaOTrase()
	{
		return formularInformaciaOTrase;
	}

	public FormularNakup ziskajFormularNakup()
	{
		return formularNakup;
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
			vsetkyZastavky.addAll(v.ziskajZastavky());
		}
		
		return vsetkyZastavky;
	}
	
	public synchronized void pridajVolnyUsek(Usek usek, KontingentThread thread)
	{
		volneUseky.add(usek);
		spojVolneUseky();
	}
	
	public ArrayList<Usek> ziskajVolneUseky()
	{
		return volneUseky;
	}
	
	public void spojVolneUseky()
	{
		if(ziskajVolneUseky().size() == 0 || ziskajVolneUseky().size() == 1)
		{
            return;
		}
		
		ArrayList<Zastavka> vsetkyZastavky = ziskajVsetkyZastavky();

        Collections.sort(volneUseky, new UsekComparator(vsetkyZastavky));

        Usek first = ziskajVolneUseky().get(0);
        Zastavka start = first.zac;
        Zastavka end = first.kon;

        ArrayList<Usek> result = new ArrayList<Usek>();

        for (int i = 1; i < ziskajVolneUseky().size(); i++)
        {
            Usek current = ziskajVolneUseky().get(i);
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
        volneUseky = result;
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
