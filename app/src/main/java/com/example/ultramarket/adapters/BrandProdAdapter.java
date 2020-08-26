package com.example.ultramarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Brand;

import java.util.ArrayList;
import java.util.List;

public class BrandProdAdapter extends RecyclerView.Adapter<BrandProdAdapter.SingleViewHolder> {

    private Context context;
    private List<Brand> brands;
    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private int checkedPosition = 0;

    public BrandProdAdapter(Context context, List<Brand> brands) {
        this.context = context;
        this.brands = brands;
    }

    public void setBrands(List<Brand> brands, int initSelection) {
        checkedPosition = initSelection;
        this.brands = new ArrayList<>();
        this.brands = brands;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_brand_cat, viewGroup, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolder singleViewHolder, int position) {
        singleViewHolder.bind(brands.get(position));
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    class SingleViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(final Brand brand) {
            if (checkedPosition == -1) {
                imageView.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
            textView.setText(brand.getName());

            itemView.setOnClickListener(view -> {
                imageView.setVisibility(View.VISIBLE);
                if (checkedPosition != getAdapterPosition()) {
                    notifyItemChanged(checkedPosition);
                    checkedPosition = getAdapterPosition();
                }
            });
        }
    }

    public Brand getSelected() {
        if (checkedPosition != -1) {
            return brands.get(checkedPosition);
        }
        return null;
    }

}