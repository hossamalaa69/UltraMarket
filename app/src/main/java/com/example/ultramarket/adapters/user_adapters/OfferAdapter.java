package com.example.ultramarket.adapters.user_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.userUi.Activities.ProductActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ProductViewHolder> {

    private Context mContext;
    private List<Product> productList;

    public OfferAdapter(Context mContext, List<Product> productList) {
        this.mContext = mContext;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_offers_list_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (productList == null) return 0;
        return productList.size();
    }

    public void setProdList(List<Product> products) {
        productList = products;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_offers_frag_item_image)
        ImageView prodImage;
        @BindView(R.id.user_offers_item_title)
        TextView prodName;
        @BindView(R.id.user_offers_item_price_layout)
        LinearLayout priceLayout;
        @BindView(R.id.user_product_item_old_price)
        TextView oldPrice;
        @BindView(R.id.user_product_item_new_price)
        TextView newPrice;
        @BindView(R.id.user_offers_item__weight_unit)
        TextView prodWeight;
        @BindView(R.id.user_offers_item_saved_amount)
        TextView prodSavedAmount;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext.getApplicationContext(), ProductActivity.class);
                    intent.putExtra("prod_id", productList.get(getAdapterPosition()).getID());
                    mContext.startActivity(intent);
                }
            });
            priceLayout.setVisibility(View.VISIBLE);
        }

        public void bind(int position) {
            if (productList.get(position).getImage() != null)
                Picasso.get().load(productList.get(position).getImage()).into(prodImage);
            prodName.setText(productList.get(position).getName());
            prodWeight.setText(productList.get(position).getUnit());
            if (productList.get(position).isHasOffer()) {
                oldPrice.setVisibility(View.VISIBLE);
                prodSavedAmount.setVisibility(View.VISIBLE);
                double nPrice = Utils.calcDiscount(productList.get(position).getPrice(),
                        productList.get(position).getDiscount_percentage());
                newPrice.setText(String.valueOf
                        (nPrice).concat(productList.get(position).getCurrency()));
                oldPrice.setText(String.valueOf
                        (productList.get(position).getPrice()).concat(productList.get(position).getCurrency()));
                prodSavedAmount.setText(mContext.getString(R.string.you_saved_money,
                        productList.get(position).getPrice() - nPrice).concat(productList.get(position).getCurrency()));
                oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                oldPrice.setVisibility(View.GONE);
                prodSavedAmount.setVisibility(View.GONE);
                newPrice.setText(String.valueOf(productList.get(position).getPrice()).concat(productList.get(position).getCurrency()));
            }

        }
    }
}
