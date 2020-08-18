package com.example.ultramarket.adapters.user_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ProductViewHolder> {

    private Context mContext;
    private List<Category> categoryList;

    public CategoriesAdapter(Context mContext, ArrayList<Category> categoryList) {
        this.mContext = mContext;
        this.categoryList = categoryList;
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
        if (categoryList == null) return 0;
        return categoryList.size();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_product_item_image)
        ImageView catImage;
        @BindView(R.id.user_product_item_name)
        TextView catName;
        @BindView(R.id.user_product_item_price_layout)
        LinearLayout priceLayout;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            priceLayout.setVisibility(View.GONE);
        }

        public void bind(int position) {
            if (categoryList.get(position).getImage() != null)
                Picasso.get().load(categoryList.get(position).getImage()).into(catImage);
            catName.setText(categoryList.get(position).getName());
        }
    }
}
