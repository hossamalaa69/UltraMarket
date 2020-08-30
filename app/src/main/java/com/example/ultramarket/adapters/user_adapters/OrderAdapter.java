package com.example.ultramarket.adapters.user_adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.Holder> {
    private List<Order> orderList;
    private Context mContext;

    public OrderAdapter(Context mContext, List<Order> orderList) {
        this.orderList = orderList;
        this.mContext = mContext;
    }

    public void insertOrder(Order order) {
        if (orderList == null) {
            orderList = new ArrayList<>();
        }
        orderList.add(order);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_order_list_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (orderList == null) return 0;
        return orderList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_order_list_item_rv)
        RecyclerView recyclerView;
        @BindView(R.id.user_order_list_item_order_id)
        TextView orderId;
        @BindView(R.id.user_order_list_item_order_time)
        TextView orderTime;
        @BindView(R.id.user_order_list_item_receive_time)
        TextView receiveTime;
        @BindView(R.id.user_order_list_item_total_price)
        TextView totalPrice;
        @BindView(R.id.user_order_list_item_products)
        TextView mShowProducts;
        OrderProductAdapter adapter;

        @OnClick(R.id.user_order_list_item_products)
        public void onProductsClicked() {
            if (mShowProducts.isSelected()) {
                hideProducts();
                mShowProducts.setCompoundDrawables(
                        null,null, ContextCompat.getDrawable(mContext,R.drawable.arrow_up_24),null);
                mShowProducts.setSelected(false);
            } else {
                showProducts();
                mShowProducts.setCompoundDrawables(
                        null,null, ContextCompat.getDrawable(mContext,R.drawable.arrow_down_24),null);
                mShowProducts.setSelected(true);
            }
        }

        private void showProducts() {
            adapter = new OrderProductAdapter(mContext, orderList.get(getAdapterPosition()).getProducts());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setVisibility(View.VISIBLE);
        }

        private void hideProducts() {
            recyclerView.setVisibility(View.GONE);
        }

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int pos) {
            recyclerView.setVisibility(View.GONE);
            mShowProducts.setSelected(false);
            totalPrice.setText(mContext.getString(R.string.price, orderList.get(pos).getPrice()));
            receiveTime.setText(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                    .format(new Date(orderList.get(pos).getReceiving_date())));
            orderTime.setText(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                    .format(new Date(orderList.get(pos).getOrder_date())));
            orderId.setText(orderList.get(pos).getID());
        }
    }
}
