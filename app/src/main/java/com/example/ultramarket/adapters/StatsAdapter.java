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

import java.util.ArrayList;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.Holder> {

    private ArrayList<String> mSubjects = new ArrayList<>();

    private ArrayList<Integer> mImagesResources = new ArrayList<>();
    /**
     * holds context that calls the adapter
     */
    private Context mContext;


    public StatsAdapter(ArrayList<String> Subjects, ArrayList<Integer> ImagesResources, Context mContext) {
        this.mSubjects = Subjects;
        this.mImagesResources = ImagesResources;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //sets layout for each item in recycler
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item_stats, parent, false);

        return new Holder(view);
    }

    /**
     * Updates the contents with the item at the given position
     * @param holder represent the contents of the item at the given position
     * @param position position of current item
     */
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    /**
     * holds total number of items in adapter
     * @return return size of array of item
     */
    @Override
    public int getItemCount() {
        return mSubjects.size();
    }


    /**
     * Handles an item view and metadata about its place within the RecyclerView.
     */
    public class Holder extends RecyclerView.ViewHolder  {

        /**
         * text view which holds free feature
         */
        TextView subject;
        /**
         * text view which holds premium feature
         */
        ImageView image;

        public Holder(@NonNull View itemView) {
            super(itemView);

            //gets text views that holds text
            subject = itemView.findViewById(R.id.txt_id);
            image = itemView.findViewById(R.id.image_id);
        }

        /**
         * Fill each item in the view with its data
         * @param position index of item in recycler view
         */
        public void bind(int position) {
            //gets each feature from array, then sets it in text views
            String t = mSubjects.get(position);
            int i = mImagesResources.get(position);
            subject.setText(t);
            image.setImageResource(i);
        }
    }
}