package com.numnu.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.numnu.android.R;
import com.numnu.android.fragments.auth.LoginFragment;
import com.numnu.android.fragments.detail.ItemInfoFragment;
import com.numnu.android.utils.PreferencesHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by thulir on 10/10/17.
 */

public class EventItemsListAdapter extends RecyclerView.Adapter<EventItemsListAdapter.ViewHolder> {

    Context context;
    ArrayList<String> stringArrayList = new ArrayList<>();
    HorizontalContentAdapter adapter;
    RecyclerView recyclerView;

    public EventItemsListAdapter(Context context, ArrayList<String> stringArrayList) {
        this.context=context;
        this.stringArrayList=stringArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listitem, parent, false);

        //view.setOnClickListener(MainActivity.myOnClickListener);

        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Picasso.with(context).load(R.drawable.large_berger)
                .placeholder(R.drawable.food_1631727_1920)
                .fit()
                .into(holder.imageViewIcon);
        holder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction =  ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_righ);
                transaction.replace(R.id.frame_layout, ItemInfoFragment.newInstance());
                transaction.addToBackStack(null).commit();
            }
        });
        holder.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction =  ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_righ);
                transaction.replace(R.id.frame_layout, ItemInfoFragment.newInstance());
                transaction.addToBackStack(null).commit();
            }
        });

        adapter = new HorizontalContentAdapter(context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//
    }


    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private  ImageView imageViewIcon;
        private TextView textViewName;


        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewName =  itemView.findViewById(R.id.item_name);
            recyclerView=(RecyclerView)itemView.findViewById(R.id.business_recyclerview);
            //this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.imageViewIcon = itemView.findViewById(R.id.item_image);

        }
    }


}
