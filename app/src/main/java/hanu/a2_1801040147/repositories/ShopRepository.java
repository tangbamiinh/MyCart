package hanu.a2_1801040147.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import hanu.a2_1801040147.MyCartDatabase;
import hanu.a2_1801040147.asyncTasks.InitProductsAsyncTask;
import hanu.a2_1801040147.daos.ProductDao;
import hanu.a2_1801040147.models.Product;

public class ShopRepository implements InitProductsAsyncTask.InitProductsCaller {

    private static final String TAG = "ShopRepository";

    private final ProductDao productDao;

    private MutableLiveData<List<Product>> mutableProductList;

    private List<Product> initiallyLoadedProducts;

    private final WeakReference<Context> context;

    public ShopRepository(WeakReference<Context> context) {
        this.context = context;
        this.productDao = getProductDao();
    }

    public LiveData<List<Product>> getProducts() {
        if (mutableProductList == null) {
            mutableProductList = new MutableLiveData<>();
            loadProducts();
        }
        return mutableProductList;
    }

    private ProductDao getProductDao() {
        return MyCartDatabase.getInstance(context.get()).productDao();
    }

    private synchronized void loadProducts() {
        InitProductsAsyncTask initProductsAsyncTask = new InitProductsAsyncTask(context, this);
        initProductsAsyncTask.execute();
    }

    public void searchProductsByName(String partialName) {
        List<Product> results = new ArrayList<>();

        for (Product product: Objects.requireNonNull(initiallyLoadedProducts))
            if (product.getName().toLowerCase().contains(partialName.toLowerCase()))
                results.add(product);

        mutableProductList.setValue(results);
    }

    public void cancelSearchProducts() {
        // Set the state to the initial one
        mutableProductList.setValue(new ArrayList<>(initiallyLoadedProducts));
    }

    @Override
    public void onInitProductsFinished(List<Product> products) {
        mutableProductList.setValue(products);
        initiallyLoadedProducts = new ArrayList<>(products);
    }
}
