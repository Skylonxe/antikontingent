package com.ondrejhrusovsky.server;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.exceptions.InaChybaException;
import com.ondrejhrusovsky.exceptions.NepodariloSaNajstSpojeException;
import com.ondrejhrusovsky.exceptions.ZlyFormatElementuException;
import com.ondrejhrusovsky.ikvcAPI.Vyhladavanie;

public class Vyhladavac extends Thread
{
	private Vyhladavanie vyhladavanie = null;
	
	private String odkial;
	private String kam;
	private String cez;
	private String datum;
	private String cas;
	
	private boolean chyba;
	private String chybovaHlaska;	
	public long casPoslednejPoziadavky;
	
	Vyhladavac(String odkial, String kam, String cez, String datum, String cas)
	{
		this.odkial = odkial;
		this.kam = kam;
		this.cez = cez;
		this.datum = datum;
		this.cas = cas;
		
		chyba = false;
		chybovaHlaska = "";
		casPoslednejPoziadavky = System.currentTimeMillis();
		
		start();
	}
		
	public void run()
	{
		try
		{
			vyhladavanie = new Vyhladavanie(odkial, kam, cez, datum, cas);
		}
		catch (FailingHttpStatusCodeException | IOException | NepodariloSaNajstSpojeException | ChybajuciElementException | ZlyFormatElementuException | InaChybaException e)
		{		
			chyba = true;
			chybovaHlaska = e.getMessage();
		}
	}
	
	public Vyhladavanie ziskajVyhladavanie()
	{
		return vyhladavanie;
	}
	
	public boolean nastalaChyba()
	{
		return chyba;
	}
	
	public String ziskajChybovuHlasku()
	{
		return chybovaHlaska;
	}
}
