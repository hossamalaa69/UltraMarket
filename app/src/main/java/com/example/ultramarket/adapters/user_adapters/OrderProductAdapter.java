package com.example.ultramarket.adapters.user_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.helpers.AppExecutors;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.Holder> {
    private Context mContext;
    private Map<String, Integer> products;

    public OrderProductAdapter(Context mContext, Map<String, Integer> products) {
        this.mContext = mContext;
        this.products = products;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_order_product_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (products == null) return 0;
        return products.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_order_product_img)
        CircleImageView prodImage;
        @BindView(R.id.user_order_product_name)
        TextView prodName;
        @BindView(R.id.user_order_product_count)
        TextView prodCount;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public Map.Entry<String, Integer> getProductAt(int pos) {
            int i = 0;
            for (Map.Entry<String, Integer> entry : products.entrySet()) {
                if (pos == i) {
                    return entry;
                }
                i++;
            }
            return null;
        }

        public void bind(int pos) {
            Map.Entry<String, Integer> prod = getProductAt(pos);
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference()
                            .child(Product.class.getSimpleName()).child(prod.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Product product = snapshot.getValue(Product.class);
                                    if (product != null) {
                                        Picasso.get().load(product.getImage()).into(prodImage);
                                        prodName.setText(product.getName());
                                        prodCount.setText(mContext.getString(R.string.count_items, prod.getValue()));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            });
        }
    }
}
