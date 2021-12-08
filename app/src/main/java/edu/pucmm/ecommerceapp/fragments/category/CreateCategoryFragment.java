package edu.pucmm.ecommerceapp.fragments.category;

import android.app.Activity;
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
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.fragment.NavHostFragment;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.database.AppDataBase;
import edu.pucmm.ecommerceapp.database.AppExecutors;
import edu.pucmm.ecommerceapp.database.DAOs.CategoryDao;
import edu.pucmm.ecommerceapp.databinding.FragmentCreateCategoryBinding;
import edu.pucmm.ecommerceapp.helpers.Functions;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.helpers.KProgressHUDUtils;
import edu.pucmm.ecommerceapp.helpers.PhotoOptions;
import edu.pucmm.ecommerceapp.models.Category;
import edu.pucmm.ecommerceapp.networks.Firebase;
import edu.pucmm.ecommerceapp.networks.NetResponse;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;


public class CreateCategoryFragment extends Fragment {

    private FragmentCreateCategoryBinding binding;
    private CategoryDao categoryDao;
    private Category element;
    private Uri uri;
    private boolean isInsert;
    private boolean wasInserted;

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

        try {
            element = (Category) getArguments().getSerializable(GlobalVariables.CATEGORY);
            isInsert = false;
        } catch (NullPointerException e) {
            element = null;
            isInsert = true;
            System.err.println("No value -> Not editing");
        }


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

            if (Functions.isEmpty(binding.categorynameTXT)) {
                return;
            }
            register();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void register() {
        if (element == null) {
            element = new Category();
        }

        AtomicBoolean pic = new AtomicBoolean(false);
        element.setName(binding.categorynameTXT.getText().toString());
        element.setAvailable(binding.availableCheckCat.isChecked());
        AppExecutors.getInstance().diskIO().execute(() -> {

            if (isInsert) {
                long uid = categoryDao.insert(element);
                System.err.println("insert uid: " + uid);
                element.setIdCategory((int) uid);
                wasInserted = true;
                isInsert = false;
                pic.set(true);
            } else {
                categoryDao.update(element);
                wasInserted = false;
            }
        });

        //have to use consumer. if not,
        //due to room creating another thread, the category id
        //doesnt sync up properly, making it not able to
        //upload a pic on the first time the element is inserted
        final KProgressHUD progressDialog = new KProgressHUDUtils(getActivity()).showConnecting();
        if (uri != null && element.getIdCategory() != 0) {
            consumer.accept(progressDialog);
        } else {
            progressDialog.dismiss();
        }

        if (wasInserted) {
            FancyToast.makeText(getContext(), "The category has been created", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
        } else {
            FancyToast.makeText(getContext(), "The category has been updated", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
        }

        NavHostFragment.findNavController(CreateCategoryFragment.this)
                .navigate(R.id.create_cat_to_view_cats);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private final Consumer<KProgressHUD> consumer = new Consumer<KProgressHUD>() {
        @Override
        public void accept(KProgressHUD progressDialog) {
            Firebase.getConstantInstance().upload(uri, String.format("categories/%s.jpg", element.getIdCategory()),
                    new NetResponse<String>() {
                        @Override
                        public void onResponse(String response) {
                            FancyToast.makeText(getContext(), "Successfully upload image", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            element.setPhoto(response);
                            AppExecutors.getInstance().diskIO().execute(() -> {
                                categoryDao.update(element);
                            });
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            progressDialog.dismiss();
                            FancyToast.makeText(getContext(), t.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        }
                    });
        }
    };

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