import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Spoj {
	private List<Vlak> vlaky;
	private int trvanieHod;
	private int trvanieMin;
	
	private FormularInformaciaOTrase formularInformaciaOTrase;
	private FormularNakup formularNakup;
	
	public Spoj(DomElement elementSpoja, int idSpoja)
	{
		HtmlPage strankaSpoje = elementSpoja.getHtmlPageOrNull();
		formularInformaciaOTrase = new FormularInformaciaOTrase(strankaSpoje.getFormByName("j_idt89:" + idSpoja + ":j_idt131"));
		
		try
		{
			formularNakup = new FormularNakup(strankaSpoje.getFormByName("j_idt89:" + idSpoja + ":j_idt144"));
		}
		catch(ElementNotFoundException e)
		{
			// Na tento spoj sa neda zakupit cestovny listok (chyba tlacidlo Nakup)
		}	
		
		
		List<Object> h2Elementy = elementSpoja.getByXPath("h2");
		vlaky = new ArrayList<Vlak>();

		for(Object e : h2Elementy)
		{
			vlaky.add(new Vlak(elementSpoja, vlaky.size()));
		}
		
		DomElement trvanieElem = (DomElement) elementSpoja.getFirstByXPath("div[@class='info']/p/strong");		
		String[] minHod = trvanieElem.asText().replace("min", "").split("hod");
		trvanieHod = Integer.parseInt(minHod[0].trim());
		trvanieMin = Integer.parseInt(minHod[1].trim());
	}

	public FormularInformaciaOTrase ziskajFormularInformaciaOTrase() {
		return formularInformaciaOTrase;
	}

	public FormularNakup ziskajFormularNakup() {
		return formularNakup;
	}

	public List<Vlak> ziskajVlaky() {
		return vlaky;
	}

	public int ziskajTrvanieHod() {
		return trvanieHod;
	}

	public int ziskajTrvanieMin() {
		return trvanieMin;
	}
}
