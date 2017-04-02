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
	public void nacitajElementy()
	{
		try
		{
			odkialTextovePole = formular.getInputByName(formular.getNameAttribute() + ":fromInput");
		}
		catch(ElementNotFoundException e)
		{
			
		}
		try
		{
			kamTextovePole = formular.getInputByName(formular.getNameAttribute() + ":toInput");
		}
		catch(ElementNotFoundException e)
		{
			
		}
		try
		{
			cezTextovePole = formular.getInputByName(formular.getNameAttribute() + ":viaInput");
		}
		catch(ElementNotFoundException e)
		{
			
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
		this.odkialTextovePole.setValueAttribute(odkial);
	}

	public void nastavKam(String kam)
	{
		this.kamTextovePole.setValueAttribute(kam);
	}

	public void nastavCez(String cez)
	{
		this.cezTextovePole.setValueAttribute(cez);
	}

	public void nastavDatum(String datum)
	{
		this.datumTextovePole.setValueAttribute("Piatok, " + datum); // Netreba urcit spravny den, pretoze ikvc nekontroluje den
	}

	public void nastavCas(String cas)
	{
		this.casTextovePole.setValueAttribute(cas);
	}
}
