package edu.pucmm.ecommerceapp.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import edu.pucmm.ecommerceapp.models.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product ORDER BY idProduct")
    LiveData<List<Product>> findAll();

    @Query("SELECT * FROM product where idCategory = :idCat ORDER BY idProduct")
    LiveData<List<Product>> findAllByCategory(int idCat);

    @Query("SELECT * FROM product WHERE idProduct = :id")
    Product find(int id);

    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);
}