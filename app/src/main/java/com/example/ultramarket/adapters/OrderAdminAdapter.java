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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderAdminAdapter extends RecyclerView.Adapter<OrderAdminAdapter.OrderAdminVH> {

    private static final String TAG = "OrderAdminAdapter";
    List<Order> orderList;
    Context context;

    public OrderAdminAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderAdminAdapter.OrderAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_order_admin, parent, false);
        return new OrderAdminAdapter.OrderAdminVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdminVH holder, int position) {

        Order order = orderList.get(position);

        boolean isExpanded = orderList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setImageResource(isExpanded? R.drawable.ic_collapse : R.drawable.ic_expand);

        holder.order_id.setText("Order ID: " + order.getID());
        holder.order_cost.setText("Cost: " + order.getPrice());
        holder.order_date.setText("Order Date: " + new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                .format(new Date(order.getOrder_date())));
        holder.order_date_delivery.setText("Deliver Date: " + new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss")
                .format(new Date(order.getReceiving_date())));

        holder.order_status.setText("Status: " + order.getStatusStr());

        holder.orderProdAdminAdapter = new OrderProdAdminAdapter(context, order.getProducts());
        holder.products_recycler.setLayoutManager(new LinearLayoutManager(context));
        holder.products_recycler.setAdapter(holder.orderProdAdminAdapter);
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setOrderList(List<Order> orders) {
        orderList = orders;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderAdminVH extends RecyclerView.ViewHolder {

        private static final String TAG = "OrderAdminVH";

        ConstraintLayout expandableLayout;
        TextView order_id, order_date, order_cost, order_date_delivery, order_status;
        RecyclerView products_recycler;
        OrderProdAdminAdapter orderProdAdminAdapter;
        ImageView expandIcon;
        RelativeLayout mainView;

        public OrderAdminVH(@NonNull final View itemView) {
            super(itemView);

            order_id = (TextView) itemView.findViewById(R.id.order_id);
            order_date = (TextView) itemView.findViewById(R.id.order_date);
            order_cost = (TextView) itemView.findViewById(R.id.order_cost);
            order_date_delivery = (TextView) itemView.findViewById(R.id.order_date_delivery);
            order_status = (TextView) itemView.findViewById(R.id.order_status);

            products_recycler = (RecyclerView) itemView.findViewById(R.id.products_order_recycler);

            expandIcon = (ImageView) itemView.findViewById(R.id.drop_button_order_admin);
            expandableLayout = itemView.findViewById(R.id.expandableLayout_order);
            mainView = (RelativeLayout) itemView.findViewById(R.id.main_view_order);

            mainView.setOnClickListener(view -> {
                Order order = orderList.get(getAdapterPosition());
                order.setExpanded(!order.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}