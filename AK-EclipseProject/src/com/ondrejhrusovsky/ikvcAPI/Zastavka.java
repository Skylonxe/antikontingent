package com.ondrejhrusovsky.ikvcAPI;

public class Zastavka
{
	public String nazov;
	public int vzdialenost;
	public String casOdchodu; // moze byt prazdny ak je cielova stanica
	public String casPrichodu; // moze byt prazdny ak je startovna stanica
	
	public Zastavka(String nazov, int vzdialenost, String casOdchodu, String casPrichodu)
	{
		this.nazov = nazov;
		this.vzdialenost = vzdialenost;
		this.casOdchodu = casOdchodu;
		this.casPrichodu = casPrichodu;
	}
	
	public String toString()
	{
		return nazov;
	}
}
