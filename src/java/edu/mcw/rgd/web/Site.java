package edu.mcw.rgd.web;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: 8/28/12
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Site {

    private static boolean isChin = false;


    public static boolean isChinchilla() {
        return isChin;
    }

    public static void setChinchilla() {
        isChin = true;
    }
}
