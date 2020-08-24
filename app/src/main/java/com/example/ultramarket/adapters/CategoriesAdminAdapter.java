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
import com.example.ultramarket.database.Entities.Category;

import java.util.List;

public class CategoriesAdminAdapter extends RecyclerView.Adapter<CategoriesAdminAdapter.CategoriesVH> {

    private static final String TAG = "CategoriesAdapter";
    List<Category> categoryList;
    Context context;

    private CategoriesAdminAdapter.OnItemClickListener mListener;

    /**
     * interface that handles clicking on items
     */
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    /**
     * handles item clicking
     * @param listener object of listener
     */
    public void setOnItemClickListener(CategoriesAdminAdapter.OnItemClickListener listener) {
        mListener = listener;
    }


    public CategoriesAdminAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoriesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_category_admin, parent, false);
        return new CategoriesVH(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesVH holder, int position) {

        Category category = categoryList.get(position);
        holder.nameTextView.setText(category.getName());
        if(category.getImage() == null){
            holder.categoryImage.setImageResource(R.drawable.ic_image_placeholder);
        }else{
            Glide.with(context)
                    .load(category.getImage())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(holder.categoryImage);
        }
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setCategoryList(List<Category> categories) {
        categoryList = categories;
        notifyDataSetChanged();
    }

    public List<Category> getCategories(){
        return categoryList;
    }

    public void clearData() {
        categoryList.clear(); // clear list
        notifyDataSetChanged(); // let your adapter know about the changes and reload view.
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoriesVH extends RecyclerView.ViewHolder {

        private static final String TAG = "CategoriesVH";

        TextView nameTextView;
        ImageView categoryImage;

        public CategoriesVH(@NonNull final View itemView, final CategoriesAdminAdapter.OnItemClickListener listener) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.category_name_rv);
            categoryImage = (ImageView) itemView.findViewById(R.id.img_category_rv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        //gets position of clicked item
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
