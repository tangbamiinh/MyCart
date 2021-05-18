package hanu.a2_1801040147.views;

import android.os.Bundle;
import android.util.Log;
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

import hanu.a2_1801040147.R;
import hanu.a2_1801040147.adapters.CartListAdapter;
import hanu.a2_1801040147.databinding.FragmentCartBinding;
import hanu.a2_1801040147.models.CartItem;
import hanu.a2_1801040147.utils.CurrencyFormatter;
import hanu.a2_1801040147.viewModels.ShopViewModel;

public class CartFragment extends Fragment implements CartListAdapter.CartInterface {

    private static final String TAG = "CartFragment";

    ShopViewModel shopViewModel;
    FragmentCartBinding fragmentCartBinding;
    NavController navController;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false);
        return fragmentCartBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        navController = Navigation.findNavController(view);

        final CartListAdapter cartListAdapter = new CartListAdapter(this);
        fragmentCartBinding.cartRecyclerView.setAdapter(cartListAdapter);

        shopViewModel = new ViewModelProvider(requireActivity()).get(ShopViewModel.class);
        shopViewModel.getCart().observe(getViewLifecycleOwner(), cartItems -> {
            cartListAdapter.submitList(cartItems);
            fragmentCartBinding.placeOrderButton.setEnabled(cartItems.size() > 0);
        });

        shopViewModel.getTotalPrice().observe(getViewLifecycleOwner(), price -> fragmentCartBinding.orderTotalTextView.setText(formatPrice(price)));

        fragmentCartBinding.placeOrderButton.setOnClickListener(v -> {
            shopViewModel.resetCart();
            navController.navigate(R.id.action_cartFragment_to_orderFragment);
        });
    }

    private String formatPrice(Long price) {
        return CurrencyFormatter.format(price);
    }

    @Override
    public void deleteItem(CartItem cartItem) {
        shopViewModel.removeItemFromCart(cartItem);
    }

    @Override
    public void changeQuantity(CartItem cartItem, int quantity) {
        shopViewModel.changeQuantity(cartItem, quantity);
    }

    @Override
    public void onItemClick(CartItem cartItem) {
        shopViewModel.setProduct(cartItem.getProduct());
        navController.navigate(R.id.action_cartFragment_to_productDetailFragment);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater){
        final MenuItem menuItem = menu.findItem(R.id.cartFragment);
        View actionView = menuItem.getActionView();

        // No action when clicking since this is already at the cart screen
        actionView.setOnClickListener(null);
    }
}