package edu.mcw.rgd.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: 5/7/14
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadGeneListController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {


        return new ModelAndView("/WEB-INF/jsp/site/uploadGeneList.jsp");

    }


}
