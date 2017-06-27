package com.ondrejhrusovsky.ikvcAPI;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.exceptions.ZlyFormatElementuException;

public class Vlak
{	
	private Spoj spoj; // Spoj, do ktoreho patri tento vlak
	
	public String meno; // Nazov vlaku (napr. "R 613 MARLENKA")
	public ArrayList<Zastavka> zastavky = new ArrayList<>();
	public String poznamka; // Dodatocne informacie vlaku

	public Zastavka nastupnaStanica; 
	public String nastupnyDatum;
	public String nastupnyCas;
	
	public Zastavka vystupnaStanica; 
	public String vystupnyDatum;
	public String vystupnyCas;
	
	public Vlak(Spoj spoj, DomElement elementSpoja, DomElement elementInfo, int idVlaku) throws ChybajuciElementException, ZlyFormatElementuException
	{
		this.spoj = spoj;
		
		// Nacitaj meno
		List<Object> h2Elementy = elementSpoja.getByXPath(Config.XPATH_SPOJE_NAZVYVLAKOV);
		
		if(h2Elementy.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element nazov vlaku (" + Config.XPATH_SPOJE_NAZVYVLAKOV + ") sa nenasiel");
		}
		
		meno = ((DomElement) h2Elementy.get(idVlaku)).asText().trim();
		
		// Nacitaj zastavky
		DomElement zoznamStanicElement = (DomElement) elementInfo.getFirstByXPath(Config.XPATH_INFO_ZOZNAMZASTAVOK);
		
		if(zoznamStanicElement == null)
		{
			throw new ChybajuciElementException("Element zoznam zastavok (" + Config.XPATH_INFO_ZOZNAMZASTAVOK + ") sa nenasiel");
		}
		
		for(DomElement riadok : zoznamStanicElement.getChildElements())
		{	
			DomElement nazovVzdialenostZastavkyElement = (DomElement) riadok.getFirstByXPath(Config.XPATH_INFO_NAZOVVZDIALENOST);
			
			if(nazovVzdialenostZastavkyElement == null)
			{
				throw new ChybajuciElementException("Element nazov zastavky (" + Config.XPATH_INFO_NAZOVVZDIALENOST + ") sa nenasiel");
			}
			
			if(!nazovVzdialenostZastavkyElement.asText().contains(",") || nazovVzdialenostZastavkyElement.asText().split(",").length != 2)
			{
				throw new ZlyFormatElementuException("Element nazov a vzdialenost (" + Config.XPATH_INFO_NAZOVVZDIALENOST + ") zastavky ma zly format");
			}
			
			String nazovZastavky = nazovVzdialenostZastavkyElement.asText().split(",")[0].trim();
			
			if(!nazovVzdialenostZastavkyElement.asText().split(",")[1].contains("km"))
			{
				throw new ZlyFormatElementuException("Element nazov a vzdialenost nema km");
			}
			
			int vzdialenostZastavky = Integer.parseInt(nazovVzdialenostZastavkyElement.asText().split(",")[1].replace("km", "").trim());
			
			DomElement cas1Element = (DomElement) riadok.getFirstByXPath(Config.XPATH_INFO_CAS1);
			DomElement cas2Element = (DomElement) riadok.getFirstByXPath(Config.XPATH_INFO_CAS2);
			String casPrichoduZastavky = "";
			String casOdchoduZastavky = "";
			
			if(cas2Element == null)
			{
				if(zastavky.size() > 0)
				{
					casPrichoduZastavky = cas1Element.asText().trim().split(" ")[1];	
				}
				else
				{
					casOdchoduZastavky = cas1Element.asText().trim().split(" ")[1];	
				}
			}
			else
			{
				casPrichoduZastavky = cas1Element.asText().trim();	
				casOdchoduZastavky = cas2Element.asText().trim();	
			}	
						
			Zastavka novaZastavka = new Zastavka(nazovZastavky, vzdialenostZastavky, casOdchoduZastavky, casPrichoduZastavky);
			
			if(!riadok.hasAttribute(Config.ATTRIBUTE_INFO_NASTUPNAVYSTUPNA))
			{
				if(nastupnaStanica == null)
				{
					nastupnaStanica = novaZastavka;
				}
				else
				{
					vystupnaStanica = novaZastavka;
				}
			}	
			
			// POZOR! Niektore vlaky (napr. vybrane EC z BRATISLAVY do PRAHY) maju zle nastavene vystupne a nastupne stanice v zozname zastavok
			// !!! Pre istotu sa snazime toto zistenie prepisat / overit neskor
			if(!riadok.hasAttribute(Config.ATTRIBUTE_INFO_NASTUPNAVYSTUPNA))
			{
				if(nastupnaStanica == null)
				{
					nastupnaStanica = novaZastavka;
				}
				else
				{
					vystupnaStanica = novaZastavka;
				}
			}	
						
			zastavky.add(novaZastavka);
		}
			
		// Nacitaj poznamku
		DomElement poznamkaElement = (DomElement) elementInfo.getFirstByXPath(Config.XPATH_INFO_POZNAMKA);
		
		if(poznamkaElement == null)
		{
			throw new ChybajuciElementException("Element poznamka (" + Config.XPATH_INFO_POZNAMKA + ") sa nenasiel");
		}
		
		poznamka = (poznamkaElement).asText().trim();	
		
		// Znova nacitaj nastupnu stanicu
		// Pre istotu skusame nacitat, ci existuje informacia o prestupe
		List<Object> nastupnaStanicaElement = elementSpoja.getByXPath(Config.XPATH_SPOJE_PRESTUP);
		if(!nastupnaStanicaElement.isEmpty())
		{
			// Tato informacia je vzdy spravna, takze prepiseme aktualnu stanicu
			
			if(nastupnaStanicaElement.size() <= idVlaku)
			{
				throw new ChybajuciElementException("Element nastupna stanica (" + Config.XPATH_SPOJE_PRESTUP + ") sa nenasiel");
			}
			
			if(!((DomElement) nastupnaStanicaElement.get(idVlaku)).asText().contains("/"))
			{
				throw new ZlyFormatElementuException("Element nastupna stanica (" + Config.XPATH_SPOJE_PRESTUP + ") neobsahuje \"/\"");
			}
			
			String nazovNastupnejStanice = ((DomElement) nastupnaStanicaElement.get(idVlaku)).asText().split("/")[0].trim();	
			nastupnaStanica = ziskajZastavku(nazovNastupnejStanice);
		}
		else
		{
			String nazovNastupnejStanice = ziskajSpoj().ziskajVyhladavanie().vychodziaStanica;	
			Zastavka temp = nastupnaStanica;
			
			// Skusime pouzit ako nastupnu stanicu informaciu z vyhladavania
			nastupnaStanica = ziskajZastavku(nazovNastupnejStanice);
			
			if(nastupnaStanica == null)
			{
				// Vo vyhladavani bola zla informacia, skusime pouzit informaciu zo zoznamu zastavok
				nastupnaStanica = temp;
							
				// Zrejme by tato zastavka mala obsahovat informaciu z vyhladavania
				// (pokus o overenie, ci prislo k chybe pri zistovani stanice zo zoznamu zastavok)
				if(!nastupnaStanica.nazov.toLowerCase().contains(nazovNastupnejStanice.toLowerCase()))
				{
					// Pouzijeme teda prvu najdenu zastavku s podobnym nazvom a modlime sa
					nastupnaStanica = ziskajPrvuZastavkuSPodobnymNazvom(nazovNastupnejStanice);		
				}	
			}				
		}
		
		// Znova nacitaj vystupnu stanicu
		// Pre istotu skusame nacitat, ci existuje informacia o prestupe
		List<Object> vystupnaStanicaElement = elementSpoja.getByXPath(Config.XPATH_SPOJE_PRESTUP);
		if(!vystupnaStanicaElement.isEmpty())
		{	
			// Tato informacia je vzdy spravna, takze prepiseme aktualnu stanicu
			
			if(vystupnaStanicaElement.size() <= idVlaku)
			{
				throw new ChybajuciElementException("Element vystupna stanica (" + Config.XPATH_SPOJE_PRESTUP + ") sa nenasiel");
			}
			
			if(!((DomElement) vystupnaStanicaElement.get(idVlaku)).asText().contains("/"))
			{
				throw new ZlyFormatElementuException("Element vystupna stanica (" + Config.XPATH_SPOJE_PRESTUP + ") neobsahuje \"/\"");
			}
			
			String nazovVystupnejStanice = ((DomElement) vystupnaStanicaElement.get(idVlaku)).asText().split("/")[1].trim();
			vystupnaStanica = ziskajZastavku(nazovVystupnejStanice);
		}
		else
		{
			String nazovVystupnejStanice = ziskajSpoj().ziskajVyhladavanie().cielovaStanica;
			Zastavka temp = vystupnaStanica;
			
			// Skusime pouzit ako vystupnu stanicu informaciu z vyhladavania
			vystupnaStanica = ziskajZastavku(nazovVystupnejStanice);
			
			if(vystupnaStanica == null)
			{
				// Vo vyhladavani bola zla informacia, skusime pouzit informaciu zo zoznamu zastavok
				vystupnaStanica = temp;
							
				// Zrejme by tato zastavka mala obsahovat informaciu z vyhladavania
				// (pokus o overenie, ci prislo k chybe pri zistovani stanice zo zoznamu zastavok)
				if(!vystupnaStanica.nazov.toLowerCase().contains(nazovVystupnejStanice.toLowerCase()))
				{
					// Pouzijeme teda POSLEDNU najdenu zastavku s podobnym nazvom a modlime sa
					vystupnaStanica = ziskajPoslednuZastavkuSPodobnymNazvom(nazovVystupnejStanice);		
				}	
			}	
		}		
		// !!! POZOR VYSTUPNA A NASTUPNA ZASTAVKA MOZE BYT STALE NESPRAVNE, TO UZ ALE NIE JE V NASEJ MOCI

		// Nacitaj nastupny cas
		List<Object> nastupnyCasElement = elementSpoja.getByXPath(Config.XPATH_SPOJE_NASTUPNYCAS);
		
		if(nastupnyCasElement.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element nastupny cas (" + Config.XPATH_SPOJE_NASTUPNYCAS + ") sa nenasiel");
		}
		
		nastupnyCas = ((DomElement) nastupnyCasElement.get(idVlaku)).asText().trim();
		
		// Nacitaj nastupny datum
		List<Object> nastupnyDatumElement = elementSpoja.getByXPath(Config.XPATH_SPOJE_NASTUPNYDATUM);
		
		if(nastupnyDatumElement.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element nastupny datum (" + Config.XPATH_SPOJE_NASTUPNYDATUM + ") sa nenasiel");
		}
		
		nastupnyDatum = ((DomElement) nastupnyDatumElement.get(idVlaku)).asText().trim();	
		
		// Nacitaj vystupny cas
		List<Object> vystupnyCasElement = elementSpoja.getByXPath(Config.XPATH_SPOJE_VYSTUPNYCAS);
		
		if(vystupnyCasElement.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element vystupny cas (" + Config.XPATH_SPOJE_VYSTUPNYCAS + ") sa nenasiel");
		}
		
		vystupnyCas = ((DomElement) vystupnyCasElement.get(idVlaku)).asText().trim();
		
		// Nacitaj vystupny datum
		List<Object> vystupnyDatumElement = elementSpoja.getByXPath(Config.XPATH_SPOJE_VYSTUPNYDATUM);
		
		if(vystupnyDatumElement.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element vystupny datum (" + Config.XPATH_SPOJE_VYSTUPNYDATUM + ") sa nenasiel");
		}
		
		vystupnyDatum = ((DomElement) vystupnyDatumElement.get(idVlaku)).asText().trim();
	}
	
	public Zastavka ziskajZastavku(String nazov)
	{
		for(Zastavka z : zastavky)
		{
			if(z.nazov.equals(nazov))
			{
				return z;
			}
		}
		
		return null;
	}
	
	public Zastavka ziskajPrvuZastavkuSPodobnymNazvom(String nazov)
	{
		for(Zastavka z : zastavky)
		{
			if(z.nazov.toLowerCase().contains(nazov.toLowerCase()))
			{
				return z;
			}
		}
		
		return null;
	}
	
	public Zastavka ziskajPoslednuZastavkuSPodobnymNazvom(String nazov)
	{
		for(int i = zastavky.size() - 1; i >= 0; i--)
		{
			if(zastavky.get(i).nazov.toLowerCase().contains(nazov.toLowerCase()))
			{
				return zastavky.get(i);
			}
		}
		
		return null;
	}
	
	public ArrayList<Zastavka> ziskajZastavky()
	{
		ArrayList<Zastavka> usek = new ArrayList<>();
		boolean bPocitam = false;
		
		for(int i = 0; i < zastavky.size(); i++)
		{
			if(zastavky.get(i).nazov.equals(nastupnaStanica.nazov))
			{
				bPocitam = true;
			}
			
			if(bPocitam)
			{
				usek.add(zastavky.get(i));
			}
			
			if(zastavky.get(i).nazov.equals(vystupnaStanica.nazov))
			{
				break;
			}
		}
		
		return usek;
	}
	
	public Spoj ziskajSpoj()
	{
		return spoj;
	}
	
	public String ziskajTriedu()
	{
		if(meno.contains("/"))
		{
			// Priamy vozen oznacime ako -
			return "-";
		}
		
		return meno.split(" ")[0];
	}
}
