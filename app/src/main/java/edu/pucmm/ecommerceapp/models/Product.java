package edu.pucmm.ecommerceapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {

    @PrimaryKey(autoGenerate = true)
    private long idProduct;
    private long idCategory;
    private String name;
    private boolean available;
    private int stockAvailable;


}
