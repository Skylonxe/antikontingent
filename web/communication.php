<?php

$address = 'localhost';
$port = 4309;

function openConnection()
{
	global $address, $port;
	
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	socket_connect($socket, $address, $port);
	
	return $socket;
}

function closeConnection($socket)
{
	socket_close($socket);
}

function sendMessage($socket, $message)
{
	global $address, $port;
	
	return socket_sendto($socket, $message, strlen($message), 0, $address, $port);
}

function readMessage($socket)
{
	$message = "";
	$next = "";

	while(true)
	{
		$next = socket_read($socket, 4096);
			
		if($next == "\n" || $next == "")
		{
			break;
		}
			
		$message .= $next;
	}
 		
	return $message;
}

?>