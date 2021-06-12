package com.example.adminbasedproductapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class userList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private useradapter myAdapter;
    private List<String> usersList;
    DatabaseReference databaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.setTitle("All User");

        recyclerView = findViewById(R.id.recycleViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.RecyclerprogressBarId);

        usersList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                usersList.clear();
                for(DataSnapshot dataSnapshot1 : datasnapshot.getChildren()){

                    if(dataSnapshot1.child("Admin").getValue().toString().equals("1")) {
                        usersList.add(dataSnapshot1.child("Email").getValue().toString() + "\nAdmin");
                    }
                    if(dataSnapshot1.child("Editor").getValue().toString().equals("1")) {
                        usersList.add(dataSnapshot1.child("Email").getValue().toString() + "\nEditor");
                    }
                    if(dataSnapshot1.child("Viewer").getValue().toString().equals("1")) {
                        usersList.add(dataSnapshot1.child("Email").getValue().toString() + "\nViewer");
                    }

                }
                myAdapter = new useradapter(userList.this,usersList);
                recyclerView.setAdapter(myAdapter);


                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        if(item.getItemId()==android.R.id.home){
            finish();
            Intent intent = new Intent(getApplicationContext(),adminHomePage.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}