package edu.pucmm.ecommerceapp.fragments.category;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import edu.pucmm.ecommerceapp.database.AppDataBase;
import edu.pucmm.ecommerceapp.models.Category;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final LiveData<List<Category>> listLiveData;


    public CategoryViewModel(@NonNull AppDataBase dataBase) {

        listLiveData = dataBase.categoryDao().findAll();
    }

    public LiveData<List<Category>> getListLiveData() {
        return listLiveData;
    }
}