package com.example.ultramarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Brand;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BrandsAdminAdapter extends RecyclerView.Adapter<BrandsAdminAdapter.BrandVH> {

    private static final String TAG = "BrandsAdapter";
    List<Brand> brandList;
    Context context;

    public BrandsAdminAdapter(Context context, List<Brand> brandList) {
        this.context = context;
        this.brandList = brandList;
    }

    @NonNull
    @Override
    public BrandVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_brand_admin, parent, false);
        return new BrandVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandVH holder, int position) {

        Brand brand = brandList.get(position);
        holder.nameTextView.setText(brand.getName());
        if(brand.getImage() == null){
            holder.brandImage.setImageResource(R.drawable.ic_image_placeholder);
        }else{
            Glide.with(context)
                    .load(brand.getImage())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.brandImage);
        }
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setBrandList(List<Brand> brands) {
        brandList = brands;
        notifyDataSetChanged();
    }

    public List<Brand> getBrands(){
        return brandList;
    }

    public void clearData() {
        brandList.clear(); // clear list
        notifyDataSetChanged(); // let your adapter know about the changes and reload view.
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    class BrandVH extends RecyclerView.ViewHolder {

        private static final String TAG = "BrandVH";

        TextView nameTextView;
        ImageView brandImage;

        public BrandVH(@NonNull final View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.brand_name_rv);
            brandImage = (ImageView) itemView.findViewById(R.id.img_brand_rv);
        }
    }
}
