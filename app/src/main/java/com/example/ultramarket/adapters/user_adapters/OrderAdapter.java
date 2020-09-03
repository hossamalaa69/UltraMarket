package com.example.ultramarket.adapters.user_adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;
import com.example.ultramarket.ui.userUi.Activities.TrackOrderActivity;

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

    public void removeOrder(Order order) {
        orderList.remove(order);
        notifyDataSetChanged();
    }

    public void updateOrder(Order order) {
        for (int i = 0; i < orderList.size(); i++) {
            if (order.getID().matches(orderList.get(i).getID())) {
                orderList.remove(i);
                orderList.add(order);
                notifyDataSetChanged();
                return;
            }
        }
        orderList.add(order);
        notifyDataSetChanged();
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
                mShowProducts.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.arrow_down_24, 0);
                mShowProducts.setSelected(false);
            } else {
                showProducts();
                mShowProducts.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.arrow_up_24, 0);
                mShowProducts.setSelected(true);
            }
        }

        private void showProducts() {
            adapter = new OrderProductAdapter(mContext, orderList.get(getAdapterPosition()).getProducts());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ValueAnimator animator = ValueAnimator.ofInt(recyclerView.getHeight(), recyclerView.getMeasuredHeight());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    params.height = value;
                    recyclerView.setLayoutParams(params);
                }
            });
            animator.setDuration(500);
            animator.start();

        }

        private void hideProducts() {
            ValueAnimator animator = ValueAnimator.ofInt(recyclerView.getHeight(), 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (int) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    params.height = value;
                    recyclerView.setLayoutParams(params);
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.setDuration(500);
            animator.start();

        }

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TrackOrderActivity.class);
                    intent.putExtra("order_id", orderList.get(getAdapterPosition()).getID());
                    mContext.startActivity(intent);
                }
            });
        }

        public void bind(int pos) {
            recyclerView.setVisibility(View.GONE);
            mShowProducts.setSelected(false);
            totalPrice.setText(mContext.getString(R.string.total_price, orderList.get(pos).getPrice()));
            if (orderList.get(pos).getStatus() != Order.STATUS_DELIVERED) {
                receiveTime.setText(mContext.getString(R.string.receiving_time, new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                        .format(new Date(orderList.get(pos).getReceiving_date()))));
            } else {
                receiveTime.setText(R.string.delivered);
            }
            orderTime.setText(mContext.getString(R.string.order_time, new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                    .format(new Date(orderList.get(pos).getOrder_date()))));
            orderId.setText(mContext.getString(R.string.order_id_is, orderList.get(pos).getID()));
        }
    }
}
