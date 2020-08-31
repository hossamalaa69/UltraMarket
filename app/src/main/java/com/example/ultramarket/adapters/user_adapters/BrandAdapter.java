package com.example.ultramarket.adapters.user_adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
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
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.ui.userUi.Activities.CategoryBrandProductsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private Context mContext;
    private List<Brand> brandList;
    private BrandCallBacks interfaceInstance;
    public interface BrandCallBacks{
        void onBrandClickedListener(Intent intent, View sharedView);
    }

    public BrandAdapter(Context mContext, List<Brand> brandList, Fragment listener) {
        this.mContext = mContext;
        interfaceInstance = (BrandCallBacks) listener;
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CategoryBrandProductsActivity.class);
                    intent.putExtra("type", Brand.TYPE_ID);
                    intent.putExtra("id", brandList.get(getAdapterPosition()).getID());
                    intent.putExtra("name", brandList.get(getAdapterPosition()).getName());
                    interfaceInstance.onBrandClickedListener(intent,brandName);

                }
            });
            priceLayout.setVisibility(View.GONE);
        }

        public void bind(int position) {
            if (brandList.get(position).getImage() != null)
                Picasso.get().load(brandList.get(position).getImage()).into(brandImage);
            brandName.setText(brandList.get(position).getName());
        }
    }
}
