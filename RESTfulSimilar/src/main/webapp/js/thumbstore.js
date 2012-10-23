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
								//console.log(data[i].al[f]);
								output+="<div id=\"imagePath\">";
								output += data[i].al[f] + "</div><br>";
							}
							output += "</div>";
						}
						var data2 = JSON.stringify(data, undefined, 2);
						// alert("Data Loaded: " + data2);
						// console.log(data2);
						// output += '</div>';
						document.getElementById('duplicate_result').innerHTML = "<div id=\"accordion\">     <h3><a href=\"#\">First header</a></h3>     <div>First content</div>     <h3><a href=\"#\">Second header</a></h3>     <div>Second content</div> </div>";
						$('#accordion').append(output).accordion('destroy')
								.accordion({
									collapsible : true,
									autoHeight : false, 
									change : function (event, ui) {
										//console.log(ui.newContent.attr("div"));
										  var activeIndex = $("#accordion").accordion("option", "active");
										  arrayOfLines = ui.newContent.text().split("\n");
										//console.log(ui.newContent.text());
										  for (a in arrayOfLines) {
											  console.log(arrayOfLines[a]);
										  }
									}
								});

						// output;
					});
}

function shrink() {
	$.get("rest/hello/shrink", function(data) {
		alert("shrink done");
	});
}

function index(currentForm) {

	// jQuery('#file').click();
	prettyPrint(currentForm);
//	val = currentForm[0]; // document.getElementById("index_path").value;
	val =  document.getElementById("index_path").value;

	console.log(val);
	
	// prettyPrint(val);
	// prettyPrint(currentForm);
	$.get("rest/hello/index", {
		path : val
	}, function(data) {
		console.log("index done on  ");
		// prettyPrint(currentForm.index_path.value);
	});
}

function prettyPrint(object) {
	for (i in object) {
		console.log(i + " " + object[i]);
	}
	
function uploadxx(object) {
	$.post("rest/hello/upload", object);
}	
	
}
