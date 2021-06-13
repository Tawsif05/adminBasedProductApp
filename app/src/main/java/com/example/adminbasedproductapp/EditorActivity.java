package com.example.adminbasedproductapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class EditorActivity extends AppCompatActivity {

    private Button view, update, search;
    private EditText name, code, price;
    private TextView text;
    ArrayList<String> arrayList;
    Dialog dialog;

    version realmVersion = new version(0);

    Realm realm;

    DatabaseReference ref,fireVer;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mAuth = FirebaseAuth.getInstance();
        this.setTitle("Editors Panel");

        ref = FirebaseDatabase.getInstance().getReference("Product");
        fireVer = FirebaseDatabase.getInstance().getReference("Version");

        view = (Button) findViewById(R.id.view);
        update = (Button) findViewById(R.id.update);
        search = (Button) findViewById(R.id.Search);
        name = findViewById(R.id.PNameID);
        code = findViewById(R.id.PCodeID);
        price = findViewById(R.id.PPriceID);
        text = findViewById(R.id.text);

        checkVersion();


        Realm.init(this);

        realm = Realm.getDefaultInstance();

        arrayList = new ArrayList<>();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewRecord();



                name.setText("");
                code.setText("");
                price.setText("");
            }

        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean a = updateRecord();
                if(a==true) {
                    updateRecordFB();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Code not found",Toast.LENGTH_SHORT).show();
                }
                name.setText("");
                code.setText("");
                price.setText("");
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrayList.clear();
                RealmResults<Products> r = realm.where(Products.class).findAll();
                for(Products p: r){
                    arrayList.add(p.getCode().toString());
                }

                dialog = new Dialog(EditorActivity.this);

                dialog.setContentView(R.layout.dialog_searchable_spinner);

                dialog.getWindow().setLayout(650,800);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                dialog.show();

                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);



                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setTextColor(Color.BLACK);
                        return textView;
                    }
                };

                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        code.setText(adapter.getItem(position));

                        RealmResults<Products> search = realm.where(Products.class).equalTo("Code", code.getText().toString()).findAll();
                        for(Products a : search){
                            name.setText(a.getName());
                            price.setText(a.getPrice());

                        }





                        dialog.dismiss();
                    }
                });
            }
        });


    }
    //SignOut Code Starts
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
    //SignOut Code Ends


    private void checkVersion() {
        fireVer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot versionData : snapshot.getChildren()) {
                    if ( versionData.getValue().toString().equalsIgnoreCase(realmVersion.toString())  ) {
                        Toast.makeText(getApplicationContext(),"No need of sync",Toast.LENGTH_SHORT).show();
                    } else {
                        realmVersion.setV(Integer.parseInt(versionData.getValue().toString()));
                        versionSync();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void versionSync() {


        DatabaseReference ver = FirebaseDatabase.getInstance().getReference().child("Product");
        ver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RealmResults<Products> results = realm.where(Products.class).findAll();
                realm.beginTransaction();
                results.deleteAllFromRealm();
                realm.commitTransaction();
                Toast.makeText(getApplicationContext(),"Sync Completed",Toast.LENGTH_SHORT).show();
                for(DataSnapshot datasnapshot1 : snapshot.getChildren()){
                    realm.beginTransaction();
                    Products p = realm.createObject(Products.class);
                    p.setCode(Objects.requireNonNull(datasnapshot1.child("code").getValue()).toString());
                    p.setName(Objects.requireNonNull(datasnapshot1.child("name").getValue()).toString());
                    p.setPrice(Objects.requireNonNull(datasnapshot1.child("price").getValue()).toString());
                    realm.commitTransaction();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void viewRecord(){
        RealmResults<Products> results = realm.where(Products.class).findAll();
        text.setText("");
        for(Products p : results){
            text.append( p.getCode() + " " + p.getName() +  " " + p.getPrice()+"\n");
        }
    }

    public boolean updateRecord(){
        RealmResults<Products> results = realm.where(Products.class).equalTo("Code", code.getText().toString()).findAll();
        if(results.isEmpty()) {
            return false;
        }
        else{
            realm.beginTransaction();

            for(Products p : results){
                p.setName(name.getText().toString());
                p.setPrice(price.getText().toString());
            }
            realm.commitTransaction();
            return true;
        }

    }
    private void updateRecordFB() {
        String n = name.getText().toString();
        String c = code.getText().toString();
        String p = price.getText().toString();
        ProductFB pro = new ProductFB(n, c, p);
        try {
            ref.child(c).setValue(pro);
            realmVersion.setV(realmVersion.getV()+1);
            fireVer.child("version number").setValue(realmVersion.getV());
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}