package hanu.a2_1801040147.models.relations;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Embedded;
import androidx.room.Relation;

import hanu.a2_1801040147.models.CartItem;
import hanu.a2_1801040147.models.Product;
import lombok.Data;

@Data
public class CartItemAndProduct {
    @Embedded
    private CartItem cartItem;

    @Relation(parentColumn = "productId", entityColumn = "id")
    private Product product;

    public static DiffUtil.ItemCallback<CartItemAndProduct> itemCallback = new DiffUtil.ItemCallback<CartItemAndProduct>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartItemAndProduct oldItem, @NonNull CartItemAndProduct newItem) {
            return oldItem.cartItem.getQuantity().equals(newItem.cartItem.getQuantity());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CartItemAndProduct oldItem, @NonNull CartItemAndProduct newItem) {
            return oldItem.equals(newItem);
        }
    };
}
