package com.example.ultramarket.adapters.user_adapters;

import android.app.ActivityOptions;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Product;
import com.example.ultramarket.helpers.Utils;
import com.example.ultramarket.ui.userUi.Activities.ProductActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mContext;
    private List<Product> productList;
    private OnItemClicked mOnItemClickedInstance;

    public interface OnItemClicked {
        void onItemClicked(String prodId,View prodImage);
    }

    public ProductAdapter(Context mContext, List<Product> productList, Fragment listener) {
        this.mContext = mContext;
        mOnItemClickedInstance = (OnItemClicked) listener;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_product_list_item, parent, false);
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

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public void setProdList(List<Product> products) {
        productList = products;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_product_item_image)
        ImageView prodImage;
        @BindView(R.id.user_product_item_name)
        TextView prodName;
        @BindView(R.id.user_product_item_price_layout)
        LinearLayout priceLayout;
        @BindView(R.id.user_product_item_old_price)
        TextView oldPrice;
        @BindView(R.id.user_product_item_new_price)
        TextView newPrice;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mOnItemClickedInstance.onItemClicked(productList.get(getAdapterPosition()).getID(),prodImage);
                }
            });
/*            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = params.height;
            itemView.setLayoutParams(params);*/
        }

        public void bind(int position) {
            if (productList.get(position).getImage() != null)
                Picasso.get().load(productList.get(position).getImage()).into(prodImage);
            priceLayout.setVisibility(View.VISIBLE);
            if (productList.get(position).isHasOffer()) {
                newPrice.setVisibility(View.VISIBLE);
                oldPrice.setTextColor(mContext.getColor(android.R.color.darker_gray));
                double nPrice = Utils.calcDiscount(productList.get(position).getPrice(),
                        productList.get(position).getDiscount_percentage());
                newPrice.setText(String.valueOf
                        (nPrice).concat(productList.get(position).getCurrency()));
                oldPrice.setText(String.valueOf
                        (productList.get(position).getPrice()).concat(productList.get(position).getCurrency()));
                oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            } else {
                oldPrice.setText(String.valueOf
                        (productList.get(position).getPrice()).concat(productList.get(position).getCurrency()));
                newPrice.setVisibility(View.GONE);
                oldPrice.setTextColor(mContext.getColor(android.R.color.black));
                oldPrice.setPaintFlags(oldPrice.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            prodName.setText(productList.get(position).getName());
        }
    }
}
