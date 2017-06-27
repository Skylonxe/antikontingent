package com.ondrejhrusovsky.ikvcAPI;

import java.io.IOException;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.exceptions.InaChybaException;
import com.ondrejhrusovsky.exceptions.NemoznoZakupitListokException;
import com.ondrejhrusovsky.exceptions.NepodariloSaNajstSpojeException;
import com.ondrejhrusovsky.exceptions.ZlyFormatElementuException;

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
			vyhladavanie = new Vyhladavanie(odkial.nazov, kam.nazov, cez, datum, cas);
		} 
		catch (FailingHttpStatusCodeException | IOException | NepodariloSaNajstSpojeException | ChybajuciElementException | ZlyFormatElementuException | InaChybaException e1) 
		{
			System.out.println("Thread " + toString() + " sa neuspesne pokusil vyhladat spojenia: " + e1.getMessage());
			pridajChybnyUsek();
			return;
		}
		
		ArrayList<String> nazvyVlakov = new ArrayList<>();
		
		for(Vlak v : ziskajSpoj().vlaky)
		{
			nazvyVlakov.add(v.meno);
		}
		
		Spoj mojSpoj = null;
		
		// Najdeme spoj, ktory obsahuje jeden z vlakov nasho spoja a pre ten budeme kupovat listok
		// Hladanie vzdy zacina s presnou minutou odchodu vlaku, takze spoj by mal byt hned prvy v zozname, ale z bezp. dovodov robime aj tuto kontrolu
		for(Spoj s : vyhladavanie.spoje)
		{
			for(Vlak v : s.vlaky)
			{
				for(String nazovVlaku : nazvyVlakov)
				{
					if(nazovVlaku.contains(v.meno))
					{
						mojSpoj = s;
						break;
					}
				}				
			}
			
			if(mojSpoj != null)
			{
				break;
			}
		}
		
		if(mojSpoj == null)
		{
			System.out.println("Thread " + toString() + " nedokazal najst svoj spoj!");
			pridajChybnyUsek();
			return;
		}	
		
		try {
			System.out.println("Pytam sa na usek: " + toString());	
			mojSpoj.nacitajKontingent();
			boolean bJeVycerpany = mojSpoj.kontingentVycerpany;
			
			if(!bJeVycerpany)
			{
				System.out.println(" ===== Volny usek: " + toString());	
				pridajVolnyUsek();
			}
			else
			{
				System.out.println(" ===== Obsadeny usek: " + toString());	
			}
			
		} catch (NemoznoZakupitListokException | IOException | ChybajuciElementException e) {
			System.out.println("Thread " + toString() + " sa neuspesne pokusil overit kontingent na useku!");
			pridajChybnyUsek();
			return;
		}		
	}
	
	public void pridajVolnyUsek()
	{
		ziskajSpoj().pridajVolnyUsek(new Usek(odkial, kam), this);
	}
	
	public void pridajChybnyUsek()
	{
		ziskajSpoj().pridajChybnyUsek(new Usek(odkial, kam), this);
	}
	
	public Spoj ziskajSpoj()
	{
		return spoj;
	}
	
	public Zastavka ziskajOdkial()
	{
		return odkial;
	}
	
	public Zastavka ziskajKam()
	{
		return kam;
	}
}
