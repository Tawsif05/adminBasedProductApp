package com.example.adminbasedproductapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class userHomePage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<Products> productList;
    DatabaseReference databaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        recyclerView = findViewById(R.id.recycleViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.RecyclerprogressBarId);

        productList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Product");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot datasnapshot) {
                productList.clear();
                for(DataSnapshot dataSnapshot1 : datasnapshot.getChildren()){

                    Products product = new Products();
                    product.setCode((dataSnapshot1.child("code").getValue()).toString());
                    product.setName((dataSnapshot1.child("name").getValue()).toString());
                    product.setPrice((dataSnapshot1.child("price").getValue()).toString());

                    productList.add(product);

                }
                myAdapter = new MyAdapter(userHomePage.this,productList);
                recyclerView.setAdapter(myAdapter);


                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error : "+ error.getMessage(),Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.signOutMenuId){
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}