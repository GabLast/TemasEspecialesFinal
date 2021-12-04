package edu.pucmm.ecommerceapp.fragments.product;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.databinding.FragmentProductManagerBinding;

public class ProductManager extends Fragment {

    private FragmentProductManagerBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }
}