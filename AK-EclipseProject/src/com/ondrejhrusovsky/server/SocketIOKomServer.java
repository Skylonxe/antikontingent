package com.ondrejhrusovsky.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckMode;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.exceptions.NemoznoZakupitListokException;
import com.ondrejhrusovsky.ikvcAPI.Spoj;
import com.ondrejhrusovsky.ikvcAPI.Vyhladavanie;

public class SocketIOKomServer {
	private static SocketIOKomServer instancia;
	private static SocketIOServer server;
	private static Configuration config;

	private final static String hostname = "0.0.0.0";
    private final static int port = 9092;
      
    protected SocketIOKomServer()
    {
    	config = new Configuration();
        config.setPort(port);
    }
    
    public static SocketIOKomServer ziskajInstanciu()
	{
		if(instancia == null)
		{
			instancia = new SocketIOKomServer();
		}
		
		return instancia;
	}
	
    public void startServer()
    {
        server = new SocketIOServer(config);
        Logger logger = LoggerFactory.getLogger(SocketIOKomServer.class);
        
        System.out.println("RUNNING");
  
        server.addEventListener("searchRequest", RequestRoutesObject.class, new DataListener<RequestRoutesObject>()
        {
            @Override
            public void onData(SocketIOClient client, RequestRoutesObject request, AckRequest ackRequest)
            {
            	System.out.println("searchRequest " + request.toString());
            	Vyhladavac v = Manazer.ziskajInstanciu().zacniVyhladavanie(request.requestId, request.from, request.to, request.through, request.date, request.time);            	
            	
            	try
            	{
					v.join();
					
					if(v.nastalaChyba())
	            	{
						System.out.println("searchError " + v.ziskajChybovuHlasku());
	            		client.sendEvent("searchError", v.ziskajChybovuHlasku());
	            	}
	            	else
	            	{
	            		System.out.println("searchSuccessful");
	            		client.sendEvent("searchSuccessful", v.ziskajVyhladavanie());
	            		client.sendEvent("test", "TEST");
	            		
	            		for(Spoj s : v.ziskajVyhladavanie().spoje)
	            		{
	            			try {
								s.nacitajKontingent();
							} catch (NemoznoZakupitListokException | IOException | ChybajuciElementException e) {
								System.out.println("Chyba nacitania kontingentu " + e.getMessage());
							}
	            			
	            			client.sendEvent("searchSuccessful", v.ziskajVyhladavanie());
	            		}  
	            		
	            		client.sendEvent("searchSuccessful", v.ziskajVyhladavanie());
	            	}
					
				}
            	catch (InterruptedException e)
            	{
            		System.out.println("searchError - interrupted exception");
					client.sendEvent("searchError", "Internal error");
				}       	            
            }
        });
        
        server.addEventListener("contingentDetailsRequest", RequestContingentDetailsObject.class, new DataListener<RequestContingentDetailsObject>()
        {
            @Override
            public void onData(SocketIOClient client, RequestContingentDetailsObject request, AckRequest ackRequest)
            {
            	System.out.println("DETAIL REQUEST");
            	Vyhladavanie v = Manazer.ziskajInstanciu().ziskajVyhladavanie(request.requestId);
            	System.out.println("B");
            	Manazer.ziskajInstanciu().nacitajUsekyKontingentu(request.requestId, request.routeIdx);  
            	System.out.println("FINE");
            	if(v == null)
            	{
            		client.sendEvent("searchError", "Poziadavke vyprsal cas, spoje je nutne opatovne vyhladat");
            	}
            	else
            	{
            		client.sendEvent("searchSuccessful", v);
            	}     	
            }
        });
                
        server.start();

        while(true)
        {
        	try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				System.out.println("Sleep Error");
			}
        }
        
        //server.stop();
    }
}

