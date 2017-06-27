<?php include 'include/header.php'; include 'manager.php'; ?>

<?php
	session_start();
	session_unset();
	date_default_timezone_set('Europe/Bratislava');
	
	populateSession();
	populateJavascript();	
?>

<script src="js/socket.io/socket.io.js"></script>

<div onclick="pushInfo()" id='forceInfo' style='display: none; position: fixed; box-shadow: 0px 0px 5px 1px rgba(0,0,0,0.8);
      margin: 20px 10%; width: 80%; background-color: rgba(220,220,220,0.95); padding: 0px 2.5% 15px 2.5%; z-index: 50000;'>    
	  <div id='volneUseky'></div>
	  <div id='chybneUseky'></div>
</div>


<script type="text/javascript">

var socket =  io.connect('158.195.210.170:9092', {
  'reconnection': true,
  'reconnectionDelay': 50,
  'reconnectionAttempts': 30,
  'transports': [ 'websocket' ]
});
var jsonObject = { requestId : requestId, from : from, to : to,  through : through, date : date, time : time };
socket.emit('searchRequest', jsonObject);
var searchResult = null;

function requestContingentDetails(routeIdx)
{
	var jsonObject = { requestId : requestId, routeIdx : routeIdx };
	socket.emit('contingentDetailsRequest', jsonObject);
	
	document.getElementById("detail-" + routeIdx).innerHTML = "<span class='spinner'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>";			
}

function showContingentDetails(routeIdx)
{
	htmlVolneUseky = "<h2>Kontingent je voľný na úsekoch</h2>";
	htmlChybneUseky = "<h2>Kontingent sa nepodarilo overiť na úsekoch</h2>";
	
	for (var i = 0; i < searchResult.spoje[routeIdx].volneUseky.length; i++)
	{
		htmlVolneUseky += "<p>" + searchResult.spoje[routeIdx].volneUseky[i].zac.nazov + " - " + searchResult.spoje[routeIdx].volneUseky[i].kon.nazov + "</p>";
	}
	
	for (var i = 0; i < searchResult.spoje[routeIdx].chybneUseky.length; i++)
	{
		htmlChybneUseky += "<p>" + searchResult.spoje[routeIdx].chybneUseky[i].zac.nazov + " - " + searchResult.spoje[routeIdx].chybneUseky[i].kon.nazov + "</p>";
	}
	
	document.getElementById("volneUseky").innerHTML = htmlVolneUseky;
	
	if(searchResult.spoje[routeIdx].chybneUseky.length > 0)
	{
		document.getElementById("chybneUseky").innerHTML = htmlChybneUseky;
	}
	
	pushInfo();
}


</script>

<div id='waiter' style='display: none; position: fixed; width: 100%; height: 100%; background-color: rgba(0,0,0,0.7);'>
      <span style='position: absolute; margin-left: -100px; top: 35%; left: 50%; width: 200px; font-size: 100px; color: white;' class='fa fa-spinner fa-pulse fa-3x fa-fw'></span>
</div>

<div id='error' style='display: none; position: fixed; box-shadow: 0px 0px 5px 1px rgba(0,0,0,0.8);
      margin: 20px 10%; width: 80%; background-color: rgba(240,200,200,0.95); padding: 0px 2.5% 15px 2.5%; z-index: 50000;'>
	<h2>Chyba!</h2>
	<p id='error-message'></p>
</div>	  

<a class='backlink' href='index.php'><span class='fa fa-chevron-circle-left'></span></a>

 <script type="text/javascript">
	showWaiter();
	
	socket.on('test', function(d) {
		console.log("test ok");
		console.log(d);
	});
 
	socket.on('searchSuccessful', function(searchResultData) {
		searchResult = searchResultData;
		hideWaiter();
		
		var htmlResult = "";		
		var info = "";
		
		info += searchResult.vychodziaStanica + " / " + searchResult.cielovaStanica + "<br>";
		info += searchResult.datum + "<b> " + searchResult.cas + "</b>";
		
		document.getElementById("mainInfo").innerHTML = info;
		 
		for(var i = 0; i < searchResult.spoje.length; i++)
		{
			var spoj = searchResult.spoje[i];
			
			htmlResult += "<div class='mainBox'>";
			 
			for(var j = 0; j < spoj.vlaky.length; j++)
			{
				var vlak = spoj.vlaky[j];
					
				htmlResult += "<div class='trainBox'>";
				htmlResult += "<b>" + vlak.meno + "</b><br>";
				htmlResult += vlak.nastupnaStanica.nazov + "/" + vlak.vystupnaStanica.nazov + "<br><br>";
				htmlResult += "Odchod: <b>" + vlak.nastupnyCas + "  </b>" + vlak.nastupnyDatum + "<br>";
				htmlResult += "Príchod: <b>" + vlak.vystupnyCas + "  </b>" + vlak.vystupnyDatum;
				htmlResult += "</div>";
			 }
			
			htmlResult += "<div class='info'>Stav kontingentu: ";		
						
			if(spoj.chybaNacitaniaKontingentu)
			{
				htmlResult += "<span class='fa fa-flag c0'></span>";
			}
			else
			{
				if(spoj.nacitanyKontingent)
				{
					if(spoj.kontingentVycerpany)
					{
						htmlResult += "<span class='fa fa-flag c-1'></span>";					
						htmlResult += "<span style='float: right;'><span class='fa fa-info-circle'>";
						htmlResult += "<span id='detail-" + i + "'>";
						
						if(spoj.chybaNacitaniaUsekovKontingentu)
						{
							htmlResult += "<span> Chyba</span>";	
						}
						else
						{
							
							if(spoj.nacitaneUsekyKontingentu)
							{
								htmlResult += "<a href='' id='" + i + "' onclick='showContingentDetails(id); return false;'> Zobraziť</a>";
							}
							else
							{
								if(spoj.nacitavamUsekyKontingentu)
								{
									htmlResult += "<span class='spinner'></span>";									
								}
								else
								{
									htmlResult += "<a href='' id='" + i + "' onclick='requestContingentDetails(id); return false;'> Podrobnosti</a>";
								}				
							}
						}
						
						htmlResult += "</span>";
						htmlResult += "</span>";
						htmlResult += "</span>";
					}
					else
					{
						htmlResult += "<span class='fa fa-flag c1'></span>";
					}
				}
				else
				{
					htmlResult += "<span class='spinner'></span>";
				}
									
			}		

			htmlResult += "</div>";
			htmlResult += "</div>";
		}
		 
		 document.getElementById("searchResultRoutes").innerHTML = htmlResult;
	});

	socket.on('searchError', function(message) {
		
		console.log("HEY");
		document.getElementById("mainInfo").innerHTML = "";
		document.getElementById("searchResultRoutes").innerHTML = "";
		document.getElementById("error-message").innerHTML = message;
		error();
		hideWaiter();
		
	});
 </script>
 
<div class="mainInfo" id="mainInfo">
</div>
 
<div id='searchResultRoutes'>
</div>

<?php include 'include/footer.php'; ?>
