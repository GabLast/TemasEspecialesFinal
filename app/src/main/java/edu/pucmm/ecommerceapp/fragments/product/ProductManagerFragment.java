package edu.pucmm.ecommerceapp.fragments.product;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import com.shashank.sony.fancytoastlib.FancyToast;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.database.AppDataBase;
import edu.pucmm.ecommerceapp.database.AppExecutors;
import edu.pucmm.ecommerceapp.database.DAOs.ProductDao;
import edu.pucmm.ecommerceapp.databinding.FragmentProductManagerBinding;
import edu.pucmm.ecommerceapp.fragments.ViewModelFactory;
import edu.pucmm.ecommerceapp.helpers.Functions;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.listeners.OnItemTouchListener;
import edu.pucmm.ecommerceapp.listeners.OptionsMenuListener;
import edu.pucmm.ecommerceapp.models.Category;
import edu.pucmm.ecommerceapp.models.Product;
import edu.pucmm.ecommerceapp.models.User;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductManagerFragment extends Fragment {

    private FragmentProductManagerBinding binding;
    private ProductDao productDao;
    private Category category;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productDao = AppDataBase.getInstance(getContext()).productDao();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductManagerBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(v -> {
            NavHostFragment.findNavController(ProductManagerFragment.this)
                    .navigate(R.id.nav_product_to_create_product);
        });

        if(GlobalVariables.getUSERSESSION() != null){
            if(GlobalVariables.getUSERSESSION().getRol().equals(User.ROL.CUSTOMER)){
                binding.fab.setVisibility(View.INVISIBLE);
            }
        }

        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        final ProductAdapter adapter = new ProductAdapter();
        binding.recyclerView.setAdapter(adapter);

        //setting manager button actions to manage and delete
        adapter.setOptionsMenuListener((OptionsMenuListener<Product>) (view1, element) -> {
            Functions.popupMenu(getContext(), view1, () -> {
                final Bundle bundle = new Bundle();
                bundle.putSerializable(GlobalVariables.PRODUCT, element);

                NavHostFragment.findNavController(ProductManagerFragment.this)
                        .navigate(R.id.nav_product_to_create_product, bundle);
            }, () -> {
                Functions.alertDialog(getContext(), "Alert!",
                        "Are you sure you want to delete this product?",
                        () -> delete(element));
            });
        });

        //setting category touch to view products from select category
        adapter.setOnItemTouchListener((OnItemTouchListener<Product>) element -> {
            final Bundle bundle = new Bundle();
            bundle.putSerializable(GlobalVariables.PRODUCT, element);

            NavHostFragment.findNavController(ProductManagerFragment.this)
                    .navigate(R.id.nav_product_to_add_to_cart, bundle);
        });

        ProductViewModel productViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext()))
                .get(ProductViewModel.class);

        productViewModel.getListLiveData().observe(this, elements -> {
            final Stream<Product> stream = GlobalVariables.getUSERSESSION().getRol().equals(User.ROL.CUSTOMER)
                    ? elements.stream().filter(f -> (f.isAvailable()))
                    : elements.stream();

            adapter.setElements(stream.filter(f -> category == null ? true : f.getIdCategory() == category.getIdCategory()).collect(Collectors.toList()));
        });
    }

    private void delete(Product element) {

        AppExecutors.getInstance().diskIO().execute(() -> {
            productDao.delete(element);
            getActivity().runOnUiThread(() -> FancyToast.makeText(getContext(), "Successfully deleted!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show());
        });
//        if (element.carousels != null && !element.carousels.isEmpty()) {
//            FirebaseNetwork.obtain().deletes(element.carousels, new NetResponse<String>() {
//                @Override
//                public void onResponse(String response) {
//                    function.apply(progressDialog).apply(true).accept(element);
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    function.apply(progressDialog).apply(false).accept(element);
//                    FancyToast.makeText(getContext(), t.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
//                }
//            });
//        }
    }
}