package com.ondrejhrusovsky.aplikacia;

import java.io.IOException;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class KontingentThread extends Thread
{
	private Vyhladavanie vyhladavanie;
	private Spoj spoj;
	
	private Zastavka odkial;
	private Zastavka kam;
	private String cez;
	private String datum;
	private String cas;	
	
	public KontingentThread(Spoj spoj, Zastavka odkial, Zastavka kam, String cez, String datum, String cas)
	{
		this.spoj = spoj;
		this.odkial = odkial;
		this.kam = kam;
		this.cez = cez;
		this.datum = datum;
		this.cas = cas;
	}
	
	public String toString()
	{
		return odkial + " - " + kam;
	}
	
	public void run()
	{		
		try
		{
			vyhladavanie = new Vyhladavanie(odkial.ziskajNazov(), kam.ziskajNazov(), cez, datum, cas);
		} 
		catch (FailingHttpStatusCodeException | IOException e1) 
		{
			e1.printStackTrace();
			return;
		}
		
		ArrayList<Vlak> vlakySpoja = ziskajSpoj().ziskajVlaky();
		ArrayList<String> nazvyVlakov = new ArrayList<>();
		
		for(Vlak v : vlakySpoja)
		{
			nazvyVlakov.add(v.ziskajMeno());
		}
		
		Spoj mojSpoj = null;
		
		// Najdeme spoj, ktory obsahuje jeden z vlakov nasho spoja a pre ten budeme kupovat listok
		// Hladanie vzdy zacina s presnou minutou odchodu vlaku, takze spoj by mal byt hned prvy v zozname, ale z bezp. dovodov robime aj tuto kontrolu
		for(Spoj s : vyhladavanie.ziskajSpoje())
		{
			for(Vlak v : s.ziskajVlaky())
			{
				if(nazvyVlakov.contains(v.ziskajMeno()))
				{
					mojSpoj = s;
					break;
				}
			}
			
			if(mojSpoj != null)
			{
				break;
			}
		}
			
		boolean bVycerpany = true;
		
		System.out.println("Pytam sa na usek: " + toString());	
		
		try
		{
			bVycerpany = mojSpoj.jeKontingentVycerpany();
		}
		catch (ElementNotFoundException | IOException e)
		{
			System.out.println("Thread " + toString() + " spadol na chybe " + e.getMessage() + "!");
			e.printStackTrace();
		} 
		catch (NemoznoZakupitListokException e)
		{
			System.out.println("Thread " + toString() + " uz nemoze zakupit listok!");
			return;
		}
		
		synchronized(mojSpoj)
		{
			if(!bVycerpany)
			{
				System.out.println("Odpoved: Usek " + toString() + " je VOLNY!");
				ziskajSpoj().pridajVolnyUsek(new Usek(odkial, kam), this);					
			}
			else
			{
				System.out.println("Odpoved: Usek " + toString() + " je obsadeny");
			}
		}
	}
	
	public Spoj ziskajSpoj()
	{
		return spoj;
	}
}
