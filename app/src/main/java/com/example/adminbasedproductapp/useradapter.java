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
import java.util.List;

public class useradapter extends RecyclerView.Adapter<useradapter.MyViewHolder>  {

    private Context context;
    private List<String> usersList;

    private OnItemClickListener listener;

    public useradapter(Context context, List<String> usersList) {
        this.context = context;
        this.usersList = usersList;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.item_layout,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  useradapter.MyViewHolder myViewHolder, int i) {
        String user = usersList.get(i);
        myViewHolder.textView.setText(user);
    }



    @Override
    public int getItemCount() {
        return usersList.size();
    }





    public class MyViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener , MenuItem.OnMenuItemClickListener{
        TextView textView;

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

            MenuItem admin = menu.add(Menu.NONE,1,1,"Make Admin");
            MenuItem editor = menu.add(Menu.NONE,2,2,"Make Editor");
            MenuItem viewer = menu.add(Menu.NONE,3,3,"Make Viewer");

            editor.setOnMenuItemClickListener(this);
            admin.setOnMenuItemClickListener(this);
            viewer.setOnMenuItemClickListener(this);


        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if(listener != null){
                int position = getAdapterPosition();

                if(position!= RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            listener.beAdmin(position);
                            return true;

                        case 2:
                            listener.beEditor(position);
                            return true;

                        case 3:
                        listener.beViewer(position);
                        return true;

                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void beAdmin(int position);
        void beEditor(int position);
        void beViewer(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){

        this.listener = listener;
    }


}
