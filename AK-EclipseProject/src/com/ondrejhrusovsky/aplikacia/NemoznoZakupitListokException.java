package com.ondrejhrusovsky.aplikacia;

public class NemoznoZakupitListokException extends Exception {
	private static final long serialVersionUID = 1L;

	public NemoznoZakupitListokException()
	{
		super("Listok sa neda zakupit (chyba tlacidlo Nakup)");
	}
}
