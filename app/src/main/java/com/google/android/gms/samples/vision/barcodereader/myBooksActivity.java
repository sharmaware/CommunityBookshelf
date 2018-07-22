package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class myBooksActivity extends Activity {
    private static FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent browseIntent = new Intent(myBooksActivity.this, BrowseActivity.class);
                        startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        //mTextMessage.setText(R.string.title_upload);
                        Intent uploadIntent = new Intent(myBooksActivity.this, UploadActivity.class);
                        startActivity(uploadIntent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
//                    Intent myBooksIntent = new Intent(getApplicationContext(), myBooksActivity.class);
//                    startActivity(myBooksIntent);
                        return true;
                }
                return false;
            }
        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        menuItem = menu.getItem(0);
        menuItem.setChecked(false);

        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(LoginActivity.mEmail).collection("Books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                String [] books = new String [documents.size()];
                for(int i = 0; i < documents.size(); i++){
                    books[i] = documents.get(i).getData().get("title").toString();
                }
                ListView listView = (ListView) findViewById(R.id.myBooksList);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, books);
                listView.setAdapter(adapter);
            }
        });
    }


}
