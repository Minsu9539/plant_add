package com.example.plnatsub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyPlantBookAdapter extends RecyclerView.Adapter<MyPlantBookAdapter.MyViewHolder> {
    //private String[] mDataset;
    private ArrayList<PlantBookData> arrayList;
    private Context context;

    public MyPlantBookAdapter(ArrayList<PlantBookData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv_thumbnail;
        protected TextView tv_title;


        public MyViewHolder(View v) {
            super(v);
            this.iv_thumbnail = (ImageView) v.findViewById(R.id.iv_thumbnail);
            this.tv_title = (TextView) v.findViewById(R.id.tv_title);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    /*
    public SearchResultAdapter(String[] myDataset) {
        mDataset = myDataset;
    }
     */

    // Create new views (invoked by the layout manager)
    @Override
    public MyPlantBookAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 뷰 만들어지는곳
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_plant_book, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //생성되는것 연결
        Glide.with(holder.itemView).load(arrayList.get(position).getIv_thumbnail())
                .into(holder.iv_thumbnail);
        holder.tv_title.setText(arrayList.get(position).getTv_title());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}