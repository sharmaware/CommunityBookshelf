package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BrowseActivity extends AppCompatActivity {

    private TextView mTextMessage;

//    private TextView statusMessage;
//    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static int size = 0;
    List<String> categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browse);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //mTextMessage = (TextView) findViewById(R.id.message);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        //mTextMessage.setText(R.string.title_browse);
//                    Intent browseIntent = new Intent(BrowseActivity.this, BrowseActivity.class);
//                    startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        Intent intent = new Intent(BrowseActivity.this, UploadActivity.class);
                        //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());

                        //Make AutoFocus always True
                        startActivity(intent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
                        Log.d("aldsk", "myBooks intent");
                        Intent myBooksIntent = new Intent(BrowseActivity.this, myBooksActivity.class);
                        startActivity(myBooksIntent);
                        return true;
                }
                return false;
            }
        });


        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);


        categories = new ArrayList<>();

        db.collection("Categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Beginning of db oncomplete");
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                categories.add(document.getId());
                            }

                            String[] categoriesArray = new String [task.getResult().size()];
                            for(int i = 0; i < categories.size(); i++){
                                categoriesArray[i] = categories.get(i);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_list_item_1, categoriesArray);
                            ListView listView = (ListView) findViewById(R.id.bookList);

                            listView.setAdapter(adapter);
//
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String category = (String)((TextView)view).getText();
                                    Log.d(TAG, category);

                                    //intent for activity to display book details
                                    Intent intent = new Intent(BrowseActivity.this, BookCategoryDisplayActivity.class);
                                    intent.putExtra("category", category);
                                    startActivity(intent);
                                }
                            });

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

//        String[] categoriesArray = new String [size];
//        for(int i = 0; i < categories.size(); i++){
//            categoriesArray[i] = categories.get(i);
//        }
//
//        for(String category: categoriesArray) {
//            Log.d(TAG, "Category: " + category);
//        }
//
////        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
////                android.R.layout.simple_list_item_1, categoriesArray);
////        ListView listView = (ListView) findViewById(R.id.bookList);
//        for(String category: categoriesArray) {
//            Log.d(TAG, "Category: " + category);
//        }
////        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String category = view.toString();
//                Log.d(TAG, category);
//
//                //intent for activity to display book details
//            }
//        });

    Log.d(TAG, "End of BrowseActivity");

    }
}





