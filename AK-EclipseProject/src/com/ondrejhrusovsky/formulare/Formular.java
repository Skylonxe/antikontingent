package com.ondrejhrusovsky.formulare;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public abstract class Formular
{
	protected HtmlForm formular;
	
	public Formular(HtmlForm formular)
	{
		this.formular = formular;
		nacitajElementy();
	}
	
	public abstract void nacitajElementy() throws ElementNotFoundException;
	
	public abstract HtmlPage posli() throws IOException;
}
