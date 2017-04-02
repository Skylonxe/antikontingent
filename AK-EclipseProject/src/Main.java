import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

// Test git druhy test git

public class Main {

	public static void main(String[] args)
	{
		Spravca spravca = new Spravca(BrowserVersion.CHROME);
		
		try
		{
			spravca.nacitajStrankuVyhladanieSpoja();
		}
		catch (FailingHttpStatusCodeException | IOException e)
		{
			System.out.println("Chyba nacitania stranky vyhladania spoja!");
		}
		
		spravca.ziskajFormularVyhladatSpojenie().nastavOdkial("Bratislava");
		spravca.ziskajFormularVyhladatSpojenie().nastavKam("Kosice");
		spravca.ziskajFormularVyhladatSpojenie().nastavDatum("30.03.2017");
		spravca.ziskajFormularVyhladatSpojenie().nastavCas("15:00");
		
		try
		{
			spravca.nacitajStrankuSpoje();
		}
		catch (IOException e)
		{
			System.out.println("Chyba nacitania stranky spojov!");
		}
		
		List<Spoj> najdeneSpoje = spravca.ziskajSpoje();
		
		for(Spoj s : najdeneSpoje) // Prejdeme si vsetky najdene spoje
		{
			for(Vlak v : s.ziskajVlaky()) // Prejdem vsetky vlaky daneho spoja (jeden spoj moze mat viac vlakov - prestupy)
			{
				System.out.println(v.ziskajMeno()); // Napr. vypis nazov vlaku
			}
			
			// Vypis ako dlho trva cesta tymto spojom
			System.out.println("----------------Cesta trva: " + s.ziskajTrvanieHod() + "hod a " + s.ziskajTrvanieMin() + "min");
		}
		
	}

}
