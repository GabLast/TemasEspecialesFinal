package edu.pucmm.ecommerceapp.database.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import edu.pucmm.ecommerceapp.models.User;

import java.util.List;

@Dao
public interface UserDao {
    //CRUD
    @Query("SELECT * FROM user ORDER BY idUser")
    LiveData<List<User>> findAll();

    @Query("SELECT * FROM user WHERE idUser = :id")
    User find(int id);

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}