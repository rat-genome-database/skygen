// used by report pages

var headerRefs = [];

function regHeader(title) {
    document.getElementById(title).onclick = showSection;
    document.getElementById(title + "_i").onclick = showSection;
    document.getElementById(title + "_content").style.display = "none";

    headerRefs.push(document.getElementById(title));
}

function openAll() {
    for (var i=0; i < headerRefs.length; i++) {
        if(headerRefs[i].id.substr(headerRefs[i].id.length -1) != "C"){
            openSection(headerRefs[i]);
        }
    }
}

function showSection(e) {
    if (!e) e = window.event;
    var obj = document.all ? e.srcElement : e.currentTarget;

    if (document.all) {
        if (obj.id.indexOf("_i") != -1) {
            var name = obj.id.substring(0,obj.id.indexOf("_i"));
            obj=document.getElementById(name);
        }
        window.event.cancelBubble = true;
    }else {

    }

    openSection(obj);
}


function openSection(obj) {
    var sister = null;
    if (obj.id.substr(obj.id.length -1) == "C") {
        sister = document.getElementById(obj.id.substr(0, obj.id.length - 1));
    }else {
        sister = document.getElementById(obj.id + "C");
    }

    for (i=0; i < 2; i++) {
        if (i==1) {
            obj=sister;
            if (!obj) return;
        }

        if (obj) {
            var content =  document.getElementById(obj.id + "_content");
            if (content) {
                if (content.style.display == "block") {
                    content.style.display="none";
                    document.getElementById(obj.id + "_i").src = "/rgdweb/common/images/add.png";
                }else {
                    content.style.display="block";
                    document.getElementById(obj.id + "_i").src = "/rgdweb/common/images/remove.png";
                }
            }
        }
    }
}

function addParam(name, value) {
    var re = new RegExp(name + "=[^\&]*");

    if (re.exec(location.href) != null) {
        location.href = location.href.replace(re, name + "=" + value)
    } else {
        location.href = location.href + "&" + name + "=" + value;
    }
}

function toggleAssociations() {
    if (document.getElementById("associationsCurator").style.display=="none") {
        document.getElementById("associationsCurator").style.display="block";
    }else {
       document.getElementById("associationsCurator").style.display="none";
    }

    if (document.getElementById("associationsStandard").style.display=="none") {
       document.getElementById("associationsStandard").style.display="block";
    }else {
       document.getElementById("associationsStandard").style.display="none";
    }
}

function toggleDivs(id_hide, id_show) {
    $('#'+id_hide).hide(400);
    $('#'+id_show).show(900);
}


