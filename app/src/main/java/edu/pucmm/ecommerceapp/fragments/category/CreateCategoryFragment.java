package edu.pucmm.ecommerceapp.fragments.category;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.fragment.NavHostFragment;
import com.shashank.sony.fancytoastlib.FancyToast;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.database.AppDataBase;
import edu.pucmm.ecommerceapp.database.AppExecutors;
import edu.pucmm.ecommerceapp.database.DAOs.CategoryDao;
import edu.pucmm.ecommerceapp.databinding.FragmentCategoryManagerBinding;
import edu.pucmm.ecommerceapp.databinding.FragmentCreateCategoryBinding;
import edu.pucmm.ecommerceapp.databinding.FragmentCreateProductBinding;
import edu.pucmm.ecommerceapp.helpers.Functions;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.helpers.PhotoOptions;
import edu.pucmm.ecommerceapp.models.Category;
import edu.pucmm.ecommerceapp.networks.Firebase;
import edu.pucmm.ecommerceapp.networks.NetResponse;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;


public class CreateCategoryFragment extends Fragment {

    private FragmentCreateCategoryBinding binding;
    private CategoryDao categoryDao;
    private Category element;
    private Uri uri;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryDao = AppDataBase.getInstance(getContext()).categoryDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateCategoryBinding.inflate(inflater, container, false);
        binding.availableCheckCat.setChecked(true);
        element = (Category) getArguments().getSerializable(GlobalVariables.CATEGORY);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        if (element != null) {
            binding.categorynameTXT.setText(element.getName());
            binding.availableCheckCat.setChecked(element.isAvailable());

            Functions.downloadImage(element.getPhoto(), binding.image);
        }

        binding.image.setOnClickListener(v -> {
            photoOptions();
        });

        binding.registerCatBTN.setOnClickListener(v -> {

            if(Functions.isEmpty(binding.categorynameTXT)){
                return;
            }

            register();
        });
    }

    private void register() {
        if (element == null) {
            element = new Category();
        }
        element.setName(binding.categorynameTXT.getText().toString());
        element.setAvailable(binding.availableCheckCat.isChecked());

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (Long.valueOf(element.getIdCategory()).equals(0)) {
                long uid = categoryDao.insert(element);
                element.setIdCategory((int) uid);
            } else {
                categoryDao.update(element);
            }

            if (uri != null && element.getIdCategory() != 0) {
                Firebase.getConstantInstance().upload(uri, String.format("categories/%s.jpg", element.getIdCategory()),
                        new NetResponse<String>() {
                            @Override
                            public void onResponse(String response) {
                                FancyToast.makeText(getContext(), "Successfully uploaded image", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                element.setPhoto(response);
                                AppExecutors.getInstance().diskIO().execute(() -> {
                                    categoryDao.update(element);
                                });
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                FancyToast.makeText(getContext(), t.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }
                        });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void photoOptions() {
        final Function<PhotoOptions, Consumer<Intent>> function = photoOptions -> intent -> {
            switch (photoOptions) {
                case CHOOSE_FOLDER:
                case CHOOSE_GALLERY:
                    pickAndChoosePictureResultLauncher.launch(intent);
                    break;
                case TAKE_PHOTO:
                    takePictureResultLauncher.launch(intent);
                    break;
            }
        };
        Functions.photoOptions(getContext(), function);
    }

    private ActivityResultLauncher<Intent> pickAndChoosePictureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            uri = result.getData().getData();
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            binding.image.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private ActivityResultLauncher<Intent> takePictureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        binding.image.setImageBitmap(bitmap);
                        uri = Functions.getImageUri(getContext(), bitmap);
                    }
                }
            });

}