package edu.pucmm.ecommerceapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Product implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idProduct")
    private int idProduct;
    @ColumnInfo(name = "idCategory")
    private int idCategory;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "available")
    private boolean available;
    @ColumnInfo(name = "stockAvailable")
    private int stockAvailable;
    @ColumnInfo(name = "price")
    private double price;

    public Product() {
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
