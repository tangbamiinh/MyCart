package hanu.a2_1801040147.viewModels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import hanu.a2_1801040147.models.CartItem;
import hanu.a2_1801040147.models.Product;
import hanu.a2_1801040147.repositories.CartRepository;
import hanu.a2_1801040147.repositories.ShopRepository;

import java.lang.ref.WeakReference;
import java.util.List;

public class ShopViewModel extends AndroidViewModel {

    ShopRepository shopRepository;
    CartRepository cartRepository;

    MutableLiveData<Product> mutableProduct = new MutableLiveData<>();

    public ShopViewModel(@NonNull Application application) {
        super(application);

        WeakReference<Context> wContext = new WeakReference<>(getApplication().getApplicationContext());
        this.shopRepository = new ShopRepository(wContext);
        this.cartRepository = new CartRepository(wContext);
    }

    public LiveData<List<Product>> getProducts() {
        return shopRepository.getProducts();
    }

    public void setProduct(Product product) {
        mutableProduct.setValue(product);
    }

    public LiveData<Product> getProduct() {
        return mutableProduct;
    }

    public LiveData<List<CartItem>> getCart() {
        return cartRepository.getCart();
    }

    public boolean addItemToCart(Product product) {
        return cartRepository.addItemToCart(product);
    }

    public void removeItemFromCart(CartItem cartItem) {
        cartRepository.removeItemFromCart(cartItem);
    }

    public void changeQuantity(CartItem cartItem, int quantity) {
        cartRepository.changeQuantity(cartItem, quantity);
    }

    public void searchProducts(String partialName) {
        shopRepository.searchProductsByName(partialName);
    }

    public void cancelSearchProducts() {
        shopRepository.cancelSearchProducts();
    }

    public LiveData<Long> getTotalPrice() {
        return cartRepository.getTotalPrice();
    }

    public void resetCart() {
        cartRepository.reset();
    }
}
