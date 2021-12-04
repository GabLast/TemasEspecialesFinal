package edu.pucmm.ecommerceapp.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import edu.pucmm.ecommerceapp.database.DAOs.CategoryDao;
import edu.pucmm.ecommerceapp.database.DAOs.ProductDao;
import edu.pucmm.ecommerceapp.models.Category;
import edu.pucmm.ecommerceapp.models.Product;

@Database(entities = {Product.class, Category.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    private static final String DATABASE_NAME = "eCommerceApp";
    private static final Object LOCK = new Object();
    private static AppDataBase sIntance;

    public static AppDataBase getInstance(Context context) {
        if (sIntance == null) {
            synchronized (LOCK) {
                sIntance = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, DATABASE_NAME).build();
            }
        }
        return sIntance;
    }

    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();

}