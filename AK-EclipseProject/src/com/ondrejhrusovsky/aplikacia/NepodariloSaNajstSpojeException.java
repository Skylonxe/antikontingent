package com.ondrejhrusovsky.aplikacia;

public class NepodariloSaNajstSpojeException extends Exception {
	private static final long serialVersionUID = 1L;

	public NepodariloSaNajstSpojeException()
	{
		super("Nepodarilo sa otvorit straku spojov, skontroluj zadane udaje");
	}
}
