package com.ondrejhrusovsky.formulare;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.ondrejhrusovsky.aplikacia.ChybajuciElementException;
import com.ondrejhrusovsky.aplikacia.WebData;

public class FormularVyhladatSpojenieAutocorrect extends FormularVyhladatSpojenie
{
	public FormularVyhladatSpojenieAutocorrect(HtmlForm formular) throws ChybajuciElementException
	{
		super(formular);
	}

	@Override
	public void nacitajElementy() throws ChybajuciElementException
	{
		try
		{
			datumTextovePole = formular.getInputByName(WebData.NAME_VYHLADAVANIE_TEXTINPUT_DATUM);
			casTextovePole = formular.getInputByName(WebData.NAME_VYHLADAVANIE_TEXTINPUT_CAS);
			vyhladatSpojenieTlacidlo = formular.getInputByName(WebData.NAME_VYHLADAVANIE_SUBMITINPUT_VYHLADAT);	
		}
		catch (ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Formular FormularVyhladatSpojenieAutocorrect nenasiel jeden z elementov");
		}
	}
}
