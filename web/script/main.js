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

function search(){
  $("#waiter").css("display", "block");
  //$("#waiter").css("display", "none");
  //error();
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
