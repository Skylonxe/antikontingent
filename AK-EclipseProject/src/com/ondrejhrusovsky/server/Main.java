package com.ondrejhrusovsky.server;

public class Main
{
	public static void main(String[] args)
	{
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
		SocketIOKomServer.ziskajInstanciu().startServer();
	}
}
