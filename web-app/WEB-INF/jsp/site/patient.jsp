<%@ page import="edu.mcw.rgd.datamodel.skygen.Patient" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ include file="/common/headerarea.jsp"%>
<script type="text/javascript">
    function accSelected(event){
        oid=event.originalEvent.data.split("|")[0];
        $("#phenoFrame").contents().find("#phenoId").val(oid);
        $("#phenoFrame").contents().find("#formAction").val('replacePheno');
        $("#phenoFrame").contents().find("#phenoForm").submit();

        $("#dialog").hide();
        $("#dialog").dialog("close");
    }
</script>
    <script type="text/javascript"  src="/skygen/js/jquery/jquery.autocomplete.js"></script>
<script type="text/javascript">
    function showOntTree(ontId) {
        $("#phenoFrame").contents().find("#oldPhenoId").val(ontId);
        var ont = ontId.substring(0, ontId.indexOf(":"));
        $("selectedId").val(ontId);

        $("#dialogFrame").attr("src", "ontology/view.html?mode=iframe&ont="+ont+"&sel_acc_id=selected_term&acc_id="+ontId);
        $( "#dialog").dialog({
            resizable: false,
            width: 800,
            height:540,
            modal: true
        });
        $( "#dialog").show();
    }

    $(document).ready(function(){
        $("#dialog").hide();
//        addEventListener("message", accSelected, false);
        $(window).on("message", accSelected);
        $("#phenoInput").autocomplete("/OntoSolr/select", {extraParams:{
                                             "fq": "cat:(RDO XP)",
                                             "wt": "velocity",
                                             "bf": "term_len_l^5",
                                             "v.template": "termidselect",
                                             "cacheLength": 0
                                           },
                                           scrollHeight: 240,
                                           max: 40
                                         });
        $("#phenoInput").result(function(data, value){
                         $("#phenoInput").val("");
                         $("#phenoFrame").contents().find("#phenoId").val(value[1].replace(/XP/g,'HP'));
                         $("#phenoFrame").contents().find("#formAction").val('addPheno');
                         $("#phenoFrame").contents().find("#phenoForm").submit();
                     });
        $("#phenoInput").focus();
        $("#geneInput").autocomplete("/OntoSolr/select", {extraParams:{
                                             "fq": "cat:(RGD_GENE) AND species_s:(human)",
                                             "wt": "velocity",
                                             "bf": "term_len_l^5",
                                             "v.template": "termidselect",
                                             "cacheLength": 0
                                           },
                                           scrollHeight: 240,
                                           max: 40
                                         });
        $("#geneInput").result(function(data, value){
            $("#geneInput").val("");
            $("#geneFrame").contents().find("#geneId").val(value[1].substr(9));
            $("#geneFrame").contents().find("#geneForm").submit();
        });
});
    </script>

<% if (request.getAttribute("log") != null) {

    List msgLst = (List) request.getAttribute("log");

    Iterator it = msgLst.iterator();
    while (it.hasNext()) {
        out.print(it.next() + "<br>");
    }
}

    sdao.log(name, Integer.parseInt(pid), "viewing patient data");

    Patient p = (Patient) request.getAttribute("patient");
    System.out.println("p here23 = " + p);
%>
<br>

<script>
    function confirmDelete() {
        if (confirm("This action will delete this patient record and all related annotation?")) {
            location.replace('index.html?pid=<%=p.getId()%>&action=delete');
        }else {
            return false;
        }
    }
</script>

<div style="background-color:#F9F9F9;">

<br>

<table align="center">
    <tr>
    <td>

<table border=0 width=100%>
    <tr>
        <td colspan='9'><span style="font-weight:700;font-size:22px;">Patient Details</span>&nbsp;&nbsp;&nbsp;<a href="javascript:confirmDelete();void(0);">(Delete Patient)</a> </td>
        <td align='right'><a href="/skygen"><<< Return to Patient List</a></td>

    </tr>
    <tr >
        <td style="font-weight:700;">MRN:</td><td><%=p.getMrn()%></td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <td style="font-weight:700;">Name:</td><td><%=p.getLastName()%>, <%=p.getFirstName()%></td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <td style="font-weight:700;">Date of Birth:</td><td><%=p.getDateOfBirth()%></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
</table>

<form>
   <input id="phenoId" type="hidden"/>
   <input id="geneId" type="hidden"/>



    <table  style="border:1px solid black; margin-top:10px;" >
        <tr>
            <td valign="top">
                <table border=0>
                    <tr>
                        <td valign="top" style="background-color:#02254F; color:white;padding:5px;"><span class="sectionTitle;">Phenotype&nbsp;&nbsp;</span><input id="phenoInput" size="60"/></td>
                    </tr>
                    <tr>
                        <td><iframe  frameBorder="0" id="phenoFrame" src="/skygen/patient.html?pid=<%=p.getId()%>&a=getPheno" width="100%" height="250"></iframe></td>
                    </tr>
                </table>

            </td>
            <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;
            </td>
            <td valign="top">
                <table border=0>
                    <tr>
                        <td colspan="1" style="background-color:#02254F; color:white;padding:5px;"><span class="sectionTitle;">Genotype&nbsp;&nbsp;</span><input id="geneInput" size="42"/>&nbsp;<a style="font-size:14px; color:white;" href="uploadList.html?pid=<%=request.getParameter("pid")%>">Upload List</a></td>
                    </tr>
                    <tr>
                        <td colspan="1"><iframe  frameBorder="0" id="geneFrame" src="/skygen/patient.html?pid=<%=p.getId()%>&a=getGene" width="100%" height="250" ></iframe></td>
                    </tr>
                </table>

            </td>
        </tr>
    </table>

    <br>
    <table border=0 style="border: 1px solid #02254F;" align="right" width="100%">
        <tr>
            <td colspan="6" style="background-color:#02254F; color:white;padding:5px;">Available Actions</td>
        </tr>
        <tr>
            <td>&nbsp;&nbsp;</td>
            <td>&nbsp;</td>
            <td><b>OntoMate</b></td>
            <td>&nbsp;</td>
            <td><b>ClinVar</b></td>
        </tr>
        <tr>
            <td>&nbsp;&nbsp;</td>
            <td><li></li></td>
            <td><a target="_blank"  href="/skygen/patient.html?pid=<%=p.getId()%>&a=searchOntomate&q=allPhenotypes">Search OntoMate Using Phenotype List</a></td>
            <td><li></li></td>
            <td><a  href="/skygen/clinVar.html?pid=<%=p.getId()%>">Search ClinVar Annotations</a></td>

        </tr>
        <tr>
            <td>&nbsp;&nbsp;</td>
            <td><li></li></td>
            <td><a target="_blank" href="/skygen/patient.html?pid=<%=p.getId()%>&a=searchOntomate&q=allGenes">Search OntoMate Using Gene List</a></td>
        </tr>
        <tr>
            <td>&nbsp;&nbsp;</td>
            <td><li></li></td>
            <td><a target="_blank" href="/skygen/patient.html?pid=<%=p.getId()%>&a=searchOntomate&q=all">Search OntoMate Using Phenotype and Gene List</a></td>
        </tr>
    </table>
</form>


 </td>
    </tr>
</table>
    <br><br><br><br><br><br>

</div>
<div id="dialog" title="Select a term">
  <iframe id="dialogFrame" width="100%" height="100%">
      <p>Loading RGD ontolog browser...</p>
  </iframe>
</div>


<%@ include file="/common/footerarea.jsp"%>