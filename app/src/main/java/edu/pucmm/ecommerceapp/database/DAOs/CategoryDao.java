package edu.pucmm.ecommerceapp.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import edu.pucmm.ecommerceapp.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category ORDER BY idCategory")
    LiveData<List<Category>> findAll();

    @Query("SELECT * FROM category")
    List<Category> getAll();

    @Query("SELECT * FROM category WHERE idCategory = :id")
    Category find(int id);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category a);

    @Update
    void update(Category a);

    @Delete
    void delete(Category a);
}
