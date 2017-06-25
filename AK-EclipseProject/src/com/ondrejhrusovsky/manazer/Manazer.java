package com.ondrejhrusovsky.manazer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.ondrejhrusovsky.aplikacia.Vyhladavanie;

public class Manazer {
	private static Manazer instancia = null;

	private HashMap<String, Vyhladavac> vyhladavaci;
	
	protected Manazer()
	{
		vyhladavaci = new HashMap<>();
	}
	
	public void zacniVyhladavanie(Socket socket, String clientId, String odkial, String kam, String cez, String datum, String cas)
	{
		Vyhladavac v = new Vyhladavac(socket, clientId, odkial, kam, cez, datum, cas);
		vyhladavaci.put(clientId, v);
	}
	
	public void vyhladavanieDokoncene(String id, Vyhladavac vyhladavac)
	{
		
		SocketKomunikacia.ziskajInstanciu().posliVysledokVyhladavania(id, vyhladavac);
	}
	
	public void vyhladavanieZlyhalo(String id, String chyba, Vyhladavac vyhladavac)
	{
		SocketKomunikacia.ziskajInstanciu().posliChybuVyhladavania(id, chyba, vyhladavac);
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
