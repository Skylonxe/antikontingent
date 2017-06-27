package com.ondrejhrusovsky.formulare;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.ikvcAPI.Config;

public class FormularVyhladatSpojenie extends Formular
{
	private HtmlTextInput odkialTextovePole;
	private HtmlTextInput kamTextovePole;
	private HtmlTextInput cezTextovePole;
	protected HtmlTextInput datumTextovePole;
	protected HtmlTextInput casTextovePole;
	protected HtmlSubmitInput vyhladatSpojenieTlacidlo;
	
	public FormularVyhladatSpojenie(HtmlForm formular) throws ChybajuciElementException
	{
		super(formular);
	}
	
	@Override
	public void nacitajElementy() throws ChybajuciElementException
	{
		try
		{
			odkialTextovePole = formular.getInputByName(Config.NAME_VYHLADAVANIE_TEXTINPUT_ODKIAL);
			kamTextovePole = formular.getInputByName(Config.NAME_VYHLADAVANIE_TEXTINPUT_KAM);
			cezTextovePole = formular.getInputByName(Config.NAME_VYHLADAVANIE_TEXTINPUT_CEZ);	
			datumTextovePole = formular.getInputByName(Config.NAME_VYHLADAVANIE_TEXTINPUT_DATUM);
			casTextovePole = formular.getInputByName(Config.NAME_VYHLADAVANIE_TEXTINPUT_CAS);
			vyhladatSpojenieTlacidlo = formular.getInputByName(Config.NAME_VYHLADAVANIE_SUBMITINPUT_VYHLADAT);
		}
		catch (ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Formular FormularVyhladatSpojenie nenasiel jeden z elementov.");
		}
	}
	
	@Override
	public HtmlPage posli() throws IOException
	{
		return vyhladatSpojenieTlacidlo.click();
	}
	
	public void nastavOdkial(String odkial)
	{
		odkialTextovePole.setValueAttribute(odkial);
	}

	public void nastavKam(String kam)
	{
		kamTextovePole.setValueAttribute(kam);
	}

	public void nastavCez(String cez)
	{
		cezTextovePole.setValueAttribute(cez);
	}

	public void nastavDatum(String datum)
	{
		datumTextovePole.setValueAttribute("Piatok, " + datum); // Netreba urcit spravny den v tyzdni, pretoze ikvc nekontroluje den
	}

	public void nastavCas(String cas)
	{
		casTextovePole.setValueAttribute(cas);
	}
}
