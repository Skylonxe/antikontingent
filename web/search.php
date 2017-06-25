<?php include 'include/header.php'; include 'manager.php'; ?>

<?php
	session_start();
	session_unset();
	set_time_limit(1000);
	date_default_timezone_set('Europe/Bratislava');
	
	populateSession();

	$result = findRoutes($_SESSION['id'], $_SESSION['from'], $_SESSION['through'], $_SESSION['to'], $_SESSION['date'], $_SESSION['time']);
	  
	$from = $result["from"];
	$thr = $result["thr"];
	$to = $result["to"];
	$date = $result["date"];
	$time = $result["time"];  
	$routes = $result["routes"];

	if(isset($error))
	{
?>

<div id='error' onclick="error()"  style='display: none; position: fixed; box-shadow: 0px 0px 5px 1px rgba(0,0,0,0.8);
      margin: 20px 10%; width: 80%; background-color: rgba(240,200,200,0.95); padding: 0px 2.5% 15px 2.5%; z-index: 50000;'>
	<h2>Chyba!</h2>
	<p><?php if(isset($error)) echo $error; ?></p>
</div>	  

<?php
		echo "<script type=\"text/javascript\">error();</script>";
  }
?>

<a class='backlink' href='index.php'><span class='fa fa-chevron-circle-left'></span></a>

  <div class="mainInfo">
	<?php echo $from; echo ($thr != "") ? " / $thr / " : " / "; echo "$to<br>"; ?>
    <?php echo "$date <b>$time</b>"; ?>
  </div>

<?php
  foreach($routes as $r){
    echo "<div class='mainBox'>";
    $status = false;
    $error = false;
	
	if ($r[1] == "T") $status = true;
	if ($r[1] == "E") $error = true;

    foreach($r[0] as $i){
      echo "<div class='trainBox'>
        <b>$i[0]</b><br>
        $i[1] / $i[2]<br><br>
        Odchod: <b>$i[3]</b> $i[4]<br>
        Príchod: <b>$i[5]</b> $i[6]
      </div>";
    }

    echo "<div class='info'>Stav kontingentu: ";
    if ($error) echo "<span class='fa fa-flag c0'></span>";
    else if ($status) echo "<span class='fa fa-flag c-1'></span> <span style='float: right;'><span class='fa fa-info-circle'></span> detaily</span>";
    else echo "<span class='fa fa-flag c1'></span>";
    echo "</div>";

    echo "</div>";
  }
?>


<?php include 'include/footer.php'; ?>
