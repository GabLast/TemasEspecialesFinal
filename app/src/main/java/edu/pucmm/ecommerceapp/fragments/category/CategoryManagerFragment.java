package edu.pucmm.ecommerceapp.fragments.category;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import com.shashank.sony.fancytoastlib.FancyToast;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.database.AppDataBase;
import edu.pucmm.ecommerceapp.database.AppExecutors;
import edu.pucmm.ecommerceapp.database.DAOs.CategoryDao;
import edu.pucmm.ecommerceapp.databinding.FragmentCategoryManagerBinding;
import edu.pucmm.ecommerceapp.fragments.ViewModelFactory;
import edu.pucmm.ecommerceapp.helpers.Functions;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.listeners.OnItemTouchListener;
import edu.pucmm.ecommerceapp.listeners.OptionsMenuListener;
import edu.pucmm.ecommerceapp.models.Category;
import edu.pucmm.ecommerceapp.models.User;
import edu.pucmm.ecommerceapp.networks.Firebase;
import edu.pucmm.ecommerceapp.networks.NetResponse;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CategoryManagerFragment extends Fragment {

    private FragmentCategoryManagerBinding binding;
    private CategoryDao categoryDao;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryDao = AppDataBase.getInstance(getContext()).categoryDao();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryManagerBinding.inflate(inflater, container, false);


        binding.fabcat.setOnClickListener(v -> {
            NavHostFragment.findNavController(CategoryManagerFragment.this)
                    .navigate(R.id.add_cat_to_create_cat);
        });

        if (GlobalVariables.getUSERSESSION() != null) {
            if (GlobalVariables.getUSERSESSION().getRol().equals(User.ROL.CUSTOMER)) {
                binding.fabcat.setVisibility(View.INVISIBLE);
            }
        }

        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3;
        }

        binding.recyclerViewcat.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        binding.recyclerViewcat.setHasFixedSize(true);
        binding.recyclerViewcat.setItemAnimator(new DefaultItemAnimator());

        final CategoryAdapter adapter = new CategoryAdapter();
        binding.recyclerViewcat.setAdapter(adapter);


        //setting manager button actions to manage and delete
        adapter.setOptionsMenuListener((OptionsMenuListener<Category>) (view1, element) -> {
            Functions.popupMenu(getContext(), view1, () -> {
                final Bundle bundle = new Bundle();
                bundle.putSerializable(GlobalVariables.CATEGORY, (Serializable) element);

                NavHostFragment.findNavController(CategoryManagerFragment.this)
                        .navigate(R.id.add_cat_to_create_cat, bundle);
            }, () -> {
                Functions.alertDialog(getContext(), "Alert!",
                        "Are you sure you want to delete this category?",
                        () -> delete(element));
            });
        });

        //setting category touch to view products from select category
        adapter.setOnItemTouchListener((OnItemTouchListener<Category>) element -> {
            final Bundle bundle = new Bundle();
            bundle.putSerializable(GlobalVariables.CATEGORY, (Serializable) element);

            NavHostFragment.findNavController(CategoryManagerFragment.this)
                    .navigate(R.id.nav_cat_to_nav_product, bundle);
        });


        //adding the categories to the view
        CategoryViewModel categoryViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext()))
                .get(CategoryViewModel.class);

        categoryViewModel.getListLiveData().observe(this, elements -> {
            final Stream<Category> stream = GlobalVariables.getUSERSESSION().getRol().equals(User.ROL.CUSTOMER)
                    ? elements.stream().filter(f -> (f.isAvailable()))
                    : elements.stream();

            adapter.setElements(stream.collect(Collectors.toList()));
        });

    }

    private void delete(Category element) {

        AppExecutors.getInstance().diskIO().execute(() -> {
            categoryDao.delete(element);
            getActivity().runOnUiThread(() ->
                    FancyToast.makeText(getContext(), "Successfully deleted!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show());
        });

        if (element.getPhoto() != null && !element.getPhoto().isEmpty()) {
            Firebase.getConstantInstance().delete(element.getPhoto(), new NetResponse<String>() {
                @Override
                public void onResponse(String response) {
                }

                @Override
                public void onFailure(Throwable t) {
                    FancyToast.makeText(getContext(), t.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            });
        }
    }


}