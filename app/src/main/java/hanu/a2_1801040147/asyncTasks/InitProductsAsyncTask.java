package hanu.a2_1801040147.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import hanu.a2_1801040147.MyCartDatabase;
import hanu.a2_1801040147.daos.ProductDao;
import hanu.a2_1801040147.models.Product;
import hanu.a2_1801040147.utils.NetworkUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class InitProductsAsyncTask extends AsyncTask<String, Void, List<Product>> {

    private static final String TAG = "GetAllProductsAsyncTask";

    private final WeakReference<Context> context;

    private final InitProductsCaller initProductsCaller;

    @SneakyThrows
    @Override
    protected List<Product> doInBackground(String... strings) {

        ProductDao productDao = MyCartDatabase.getInstance(context.get()).productDao();

        List<Product> products;

        if (NetworkUtils.isNetworkAvailable(context.get())) {
            // Then try to update from the API
            URL url = new URL("https://mpr-cart-api.herokuapp.com/products");

            Product[] results = new ObjectMapper().readValue(url, Product[].class);

            products = Arrays.asList(results);

            // Reset & save into db
            productDao.insertOrUpdateAll(results);

        } else {
            // Else, populate with the cached products in the local storage
            // (coming from the last API call, when the app is used with internet access).

            // This conditional branch is also applied for the
            // first time that the app was used since the process flow is the same
            products = productDao.getAll();
        }
        return products;
    }

    @Override
    protected void onPostExecute(List<Product> products) {
        super.onPostExecute(products);
        initProductsCaller.onInitProductsFinished(products);
    }

    public interface InitProductsCaller {
        void onInitProductsFinished(List<Product> products);
    }
}