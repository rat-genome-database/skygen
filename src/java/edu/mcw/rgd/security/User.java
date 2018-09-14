package edu.mcw.rgd.security;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: Jul 5, 2011
 * Time: 8:08:24 AM
 */
public class User {

    public static boolean isCurator() {

        try {

            InetAddress addr = InetAddress.getLocalHost();

            // Get hostname
            String hostname = addr.getHostName();

            if (hostname.equals("kyle.hmgc.mcw.edu") || hostname.equals("hastings.hmgc.mcw.edu")) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public static boolean isProduction() {

        try {

            InetAddress addr = InetAddress.getLocalHost();

            // Get hostname
            String hostname = addr.getHostName();

            if (hostname.equals("horan.hmgc.mcw.edu") || hostname.equals("osler.hmgc.mcw.edu")) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }
}
