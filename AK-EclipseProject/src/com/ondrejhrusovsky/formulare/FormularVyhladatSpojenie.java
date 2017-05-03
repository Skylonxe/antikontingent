package com.ondrejhrusovsky.formulare;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class FormularVyhladatSpojenie extends Formular
{
	private HtmlTextInput odkialTextovePole;
	private HtmlTextInput kamTextovePole;
	private HtmlTextInput cezTextovePole;
	private HtmlTextInput datumTextovePole;
	private HtmlTextInput casTextovePole;
	private HtmlSubmitInput vyhladatSpojenieTlacidlo;
	
	public FormularVyhladatSpojenie(HtmlForm formular)
	{
		super(formular);
	}
	
	@Override
	public void nacitajElementy() throws ElementNotFoundException
	{
		try
		{
			odkialTextovePole = formular.getInputByName(formular.getNameAttribute() + ":fromInput");
			kamTextovePole = formular.getInputByName(formular.getNameAttribute() + ":toInput");
			cezTextovePole = formular.getInputByName(formular.getNameAttribute() + ":viaInput");		
		}
		catch (ElementNotFoundException e)
		{
			// Je to v poriadku, pri pokuse stranky o autocorrect (ziadost o vyber z dropdown) tieto textove polia neexistuju
		}
		
		datumTextovePole = formular.getInputByName(formular.getNameAttribute() + ":date");
		casTextovePole = formular.getInputByName(formular.getNameAttribute() + ":time");
		vyhladatSpojenieTlacidlo = formular.getInputByName(formular.getNameAttribute() + ":submit");
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
