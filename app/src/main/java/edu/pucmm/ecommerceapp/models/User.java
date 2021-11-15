package edu.pucmm.ecommerceapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private long idUser;
    private String username;
    private String password;


}
