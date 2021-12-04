package edu.pucmm.ecommerceapp.helpers;

import edu.pucmm.ecommerceapp.models.User;

public class GlobalVariables {

    public static final String API_URL = "http://137.184.110.89:7002";
    public static User USERSESSION = null;

    public static User getUSERSESSION() {
        return USERSESSION;
    }

    public static void setUSERSESSION(User USERSESSION) {
        GlobalVariables.USERSESSION = USERSESSION;
    }
}
