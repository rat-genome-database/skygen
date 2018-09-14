 <tr>
                    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                    <td style='font-weight:700;'>

                    <div style="margin-top:10px;"><%=thisGene.getSymbol()%></div>
                    <div style="font-size:12px; font-weight: 200;"><%=edu.mcw.rgd.process.Utils.getGeneDescription(thisGene)%></div>

                    <div style="margin-top:10px;"><%=geList.size()%> ClinVar variants found in <%=thisGene.getSymbol()%>
                        <a href="javascript:toggle('<%=thisGene.getRgdId()%>')">Show/Hide Variants</a>
                    </div>

                    <div id="<%=thisGene.getRgdId()%>" style='width:400px; border: 1px solid blue; background-color:white; display:none;'>
                    <table border=0>
                        <% for (GenomicElement ge: geList) { %>
                            <tr>
                                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                <td><a style='font-size:12px;' href='http://rgd.mcw.edu/<%=Link.it(ge.getRgdId())%>'><%=ge.getName()%></a></td>
                            </tr>
                        <% } %>

                    </table>
                    </div>
                    </td>
                </tr>
 </table>