package com.ondrejhrusovsky.test;
import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.ondrejhrusovsky.aplikacia.NemoznoZakupitListokException;
import com.ondrejhrusovsky.aplikacia.Spoj;
import com.ondrejhrusovsky.aplikacia.Usek;
import com.ondrejhrusovsky.aplikacia.Vlak;
import com.ondrejhrusovsky.aplikacia.Vyhladavanie;

public class Main {

	public static void main(String[] args)
	{
		Vyhladavanie vyhladavanie = null;
		
		try
		{
			vyhladavanie = new Vyhladavanie("Bratislava", "Kosice", "", "04.05.2017", "23:00");
		} 
		catch (FailingHttpStatusCodeException | IOException e) 
		{
			e.printStackTrace();
			return;
		}	
		
		List<Spoj> najdeneSpoje = vyhladavanie.ziskajSpoje();
		boolean b = false;
		
		for(Spoj s : najdeneSpoje) // Prejdeme si vsetky najdene spoje
		{		
			for(Vlak v : s.ziskajVlaky()) // Prejdem vsetky vlaky daneho spoja (jeden spoj moze mat viac vlakov - prestupy)
			{
				System.out.println(v.ziskajMeno()); // Napr. vypis nazov vlaku
			}
			
			if(!b)
			{
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
						b = true;
					}				
					else
					{
						System.out.println("Vlak ma volny kontingent!");
					}
				} 
				catch (ElementNotFoundException | NemoznoZakupitListokException | IOException e) 
				{
					e.printStackTrace();
				}				
			}
			
			break;
		}		
	}
}
