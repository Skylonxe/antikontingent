package com.ondrejhrusovsky.test;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.exceptions.InaChybaException;
import com.ondrejhrusovsky.exceptions.NemoznoZakupitListokException;
import com.ondrejhrusovsky.exceptions.NepodariloSaNajstSpojeException;
import com.ondrejhrusovsky.exceptions.ZlyFormatElementuException;
import com.ondrejhrusovsky.ikvcAPI.Spoj;
import com.ondrejhrusovsky.ikvcAPI.Usek;
import com.ondrejhrusovsky.ikvcAPI.Vlak;
import com.ondrejhrusovsky.ikvcAPI.Vyhladavanie;

public class TestLaunch {

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
		
		for(Spoj s : vyhladavanie.spoje)
		{		
			for(Vlak v : s.vlaky) // Prejdem vsetky vlaky daneho spoja (jeden spoj moze mat viac vlakov - prestupy)
			{
				System.out.println(v.meno); // Napr. vypis nazov vlaku
			}
			
			try 
			{
				s.nacitajKontingent();
				
				if(s.kontingentVycerpany)
				{
					System.out.println("Vlak ma vycerpany kontingent!");
					
					s.nacitajUsekyKontingentu();
					
					System.out.println("Volny na useku:");
					for(Usek u : s.volneUseky)
					{				
						System.out.println(u.zac.nazov + " - " + u.kon.nazov);
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
