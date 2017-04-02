import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.*;

public abstract class Formular {
	protected HtmlForm formular;
	
	public Formular(HtmlForm formular)
	{
		this.formular = formular;
		nacitajElementy();
	}
	
	public abstract void nacitajElementy();
	
	public abstract HtmlPage posli() throws IOException;
}
