package com.ondrejhrusovsky.ikvcAPI;

public class Usek
{
	public Zastavka zac;
	public Zastavka kon;
	
	public Usek(Zastavka zac, Zastavka kon) 
	{
		this.zac = zac;
		this.kon = kon;
	}
	
	public String toString()
	{
		return zac.toString() + " - " + kon.toString();
	}
}
