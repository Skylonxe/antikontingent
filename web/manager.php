<?php

include 'communication.php';

function populateSession()
{
	$_SESSION['id'] = generateId(); 
	$_SESSION['from'] = $_GET['from'];
	$_SESSION['to'] = $_GET['to'];
	$_SESSION['through'] = $_GET['through'];
	$_SESSION['date'] = $_GET['date'];
	$_SESSION['time'] = $_GET['time'];
}

function generateId()
{
	return bin2hex(mcrypt_create_iv(22, MCRYPT_DEV_URANDOM));
}

function findRoutes($id, $from, $thr, $to, $date, $time)
{
	$socket = openConnection();	
	$input = prepareFindRoutesRequest($id, $from, $thr, $to, $date, $time);	
	$status = sendMessage($socket, $input);
	
	if($status !== FALSE)
	{
		$output = readMessage($socket);
		$output = substr($output, 2); 
	}
	else
	{
		echo "Failed";
	}
	
	closeConnection($socket);
	
	if(substr($output, 0, 5) == "ERROR")
	{
		$error = $output;
		return array("from" => "", "thr" => "", "to" => "", "date" => "", "time" => "", "routes" => []);
	}
	
	return parseFindRoutesResult($output);
}

function prepareFindRoutesRequest($id, $from, $thr, $to, $date, $time)
{
	return $id . " " . $from . " " . $to . " " . $thr . " " . date("d.m.Y", strtotime($date)) . " " . $time . "\n";
}

function parseFindRoutesResult($result)
{
	$data = explode("+", $result);
	
	$searchInfo = $data[0];
	$routeInfo = $data[1];
	
	$searchInfo = explode("@", $searchInfo);
	
	$from = $searchInfo[0];
	$thr = $searchInfo[1];
	$to = $searchInfo[2];
	$date = $searchInfo[3];
	$time = $searchInfo[4]; 
  
	$routes = explode("|", $routeInfo);
  
	for($i = 0; $i < count($routes); $i++)
	{
		$routes[$i] = explode("*", $routes[$i]);	   
		$routes[$i][0] = explode("&", $routes[$i][0]);
	   
		for($j = 0; $j < count($routes[$i][0]); $j++)
		{
			$routes[$i][0][$j] = explode("@", $routes[$i][0][$j]);
		}	   
	}
	
	return array("from" => $from, "thr" => $thr, "to" => $to, "date" => $date, "time" => $time, "routes" => $routes);
}

?>