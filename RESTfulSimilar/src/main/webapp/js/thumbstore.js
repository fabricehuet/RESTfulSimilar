function getSize() {
	$.get("rest/hello/db/size", function(data) {
		// alert('page content: ' + data);
		document.getElementById('db_size').innerHTML = data;
	});
}

function getPath() {
	$.get("rest/hello/db/path", function(data) {
		// alert('page content: ' + data);
		document.getElementById('db_path').innerHTML = data;
	});
}

function getDuplicate() {
	// alert( $("input[name=max]").val() );
	// document.myform.submit();
	$
			.get(
					"rest/hello/identical",
					{
						max : $("input[name=max]").val()
					},
					function(data) {

						var i = 1;
						var output = ""; // '<div id="accordion">';
						for (i in data) {
							output += "<h3>";
							// output += property + ': ' + data[property] + ';
							// ';
							output += data[i].fileSize;
							output += "</h3>";
							output += "<div>";
							console.log(data[i]);
                            for (f in data[i].al) {
							// <p>" + i + "</p>"" +
                            	console.log(data[i].al[f]);
                                output+= data[i].al[f]+"<br>";
                            }
							output += "</div>";
						}
						var data2 = JSON.stringify(data, undefined, 2);
						// alert("Data Loaded: " + data2);
					// console.log(data2);
						// output += '</div>';
						document.getElementById('duplicate_result').innerHTML = "<div id=\"accordion\">     <h3><a href=\"#\">First header</a></h3>     <div>First content</div>     <h3><a href=\"#\">Second header</a></h3>     <div>Second content</div> </div>";
						$('#accordion').append(output).accordion('destroy')
								.accordion({collapsible:true,autoHeight: false});

						// output;
					});
}

function shrink() {
	$.get("rest/hello/shrink", function(data) {
		alert("shrink done");
	});
}

function index() {
	$.get("rest/hello/index", {path : $("input[name=index_path]").val()}, function(data) {
		console.log("index done on  ");
		prettyPrint($("input[name=index_path]").val());
	});
}

function prettyPrint(object) {
	for (i in object) {
		console.log(i + " " + object[i]);
	}
}
