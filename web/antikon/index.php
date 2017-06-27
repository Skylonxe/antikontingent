<?php include 'include/header.php'; ?>

<?php

session_start();
date_default_timezone_set('Europe/Bratislava');

?>
    <div id='error' onclick="error()"  style='display: none; position: fixed; box-shadow: 0px 0px 5px 1px rgba(0,0,0,0.8);
      margin: 20px 10%; width: 80%; background-color: rgba(240,200,200,0.95); padding: 0px 2.5% 15px 2.5%; z-index: 50000;'>
      <h2>Chyba!</h2>
      <p>Chod do pice...</p>
    </div>

    <div onclick="pushInfo()" id='forceInfo' style='display: none; position: fixed; box-shadow: 0px 0px 5px 1px rgba(0,0,0,0.8);
      margin: 20px 10%; width: 80%; background-color: rgba(220,220,220,0.95); padding: 0px 2.5% 15px 2.5%; z-index: 50000;'>
      <h2>Informácie</h2>
      <p>Projekt na extreme programovanie (Oliver Sabik, Simona Backovska, Ondrej Hrusovsky)</p>

      <p style='font-size: large;'>
        Stav kontingentu:<br>
        <span class='fa fa-flag' style='color: green;'></span> Dostupný<br>
        <span class='fa fa-flag' style='color: orange;'></span> Nepodarilo sa získať stav<br>
        <span class='fa fa-flag' style='color: red;'></span> Nedostupný
      </p>
    </div>

    <div class="container" style='transition-duration: 0.5s;'>
      <img src='img/logo.png' alt='logo' style='width: 80%; max-width: 800px; display: block; margin: 30px auto;' />
      <form action="/antikon/search.php" method="get">
        <div class="form-group">
          <label for="form_from">Okiaľ:</label>
          <input type="text" class="form-control" id="form_from" placeholder="Mesto/obec" name="from" value="<?php if(isset($_SESSION['from'])) echo $_SESSION['from']; ?>">
        </div>
        <div class="form-group">
          <label for="form_to">Kam:</label>
          <input type="text" class="form-control" id="form_to" placeholder="Mesto/obec" name="to" value="<?php if(isset($_SESSION['to'])) echo $_SESSION['to']; ?>">
        </div>
        <div class="form-group">
          <label for="form_through">Cez:</label>
          <input type="text" class="form-control" id="form_through" placeholder="Mesto/obec" name="through" value="<?php if(isset($_SESSION['through'])) echo $_SESSION['through']; ?>">
        </div>
        <div class="form-group">
          <label for="form_date">Dátum:</label>
          <input type="date" class="form-control" id="form_date" name="date" value="<?php if(isset($_SESSION['date'])) echo $_SESSION['date']; else date('d.m.Y'); ?>">
        </div>
        <div class="form-group">
          <label for="form_time">Čas:</label>
          <input type="time" class="form-control" id="form_time" name="time" value="<?php if(isset($_SESSION['time'])) echo $_SESSION['time']; else echo date('G:i'); ?>">
        </div>
        <button type="submit" class="btn btn-warning">Vyhľadať</button>
        <button type="button" class="btn btn-info" onclick="pushInfo()">?</button>
      </form>
    </div>

<?php include 'include/footer.php';
 ?>
