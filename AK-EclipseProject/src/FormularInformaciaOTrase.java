import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class FormularInformaciaOTrase extends Formular {
	private HtmlSubmitInput informaciaOTraseTlacidlo;
	
	public FormularInformaciaOTrase(HtmlForm formular) {
		super(formular);
	}
	
	@Override
	public void nacitajElementy() {
		informaciaOTraseTlacidlo = formular.getInputByName(formular.getNameAttribute() + ":submit");
	}
	
	@Override
	public HtmlPage posli() throws IOException {
		return informaciaOTraseTlacidlo.click();
	}
}
