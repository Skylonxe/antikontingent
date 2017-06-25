package com.ondrejhrusovsky.test;
import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.ondrejhrusovsky.aplikacia.ChybajuciElementException;
import com.ondrejhrusovsky.aplikacia.InaChybaException;
import com.ondrejhrusovsky.aplikacia.NemoznoZakupitListokException;
import com.ondrejhrusovsky.aplikacia.Spoj;
import com.ondrejhrusovsky.aplikacia.Usek;
import com.ondrejhrusovsky.aplikacia.Vlak;
import com.ondrejhrusovsky.aplikacia.Vyhladavanie;
import com.ondrejhrusovsky.aplikacia.ZlyFormatElementuException;
import com.ondrejhrusovsky.aplikacia.NepodariloSaNajstSpojeException;

public class Main {

	public static void main(String[] args)
	{
		Vyhladavanie vyhladavanie = null;
		
		try
		{
			vyhladavanie = new Vyhladavanie("Kosice", "Bratislava", "", "25.06.2017", "13:00");
		} 
		catch (NepodariloSaNajstSpojeException | FailingHttpStatusCodeException | IOException | ChybajuciElementException | ZlyFormatElementuException | InaChybaException e) 
		{
			e.printStackTrace();
			return;
		}
		
		List<Spoj> najdeneSpoje = vyhladavanie.ziskajSpoje();
		
		//Spoj s = najdeneSpoje.get(3);
		
		for(Spoj s : najdeneSpoje) {
		
		for(Vlak v : s.ziskajVlaky()) // Prejdem vsetky vlaky daneho spoja (jeden spoj moze mat viac vlakov - prestupy)
		{
			System.out.println(v.ziskajMeno()); // Napr. vypis nazov vlaku
		}
		
		try 
		{
			if(s.jeKontingentVycerpany())
			{
				System.out.println("Vlak ma vycerpany kontingent!");
				
				s.spustiHladanieVolnehoKontingentu();
				
				System.out.println("Volny na useku:");
				for(Usek u : s.ziskajVolneUseky())
				{				
					System.out.println(u.zac.ziskajNazov() + " - " + u.kon.ziskajNazov());
				}
			}				
			else
			{
				System.out.println("Vlak ma volny kontingent!");
			}
		} 
		catch (ElementNotFoundException | NemoznoZakupitListokException | IOException | ChybajuciElementException | ZlyFormatElementuException | InaChybaException e) 
		{
			e.printStackTrace();
		}	
		}
	}
}
