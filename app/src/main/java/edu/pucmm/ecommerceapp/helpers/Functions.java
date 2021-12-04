package edu.pucmm.ecommerceapp.helpers;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class Functions {

    public static boolean isEmpty(final View... views) {
        for (View view : views) {
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                if(editText.getText().toString().trim().isEmpty()){
                    editText.setError(null);
                    editText.setError("Field can't be empty");
                    editText.requestFocus();
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isEmailValid(final EditText view, final String email) {
        if (email.length() < 4 || email.length() > 30) {
            view.setError("Email Must consist of 4 to 30 characters");
            return false;
        } else if (!email.matches("^[A-za-z0-9.@]+")) {
            view.setError("Only . and @ characters allowed");
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            view.setError("Email must contain @ and .");
            return false;
        }
        return true;
    }
}
