import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

public class Spravca {
	private WebClient webClient;
	
	private HtmlPage strankaVyhladanieSpoja;
	private FormularVyhladatSpojenie formularVyhladatSpojenie;

	private HtmlPage strankaSpoje;
	private List<Spoj> spoje;

	private String vychodziaStanica;
	private String cielovaStanica;
	
	public Spravca(BrowserVersion verziaPrehliadaca)
	{
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		webClient = new WebClient(verziaPrehliadaca);
		webClient.getOptions().setJavaScriptEnabled(false);	
	}
	
	public void nacitajStrankuVyhladanieSpoja() throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		strankaVyhladanieSpoja = webClient.getPage("https://ikvc.slovakrail.sk/mobile-sales-web/pages/connection/searchParam.xhtml");		
		formularVyhladatSpojenie = new FormularVyhladatSpojenie(strankaVyhladanieSpoja.getFormByName("connectionParam"));    
	}
	
	public void nacitajStrankuSpoje() throws IOException
	{
		HtmlPage strankaVyhladanieSpoja2 = formularVyhladatSpojenie.posli();		
		final FormularVyhladatSpojenie formularVyhladatSpojenie2 = new FormularVyhladatSpojenie(strankaVyhladanieSpoja2.getFormByName("connectionParam")); 		
		
		strankaSpoje = formularVyhladatSpojenie2.posli();	
		parsujSpoje();
	}
	
	private void parsujSpoje()
	{	
		List<Object> elementySpojov = strankaSpoje.getByXPath("//div[@class='searched_connections']/div[@class='i']");
		spoje = new ArrayList<Spoj>();
		
		for(Object ch : elementySpojov)
		{
			spoje.add(new Spoj((DomElement) ch, spoje.size()));
		}
		
		DomElement zaciatokKoniecElem = (DomElement) strankaSpoje.getFirstByXPath("//div[@class='searched_connections']/div[@class='spoj']");
		String[] zaciatokKoniec = zaciatokKoniecElem.asText().split("/");
		vychodziaStanica = zaciatokKoniec[0].trim();
		cielovaStanica = zaciatokKoniec[1].trim();		
	}
	
	// Getters / Setters
	
	public FormularVyhladatSpojenie ziskajFormularVyhladatSpojenie() {
		return formularVyhladatSpojenie;
	}
	
	public List<Spoj> ziskajSpoje() {
		return spoje;
	}
}
