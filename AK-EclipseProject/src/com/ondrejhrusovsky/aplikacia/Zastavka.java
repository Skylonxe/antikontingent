package com.ondrejhrusovsky.aplikacia;

public class Zastavka
{
	private String nazov;
	private int vzdialenost;
	private String casOdchodu; // moze byt prazdny ak je cielova stanica
	private String casPrichodu; // moze byt prazdny ak je startovna stanica
	
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
	
	public void nastavCasOdchodu(String casOdchodu)
	{
		this.casOdchodu = casOdchodu;
	}

	public String ziskajNazov()
	{
		return nazov;
	}

	public int ziskajVzdialenost()
	{
		return vzdialenost;
	}

	public String ziskajCasOdchodu()
	{
		return casOdchodu;
	}

	public String ziskajCasPrichodu()
	{
		return casPrichodu;
	}
}
