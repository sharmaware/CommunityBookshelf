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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyBooksActivity extends AppCompatActivity {
    private static FirebaseFirestore db;
    public static ListView listView;
    public static String loginEmail = "sample_user3@example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        getSupportActionBar().setTitle("My Books"); // for set actionbar title
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar

        MyApplication loginEmailObject = (MyApplication)getApplication();
        loginEmail = loginEmailObject.getUserEmail();

        Log.d(TAG, "loginEmail onCreate: " + loginEmail );

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent browseIntent = new Intent(MyBooksActivity.this, BrowseActivity.class);
                        startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        //mTextMessage.setText(R.string.title_upload);
                        Intent uploadIntent = new Intent(MyBooksActivity.this, UploadActivity.class);
                        startActivity(uploadIntent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
//                    Intent myBooksIntent = new Intent(getApplicationContext(), MyBooksActivity.class);
//                    startActivity(myBooksIntent);
                        return true;
                    case R.id.navigation_messages:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent messageIntent = new Intent(MyBooksActivity.this, MessagesActivity.class);
                        startActivity(messageIntent);
                        return true;
                    case R.id.navigation_home:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent dashBoardIntent = new Intent(MyBooksActivity.this, DashBoardActivity.class);
                        startActivity(dashBoardIntent);
                        return true;


                }
                return false;
            }
        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        for(int i = 0; i < 3; i++) {
            menuItem = menu.getItem(i);
            menuItem.setChecked(false);
        }


        db = FirebaseFirestore.getInstance();

        Log.d(TAG, "Fetching Book records of mEmail" + loginEmail);

        db.collection("Users").document(loginEmail).collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                Log.d(TAG, "onComplete setting adaptor for records:" + documents.size());

                String [] books = new String [documents.size()];
                for(int i = 0; i < documents.size(); i++){
                    books[i] = documents.get(i).getData().get("title").toString();
                }

                if (documents.size() > 0) {

                    Log.d(TAG, "setting adaptor for records:" + documents.size());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, books);


                    MyBooksActivity.listView = (ListView) findViewById(R.id.myBooksList);
                    MyBooksActivity.listView.setAdapter(adapter);

                }

                if(MyBooksActivity.listView != null) {
                    MyBooksActivity.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String title = ((TextView) view).getText().toString();

                            Log.d(TAG, "Start new activity for title:" + title);
                            Intent intent = new Intent(MyBooksActivity.this, MyBookDisplayActivity.class);
                            intent.putExtra("name", title);
                            startActivity(intent);
                        }
                    });
                }
                else {
                    Log.d(TAG, "ListView is null");
                }

            }
        });


    }
}
