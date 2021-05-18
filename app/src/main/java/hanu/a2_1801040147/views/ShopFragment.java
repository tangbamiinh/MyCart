package hanu.a2_1801040147.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import hanu.a2_1801040147.R;
import hanu.a2_1801040147.adapters.ShopListAdapter;
import hanu.a2_1801040147.databinding.FragmentShopBinding;
import hanu.a2_1801040147.models.Product;
import hanu.a2_1801040147.viewModels.ShopViewModel;

public class ShopFragment extends Fragment implements ShopListAdapter.ShopInterface {

    private static final String TAG = "ShopFragment";

    FragmentShopBinding fragmentShopBinding;

    private ShopListAdapter shopListAdapter;

    private ShopViewModel shopViewModel;

    private NavController navController;

    private ProgressDialog progressDialog;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentShopBinding = FragmentShopBinding.inflate(inflater, container, false);
        return fragmentShopBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(this.requireContext());
        showDialog();

        shopListAdapter = new ShopListAdapter(this);
        fragmentShopBinding.shopRecyclerView.setAdapter(shopListAdapter);

        shopViewModel = new ViewModelProvider(requireActivity()).get(ShopViewModel.class);

        shopViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products == null) {
                if (!progressDialog.isShowing())
                    showDialog();
            } else {
                shopListAdapter.submitList(products);

                if (progressDialog.isShowing())
                    progressDialog.hide();
            }
        });

        navController = Navigation.findNavController(view);

        registerSearchActions();
    }

    private void showDialog() {
        progressDialog.setMessage("Loading Products");
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
        progressDialog.show();
    }

    private void registerSearchActions() {
        // When text changed
        fragmentShopBinding.searchTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Log.i(TAG, "onTextChanged: " + s.toString());
                    shopViewModel.searchProducts(s.toString());
                } else {
                    shopViewModel.cancelSearchProducts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // OnEditorAction
        fragmentShopBinding.searchTextInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoftKeyboard(this.requireActivity(), this.requireView());
                fragmentShopBinding.searchTextInputEditText.clearFocus();
                return true;
            }
            return false;
        });
    }

    @Override
    public void addItem(Product product) {
        boolean isAdded = shopViewModel.addItemToCart(product);
        if (isAdded) {
            Snackbar.make(requireView(), product.getName() + " added to cart.", Snackbar.LENGTH_LONG)
                    .setAction("Checkout", v -> navController.navigate(R.id.action_shopFragment_to_cartFragment))
                    .show();
        } else {
            Snackbar.make(requireView(), "Already have the max quantity in cart.", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onItemClick(Product product) {
        shopViewModel.setProduct(product);
        navController.navigate(R.id.action_shopFragment_to_productDetailFragment);
    }

    public void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}