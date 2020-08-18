package com.example.ultramarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultramarket.R;
import com.example.ultramarket.database.Entities.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.MovieVH> {

    private static final String TAG = "CustomersAdapter";
    List<User> userList;
    Context context;

    public CustomersAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MovieVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_customer, parent, false);
        return new MovieVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVH holder, int position) {

        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());
        holder.phoneTextView.setText(user.getPhone());
        holder.rateTextView.setText("" + user.getRate());
        holder.ordersTextView.setText("" + user.getNumOrders());

        holder.userImage.setImageResource(user.getImageID());

        boolean isExpanded = userList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setImageResource(isExpanded? R.drawable.ic_collapse : R.drawable.ic_expand);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MovieVH extends RecyclerView.ViewHolder {

        private static final String TAG = "CustomerVH";

        ConstraintLayout expandableLayout;
        TextView nameTextView, emailTextView, phoneTextView, ordersTextView, rateTextView;
        CircleImageView userImage;
        ImageView expandIcon, callIcon, mailIcon;

        RelativeLayout mainView;

        public MovieVH(@NonNull final View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.user_name);
            emailTextView = (TextView) itemView.findViewById(R.id.user_email);
            phoneTextView = (TextView) itemView.findViewById(R.id.user_phone);
            ordersTextView = (TextView) itemView.findViewById(R.id.user_orders);
            rateTextView = (TextView) itemView.findViewById(R.id.user_rate);

            userImage = (CircleImageView) itemView.findViewById(R.id.user_image);
            expandIcon = (ImageView) itemView.findViewById(R.id.drop_button);
            callIcon = (ImageView) itemView.findViewById(R.id.image_phone);
            mailIcon = (ImageView) itemView.findViewById(R.id.image_email);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            mainView = (RelativeLayout) itemView.findViewById(R.id.main_view);

            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = userList.get(getAdapterPosition());
                    user.setExpanded(!user.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            callIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:"+phoneTextView.getText().toString()));
                    context.startActivity(i);
                }
            });

            mailIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"+emailTextView.getText().toString()));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}