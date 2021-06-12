package com.example.adminbasedproductapp;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MyAdapter<productsList> extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<String> productsList;
    private List<String> productListAll;
    private OnItemClickListener listener;
    private List<Products> pdeatils;



    public MyAdapter(Context context, List<String> productsList,List<Products> pdeatils) {
        this.context = context;
        this.productsList = productsList;
        this.productListAll = new ArrayList<>(productsList);
        this.pdeatils = pdeatils;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.item_layout,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String product =productsList.get(i);
        for(Products p : pdeatils) {
            if (p.getName().equals(product)) {
                myViewHolder.textView.setText("Product : "+p.getName()+"\nCode : "+p.getCode()+"\nPrice : "+p.getPrice());
            }
        }

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public  Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        //RUn on Background
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {


            List<String> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredList.addAll(productListAll);

            }else{
                for(String user:productListAll){
                    if(user.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(user);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;


            return filterResults;
        }
        //Run on UI Thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            productsList.clear();
            productsList.addAll((Collection<? extends String>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener , MenuItem.OnMenuItemClickListener{
        TextView textView;
        ImageView imageView;
        private OnItemClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.cardTextViewId);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {
            if(listener != null){
                int position = getAdapterPosition();

                if(position!= RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Choose an action");
            MenuItem doanyTask = menu.add(Menu.NONE,1,1,"Do any Task");
            MenuItem delete = menu.add(Menu.NONE,2,2,"Delete");

            doanyTask.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);


        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if(listener != null){
                int position = getAdapterPosition();

                if(position!= RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:

                            listener.onDoAnyTask(position);
                            return true;

                        case 2:
                            listener.onDelete(position);
                            return true;

                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDoAnyTask(int position);
        void onDelete(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){

        this.listener = listener;
    }
}
