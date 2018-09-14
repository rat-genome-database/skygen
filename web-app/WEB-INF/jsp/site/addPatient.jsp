
<%@ include file="/common/headerarea.jsp"%>


<script type="text/javascript" src="/skygen/js/calendarDateInput.js">

/***********************************************
* Jason's Date Input Calendar- By Jason Moon http://calendar.moonscript.com/dateinput.cfm
* Script featured on and available at http://www.dynamicdrive.com
* Keep this notice intact for use.
***********************************************/

</script>


<br>
<br>
<br>




<form action="dashboard.html" method='post'>
<input type="hidden" name="action" value="add"/>
<table border=0 cellspacing=0 width=50% align="center">
    <tr>
        <td>MRN</td>
        <td>First Name</td>
        <td>Last Name</td>
        <td>Date of Birth</td>
    <tr>

    <tr >
        <td><input name="mrn" type="text" value="" size='25'/></td>
        <td><input name="firstName" type="text" value="" size='25'/></td>
        <td><input name="lastName" type="text" value="" size='25'/></td>
        <td><script>DateInput('dob', true, 'DD-MM-YYYY')</script></td>
    <tr>
    <tr>
    <td>&nbsp;</td>
    </tr>
    <tr>
    <td align="right" colspan=4><input type="submit" value="Add Patient"/></td>
    </tr>
</table>
</form>



<%@ include file="/common/footerarea.jsp"%>