<?php

include 'communication.php';

function populateSession()
{
	$_SESSION['requestId'] = generateRequestId(); 
	$_SESSION['from'] = $_GET['from'];
	$_SESSION['to'] = $_GET['to'];
	$_SESSION['through'] = $_GET['through'];
	$_SESSION['date'] = $_GET['date'];
	$_SESSION['time'] = $_GET['time'];
}

function populateJavascript()
{
	$requestId = $_SESSION['requestId'];
	$from = $_SESSION['from'];
	$to = $_SESSION['to'];
	$through = $_SESSION['through'];
	$date = date("d.m.Y", strtotime($_SESSION['date']));
	$time = $_SESSION['time'];
	
	echo "<script type=\"text/javascript\">" .
			"var requestId = \"$requestId\";" .
			"var from = \"$from\";" .
			"var to = \"$to\";" .
			"var through = \"$through\";" .
			"var date = \"$date\";" .
			"var time = \"$time\";" .
			"</script>";
}

function generateRequestId()
{
	return mt_rand(1,100000);
}

?>