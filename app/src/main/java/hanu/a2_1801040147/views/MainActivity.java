package hanu.a2_1801040147.views;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import hanu.a2_1801040147.R;
import hanu.a2_1801040147.databinding.ActivityMainBinding;
import hanu.a2_1801040147.models.CartItem;
import hanu.a2_1801040147.viewModels.ShopViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ActivityMainBinding activityMainBinding;

    NavController navController;
    ShopViewModel shopViewModel;

    private int cartQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        setSupportActionBar(activityMainBinding.toolbar);
        NavigationUI.setupWithNavController(activityMainBinding.toolbar, navController, appBarConfiguration);

        shopViewModel = new ViewModelProvider(this).get(ShopViewModel.class);

        shopViewModel.getCart().observe(this, cartItems -> {
            int quantity = 0;
            for (CartItem cartItem : cartItems) {
                quantity += cartItem.getQuantity();
            }
            cartQuantity = quantity;
            invalidateOptionsMenu();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.cartFragment);
        View actionView = menuItem.getActionView();

        TextView cartBadgeTextView = actionView.findViewById(R.id.cart_badge_text_view);

        cartBadgeTextView.setText(String.valueOf(cartQuantity));
        cartBadgeTextView.setVisibility(cartQuantity == 0 ? View.GONE : View.VISIBLE);

        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navController.navigate(R.id.action_shopFragment_to_cartFragment);
        return true;
    }
}