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
import com.example.ultramarket.database.Entities.Brand;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private Context mContext;
    private List<Brand> brandList;

    public BrandAdapter(Context mContext, List<Brand> brandList) {
        this.mContext = mContext;
        this.brandList = brandList;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_product_list_item, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (brandList == null) return 0;
        return brandList.size();
    }

    public void setBrandList(List<Brand> brands) {
        brandList = brands;
        notifyDataSetChanged();
    }

    public class BrandViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_product_item_image)
        ImageView brandImage;
        @BindView(R.id.user_product_item_name)
        TextView brandName;
        @BindView(R.id.user_product_item_price_layout)
        LinearLayout priceLayout;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            priceLayout.setVisibility(View.GONE);
        }

        public void bind(int position) {
            if (brandList.get(position).getImage() != null)
                Picasso.get().load(brandList.get(position).getImage()).into(brandImage);
            brandName.setVisibility(View.GONE);
        }
    }
}
