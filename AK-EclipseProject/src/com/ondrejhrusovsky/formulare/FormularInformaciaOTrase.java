package com.ondrejhrusovsky.formulare;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.ondrejhrusovsky.exceptions.ChybajuciElementException;
import com.ondrejhrusovsky.ikvcAPI.Config;

public class FormularInformaciaOTrase extends Formular
{
	private HtmlSubmitInput informaciaOTraseTlacidlo;
	
	public FormularInformaciaOTrase(HtmlForm formular) throws ChybajuciElementException
	{
		super(formular);
	}
	
	@Override
	public void nacitajElementy() throws ChybajuciElementException
	{
		try
		{		
			informaciaOTraseTlacidlo = formular.getInputByName(formular.getNameAttribute() + Config.NAME_SPOJE_SUBMITINPUT_INFO_SUFFIX);
		}
		catch(ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Formular FormularInformaciaOTrase nenasiel tlacidlo informacie o trase");
		}
	}
	
	@Override
	public HtmlPage posli() throws IOException
	{
		return informaciaOTraseTlacidlo.click();
	}
}
