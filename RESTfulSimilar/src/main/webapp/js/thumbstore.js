
function getSize() {
$.get(
    "rest/hello/db/size",
    function(data) {
      // alert('page content: ' + data);
    	document.getElementById('db_size').innerHTML=data;
    }
);
}


function getPath() {
$.get(
    "rest/hello/db/path",
    function(data) {
      // alert('page content: ' + data);
    	document.getElementById('db_path').innerHTML=data;
    }
);
}


function getDuplicate() {
	//alert( $("input[name=max]").val() );
	// document.myform.submit();
	$.get("rest/hello/identical", { max: $("input[name=max]").val() },
			function(data){
	//	alert("Data Loaded: " + data);
		document.getElementById('duplicate_result').innerHTML = data
	   });
}
