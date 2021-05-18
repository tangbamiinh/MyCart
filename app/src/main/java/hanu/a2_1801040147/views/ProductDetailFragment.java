package hanu.a2_1801040147.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import hanu.a2_1801040147.R;
import hanu.a2_1801040147.databinding.FragmentProductDetailBinding;
import hanu.a2_1801040147.models.Product;
import hanu.a2_1801040147.viewModels.ShopViewModel;

public class ProductDetailFragment extends Fragment {

    FragmentProductDetailBinding fragmentProductDetailBinding;
    ShopViewModel shopViewModel;
    private NavController navController;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentProductDetailBinding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return fragmentProductDetailBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        shopViewModel = new ViewModelProvider(requireActivity()).get(ShopViewModel.class);
        fragmentProductDetailBinding.setShopViewModel(shopViewModel);

        navController = Navigation.findNavController(requireView());

        fragmentProductDetailBinding.addToCartBtn.setOnClickListener(v -> {
            addItem();
        });
    }

    public void addItem() {
        Product product = Objects.requireNonNull(shopViewModel.getProduct().getValue());

        boolean isAdded = shopViewModel.addItemToCart(product);

        if (isAdded) {
            Snackbar.make(requireView(), product.getName() + " added to cart.", Snackbar.LENGTH_LONG)
                    .setAction("Checkout", v -> navController.navigate(R.id.action_productDetailFragment_to_cartFragment))
                    .show();
        } else {
            Snackbar.make(requireView(), "Already have the max quantity in cart.", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater){
        final MenuItem menuItem = menu.findItem(R.id.cartFragment);
        View actionView = menuItem.getActionView();

        actionView.setOnClickListener(v -> {
            navController.navigate(R.id.action_productDetailFragment_to_cartFragment);
        });
    }
}