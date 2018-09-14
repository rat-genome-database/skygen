package edu.mcw.rgd.controller;

import edu.mcw.rgd.web.HttpRequestFacade;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import edu.mcw.rgd.dao.impl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import edu.mcw.rgd.datamodel.skygen.Patient;
import sun.util.calendar.Gregorian;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: 5/7/14
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DashboardController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        edu.mcw.rgd.dao.impl.SkyGenDAO sgdao = new SkyGenDAO();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username


        HttpRequestFacade req = new HttpRequestFacade(request);

        ArrayList errorList = new ArrayList();

        String action = req.getParameter("action");
        if (action.equals("add")) {

            sgdao.log(name, 0, "adding patient " + req.getParameter("mrn"));

            if (req.getParameter("firstName").equals("") || req.getParameter("lastName").equals("")) {
                errorList.add("First Name, and Last Name Fields are Required");
            }

            String mrn =  req.getParameter("mrn");

            Patient p = new Patient();

            if (mrn != null) {
                p.setMrn(mrn);
            }
            p.setFirstName(req.getParameter("firstName"));
            p.setLastName(req.getParameter("lastName"));

            String[] birthDate = req.getParameter("dob").split("-");

            GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(birthDate[2]),Integer.parseInt(birthDate[1]) -1 ,Integer.parseInt(birthDate[0]));

            p.setDateOfBirth(gc.getTime());

            try {
                sgdao.insertPatient(name,p);
            }catch (Exception e) {

                if (errorList.size()==0) {
                    errorList.add(e.getMessage());
                }
            }

        }else if (action.equals("delete")) {
            sgdao.log(name, Integer.parseInt(req.getParameter("pid")), "deleting patient ");
            int pid=Integer.parseInt(request.getParameter("pid"));

            try {
                sgdao.deleteAllPatientGenes(pid);
                sgdao.deleteAllPatientPhenotypes(pid);
                sgdao.deletePatient(pid);
            }catch (Exception e) {
                errorList.add(e.getMessage());
            }

        }

        List<Patient> patients=null;
        try {
            patients = sgdao.getPatients(name);
        }catch (Exception e) {
            errorList.add(e.getMessage());
        }

        request.setAttribute("patients", patients);
        request.setAttribute("error", errorList);

        return new ModelAndView("/WEB-INF/jsp/site/dashboard.jsp");

    }
}
