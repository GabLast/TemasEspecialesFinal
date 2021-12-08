package edu.pucmm.ecommerceapp.fragments.product;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.shashank.sony.fancytoastlib.FancyToast;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.database.AppDataBase;
import edu.pucmm.ecommerceapp.database.AppExecutors;
import edu.pucmm.ecommerceapp.database.DAOs.CategoryDao;
import edu.pucmm.ecommerceapp.database.DAOs.ProductDao;
import edu.pucmm.ecommerceapp.databinding.FragmentCreateProductBinding;
import edu.pucmm.ecommerceapp.fragments.ViewModelFactory;
import edu.pucmm.ecommerceapp.fragments.category.CategoryManagerFragment;
import edu.pucmm.ecommerceapp.fragments.category.CategoryViewModel;
import edu.pucmm.ecommerceapp.helpers.Functions;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.models.Category;
import edu.pucmm.ecommerceapp.models.Product;
import edu.pucmm.ecommerceapp.models.User;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CreateProductFragment extends Fragment {

    private FragmentCreateProductBinding binding;
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private Product element;
    private Category category;
    private List<Category> list = new ArrayList<>();
    //Image management objects
    private int position = 0;
    private ArrayList<Drawable> drawables;
    private ArrayList<Uri> uris;
    private boolean isInsert;
    private boolean wasInserted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productDao = AppDataBase.getInstance(getContext()).productDao();
        categoryDao = AppDataBase.getInstance(getContext()).categoryDao();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateProductBinding.inflate(inflater, container, false);
        binding.availableCheck.setChecked(true);

        try {
            element = (Product) getArguments().getSerializable(GlobalVariables.PRODUCT);
            isInsert = false;
        } catch (NullPointerException e) {
            System.err.println("No value -> Not editing");
            isInsert = true;
        }

//        categoryDao.findAll().observe(this, cats -> {
//            ArrayAdapter<Category> adapter;
//            if (GlobalVariables.getUSERSESSION().getRol().equals(User.ROL.SELLER)) {
//                List<Category> aux = new ArrayList<>();
//                for (Category c : cats) {
//                    if (c.isAvailable()) {
//                        aux.add(c);
//                    }
//                }
//                adapter = new ArrayAdapter<>(getContext(), R.layout.pretty_spinner_item, aux);
//            }else {
//                adapter = new ArrayAdapter<>(getContext(), R.layout.pretty_spinner_item, cats);
//            }
//            binding.categorySpinner.setAdapter(adapter);
//        });

        categoryDao.findAll().observe(this, categories -> {
            final Stream<Category> stream = GlobalVariables.getUSERSESSION().getRol().equals(User.ROL.CUSTOMER)
                    ? categories.stream().filter(f -> (f.isAvailable()))
                    : categories.stream();
            final ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), R.layout.pretty_spinner_item, stream.collect(Collectors.toList()));
            binding.categorySpinner.setAdapter(adapter);
        });


        List<Category> aux = new ArrayList<>();
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), R.layout.pretty_spinner_item, aux);
        binding.categorySpinner.setAdapter(adapter);

        CategoryViewModel categoryViewModel = new ViewModelProvider(this, new ViewModelFactory(getContext())).get(CategoryViewModel.class);
        categoryViewModel.getListLiveData().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> terms) {
                for (Category term : terms) {
                    aux.add(term);
                }
                //notifyDataSetChanged after update termsList variable here
                adapter.notifyDataSetChanged();
            }
        });

        drawables = new ArrayList<>();
        uris = new ArrayList<>();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        if (element != null) {
            binding.priceTXT.setText(String.valueOf(element.getPrice()));
            binding.availableCheck.setChecked(element.isAvailable());
            binding.productNameTXT.setText(element.getName());
            binding.stockAvailableTXT.setText(String.valueOf(element.getStockAvailable()));

        }

        binding.images.setFactory(() -> new ImageView(getContext()));
        //Images management
        binding.next.setOnClickListener(v -> {
            if (position < drawables.size() - 1) {
                binding.images.setImageDrawable(drawables.get(++position));
            } else {
                FancyToast.makeText(getContext(), "There are no more images", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
            }
        });
        binding.previous.setOnClickListener(v -> {
            if (position > 0) {
                binding.images.setImageDrawable(drawables.get(--position));
            } else {
                FancyToast.makeText(getContext(), "There are no more images to the left", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
            }
        });

        binding.select.setOnClickListener(v -> photoOptions());

        binding.registerProductBTN.setOnClickListener(v -> {
            if (Functions.isEmpty(binding.productNameTXT, binding.priceTXT, binding.stockAvailableTXT)) {
                return;
            }

            if (binding.categorySpinner.getText().toString().isEmpty()) {
                binding.categorySpinner.setError("No category was chosen");
                return;
            }

            binding.categorySpinner.setOnItemClickListener((parent, view1, position, id) -> category = (Category) parent.getItemAtPosition(position));

            register();
        });

    }

    private void register() {
        if (element == null) {
            element = new Product();
        }

        element.setName(binding.productNameTXT.getText().toString());
        element.setPrice(Double.parseDouble(binding.priceTXT.getText().toString()));
        element.setIdCategory(category.getIdCategory());
        element.setAvailable(binding.availableCheck.isChecked());
        element.setStockAvailable(Integer.parseInt(binding.stockAvailableTXT.getText().toString()));

        AppExecutors.getInstance().diskIO().execute(() -> {

            if (isInsert) {
                long uid = productDao.insert(element);
                element.setIdProduct((int) uid);
                wasInserted = true;
                isInsert = false;
            } else {
                productDao.update(element);
                wasInserted = false;
            }

        });

        if (wasInserted) {
            FancyToast.makeText(getContext(), "The product has been created", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
        } else {
            FancyToast.makeText(getContext(), "The product has been updated", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
        }

        NavHostFragment.findNavController(CreateProductFragment.this)
                .navigate(R.id.create_prod_to_view_all);
    }

    private void photoOptions() {
        // initialising intent
        Intent intent = new Intent();
        // setting type to select to be image
        intent.setType("image/*");
        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        pickAndChoosePictureResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> pickAndChoosePictureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            final ClipData clipData = result.getData().getClipData();
                            if (clipData != null) {
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    // adding imageuri in array
                                    final Uri uri = clipData.getItemAt(i).getUri();
                                    final InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    drawables.add(new BitmapDrawable(getContext().getResources(), bitmap));
                                    uris.add(uri);
                                }
                            } else {
                                final Uri uri = result.getData().getData();
                                final InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                drawables.add(new BitmapDrawable(getContext().getResources(), bitmap));
                                uris.add(uri);
                            }
                            binding.images.setImageDrawable(drawables.get(0));
                            position = 0;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}