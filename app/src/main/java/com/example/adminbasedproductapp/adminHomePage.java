package com.example.adminbasedproductapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class adminHomePage extends AppCompatActivity {
    private Button add, view, update, delete,search,version;
    private EditText name, code, price;
    private TextView text;
    ArrayList<String> arrayList;
    Dialog dialog;

    version realmVersion = new version(0);

    Realm realm;

    DatabaseReference ref,fireVer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ref = FirebaseDatabase.getInstance().getReference("Product");
        fireVer = FirebaseDatabase.getInstance().getReference("Version");

        add = (Button) findViewById(R.id.add);
        view = (Button) findViewById(R.id.view);
        update = (Button) findViewById(R.id.update);
        delete = (Button) findViewById(R.id.delete);
        search = (Button) findViewById(R.id.Search);
        name = findViewById(R.id.PNameID);
        version = (Button) findViewById(R.id.version);
        code = findViewById(R.id.PCodeID);
        price = findViewById(R.id.PPriceID);
        text = findViewById(R.id.text);


        Realm.init(this);

        realm = Realm.getDefaultInstance();

        arrayList = new ArrayList<>();






        version.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                Toast.makeText(getApplicationContext(),"button Ok",Toast.LENGTH_SHORT).show();
                checkVersion();


            }
        });

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean a = addRecord();
                if(a == true) {
                    addToFB();
                }
                else{
//                    Toast.makeText(getApplicationContext(),"Code Exist in FB",Toast.LENGTH_SHORT).show();
                }
                name.setText("");
                code.setText("");
                price.setText("");

            }

        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewRecord();



                name.setText("");
                code.setText("");
                price.setText("");
            }

        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();
                RealmResults<Products> r = realm.where(Products.class).findAll();
                for(Products p: r){
                    arrayList.add(p.getCode().toString());
                }

                dialog = new Dialog(adminHomePage.this);

                dialog.setContentView(R.layout.delete_search);

                dialog.getWindow().setLayout(650,800);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                dialog.show();

                ListView listView = dialog.findViewById(R.id.delete_list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setTextColor(Color.BLACK);
                        return textView;
                    }
                };

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        AlertDialog.Builder alert = new AlertDialog.Builder(adminHomePage.this);

                        alert.setTitle("Delete entry");
                        RealmResults<Products> search = realm.where(Products.class).equalTo("Code", adapter.getItem(position)).findAll();
                        for(Products a : search) {
                            alert.setMessage("Are you sure you want to delete?\n" + "Product Name: "+ a.getName() + "\nProduct Code: " + a.getCode() + "\nProduct Price: " + a.getPrice()+"\n");

                        }
                        alert.setCancelable(false);
                        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                            public void onClick(DialogInterface dialog1, int which) {
                                deleteRecord(adapter.getItem(position));
                                deleteFromFB(adapter.getItem(position));
                                dialog1.dismiss();

                            }
                        });
                        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog1, int which) {
                                // close dialog
                                dialog1.dismiss();

                            }
                        });
                        alert.create().show();

//                        code.setText(adapter.getItem(position));

//                        RealmResults<Products> search = realm.where(Products.class).equalTo("Code", code.getText().toString()).findAll();
//                        for(Products a : search){
//                            name.setText(a.getName());
//                            price.setText(a.getPrice());
//
//                        }





//                        dialog.dismiss();
                    }
                });
//                deleteRecord();
//                deleteFromFB();

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

                dialog = new Dialog(adminHomePage.this);

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

    private void checkVersion() {
//        DatabaseReference verSync = FirebaseDatabase.getInstance().getReference().child("Version");
        Toast.makeText(getApplicationContext(),"function Ok",Toast.LENGTH_SHORT).show();
        fireVer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot versionData : snapshot.getChildren()) {
                    Toast.makeText(getApplicationContext(),versionData.child("v").getValue().toString(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),realmVersion.toString(),Toast.LENGTH_SHORT).show();
                    if ( versionData.child("v").getValue().toString().equalsIgnoreCase(realmVersion.toString())  ) {
                        Toast.makeText(getApplicationContext(),"No need of sync",Toast.LENGTH_SHORT).show();

                    } else {


                        realmVersion.setV(Integer.parseInt(versionData.child("v").getValue().toString()));
                        Toast.makeText(getApplicationContext(),versionData.child("v").getValue().toString(),Toast.LENGTH_SHORT).show();
                        versionSync();
                    }



                }
//                    for(DataSnapshot datasnapshot1 : snapshot.getChildren()) {
//                        Toast.makeText(getApplicationContext(),"logic Ok," + datasnapshot1.child("v").getValue(),Toast.LENGTH_SHORT).show();
//                        System.out.println(datasnapshot1.child("v").getValue());
//                    }




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

//                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void addToFB() {

        String n = name.getText().toString();
        String c = code.getText().toString();
        String p = price.getText().toString();

        ProductFB pro = new ProductFB(n, c, p);
        try {
            ref.child(c).setValue(pro);

            realmVersion.setV(realmVersion.getV()+1);
            fireVer.child("version").setValue(realmVersion);

            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public boolean addRecord(){

        RealmResults<Products> search = realm.where(Products.class).equalTo("Code", code.getText().toString()).findAll();
        if (search.isEmpty()) {
            if (TextUtils.isEmpty(code.getText())) {
                Toast.makeText(getApplicationContext(), "Code Exist or Code is empty", Toast.LENGTH_SHORT).show();
//                    viewRecord();
                return false;
            } else {
                realm.beginTransaction();
                Products p = realm.createObject(Products.class);
                p.setCode(code.getText().toString());
                p.setName(name.getText().toString());
                p.setPrice(price.getText().toString());

                realm.commitTransaction();


                return true;
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Code Exist or Code is empty", Toast.LENGTH_SHORT).show();



            return false;
        }



    }



    public void viewRecord(){
        RealmResults<Products> results = realm.where(Products.class).findAll();

        text.setText("");

        for(Products p : results){
            text.append( p.getCode() + " " + p.getName() +  " " + p.getPrice()+"\n");
        }




    }

    public void deleteRecord(String c){
        if(c.equals(null)) {

        }
        else{
            RealmResults<Products> results = realm.where(Products.class).equalTo("Code", c).findAll();
            realm.beginTransaction();
            results.deleteAllFromRealm();
            realm.commitTransaction();
        }



    }

    private void deleteFromFB(String co) {
        String c = co;

        if(c.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Enter Code to remove",Toast.LENGTH_SHORT).show();
        }
        else{
            ref.child(c).removeValue();

            realmVersion.setV(realmVersion.getV()+1);
            fireVer.child("version").setValue(realmVersion);
        }

    }

    public boolean updateRecord(){
        RealmResults<Products> results = realm.where(Products.class).equalTo("Code", code.getText().toString()).findAll();
        if(results.isEmpty()) {


            return false;
        }

        else {
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
            fireVer.child("version").setValue(realmVersion);

            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}