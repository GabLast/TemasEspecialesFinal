package edu.pucmm.ecommerceapp.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.helpers.interfaces.VoidListener;
import edu.pucmm.ecommerceapp.networks.Firebase;
import edu.pucmm.ecommerceapp.networks.NetResponse;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public static void downloadImage(@NonNull String photo, @NonNull ImageView imageView) {
        if (photo != null && !photo.isEmpty()) {
            Firebase.getConstantInstance().download(photo, new NetResponse<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imageView.setImageBitmap(response);
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("Product Image: ", t.getMessage());
                }
            });
        }
    }

    public static void popupMenu(@NonNull Context context, @NonNull View view, @NonNull VoidListener manager, @NonNull VoidListener delete) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.action_menu);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_manager:
                    manager.accept();
                    return true;
                case R.id.action_delete:
                    delete.accept();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();

    }

    public static void alertDialog(@NonNull Context context, @NonNull String title, @NonNull String message, @NonNull VoidListener consumer) {
        new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> consumer.accept())
                .setNegativeButton("No", (dialog, which) -> dialog.cancel()).show();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void photoOptions(@NonNull Context context, @NonNull Function<PhotoOptions, Consumer<Intent>> consumer) {
        final CharSequence[] options = getOptions();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your category picture");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals(PhotoOptions.TAKE_PHOTO.getValue())) {
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                consumer.apply(PhotoOptions.TAKE_PHOTO).accept(takePicture);

            } else if (options[item].equals(PhotoOptions.CHOOSE_GALLERY.getValue())) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                consumer.apply(PhotoOptions.CHOOSE_GALLERY).accept(pickPhoto);

            } else if (options[item].equals(PhotoOptions.CHOOSE_FOLDER.getValue())) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                consumer.apply(PhotoOptions.CHOOSE_FOLDER).accept(Intent.createChooser(intent, "Select Picture"));

            } else if (options[item].equals(PhotoOptions.CANCEL.getValue())) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static CharSequence[] getOptions() {
        final CharSequence[] options = new CharSequence[PhotoOptions.values().length];
        final AtomicInteger atomic = new AtomicInteger(0);

        for (PhotoOptions obj : Arrays.asList(PhotoOptions.values())) {
            options[atomic.getAndIncrement()] = obj.getValue();
        }

        return options;
    }

    public static Uri getImageUri(@NonNull Context context, @NonNull Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
        return Uri.parse(path);
    }

}
