package edu.pucmm.ecommerceapp.fragments.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.pucmm.ecommerceapp.databinding.ProductHolderBinding;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.listeners.OnItemTouchListener;
import edu.pucmm.ecommerceapp.listeners.OptionsMenuListener;
import edu.pucmm.ecommerceapp.models.Product;
import edu.pucmm.ecommerceapp.models.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>{

    private ProductHolderBinding binding;
    private OptionsMenuListener optionsMenuListener;
    private OnItemTouchListener onItemTouchListener;
    private List<Product> elements;

    public ProductAdapter() {
    }

    @NonNull
    @NotNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        binding = ProductHolderBinding.inflate(layoutInflater, parent, false);

        return new ProductAdapter.MyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Product element = elements.get(position);
        holder.itemCode.setText(String.valueOf(element.getIdProduct()));
        holder.itemName.setText(element.getName());
        holder.price.setText(String.valueOf(element.getPrice()));

        if(GlobalVariables.getUSERSESSION() != null){

            if(!GlobalVariables.getUSERSESSION().getRol().equals(User.ROL.SELLER)){
                holder.action.setVisibility(View.INVISIBLE);
            }

            holder.itemCode.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });
            holder.itemName.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });
            holder.price.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });
            holder.avatar.setOnClickListener(v -> {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onClick(element);
                }
            });
            holder.action.setOnClickListener(v -> {
                if (optionsMenuListener != null) {
                    optionsMenuListener.onCreateOptionsMenu(holder.action, element);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setOptionsMenuListener(OptionsMenuListener optionsMenuListener) {
        this.optionsMenuListener = optionsMenuListener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public void setElements(List<Product> elements) {
        this.elements = elements;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView itemCode, itemName, price, stockAvailable;
        private ImageView avatar, action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCode = binding.productIDTXT;
            itemName = binding.productName;
            price = binding.productPriceTXT;
            avatar = binding.productImage;
            action = binding.manager;
            stockAvailable = null;
        }
    }
}
