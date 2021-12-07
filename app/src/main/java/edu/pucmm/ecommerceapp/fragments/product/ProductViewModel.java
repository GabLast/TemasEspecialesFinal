package edu.pucmm.ecommerceapp.fragments.product;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import edu.pucmm.ecommerceapp.database.AppDataBase;
import edu.pucmm.ecommerceapp.models.Product;

import java.util.List;

public class ProductViewModel extends ViewModel {

    private final LiveData<List<Product>> listLiveData;


    public ProductViewModel(@NonNull AppDataBase dataBase) {

        listLiveData = dataBase.productDao().findAll();
    }

    public LiveData<List<Product>> getListLiveData() {
        return listLiveData;
    }

}
