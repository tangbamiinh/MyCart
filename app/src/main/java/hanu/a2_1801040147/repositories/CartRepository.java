package hanu.a2_1801040147.repositories;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import hanu.a2_1801040147.MyCartDatabase;
import hanu.a2_1801040147.daos.CartItemDao;
import hanu.a2_1801040147.models.CartItem;
import hanu.a2_1801040147.models.Product;
import lombok.AllArgsConstructor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    private final MutableLiveData<List<CartItem>> mutableCart = new MutableLiveData<>();
    private final MutableLiveData<Long> mutableTotalPrice = new MutableLiveData<>();

    private final CartItemDao cartItemDao;

    public CartRepository(WeakReference<Context> wContext) {
        cartItemDao = MyCartDatabase.getInstance(wContext.get()).cartItemDao();
    }

    public LiveData<List<CartItem>> getCart() {
        if (mutableCart.getValue() == null) {
            initCart();
        }
        return mutableCart;
    }

    public void initCart() {
        new GetAllLocalAsync(cartItemDao).execute();
        calculateCartTotal();
    }

    public boolean addItemToCart(Product product) {
        if (mutableCart.getValue() == null) {
            initCart();
        }
        List<CartItem> cartItemList = new ArrayList<>(mutableCart.getValue());

        for (CartItem cartItem: cartItemList) {
            if (cartItem.getProduct().getId().equals(product.getId())) {

                int index = cartItemList.indexOf(cartItem);
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItemList.set(index, cartItem);

                mutableCart.setValue(cartItemList);

                // Async update the new quantity to db
                new UpdateLocalAsync(cartItemDao).execute(cartItem);

                calculateCartTotal();
                return true;
            }
        }

        CartItem cartItem = CartItem.builder()
                .product(product)
                .productId(product.getId())
                .quantity(1)
                .build();

        // Async Save to db
        new SaveLocalAsync(cartItemDao).execute(cartItem);

        cartItemList.add(cartItem);
        mutableCart.setValue(cartItemList);
        calculateCartTotal();
        return true;
    }

    public void reset() {
        new DeleteAllLocalAsync(cartItemDao).execute();
        mutableCart.setValue(new ArrayList<>());
        calculateCartTotal();
    }

    public void removeItemFromCart(CartItem cartItem) {
        if (mutableCart.getValue() == null) {
            return;
        }
        List<CartItem> cartItemList = new ArrayList<>(mutableCart.getValue());
        cartItemList.remove(cartItem);
        mutableCart.setValue(cartItemList);

        // Async delete from db
        new DeleteLocalAsync(cartItemDao).execute(cartItem);

        calculateCartTotal();
    }

    public void changeQuantity(CartItem cartItem, int quantity) {
        if (mutableCart.getValue() == null) return;

        List<CartItem> cartItemList = new ArrayList<>(mutableCart.getValue());

        CartItem updatedItem = CartItem.builder()
                .product(cartItem.getProduct())
                .productId(cartItem.getProductId())
                .quantity(quantity)
                .build();

        cartItemList.set(cartItemList.indexOf(cartItem), updatedItem);

        mutableCart.setValue(cartItemList);

        // Update cart item quantity async
        new UpdateLocalAsync(cartItemDao).execute(updatedItem);

        calculateCartTotal();
    }

    private void calculateCartTotal() {
        if (mutableCart.getValue() == null) return;

        long total = 0L;

        List<CartItem> cartItemList = mutableCart.getValue();

        for (CartItem cartItem: cartItemList)
            total += cartItem.getProduct().getUnitPrice() * cartItem.getQuantity();

        mutableTotalPrice.setValue(total);
    }

    public LiveData<Long> getTotalPrice() {
        if (mutableTotalPrice.getValue() == null) {
            mutableTotalPrice.setValue(0L);
        }
        return mutableTotalPrice;
    }

    @AllArgsConstructor
    public static class DeleteLocalAsync extends AsyncTask<CartItem, Void, Void> {

        private final CartItemDao cartItemDao;

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            cartItemDao.delete(cartItems[0]);
            return null;
        }
    }

    @AllArgsConstructor
    public static class DeleteAllLocalAsync extends AsyncTask<Void, Void, Void> {

        private final CartItemDao cartItemDao;

        @Override
        protected Void doInBackground(Void... voids) {
            cartItemDao.deleteAll();
            return null;
        }
    }

    @AllArgsConstructor
    public static class SaveLocalAsync extends AsyncTask<CartItem, Void, Boolean> {

        private final CartItemDao cartItemDao;

        @Override
        protected Boolean doInBackground(CartItem... cartItems) {
            cartItemDao.insert(cartItems);
            return null;
        }
    }

    @AllArgsConstructor
    public static class UpdateLocalAsync extends AsyncTask<CartItem, Void, Void> {

        private final CartItemDao cartItemDao;

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            cartItemDao.update(cartItems[0]);
            return null;
        }
    }

    @AllArgsConstructor
    public class GetAllLocalAsync extends AsyncTask<Void, Void, List<CartItem>> {

        private final CartItemDao cartItemDao;

        @Override
        protected List<CartItem> doInBackground(Void... voids) {
            return cartItemDao.getAll();
        }

        @Override
        protected void onPostExecute(List<CartItem> cartItems) {
            super.onPostExecute(cartItems);

            mutableCart.setValue(cartItems);
            calculateCartTotal();
        }
    }
}
