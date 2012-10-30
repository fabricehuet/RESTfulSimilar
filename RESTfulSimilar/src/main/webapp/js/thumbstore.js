function getSize() {
    $.get("rest/hello/db/size", function (data) {
        // alert('page content: ' + data);
        document.getElementById('db_size').innerHTML = data;
    });
}

function getPath() {
    $.get("rest/hello/db/path", function (data) {
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
            max:$("input[name=max]").val()
        },
        function (data) {

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
                    output += "<div class=\"floated_img\" id=\"imagePath\">";
                    output += data[i].al[f] + "</div>";
                }
                output += "</div>";
            }
            // var data2 = JSON.stringify(data, undefined, 2);
            // alert("Data Loaded: " + data2);
            // console.log(data2);
            // output += '</div>';
            //document.getElementById('duplicate_result').innerHTML = "<div id=\"accordion\">     <h3><a href=\"#\">First header</a></h3>     <div>First content</div>     <h3><a href=\"#\">Second header</a></h3>     <div>Second content</div> </div>";
            updateAccordion(output);
//						updateCoverFlip(output);

            //  jQuery('').nailthumb();

        });
}

function updateAccordion(output) {
    $('#accordion').children().remove();

    $('#accordion').append(output).accordion('destroy').accordion({
        collapsible:true,
        autoHeight:false,
        active:false,
        change:function (event, ui) {
            //  var activeIndex = $("#accordion").accordion("option", "active");
            //   var arrayOfLines = $("div", ui.newContent).contents(); //ui.newContent.text().split("\n");
            if ($(".nailthumb-container", ui.newContent).length == 0) {
                $.each($("[id=imagePath]", ui.newContent), function (index, data) {
                    console.log($(data).html());
                    $.post("rest/hello/getImage/", $(data).html(), function (image) {
                        var template = "<img src=\"data:image;base64,{{data}}\" title=\" {{title}} \"/>";
                        var imgTag = Mustache.to_html(template, image);
                        $(data).prepend('<div class="nailthumb-container nailthumb-image-titles-animated-onhover square">' + imgTag + "</div>");
                        jQuery(document).ready(function () {
                            jQuery('.nailthumb-container').nailthumb();
                            jQuery('.nailthumb-image-titles-animated-onhover').nailthumb();
                        })
                    });
                });
            }
        }

    });
}


function getDuplicateFolder() {
    console.log("get duplicate folder");
    $.getJSON('rest/hello/duplicateFolder', function (data) {

        $.each(data, function(key,val) {
            console.log("data is back");
                   console.log(val);
                   //var test = ""
                   console.log(JSON.stringify(data));
                   var template = "<h3>{{occurences}}</h3><div><div>{{folder1}}</div> <div>{{folder2}}</div></div>";
                   var html = Mustache.to_html(template, val);
            $('#accordion-duplicate-folders').append(html);
        });
        $('#accordion-duplicate-folders').accordion({
            collapsible:true,
            autoHeight:false,
            active:false
        });
        //$('#accordion-duplicate-folders').html(html);



    });
}

function shrink() {
    $.get("rest/hello/shrink", function (data) {
        alert("shrink done");
    });
}

function index(currentForm) {

    // jQuery('#file').click();
    prettyPrint(currentForm);
//	val = currentForm[0]; // document.getElementById("index_path").value;
    val = document.getElementById("index_path").value;

    console.log(val);

    // prettyPrint(val);
    // prettyPrint(currentForm);
    $.get("rest/hello/index", {
        path:val
    }, function (data) {
        console.log("index done on  ");
        // prettyPrint(currentForm.index_path.value);
    });
}

function prettyPrint(object) {
    for (i in object) {
        console.log(i + " " + object[i]);
    }
}

function uploadFinished(object) {
  //  console.log(object);
    //var output = "";
    $('#duplicate_upload_result').children().remove();
    for (f in object)  {
        //console.log(f);
        //output+= object.result[f].path+"<br>";
        var image = object[f];
        var rmse  = (image.rmse);
      //  console.log("Requesting file " + object.result[f].path);
       // getWithRMSE(object.result[f], rmse);

        var template = '<img src="data:image;base64,{{base64Data}}" title=" {{path}} "/>';
        var imgTag = Mustache.to_html(template, image);


        description = '<div class="description"> Score:' + rmse + '<br>Path:'+ image.path +'</div>'

//        $("#duplicate_upload_result").append('<div class="floated_img"><div class="nailthumb-container nailthumb-image-titles-animated-onhover square">' + imgTag + "</div>" + rmse +  "  " + image.path+ "</div>");
        $("#duplicate_upload_result").append('<div class="floated_img"><div class="nailthumb-container nailthumb-image-titles-animated-onhover square">' + imgTag + "</div>"+  description +"</div>");

        jQuery(document).ready(function () {
            jQuery('.nailthumb-container').nailthumb();
            jQuery('.nailthumb-image-titles-animated-onhover').nailthumb();
        });

    }
}

function getWithRMSE(param, rmse ) {
    $.post("rest/hello/getImage/", param.path, function (image) {
        //$('[title]',image);
        var template = "<img src=\"data:image;base64,{{data}}\" title=\" {{title}} \"/>";
        var imgTag = Mustache.to_html(template, image);
        //var imgTag = "<img src=\"data:image;base64," + image.data + "\" title=\"" + image.title + "\"/>";
        $("#duplicate_upload_result").prepend('<div class="floated_img"><div class="nailthumb-container nailthumb-image-titles-animated-onhover square">' + imgTag + "</div>" + rmse +  "  " + image.title+ "</div>");
//        Array.prototype.sort.call($("#floated_img"), function(a,b){
//            var av = $(a).find("rmse"+sortby).text();
//            var bv = $(b).find("rmse"+sortby).text();
//            return av-bv;
//        }).appendTo("#hotels");
        jQuery(document).ready(function () {
            jQuery('.nailthumb-container').nailthumb();
            jQuery('.nailthumb-image-titles-animated-onhover').nailthumb();
        }) ;

    });
}
