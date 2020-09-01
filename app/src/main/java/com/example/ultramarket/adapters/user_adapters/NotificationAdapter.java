package com.example.ultramarket.adapters.user_adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.Notification;
import com.example.ultramarket.ui.userUi.Activities.CategoryBrandProductsActivity;
import com.example.ultramarket.ui.userUi.Activities.ProductActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context mContext;
    private List<Notification> notificationList;
    private NotificationCallBacks interfaceInstance;

    public interface NotificationCallBacks {
        void onNotificationClickedListener(Intent intent, View sharedView);
    }

    public NotificationAdapter(Context mContext, List<Notification> notificationList) {
        this.mContext = mContext;
        this.interfaceInstance =(NotificationCallBacks) mContext;
        this.notificationList = notificationList;
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_ad_layout, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (notificationList == null) return 0;
        return notificationList.size();
    }

    public void setNotificationList(List<Notification> brands) {
        notificationList = brands;
        notifyDataSetChanged();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_ad_img)
        ImageView NotificationImage;
        @BindView(R.id.user_ad_title)
        TextView NotificationTitle;
        @BindView(R.id.user_ad_body)
        TextView NotificationBody;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProductActivity.class);
                    intent.putExtra("prod_id", notificationList.get(getAdapterPosition()).getProductID());
                    interfaceInstance.onNotificationClickedListener(intent, NotificationImage);

                }
            });
        }

        public void bind(int position) {
            if (notificationList.get(position).getImageUrl() != null)
                Picasso.get().load(notificationList.get(position).getImageUrl()).into(NotificationImage);
            NotificationTitle.setText(notificationList.get(position).getTitle());
            NotificationBody.setText(notificationList.get(position).getBody());
        }
    }
}
