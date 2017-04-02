import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomElement;

public class Vlak {	
	private String meno;
	private Zastavka[] zastavky;
	private String poznamka;

	private String nastupnaStanica;
	private String nastupnyDatum;
	private String nastupnyCas;
	
	private String vystupnaStanica;
	private String vystupnyDatum;
	private String vystupnyCas;
	
	public Vlak(DomElement elementSpoja, int idVlaku) {
		List<Object> h2Elementy = elementSpoja.getByXPath("h2");
		
		meno = ((DomElement) h2Elementy.get(idVlaku)).asText().trim();
		
		// to-do
	}

	public String ziskajMeno() {
		return meno;
	}

	public Zastavka[] ziskajZastavky() {
		return zastavky;
	}

	public String ziskajPoznamku() {
		return poznamka;
	}

	public String ziskajNastupnaStanica() {
		return nastupnaStanica;
	}

	public String ziskajNastupnyDatum() {
		return nastupnyDatum;
	}

	public String ziskajNastupnyCas() {
		return nastupnyCas;
	}

	public String ziskajVystupnaStanica() {
		return vystupnaStanica;
	}

	public String ziskajVystupnyDatum() {
		return vystupnyDatum;
	}

	public String ziskajVystupnyCas() {
		return vystupnyCas;
	}
}
