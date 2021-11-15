package edu.pucmm.ecommerceapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Category {
    @PrimaryKey(autoGenerate = true)
    private long idCategory;
    private String name;
    private boolean available;

}
