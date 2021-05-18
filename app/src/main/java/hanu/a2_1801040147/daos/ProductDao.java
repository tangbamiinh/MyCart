package hanu.a2_1801040147.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import hanu.a2_1801040147.models.Product;

@Dao
public abstract class ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(Product product);

    @Transaction
    public void insertOrUpdateAll(Product... products) {
        for (Product product : products) {
            if (existsById(product.getId()))
                update(product);
            else
                insert(product);
        }
    }

    @Update
    abstract void update(Product product);

    @Query("SELECT * FROM Product")
    public abstract List<Product> getAll();

    @Query("SELECT EXISTS(SELECT * FROM Product WHERE id = :id)")
    abstract boolean existsById(String id);
}
