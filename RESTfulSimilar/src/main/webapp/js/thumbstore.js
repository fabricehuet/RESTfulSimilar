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

function getIndexedPaths(div) {
    $.get("rest/hello/paths", function (data) {
        var val = 1;
        var cbh = document.getElementById('db_paths');
        for (i in data) {
            var cb = document.createElement('input');
            cb.type = 'checkbox';
            cb.checked=true;
            cbh.appendChild(cb);
            cb.name = "folder";
            cb.value = data[i];
            cbh.appendChild(document.createTextNode(data[i]));
            val++;
            cbh.appendChild(document.createElement('br'));
        }
    });
}


function getStatus() {
    $.get("rest/hello/status", function (data) {
        // alert('page content: ' + data);
        document.getElementById('db_status').innerHTML = data["stringStatus"];
    });
}


function getDuplicate() {
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
                output += data[i].fileSize / 1024.0 / 1024;
                output += "</h3>";
                output += "<div>";
                console.log(data[i]);
                for (f in data[i].al) {
                    // <p>" + i + "</p>"" +
                    //console.log(data[i].al[f]);
                    output += '<div id="imagePath">';
                    // output += data[i].al[f] + "</div>";
                    output += toFolderAndFileLink(data[i].al[f]) + "</div>";
                }
                output += "</div>";
            }
            updateAccordion(output);
        });
}

function toFolderLink(path) {
    return path + '  <a href="explorer://rest/hello/folder/?path=' + path + '">[folder]</a>'
}


function toFolderAndFileLink(path) {
    var n = path.lastIndexOf('/');
    //  var file = path.substring(n + 1);
    var folder = path.substring(0, n);

    return path + '  <a href="explorer://rest/hello/folder/?path=' + path + '">[file]</a>' +
        '  <a href="explorer://rest/hello/folder/?path=' + folder + '">[folder]</a>'
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
                    //  console.log($(data).html());
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


function getDuplicateFolderDetails(ui, folder1, folder2) {
    // alert('' + folder1 + "\n" + folder2);


    $.getJSON('rest/hello/duplicateFolderDetails', {
            folder1:folder1,
            folder2:folder2
        },
        function (data) {
            console.log(data);
            var tab = { files:[ ] };
            //debugger;
            for (var i = 0; i < data.file1.length; ++i)
                tab.files.push({
                    f1:data.file1[i],
                    f2:data.file2[i]
                });
            var templateFiles = ' {{#files}} ' + '<div>{{f1}}   <a href="explorer://rest/hello/folder/?path={{f1}}">[file]</a><br>' +
                '{{f2}}   <a href="explorer://rest/hello/folder/?path={{f2}}">[file]</a></div><br>' +
                '{{/files}}';
            var htmlFiles = Mustache.to_html(templateFiles, tab);
            $('#duplicate-folders-details').children().remove();
            $('#duplicate-folders-details').append(htmlFiles);
        });

}


function getSelectedFolders() {
    var inputs = $("input[name=folder]");
    var folders = [];
    for (i=0; i<inputs.length;i++) {
        if (inputs[i].checked) {
            folders[i] = inputs[i].value;
        }

    }
    console.log(folders);
    return folders;
}

function getDuplicateFolder() {

    jQuery.ajaxSettings.traditional = true;
    console.log("get duplicate folder");
    var folders = getSelectedFolders();
    $('#accordion-duplicate-folders').children().remove();
    $.getJSON('rest/hello/duplicateFolder', {folder:folders}, function (data) {
        $.each(data, function (key, val) {
            console.log("data is back");
            console.log(val);
            val['totalSize'] = val['totalSize'] / 1024.0 / 1024;
            var template = '<h3>{{occurences}} ({{totalSize}})</h3>' +
                '<div>' +
                '<div>' + toFolderLink("{{folder1}}") + '</div> ' +
                '<div>' + toFolderLink("{{folder2}}") + '</div> ' +
                '</div>';
            var html = Mustache.to_html(template, val);

            $('#accordion-duplicate-folders').append(html);
        });

        $('#accordion-duplicate-folders').accordion('destroy').accordion({
            collapsible:true,
            autoHeight:false,
            active:false,
            activate:function (event, ui) {
                // console.log("activated baby! " + ui);
                // debugger;
                if (ui.newHeader.length > 0) {
                    folder1 = ui.newPanel.find('a')[0].text;
                    folder2 = ui.newPanel.find('a')[1].text;
                    getDuplicateFolderDetails(ui, folder1, folder2);
                } else {
                    $('#duplicate-folders-details').children().remove();
                }

            }
        });
       // updateAccortion();
        //$('#accordion-duplicate-folders').html(html);


    });
}

function shrink() {
    $.get("rest/hello/shrink", function (data) {
        //alert("shrink done");
    });
}

function update() {
    $.get("rest/hello/update", function (data) {
        //alert("shrink done");
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
    for (f in object) {
        //console.log(f);
        //output+= object.result[f].path+"<br>";
        var image = object[f];
        var rmse = (image.rmse);
        //  console.log("Requesting file " + object.result[f].path);
        // getWithRMSE(object.result[f], rmse);

        var template = '<img src="data:image;base64,{{base64Data}}" title="{{path}} "/>';
        var imgTag = Mustache.to_html(template, image);

        //console.log($(imgTag).exifPretty());
        description = '<div class="description flt"> Distance:' + rmse + '<br>  ' + toFolderAndFileLink(image.path)+ '</a><br></div>'

//        $("#duplicate_upload_result").append('<div class="floated_img"><div class="nailthumb-container nailthumb-image-titles-animated-onhover square">' + imgTag + "</div>" + rmse +  "  " + image.path+ "</div>");
        $("#duplicate_upload_result").append('<div class="floated_img cls"><div class="nailthumb-container nailthumb-image-titles-animated-onhover square flt">' + imgTag + "</div>" + description + "</div>");


    }
    jQuery(document).ready(function () {
        jQuery('.nailthumb-container').nailthumb();
        jQuery('.nailthumb-image-titles-animated-onhover').nailthumb();
    });
}


function getWithRMSE(param, rmse) {
    $.post("rest/hello/getImage/", param.path, function (image) {
        //$('[title]',image);
        var template = "<img src=\"data:image;base64,{{data}}\" title=\" {{title}} \"/>";
        var imgTag = Mustache.to_html(template, image);
        //var imgTag = "<img src=\"data:image;base64," + image.data + "\" title=\"" + image.title + "\"/>";
        $("#duplicate_upload_result").prepend('<div class="floated_img"><div class="nailthumb-container nailthumb-image-titles-animated-onhover square">' + imgTag + "</div>" + rmse + "  " + image.title + "</div>");
//        Array.prototype.sort.call($("#floated_img"), function(a,b){
//            var av = $(a).find("rmse"+sortby).text();
//            var bv = $(b).find("rmse"+sortby).text();
//            return av-bv;
//        }).appendTo("#hotels");
        jQuery(document).ready(function () {
            jQuery('.nailthumb-container').nailthumb();
            jQuery('.nailthumb-image-titles-animated-onhover').nailthumb();
        });

    });
}
