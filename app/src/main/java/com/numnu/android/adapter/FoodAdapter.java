package com.numnu.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.numnu.android.R;
import com.numnu.android.activity.CompleteSignupActivity;

/**
 * Created by lenovo on 11/6/2017.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    String[] arr = {"Biryani", "Biryani", "Biryani", "Biryani", "Biryani", "Biryani",  "Biryani", "Biryani"};
//
    Context context;
    LayoutInflater layout;

    public FoodAdapter(CompleteSignupActivity completeSignupActivity, Context context) {
        this.context = context;
        layout = LayoutInflater.from(context);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView FoodText;


        public ViewHolder(View itemView) {
            super(itemView);
            FoodText=(TextView)itemView.findViewById(R.id.food_txt);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder Holder, final int position) {

        Holder.FoodText.setText(arr[position]);

//

    }

    @Override
    public int getItemCount() {
        return arr.length;
    }
}


