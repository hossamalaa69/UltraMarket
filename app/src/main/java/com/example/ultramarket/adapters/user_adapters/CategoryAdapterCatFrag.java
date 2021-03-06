package com.example.ultramarket.adapters.user_adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
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
import com.example.ultramarket.database.Entities.Category;
import com.example.ultramarket.ui.userUi.Activities.CategoryBrandProductsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapterCatFrag extends RecyclerView.Adapter<CategoryAdapterCatFrag.ProductViewHolder> {

    private Context mContext;
    private List<Category> categoryList;
    private CategoryCallBacks interfaceInstance;
    public interface CategoryCallBacks{
        void onCategoryClickedListener(Intent intent, View sharedView);
    }

    public CategoryAdapterCatFrag(Context mContext, ArrayList<Category> categoryList, Fragment listener) {
        this.mContext = mContext;
        interfaceInstance = (CategoryCallBacks) listener;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryAdapterCatFrag.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_product_cat_frag_list_item, parent, false);
        return new CategoryAdapterCatFrag.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapterCatFrag.ProductViewHolder holder, int position) {
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

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CategoryBrandProductsActivity.class);
                    intent.putExtra("type", Category.TYPE_ID);
                    intent.putExtra("name", categoryList.get(getAdapterPosition()).getName());
                    intent.putExtra("id", categoryList.get(getAdapterPosition()).getID());
                    interfaceInstance.onCategoryClickedListener(intent,catName);
                }
            });
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width =  displayMetrics.widthPixels;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemView.getLayoutParams());
            params.width =  width / 2 - (width/10);
            params.height = width /2;
            params.setMargins(20,20,20,20);
            itemView.setLayoutParams(params);
        }
        public void bind(int position) {
            if (categoryList.get(position).getImage() != null)
                Picasso.get().load(categoryList.get(position).getImage()).into(catImage);
            catName.setText(categoryList.get(position).getName());
        }
    }
}
