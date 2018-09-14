
<%@ page import="edu.mcw.rgd.dao.impl.GeneDAO" %>
<%@ page import="edu.mcw.rgd.dao.impl.MapDAO" %>
<%@ page import="edu.mcw.rgd.datamodel.Map" %>
<%@ page import="edu.mcw.rgd.datamodel.SpeciesType" %>
<%@ page import="java.util.*" %>
<%@ page import="edu.mcw.rgd.web.*" %>


<%
    String pageTitle = "GA Tool: Annotation Search and Export";
    String headContent = "";
    String pageDescription = "Generate an annotation report for a list of genes.";
%>
<%@ include file="/common/headerarea.jsp" %>

<% try { %>

<%
    MapDAO mdao = new MapDAO();

    List<Map> chinMaps = null;

    if (Site.isChinchilla()) {
       chinMaps= mdao.getMaps(SpeciesType.CHINCHILLA, "bp");
    }

    List<Map> ratMaps= mdao.getMaps(SpeciesType.RAT, "bp");
    List<Map> mouseMaps= mdao.getMaps(SpeciesType.MOUSE, "bp");
    List<Map> humanMaps= mdao.getMaps(SpeciesType.HUMAN, "bp");

    LinkedHashMap chinKeyValues= new LinkedHashMap();
    LinkedHashMap ratKeyValues= new LinkedHashMap();
    LinkedHashMap humanKeyValues= new LinkedHashMap();
    LinkedHashMap mouseKeyValues= new LinkedHashMap();

    Iterator it = ratMaps.iterator();
    while (it.hasNext()) {
        Map m = (Map)it.next();
        ratKeyValues.put(m.getKey() + "", m.getName());
    }
    it = mouseMaps.iterator();
    while (it.hasNext()) {
        Map m = (Map)it.next();
        mouseKeyValues.put(m.getKey() + "", m.getName());
    }
    it = humanMaps.iterator();
    while (it.hasNext()) {
        Map m = (Map)it.next();
        humanKeyValues.put(m.getKey() + "", m.getName());
    }

    if (edu.mcw.rgd.web.Site.isChinchilla()) {
        it = chinMaps.iterator();
        while (it.hasNext()) {
            Map m = (Map)it.next();
            chinKeyValues.put(m.getKey() + "", m.getName());
        }
    }

    String tutorialLink="/wg/home/rgd_rat_community_videos/gene-annotator-tutorial";
    String pageHeader="GA Tool: Annotation Search and Export";

%>
<br>


<form action="patient.html" method="POST">
<table border=0 align="center">
    <input type="hidden" name="a" value="upload"/>
    <input type="hidden" name="pid" value="<%=request.getParameter("pid")%>"/>


    <tr><td colspan=2 align='right'><a href="patient.html?pid=<%=request.getParameter("pid")%>"><<<&nbsp;Return&nbsp;to&nbsp;Patient&nbsp;Details</a></td></tr>

    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td valign="top" >
            <span class="gaLabel">Enter Symbols</span>
            <br><br>
            When entering multiple identifiers<br> your list can be separated by commas,<br>spaces, tabs, or line feeds
            <br><br>

            <table style="border: 1px solid #e6e6e6;">
                <tr>
                    <td colspan=3 style="font-weight:700">Valid identifier types:</td>
                </tr>
                <tr>
                    <td style="font-size:11px;">Affymetrix</td>
                    <td style="font-size:11px;">GenBank Nucleotide</td>
                    <!--<td style="font-size:11px;">Ontology Term ID</td>-->
                </tr>
                <tr>
                    <td style="font-size:11px;">Ensembl Gene</td>
                    <td style="font-size:11px;">GenBank Protein</td>
                    <!--<td style="font-size:11px;">RGD ID</td>-->
                </tr>
                <tr>
                    <td style="font-size:11px;">Ensembl Protein</td>
                    <td style="font-size:11px;">Gene Symbol</td>
                    <td style="font-size:11px;">dbSNP ID</td>
                </tr>
                <!--
                <tr>
                    <td style="font-size:11px;">EntrezGene ID</td>
                    <td style="font-size:11px;">Kegg Pathway</td>
                </tr>
                -->
             </table>
        </td>
        <td>
            <textarea name="genes" rows="18" cols=70 ></textarea>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
        <td colspan=2 align="right"><input type="submit" value="Upload Gene List"/></td>
    </tr>
</table>

<script>
    setMap(document.getElementById("species"));
</script>

   <% } catch (Exception e) {
        e.printStackTrace();

     } %>


<%@ include file="/common/footerarea.jsp" %>

