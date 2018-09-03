package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class BookDisplayActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    public static List<String> owners;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_display);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.BookDisplayToolbar);
        String title = getIntent().getStringExtra("name");
        //toolbar.setTitle(title);

        getSupportActionBar().setTitle(title); // for set actionbar title
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar


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
                        Intent intent = new Intent(BookDisplayActivity.this, UploadActivity.class);
                        //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());

                        //Make AutoFocus always True
                        startActivity(intent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
                        Log.d("aldsk", "myBooks intent");
                        Intent myBooksIntent = new Intent(BookDisplayActivity.this, myBooksActivity.class);
                        startActivity(myBooksIntent);
                        return true;
                    case R.id.navigation_messages:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent messageIntent = new Intent(BookDisplayActivity.this, MessagesActivity.class);
                        messageIntent.putExtra("login email", MainScreenActivity.mEmail);
                        startActivity(messageIntent);
                        return true;


                }
                return false;
            }
        });


        db = FirebaseFirestore.getInstance();
        CollectionReference books = db.collection("Books");
        books.whereEqualTo("title", title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    List<DocumentSnapshot> bookMatches = task.getResult().getDocuments();
                    if(bookMatches.size() > 0){
                        TextView bookInfo = (TextView) findViewById(R.id.bookDisplayInfo);
                        Map<String, Object> book = bookMatches.get(0).getData();
                        //TODO: add authors to text
                        String text = "Title: " + book.get("title").toString() +"\n" +
                                "ISBN: " + book.get("isbn").toString() + "\n\n" +
                                "Description: " + book.get("description") + "\n";
                        Log.d(TAG, book.get("authors").toString());
                        Log.d(TAG, "here");

//                            bookInfo.setText("Book added!" +
//                                    "\n" + "isbn:" + book.isbn +
//                                    "\n" + "title:" + book.title +
//                                    "\n" + "Author:" + book.authors.get(0) +
//                                    "\n" + "Details:" + book.message +
//                                    "\n" + "WebLink:" + book.webLink);
                        bookInfo.setText(text);
                        ListView listView = (ListView) findViewById(R.id.BookOwnersList);
                        String [] ownerList = new String [bookMatches.size()];
                        for(int i = 0; i < bookMatches.size(); i++){
                            //TODO: add zipcode after zipcode collected from users
                            String owner = bookMatches.get(i).getData().get("owner").toString();
                            ownerList[i] = owner;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_list_item_1, ownerList);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String username = ((TextView) view).getText().toString();
                                Intent messagingIntent = new Intent(BookDisplayActivity.this, MessagingActivity.class);
                                messagingIntent.putExtra("other person", username);
                                startActivity(messagingIntent);
                            }
                        });
                    }

                }
            }
        });

    }

}
