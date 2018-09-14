<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="java.util.List" %>
<%@ page import="edu.mcw.rgd.datamodel.skygen.Patient" %>

<%@ include file="/common/headerarea.jsp"%>

<style>
    tr { cursor: default; }
.highlight { background: #063664; color:white; }
</style>




<script>
    $(function() {

    /* Get all rows from your 'table' but not the first one
     * that includes headers. */
    var rows = $('tr').not(':first');

    /* Create 'click' event handler for rows */
    rows.on('click', function(e) {

        /* Get current row */
        var row = $(this);

        if (!row.closest('tr').attr('id')) {
            rows.removeClass('highlight');
            return;
        }



        /* Check if 'Ctrl', 'cmd' or 'Shift' keyboard key was pressed
         * 'Ctrl' => is represented by 'e.ctrlKey' or 'e.metaKey'
         * 'Shift' => is represented by 'e.shiftKey' */
        if ((e.ctrlKey || e.metaKey) || e.shiftKey) {
            /* If pressed highlight the other row that was clicked */
            row.addClass('highlight');
        } else {
            /* Otherwise just highlight one row and clean others */
            rows.removeClass('highlight');
            row.addClass('highlight');
        }

        location.href="patient.html?pid=" + row.closest('tr').attr('id');
        //alert(row.closest('tr').attr('id'));



    });





    /* This 'event' is used just to avoid that the table text
     * gets selected (just for styling).
     * For example, when pressing 'Shift' keyboard key and clicking
     * (without this 'event') the text of the 'table' will be selected.
     * You can remove it if you want, I just tested this in
     * Chrome v30.0.1599.69 */
    $(document).bind('selectstart dragstart', function(e) {
        e.preventDefault(); return false;
    });

});








    $(function() {

    /* Get all rows from your 'table' but not the first one
     * that includes headers. */
    var rows = $('tr').not(':first');

    /* Create 'click' event handler for rows */
    rows.on('mouseover', function(e) {

        /* Get current row */
        var row = $(this);

        if (!row.closest('tr').attr('id')) {
            rows.removeClass('highlight');
            return;
        }

      //  alert(row.closest('tr').attr('id'));

        /* Check if 'Ctrl', 'cmd' or 'Shift' keyboard key was pressed
         * 'Ctrl' => is represented by 'e.ctrlKey' or 'e.metaKey'
         * 'Shift' => is represented by 'e.shiftKey' */
        if ((e.ctrlKey || e.metaKey) || e.shiftKey) {
            /* If pressed highlight the other row that was clicked */
            row.addClass('highlight');
        } else {
            /* Otherwise just highlight one row and clean others */
            rows.removeClass('highlight');
            row.addClass('highlight');
        }

    });





    /* This 'event' is used just to avoid that the table text
     * gets selected (just for styling).
     * For example, when pressing 'Shift' keyboard key and clicking
     * (without this 'event') the text of the 'table' will be selected.
     * You can remove it if you want, I just tested this in
     * Chrome v30.0.1599.69 */
    $(document).bind('selectstart dragstart', function(e) {
        e.preventDefault(); return false;
    });

});


    $(function() {

     /* Get all rows from your 'table' but not the first one
      * that includes headers. */
     var rows = $('tr').not(':first');

     /* Create 'click' event handler for rows */
     rows.on('mouseout', function(e) {

         /* Get current row */
         var row = $(this);

         if (true) {
             row.removeClass('highlight');
         }

         if (!row.closest('tr').attr('id')) {
             rows.removeClass('highlight');
             return;
         }

     });


     /* This 'event' is used just to avoid that the table text
      * gets selected (just for styling).
      * For example, when pressing 'Shift' keyboard key and clicking
      * (without this 'event') the text of the 'table' will be selected.
      * You can remove it if you want, I just tested this in
      * Chrome v30.0.1599.69 */
     $(document).bind('selectstart dragstart', function(e) {
         e.preventDefault(); return false;
     });

 });




function addPatient() {
    location.href="addPatient.html";
}
</script>

<%
    sdao.log(name, 0, "viewing patient dashboard");

%>

<br>

<span style="font-size:20px;padding: 4px;">Welcome <%=name%></span>

<br> <br>
<table border=0 width=50% align="center">
    <tr>
        <td style="font-size:20px;">My Patients</td>
        <td align="right"><input type="button" value="Add Patient" style="background-color:#015449;color:white;font-size:20px;" onclick="addPatient()"/></td>
    </tr>
</table>

<%
    List<Patient> patients = (List <Patient>) request.getAttribute("patients");
    //out.print("patent len = " + patients.size());

%>


<table border=0 cellspacing=0 width=50% align="center" style="background-color:white;margin-top:10px; padding:10px;">
    <tr>
        <td style="background-color:#063A69; color:white;font-family: Impact, Charcoal, sans-serif;padding:2px;">Identifier</td>
        <td style="background-color:#063A69; color:white;font-family: Impact, Charcoal, sans-serif;padding:2px;">Name</td>
        <td style="background-color:#063A69; color:white;font-family: Impact, Charcoal, sans-serif;padding:2px;">Date of Birth</td>
    <tr>

<%
        for (Patient p: patients) {
%>
    <tr id="<%=p.getId()%>">
        <td><%=p.getMrn()%></td>
        <td><%=p.getLastName()%>, <%=p.getFirstName()%></td>
        <td><%=p.getDateOfBirth().toString()%></td>
    <tr>
<% } %>


</table>




<%@ include file="/common/footerarea.jsp"%>
