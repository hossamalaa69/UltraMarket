package com.example.ultramarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Brand;
import com.example.ultramarket.database.Entities.Product;

import java.util.List;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ProductAdminVH> {

    private static final String TAG = "ProductAdminAdapter";
    List<Product> productList;
    Context context;

    public ProductAdminAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductAdminAdapter.ProductAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_product_admin, parent, false);
        return new ProductAdminAdapter.ProductAdminVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdminAdapter.ProductAdminVH holder, int position) {

        Product product = productList.get(position);

        holder.product_name_admin.setText(product.getName());
        holder.product_desc_admin.setText(product.getDescription());
        holder.prod_price_admin.setText(product.getPrice() + " " + product.getCurrency() + ", per " + product.getUnit());
        holder.prod_discount_admin.setText("Discount " + product.getDiscount_percentage() + "%");
        holder.prod_quantity_admin.setText("" + product.getCount() + " Units");
        holder.prod_orders_admin.setText("" + product.getOrders_number());
        holder.product_brand_admin.setText(product.getBrand_name());
        holder.product_category_admin.setText(product.getCategory_name());

        if(product.getImage() == null){
            holder.product_image_admin.setImageResource(R.drawable.ic_products);
        }else{
            Glide.with(context)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_products)
                    .into(holder.product_image_admin);
        }

        boolean isExpanded = productList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setImageResource(isExpanded? R.drawable.ic_collapse : R.drawable.ic_expand);
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setProductList(List<Product> products) {
        productList = products;
        notifyDataSetChanged();
    }

    public List<Product> getProducts(){
        return productList;
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductAdminVH extends RecyclerView.ViewHolder {

        private static final String TAG = "ProductAdminVH";

        ImageView product_image_admin, expandIcon;
        TextView product_name_admin, product_desc_admin, prod_price_admin, prod_discount_admin
                , prod_quantity_admin, prod_orders_admin, product_brand_admin, product_category_admin;

        ConstraintLayout expandableLayout;
        RelativeLayout mainView;

        public ProductAdminVH(@NonNull final View itemView) {
            super(itemView);

            product_image_admin = (ImageView) itemView.findViewById(R.id.product_image_admin);
            product_name_admin = (TextView) itemView.findViewById(R.id.product_name_admin);
            product_desc_admin = (TextView) itemView.findViewById(R.id.product_desc_admin);
            prod_price_admin = (TextView) itemView.findViewById(R.id.prod_price_admin);
            prod_discount_admin = (TextView) itemView.findViewById(R.id.prod_discount_admin);
            prod_quantity_admin = (TextView) itemView.findViewById(R.id.prod_quantity_admin);
            prod_orders_admin = (TextView) itemView.findViewById(R.id.prod_orders_admin);
            product_brand_admin = (TextView) itemView.findViewById(R.id.product_brand_admin);
            product_category_admin = (TextView) itemView.findViewById(R.id.product_category_admin);

            expandIcon = (ImageView) itemView.findViewById(R.id.drop_button_product_admin);

            expandableLayout = itemView.findViewById(R.id.expandableLayout_product);
            mainView = (RelativeLayout) itemView.findViewById(R.id.main_view_product);

            mainView.setOnClickListener(view -> {
                Product product = productList.get(getAdapterPosition());
                product.setExpanded(!product.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}