package com.ondrejhrusovsky.console;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.exceptions.InaChybaException;
import com.ondrejhrusovsky.exceptions.NemoznoZakupitListokException;
import com.ondrejhrusovsky.exceptions.NepodariloSaNajstSpojeException;
import com.ondrejhrusovsky.exceptions.ZlyFormatElementuException;
import com.ondrejhrusovsky.ikvcAPI.Config;
import com.ondrejhrusovsky.ikvcAPI.Spoj;
import com.ondrejhrusovsky.ikvcAPI.Usek;
import com.ondrejhrusovsky.ikvcAPI.Vlak;
import com.ondrejhrusovsky.ikvcAPI.Vyhladavanie;

public class ConsoleMain {
	
	public static final String consoleAppVerzia = "1.0";
	
	public static final String ANSI_RESET = "";// "\u001B[0m";
	public static final String ANSI_BLACK = "";//"\u001B[30m";
	public static final String ANSI_RED = "";//"\u001B[31m";
	public static final String ANSI_GREEN = "";//"\u001B[32m";
	public static final String ANSI_YELLOW = "";//"\u001B[33m";
	public static final String ANSI_BLUE = "";//"\u001B[34m";
	public static final String ANSI_PURPLE = "";//"\u001B[35m";
	public static final String ANSI_CYAN = "";//"\u001B[36m";
	public static final String ANSI_WHITE = "";//"\u001B[37m";

	public static void main(String[] args) throws IOException
	{	
		PrintStream out = new PrintStream(System.out, true, "UTF-8");
		
		out.println(ANSI_CYAN + "\nANTIKONTINGENT (Console)\n" + ANSI_RESET);
		out.println(ANSI_WHITE + "=============================================\n");
		out.println("Autori: Simona Backovska, Ondrej Hrusovsky, Oliver Sabik" + "\n");
		out.println("Vyhladavanie najde par spojov, ktore odchadzaju po zadanom datume a case a vypise ich stav kontingentu.");
		out.println("Ak ma vlak obsadeny kontingent, tak sa okamzite zacne vyhladavanie volnych usekov kontingentu.");
		out.println("Cele vyhladavanie moze chvilu trvat.\n");
		out.println("Konzolova aplikacia nepodporuje diakritiku.\n");

		Scanner keyboard = new Scanner(new InputStreamReader(System.in, "UTF-8"));
		String odkial = "";
		
		out.println("=============================================\n" + ANSI_RESET);
		out.println(ANSI_CYAN + "VYHLADAVANIE\n" + ANSI_RESET);
		
		out.println(ANSI_WHITE + "Zadaj odkial:" + ANSI_RESET + ANSI_CYAN);	
		odkial = keyboard.nextLine();
		out.println(ANSI_RESET + ANSI_WHITE + "Zadaj kam:" + ANSI_RESET + ANSI_CYAN);		
		String kam = keyboard.nextLine();
		out.println(ANSI_RESET + ANSI_WHITE + "Zadaj cez (alebo nechaj prazdne):" + ANSI_RESET + ANSI_CYAN);		
		String cez = keyboard.nextLine();
		out.println(ANSI_RESET + ANSI_WHITE + "Zadaj cas (tvar 00:00):" + ANSI_RESET + ANSI_CYAN);		
		String cas = keyboard.nextLine();
		out.println(ANSI_RESET + ANSI_WHITE + "Zadaj datum (tvar 05.06.2017):" + ANSI_RESET + ANSI_CYAN);		
		String datum = keyboard.nextLine();
		out.print(ANSI_RESET);
		
		out.println("Vyhladavam spoje...");
		
		Vyhladavanie vyh;
		try {
			vyh = new Vyhladavanie(odkial.trim(), kam.trim(), cez.trim(), datum.trim(), cas.trim());
		} catch (FailingHttpStatusCodeException | IOException | NepodariloSaNajstSpojeException
				| ChybajuciElementException | ZlyFormatElementuException | InaChybaException e) {
			out.println(ANSI_RED + "Nastala chyba: " + e.getMessage() + ANSI_RESET);
			while(true)
			{
				try {
					Thread.sleep(9999999);
				} catch (InterruptedException e1) {
				}
			}
		}
		
		for(Spoj s : vyh.spoje)
		{		
			out.println("");
			out.print(">>>>>>  ");
			for(Vlak v : s.vlaky)
			{
				out.print(v.meno);
				
				if(s.vlaky.get(s.vlaky.size()-1) != v)
				{
					out.print(" --> ");
				}
			}
			out.println("  <<<<<<");
			
			try {
				out.println("Nacitavam kontingent...");
				s.nacitajKontingent();
			} catch (NemoznoZakupitListokException | IOException | ChybajuciElementException e) {
				out.println(ANSI_YELLOW + "Nastala chyba nacitania kontingentu: " + e.getMessage() + ANSI_RESET);
			}
			
			if(s.nacitanyKontingent)
			{
				if(s.kontingentVycerpany)
				{
					out.println(ANSI_RED + "--->>> Vlak ma vycerpany kontingent, zistujem poduseky..." + ANSI_RESET);	
					
					try {
						s.nacitajUsekyKontingentu();
					} catch (ZlyFormatElementuException | InaChybaException e) {
						out.println(ANSI_YELLOW + "Nastala chyba nacitania usekov kontingentu: " + e.getMessage() + ANSI_RESET);
					}
					
					if(s.nacitaneUsekyKontingentu)
					{
						for(Usek u : s.volneUseky)
						{				
							out.println(ANSI_GREEN + u.zac.nazov + " - " + u.kon.nazov + ANSI_RESET);
						}
					}	
				}
				else
				{
					out.println(ANSI_GREEN + "--->>> Vlak nema vycerpany kontingent" + ANSI_RESET);	
				}
			}
		}	
		
		out.println(ANSI_CYAN + "================ HOTOVO ================" + ANSI_RESET);
				
		keyboard.close();
		
		while(true)
		{
			try {
				Thread.sleep(9999999);
			} catch (InterruptedException e1) {
			}
		}
	}
}