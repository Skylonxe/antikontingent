package com.ondrejhrusovsky.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.ondrejhrusovsky.exceptions.InaChybaException;
import com.ondrejhrusovsky.exceptions.ZlyFormatElementuException;
import com.ondrejhrusovsky.ikvcAPI.Vyhladavanie;

public class Manazer {
	private static Manazer instancia = null;

	private HashMap<String, Vyhladavac> vyhladavaci;
	
	protected Manazer()
	{
		vyhladavaci = new HashMap<>();
	}
	
	public Vyhladavac zacniVyhladavanie(String requestId, String odkial, String kam, String cez, String datum, String cas)
	{
		zmazStareVyhladavace();
		Vyhladavac v = new Vyhladavac(odkial, kam, cez, datum, cas);
		vyhladavaci.put(requestId, v);

		return v;
	}
	
	public void zmazStareVyhladavace()
	{
		ArrayList<String> naVyhodenie = new ArrayList<>();
		
		for(String key : vyhladavaci.keySet())
		{
			if(System.currentTimeMillis() - vyhladavaci.get(key).casPoslednejPoziadavky > 100)
			{
				naVyhodenie.add(key);
			}
		}
		
		for(String key : naVyhodenie)
		{
			vyhladavaci.remove(key);
		}
	}
	
	public void nacitajUsekyKontingentu(String requestId, int spojIdx)
	{
		if(vyhladavaci.containsKey(requestId))
		{
			System.out.println("Nasiel som request");
			try {
				vyhladavaci.get(requestId).casPoslednejPoziadavky = System.currentTimeMillis();
				vyhladavaci.get(requestId).ziskajVyhladavanie().spoje.get(spojIdx).nacitajUsekyKontingentu();
			} catch (ZlyFormatElementuException | InaChybaException e) {
				System.out.println("Chyba nacitania usekov kontingentu " + e.getMessage());
			}
		}
		else
		{
			System.out.println("Neexistujuci request");
		}
	}
	
	public Vyhladavanie ziskajVyhladavanie(String requestId)
	{
		if(vyhladavaci.containsKey(requestId))
		{
			return vyhladavaci.get(requestId).ziskajVyhladavanie();
		}
		
		return null;		
	}
	
	public static Manazer ziskajInstanciu()
	{
		if(instancia == null)
		{
			instancia = new Manazer();
		}
		
		return instancia;
	}
}
