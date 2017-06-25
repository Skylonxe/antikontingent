package com.ondrejhrusovsky.manazer;

import java.io.IOException;
import java.net.Socket;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.ondrejhrusovsky.aplikacia.Vyhladavanie;
import com.ondrejhrusovsky.aplikacia.ZlyFormatElementuException;
import com.ondrejhrusovsky.aplikacia.ChybajuciElementException;
import com.ondrejhrusovsky.aplikacia.InaChybaException;
import com.ondrejhrusovsky.aplikacia.NepodariloSaNajstSpojeException;

public class Vyhladavac extends Thread
{
	public Vyhladavanie vyhladavanie = null;
	
	String id;
	Socket socket;
	String odkial;
	String kam;
	String cez;
	String datum;
	String cas;
	
	Vyhladavac(Socket socket, String id, String odkial, String kam, String cez, String datum, String cas)
	{
		this.socket = socket;
		this.id = id;
		this.odkial = odkial;
		this.kam = kam;
		this.cez = cez;
		this.datum = datum;
		this.cas = cas;
		start();
	}
		
	public void run()
	{
		try {
			vyhladavanie = new Vyhladavanie(odkial, kam, cez, datum, cas);
			Manazer.ziskajInstanciu().vyhladavanieDokoncene(id, this);
		} catch (FailingHttpStatusCodeException | IOException | NepodariloSaNajstSpojeException | ChybajuciElementException | ZlyFormatElementuException | InaChybaException e) {		
			Manazer.ziskajInstanciu().vyhladavanieZlyhalo(id, e.getMessage(), this);
		}
	}
}
