package com.ondrejhrusovsky.manazer;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.ondrejhrusovsky.aplikacia.ChybajuciElementException;
import com.ondrejhrusovsky.aplikacia.NemoznoZakupitListokException;
import com.ondrejhrusovsky.aplikacia.Spoj;
import com.ondrejhrusovsky.aplikacia.Vlak;
import com.ondrejhrusovsky.aplikacia.Vyhladavanie;

import java.io.*;

public class SocketKomunikacia {
	private static SocketKomunikacia instancia;
	
    private static ServerSocket socket;
    
    private static int resultRetryAttemptCount = 5;

    private static int port = 4309;
   
    protected SocketKomunikacia()
    {
    	
    }
    
    public static SocketKomunikacia ziskajInstanciu()
	{
		if(instancia == null)
		{
			instancia = new SocketKomunikacia();
		}
		
		return instancia;
	}

    public void inicializuj()  {
        System.out.println("Back-end is starting.");

        try  {
            socket = new ServerSocket(port);

            while (true)  {
                Socket pripojenie = socket.accept();
                pripojenie.setKeepAlive(true);

                System.out.println("Accepted");

                InputStreamReader inputStream = new InputStreamReader(pripojenie.getInputStream(), "UTF-8");             
                BufferedReader input = new BufferedReader(inputStream);

                System.out.println("Waiting");
                
                DataInputStream datainputStream = new DataInputStream(pripojenie.getInputStream());
                String received = input.readLine();
                               
                System.out.println("Received " +  received);
                
                String[] s = received.split(" ");                            
                
                Manazer.ziskajInstanciu().zacniVyhladavanie(pripojenie, s[0], s[1], s[2], s[3], s[4], s[5]);

                System.out.println("Started search");
            }                   
            
        } catch (IOException e)  {
            System.out.println("Fail!: " + e.toString());
        }

        System.out.println("Closing...");
    }
    
    public synchronized void posliChybuVyhladavania(String id, String chyba, Vyhladavac vyhladavac)
    {
    	for(int i = 0; i < resultRetryAttemptCount; i++)
    	{
	    	if(socket != null)
	    	{
	    		try
	    		{
	    			DataOutputStream odpoved = new DataOutputStream(vyhladavac.socket.getOutputStream());
	    			StringBuilder data = new StringBuilder();
	    			
	    			data.append("ERROR " + chyba);
	    				    			
	    			System.out.println(data);
		    		
		    		odpoved.writeUTF(data.toString());
		    		odpoved.writeBytes("\n");
		    		odpoved.flush();
		    		break;
	    		}
	    		catch(IOException e)
	    		{
	    			continue;
	    		}
	    	}
    	}
    }
    
    public synchronized void posliVysledokVyhladavania(String id, Vyhladavac vyhladavac)
    {
    	Vyhladavanie vyhladavanie = vyhladavac.vyhladavanie;
    	
    	for(int i = 0; i < resultRetryAttemptCount; i++)
    	{
	    	if(socket != null)
	    	{
	    		try
	    		{
		    		DataOutputStream odpoved = new DataOutputStream(vyhladavac.socket.getOutputStream());
		    		
		    		List<Spoj> spoje = vyhladavanie.ziskajSpoje();
		    		
		    		StringBuilder data = new StringBuilder();
		    		
		    		data.append(vyhladavanie.ziskajVychodziaStanica() + "@");
		    		data.append(vyhladavanie.ziskajCez() + "@");
		    		data.append(vyhladavanie.ziskajCielovaStanica() + "@");
		    		data.append(vyhladavanie.ziskajDatum() + "@");
		    		data.append(vyhladavanie.ziskajCas() + "+");
		    		
		    		for(Spoj s : spoje)
		    		{
		    			for(Vlak v : s.ziskajVlaky())
		    			{
		    				data.append(v.ziskajMeno() + "@");
		    				data.append(v.ziskajNastupnaStanica() + "@"); 
		    				data.append(v.ziskajVystupnaStanica() + "@");
		    				data.append(v.ziskajNastupnyCas() + "@"); 
		    				data.append(v.ziskajNastupnyDatum() + "@"); 
		    				data.append(v.ziskajVystupnyCas() + "@"); 
		    				data.append(v.ziskajVystupnyDatum() + "@");
		    				data.append(v.ziskajVystupnyDatum() + "&");
		    			}
		    			data.deleteCharAt(data.length()-1);
		    			try {
							data.append("*" + (s.jeKontingentVycerpany() ? "T" : "F") + "|");
						} catch (ElementNotFoundException | NemoznoZakupitListokException | ChybajuciElementException e) {
							data.append("*" + "E" + "|");
						}
		    		}
		    		data.deleteCharAt(data.length()-1);
		    		
		    		System.out.println(data);
		    		
		    		odpoved.writeUTF(data.toString());
		    		odpoved.writeBytes("\n");
		    		odpoved.flush();
		    		break;
	    		}
	    		catch (IOException e)
	    		{
	    			continue;
	    		}
	    	}
	    }
    }
}