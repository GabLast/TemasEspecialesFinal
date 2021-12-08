package edu.pucmm.ecommerceapp.fragments.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.pucmm.ecommerceapp.databinding.CategoryHolderBinding;
import edu.pucmm.ecommerceapp.helpers.Functions;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.listeners.OnItemTouchListener;
import edu.pucmm.ecommerceapp.listeners.OptionsMenuListener;
import edu.pucmm.ecommerceapp.models.Category;
import edu.pucmm.ecommerceapp.models.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private CategoryHolderBinding binding;
    private List<Category> elements;
    private OptionsMenuListener optionsMenuListener;
    private OnItemTouchListener onItemTouchListener;

    public CategoryAdapter() {

    }

    @NonNull
    @NotNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        binding = CategoryHolderBinding.inflate(layoutInflater, parent, false);
        return new CategoryAdapter.MyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final Category element = elements.get(position);
        holder.name.setText(element.getName().toString());

        try {
            if (!GlobalVariables.getUSERSESSION().getRol().equals(User.ROL.SELLER)) {
                holder.action.setVisibility(View.INVISIBLE);
            }

            holder.action.setOnClickListener(v -> {
                if (optionsMenuListener != null) {
                    optionsMenuListener.onCreateOptionsMenu(holder.action, element);
                }
            });

            holder.avatar.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });

            holder.name.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });
        }catch (NullPointerException e) {

        }

        Functions.downloadImage(element.getPhoto(), binding.avatar);

    }


    @Override
    public int getItemCount() {
        if (elements == null) {
            return 0;
        }
        return elements.size();
    }

    public void setElements(List<Category> elements) {
        this.elements = elements;
        notifyDataSetChanged();
    }

    public void setOptionsMenuListener(OptionsMenuListener optionsMenuListener) {
        this.optionsMenuListener = optionsMenuListener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView avatar, action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = binding.name;
            avatar = binding.avatar;
            action = binding.manager;
        }
    }
}
