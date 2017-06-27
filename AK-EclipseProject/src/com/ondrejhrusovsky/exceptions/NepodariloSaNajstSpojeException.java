package com.ondrejhrusovsky.exceptions;

public class NepodariloSaNajstSpojeException extends Exception {
	private static final long serialVersionUID = 1L;

	public NepodariloSaNajstSpojeException()
	{
		super("Nepodarilo sa otvorit stranku spojov, skontroluj zadane udaje");
	}
}
