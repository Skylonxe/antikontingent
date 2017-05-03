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
	
	public Vlak(Spoj spoj, DomElement elementSpoja, DomElement elementInfo, int idVlaku)
	{
		this.spoj = spoj;
		
		// Nacitaj meno
		List<Object> h2Elementy = elementSpoja.getByXPath("h2");
		meno = ((DomElement) h2Elementy.get(idVlaku)).asText().trim();
		
		// Nacitaj zastavky
		DomElement zoznamStanicElement = (DomElement) elementInfo.getFirstByXPath("ul");
		
		for(DomElement riadok : zoznamStanicElement.getChildElements())
		{	
			DomElement nazovZastavkyElement = (DomElement) riadok.getFirstByXPath("strong");
			String nazovZastavky = nazovZastavkyElement.asText().split(",")[0].trim();
			
			DomElement vzdialenostZastavkyElement = (DomElement) riadok.getFirstByXPath("strong");
			int vzdialenostZastavky = Integer.parseInt(vzdialenostZastavkyElement.asText().split(",")[1].replace("km", "").trim());
			
			DomElement cas1Element = (DomElement) riadok.getFirstByXPath("p/em[1]");
			DomElement cas2Element = (DomElement) riadok.getFirstByXPath("p/em[2]");
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
			zastavky.add(novaZastavka);
		}
			
		// Nacitaj poznamku
		DomElement poznamkaElement = (DomElement) elementInfo.getFirstByXPath("div[@class='note']");
		poznamka = (poznamkaElement).asText().trim();		
		
		// Nacitaj nastupnu stanicu
		List<Object> nastupnaStanicaElement = elementSpoja.getByXPath("p[@class='prestup']");
		if(!nastupnaStanicaElement.isEmpty())
		{
			String nazovNastupnejStanice = ((DomElement) nastupnaStanicaElement.get(idVlaku)).asText().split("/")[0].trim();	
			nastupnaStanica = ziskajZastavku(nazovNastupnejStanice);
		}
		else
		{
			String nazovNastupnejStanice = ziskajSpoj().ziskajVyhladavanie().ziskajVychodziaStanica();	
			nastupnaStanica = ziskajZastavku(nazovNastupnejStanice);
		}

		// Nacitaj nastupny cas
		List<Object> nastupnyCasElement = elementSpoja.getByXPath("div[@class='time']/p[1]/strong");
		nastupnyCas = ((DomElement) nastupnyCasElement.get(idVlaku)).asText().trim();
		
		// Nacitaj nastupny datum
		List<Object> nastupnyDatumElement = elementSpoja.getByXPath("div[@class='time']/p[1]/span[2]");
		nastupnyDatum = ((DomElement) nastupnyDatumElement.get(idVlaku)).asText().trim();
		
		// Nacitaj vystupnu stanicu
		List<Object> vystupnaStanicaElement = elementSpoja.getByXPath("p[@class='prestup']");
		if(!nastupnaStanicaElement.isEmpty())
		{		
			String nazovVystupnejStanice = ((DomElement) vystupnaStanicaElement.get(idVlaku)).asText().split("/")[1].trim();
			vystupnaStanica = ziskajZastavku(nazovVystupnejStanice);
		}
		else
		{
			String nazovVystupnejStanice = ziskajSpoj().ziskajVyhladavanie().ziskajCielovaStanica();
			vystupnaStanica = ziskajZastavku(nazovVystupnejStanice);
		}
		
		// Nacitaj vystupny cas
		List<Object> vystupnyCasElement = elementSpoja.getByXPath("div[@class='time']/p[2]/strong");
		vystupnyCas = ((DomElement) vystupnyCasElement.get(idVlaku)).asText().trim();
		
		// Nacitaj vystupny datum
		List<Object> vystupnyDatumElement = elementSpoja.getByXPath("div[@class='time']/p[2]/span[2]");
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
