import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class FormularNakup extends Formular {
	private HtmlSubmitInput nakupTlacidlo;

	public FormularNakup(HtmlForm formular) {
		super(formular);
	}

	@Override
	public void nacitajElementy() {
		nakupTlacidlo = formular.getInputByName(formular.getNameAttribute() + ":submit");
	}

	@Override
	public HtmlPage posli() throws IOException {
		return nakupTlacidlo.click();
	}

}
