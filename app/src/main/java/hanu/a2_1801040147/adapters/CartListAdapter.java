package hanu.a2_1801040147.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import hanu.a2_1801040147.databinding.CartRowBinding;
import hanu.a2_1801040147.models.CartItem;

public class CartListAdapter extends ListAdapter<CartItem, CartListAdapter.CartViewHolder> {

    private final CartInterface cartInterface;
    public CartListAdapter(CartInterface cartInterface) {
        super(CartItem.itemCallback);
        this.cartInterface = cartInterface;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CartRowBinding cartRowBinding = CartRowBinding.inflate(layoutInflater, parent, false);
        return new CartViewHolder(cartRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.cartRowBinding.setCartItem(getItem(position));
        holder.cartRowBinding.executePendingBindings();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        CartRowBinding cartRowBinding;
        public CartViewHolder(@NonNull CartRowBinding cartRowBinding) {
            super(cartRowBinding.getRoot());
            this.cartRowBinding = cartRowBinding;

            cartRowBinding.setCartInterface(cartInterface);

            cartRowBinding.increaseQuantityButton.setOnClickListener(v -> {
                try {
                    CartItem cartItem = cartRowBinding.getCartItem();

                    int quantity = cartItem.getQuantity();

                    cartInterface.changeQuantity(cartItem, quantity + 1);
                } catch (ArrayIndexOutOfBoundsException ignored) { }
            });

            cartRowBinding.decreaseQuantityButton.setOnClickListener(v -> {
                try {
                    CartItem cartItem = cartRowBinding.getCartItem();

                    int quantity = cartItem.getQuantity();

                    if (quantity <= 1)
                        cartInterface.deleteItem(cartItem);
                    else // quantity > 1
                        cartInterface.changeQuantity(cartItem, quantity - 1);
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            });

//            cartRowBinding.deleteProductButton.setOnClickListener(v -> cartInterface.deleteItem(getItem(getAdapterPosition())));

//            cartRowBinding.quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    int quantity = position + 1;
//                    if (quantity == getItem(getAdapterPosition()).getQuantity()) {
//                        return;
//                    }
//                    cartInterface.changeQuantity(getItem(getAdapterPosition()), quantity);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
        }
    }

    public interface CartInterface {

        void deleteItem(CartItem cartItem);

        void changeQuantity(CartItem cartItem, int quantity);

        void onItemClick(CartItem cartItem);
    }
}
