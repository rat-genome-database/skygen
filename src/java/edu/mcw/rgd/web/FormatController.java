package edu.mcw.rgd.web;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import edu.mcw.rgd.reporting.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: Jun 2, 2008
 * Time: 8:59:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormatController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ArrayList error = new ArrayList();
        ArrayList warning = new ArrayList();
        ArrayList status = new ArrayList();
        //check for SQL injection

        HttpRequestFacade req = new HttpRequestFacade(request);

        String fmt = req.getParameter("fmt");
        int height = Integer.parseInt(req.getParameter("height"));
        int width = Integer.parseInt(req.getParameter("width"));

        Report report = new Report();

        for(int i=0; i < height; i++) {
            Record record = new Record();
            for (int j=0; j< width; j++) {
                record.append(req.getParameter("v" + i + "_" + j));
            }
            report.append(record);
        }

        report.sort(1,Report.CHROMOSOME_SORT,Report.ASCENDING_SORT, false);


        response.setContentType("application/msexcel");
        //response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "inline; filename=gviewer.csv" );

        response.getWriter().println(new DelimitedReportStrategy().format(report));
        return null;
//            response.sendRedirect("/plf/plfRGD/?chromosome=" + search.getChr().toUpperCase() + "&start_pos=" + start + "&stop_pos=" + stop + "&submitBtn=Search+by+Position&hiddenXYZ123=&module=searchByPosition&func=form");
/*
        String page = request.getRequestURL().substring(request.getRequestURL().lastIndexOf("/") + 1);
        page = page.substring(0, page.indexOf("."));

        Report report = this.getReport(search, req);

        if (!termOfLength) {
            search.setTerm(req.getParameter("term"));
        }

        if (search.getSort() == -1) {
            search.setSort(0);
        }

        if (search.getSort() == 0 && search.getOrder() == -1) {
            search.setOrder(1);
        } else if (search.getOrder() == -1) {
            search.setOrder(0);
        }

        if (search.getOrder() == 1) {
            report.sort(search.getSort(), Report.DECENDING_SORT, true);
        } else {
            report.sort(search.getSort(), Report.ASCENDING_SORT, true);
        }

        request.setAttribute("report", report);


        request.setAttribute("error", error);
        request.setAttribute("status", warning);
        request.setAttribute("warn", warning);

        if (search.getFmt() == 2) {
            return new ModelAndView("csv.jsp");
        } else if (search.getFmt() == 3) {
            return new ModelAndView("tab.jsp");

        } else if (search.getFmt() == 4) {
            return new ModelAndView("print.jsp");

        } else if (search.getFmt() == 5) {
            return new ModelAndView("gviewer.jsp");
        } else if (search.getFmt() == 6) {
            return new ModelAndView("gviewerXML.jsp");
        }

        return new ModelAndView(this.getViewUrl());
    */
    }


}
