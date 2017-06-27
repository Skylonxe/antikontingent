function pushInfo(){
  if( $("#forceInfo").is(":visible") ){
    $("#forceInfo").css("display", "none");
    $(".container").css("opacity", "1");
  }
  else {
    $("#forceInfo").css("display", "block");
    $(".container").css("opacity", "0.2")
  }
}

function showWaiter(){
	$("#waiter").css("display", "block");
}

function hideWaiter(){
	$("#waiter").css("display", "none");
}

function error(){
  if( $("#error").is(":visible") ){
    $("#error").css("display", "none");
    $(".container").css("opacity", "1");
  }
  else {
    $("#error").css("display", "block");
    $(".container").css("opacity", "0.2")
  }
}
