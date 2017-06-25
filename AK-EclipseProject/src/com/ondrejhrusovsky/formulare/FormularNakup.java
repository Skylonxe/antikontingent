package com.ondrejhrusovsky.formulare;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.ondrejhrusovsky.aplikacia.ChybajuciElementException;
import com.ondrejhrusovsky.aplikacia.WebData;

public class FormularNakup extends Formular
{
	private HtmlSubmitInput nakupTlacidlo;

	public FormularNakup(HtmlForm formular) throws ChybajuciElementException
	{
		super(formular);
	}

	@Override
	public void nacitajElementy() throws ChybajuciElementException
	{
		try
		{
			nakupTlacidlo = formular.getInputByName(formular.getNameAttribute() + WebData.NAME_SPOJE_SUBMITINPUT_NAKUP_SUFFIX);
		}
		catch (ElementNotFoundException e)
		{
			throw new ChybajuciElementException("Formular FormularNakup nenasiel tlacidlo nakup");
		}
	}

	@Override
	public HtmlPage posli() throws IOException
	{
		return nakupTlacidlo.click();
	}
}
