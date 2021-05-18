package hanu.a2_1801040147.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import hanu.a2_1801040147.models.CartItem;
import hanu.a2_1801040147.models.relations.CartItemAndProduct;

@Dao
public abstract class CartItemDao {
    
    @Insert
    public abstract void insert(CartItem cartItem);

    @Insert
    public abstract void insert(CartItem... cartItem);

    @Update
    public abstract void update(CartItem cartItem);

    @Delete
    public abstract void delete(CartItem cartItem);

    @Query("DELETE FROM CartItem")
    public abstract void deleteAll();

    @Transaction
    @Query("SELECT * FROM CartItem")
    abstract List<CartItemAndProduct> getAllCartItemsAndProducts();

    public List<CartItem> getAll() {
        List<CartItemAndProduct> cartItemAndProducts = this.getAllCartItemsAndProducts();

        ArrayList<CartItem> cartItems = new ArrayList<>();

        for (CartItemAndProduct cartItemAndProduct: cartItemAndProducts) {
            CartItem cartItem = new CartItem();
            cartItem.setQuantity(cartItemAndProduct.getCartItem().getQuantity());
            cartItem.setProduct(cartItemAndProduct.getProduct());
            cartItem.setProductId(cartItemAndProduct.getProduct().getId());
            cartItems.add(cartItem);
        }
        return cartItems;
    }
}
