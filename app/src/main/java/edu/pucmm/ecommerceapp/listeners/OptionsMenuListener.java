package edu.pucmm.ecommerceapp.listeners;

import android.view.View;

public interface OptionsMenuListener<T> {

    void onCreateOptionsMenu(View view, T element);
}
