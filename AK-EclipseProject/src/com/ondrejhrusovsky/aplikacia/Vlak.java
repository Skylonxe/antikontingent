package com.ondrejhrusovsky.aplikacia;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomElement;

public class Vlak
{	
	private Spoj spoj; // Spoj, do ktoreho patri tento vlak
	
	private String meno; // Nazov vlaku (napr. "R 613 MARLENKA")
	private ArrayList<Zastavka> zastavky = new ArrayList<>();
	private String poznamka; // Dodatocne informacie vlaku

	private Zastavka nastupnaStanica; 
	private String nastupnyDatum;
	private String nastupnyCas;
	
	private Zastavka vystupnaStanica; 
	private String vystupnyDatum;
	private String vystupnyCas;
	
	public Vlak(Spoj spoj, DomElement elementSpoja, DomElement elementInfo, int idVlaku) throws ChybajuciElementException, ZlyFormatElementuException
	{
		this.spoj = spoj;
		
		// Nacitaj meno
		List<Object> h2Elementy = elementSpoja.getByXPath(WebData.XPATH_SPOJE_NAZVYVLAKOV);
		
		if(h2Elementy.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element nazov vlaku (" + WebData.XPATH_SPOJE_NAZVYVLAKOV + ") sa nenasiel");
		}
		
		meno = ((DomElement) h2Elementy.get(idVlaku)).asText().trim();
		
		// Nacitaj zastavky
		DomElement zoznamStanicElement = (DomElement) elementInfo.getFirstByXPath(WebData.XPATH_INFO_ZOZNAMZASTAVOK);
		
		if(zoznamStanicElement == null)
		{
			throw new ChybajuciElementException("Element zoznam zastavok (" + WebData.XPATH_INFO_ZOZNAMZASTAVOK + ") sa nenasiel");
		}
		
		for(DomElement riadok : zoznamStanicElement.getChildElements())
		{	
			DomElement nazovVzdialenostZastavkyElement = (DomElement) riadok.getFirstByXPath(WebData.XPATH_INFO_NAZOVVZDIALENOST);
			
			if(nazovVzdialenostZastavkyElement == null)
			{
				throw new ChybajuciElementException("Element nazov zastavky (" + WebData.XPATH_INFO_NAZOVVZDIALENOST + ") sa nenasiel");
			}
			
			if(!nazovVzdialenostZastavkyElement.asText().contains(",") || nazovVzdialenostZastavkyElement.asText().split(",").length != 2)
			{
				throw new ZlyFormatElementuException("Element nazov a vzdialenost (" + WebData.XPATH_INFO_NAZOVVZDIALENOST + ") zastavky ma zly format");
			}
			
			String nazovZastavky = nazovVzdialenostZastavkyElement.asText().split(",")[0].trim();
			
			if(!nazovVzdialenostZastavkyElement.asText().split(",")[1].contains("km"))
			{
				throw new ZlyFormatElementuException("Element nazov a vzdialenost nema km");
			}
			
			int vzdialenostZastavky = Integer.parseInt(nazovVzdialenostZastavkyElement.asText().split(",")[1].replace("km", "").trim());
			
			DomElement cas1Element = (DomElement) riadok.getFirstByXPath(WebData.XPATH_INFO_CAS1);
			DomElement cas2Element = (DomElement) riadok.getFirstByXPath(WebData.XPATH_INFO_CAS2);
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
			
			if(!riadok.hasAttribute(WebData.ATTRIBUTE_INFO_NASTUPNAVYSTUPNA))
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
			if(!riadok.hasAttribute(WebData.ATTRIBUTE_INFO_NASTUPNAVYSTUPNA))
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
		DomElement poznamkaElement = (DomElement) elementInfo.getFirstByXPath(WebData.XPATH_INFO_POZNAMKA);
		
		if(poznamkaElement == null)
		{
			throw new ChybajuciElementException("Element poznamka (" + WebData.XPATH_INFO_POZNAMKA + ") sa nenasiel");
		}
		
		poznamka = (poznamkaElement).asText().trim();	
		
		// Znova nacitaj nastupnu stanicu
		// Pre istotu skusame nacitat, ci existuje informacia o prestupe
		List<Object> nastupnaStanicaElement = elementSpoja.getByXPath(WebData.XPATH_SPOJE_PRESTUP);
		if(!nastupnaStanicaElement.isEmpty())
		{
			// Tato informacia je vzdy spravna, takze prepiseme aktualnu stanicu
			
			if(nastupnaStanicaElement.size() <= idVlaku)
			{
				throw new ChybajuciElementException("Element nastupna stanica (" + WebData.XPATH_SPOJE_PRESTUP + ") sa nenasiel");
			}
			
			if(!((DomElement) nastupnaStanicaElement.get(idVlaku)).asText().contains("/"))
			{
				throw new ZlyFormatElementuException("Element nastupna stanica (" + WebData.XPATH_SPOJE_PRESTUP + ") neobsahuje \"/\"");
			}
			
			String nazovNastupnejStanice = ((DomElement) nastupnaStanicaElement.get(idVlaku)).asText().split("/")[0].trim();	
			nastupnaStanica = ziskajZastavku(nazovNastupnejStanice);
		}
		else
		{
			String nazovNastupnejStanice = ziskajSpoj().ziskajVyhladavanie().ziskajVychodziaStanica();	
			Zastavka temp = nastupnaStanica;
			
			// Skusime pouzit ako nastupnu stanicu informaciu z vyhladavania
			nastupnaStanica = ziskajZastavku(nazovNastupnejStanice);
			
			if(nastupnaStanica == null)
			{
				// Vo vyhladavani bola zla informacia, skusime pouzit informaciu zo zoznamu zastavok
				nastupnaStanica = temp;
							
				// Zrejme by tato zastavka mala obsahovat informaciu z vyhladavania
				// (pokus o overenie, ci prislo k chybe pri zistovani stanice zo zoznamu zastavok)
				if(!nastupnaStanica.ziskajNazov().toLowerCase().contains(nazovNastupnejStanice.toLowerCase()))
				{
					// Pouzijeme teda prvu najdenu zastavku s podobnym nazvom a modlime sa
					nastupnaStanica = ziskajPrvuZastavkuSPodobnymNazvom(nazovNastupnejStanice);		
				}	
			}				
		}
		
		// Znova nacitaj vystupnu stanicu
		// Pre istotu skusame nacitat, ci existuje informacia o prestupe
		List<Object> vystupnaStanicaElement = elementSpoja.getByXPath(WebData.XPATH_SPOJE_PRESTUP);
		if(!vystupnaStanicaElement.isEmpty())
		{	
			// Tato informacia je vzdy spravna, takze prepiseme aktualnu stanicu
			
			if(vystupnaStanicaElement.size() <= idVlaku)
			{
				throw new ChybajuciElementException("Element vystupna stanica (" + WebData.XPATH_SPOJE_PRESTUP + ") sa nenasiel");
			}
			
			if(!((DomElement) vystupnaStanicaElement.get(idVlaku)).asText().contains("/"))
			{
				throw new ZlyFormatElementuException("Element vystupna stanica (" + WebData.XPATH_SPOJE_PRESTUP + ") neobsahuje \"/\"");
			}
			
			String nazovVystupnejStanice = ((DomElement) vystupnaStanicaElement.get(idVlaku)).asText().split("/")[1].trim();
			vystupnaStanica = ziskajZastavku(nazovVystupnejStanice);
		}
		else
		{
			String nazovVystupnejStanice = ziskajSpoj().ziskajVyhladavanie().ziskajCielovaStanica();
			Zastavka temp = vystupnaStanica;
			
			// Skusime pouzit ako vystupnu stanicu informaciu z vyhladavania
			vystupnaStanica = ziskajZastavku(nazovVystupnejStanice);
			
			if(vystupnaStanica == null)
			{
				// Vo vyhladavani bola zla informacia, skusime pouzit informaciu zo zoznamu zastavok
				vystupnaStanica = temp;
							
				// Zrejme by tato zastavka mala obsahovat informaciu z vyhladavania
				// (pokus o overenie, ci prislo k chybe pri zistovani stanice zo zoznamu zastavok)
				if(!vystupnaStanica.ziskajNazov().toLowerCase().contains(nazovVystupnejStanice.toLowerCase()))
				{
					// Pouzijeme teda POSLEDNU najdenu zastavku s podobnym nazvom a modlime sa
					vystupnaStanica = ziskajPoslednuZastavkuSPodobnymNazvom(nazovVystupnejStanice);		
				}	
			}	
		}		
		// !!! POZOR VYSTUPNA A NASTUPNA ZASTAVKA MOZE BYT STALE NESPRAVNE, TO UZ ALE NIE JE V NASEJ MOCI

		// Nacitaj nastupny cas
		List<Object> nastupnyCasElement = elementSpoja.getByXPath(WebData.XPATH_SPOJE_NASTUPNYCAS);
		
		if(nastupnyCasElement.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element nastupny cas (" + WebData.XPATH_SPOJE_NASTUPNYCAS + ") sa nenasiel");
		}
		
		nastupnyCas = ((DomElement) nastupnyCasElement.get(idVlaku)).asText().trim();
		
		// Nacitaj nastupny datum
		List<Object> nastupnyDatumElement = elementSpoja.getByXPath(WebData.XPATH_SPOJE_NASTUPNYDATUM);
		
		if(nastupnyDatumElement.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element nastupny datum (" + WebData.XPATH_SPOJE_NASTUPNYDATUM + ") sa nenasiel");
		}
		
		nastupnyDatum = ((DomElement) nastupnyDatumElement.get(idVlaku)).asText().trim();	
		
		// Nacitaj vystupny cas
		List<Object> vystupnyCasElement = elementSpoja.getByXPath(WebData.XPATH_SPOJE_VYSTUPNYCAS);
		
		if(vystupnyCasElement.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element vystupny cas (" + WebData.XPATH_SPOJE_VYSTUPNYCAS + ") sa nenasiel");
		}
		
		vystupnyCas = ((DomElement) vystupnyCasElement.get(idVlaku)).asText().trim();
		
		// Nacitaj vystupny datum
		List<Object> vystupnyDatumElement = elementSpoja.getByXPath(WebData.XPATH_SPOJE_VYSTUPNYDATUM);
		
		if(vystupnyDatumElement.size() <= idVlaku)
		{
			throw new ChybajuciElementException("Element vystupny datum (" + WebData.XPATH_SPOJE_VYSTUPNYDATUM + ") sa nenasiel");
		}
		
		vystupnyDatum = ((DomElement) vystupnyDatumElement.get(idVlaku)).asText().trim();
	}
	
	public Zastavka ziskajZastavku(String nazov)
	{
		for(Zastavka z : ziskajVsetkyZastavky())
		{
			if(z.ziskajNazov().equals(nazov))
			{
				return z;
			}
		}
		
		return null;
	}
	
	public Zastavka ziskajPrvuZastavkuSPodobnymNazvom(String nazov)
	{
		for(Zastavka z : ziskajVsetkyZastavky())
		{
			if(z.ziskajNazov().toLowerCase().contains(nazov.toLowerCase()))
			{
				return z;
			}
		}
		
		return null;
	}
	
	public Zastavka ziskajPoslednuZastavkuSPodobnymNazvom(String nazov)
	{
		ArrayList<Zastavka> vsetZast = ziskajVsetkyZastavky();
		
		for(int i = vsetZast.size() - 1; i >= 0; i--)
		{
			if(vsetZast.get(i).ziskajNazov().toLowerCase().contains(nazov.toLowerCase()))
			{
				return vsetZast.get(i);
			}
		}
		
		return null;
	}
	
	public ArrayList<Zastavka> ziskajZastavky()
	{
		ArrayList<Zastavka> usek = new ArrayList<>();
		boolean bPocitam = false;
		
		for(int i = 0; i < ziskajVsetkyZastavky().size(); i++)
		{
			if(ziskajVsetkyZastavky().get(i).ziskajNazov().equals(ziskajNastupnaStanica().ziskajNazov()))
			{
				bPocitam = true;
			}
			
			if(bPocitam)
			{
				usek.add(ziskajVsetkyZastavky().get(i));
			}
			
			if(ziskajVsetkyZastavky().get(i).ziskajNazov().equals(ziskajVystupnaStanica().ziskajNazov()))
			{
				break;
			}
		}
		
		return usek;
	}
	
	public ArrayList<Zastavka> ziskajVsetkyZastavky()
	{
		return zastavky;
	}
	
	public Spoj ziskajSpoj()
	{
		return spoj;
	}

	public String ziskajMeno()
	{
		return meno;
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

	public String ziskajPoznamku()
	{
		return poznamka;
	}

	public Zastavka ziskajNastupnaStanica()
	{
		return nastupnaStanica;
	}

	public String ziskajNastupnyDatum()
	{
		return nastupnyDatum;
	}

	public String ziskajNastupnyCas()
	{
		return nastupnyCas;
	}

	public Zastavka ziskajVystupnaStanica()
	{
		return vystupnaStanica;
	}

	public String ziskajVystupnyDatum()
	{
		return vystupnyDatum;
	}

	public String ziskajVystupnyCas()
	{
		return vystupnyCas;
	}
}
