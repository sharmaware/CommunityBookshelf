package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyBookDisplayActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    public static String title = "math";
    public static String loginEmail = "sample_user3@example.com";
    private MyBookDisplayActivity.UserBookDisplayTask mBookDisplayTask = null;
    public static Map<String, Object> book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_display);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.BookDisplayToolbar);
        title = getIntent().getStringExtra("name");
        //toolbar.setTitle(title);

        getSupportActionBar().setTitle(title); // for set actionbar title
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar

        MyApplication loginEmailObject = (MyApplication) getApplication();
        loginEmail = loginEmailObject.getUserEmail();

        Log.d(TAG, "Started my book display activity for user, title:" + loginEmail + title);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //mTextMessage = (TextView) findViewById(R.id.message);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent browseIntent = new Intent(MyBookDisplayActivity.this, BrowseActivity.class);
                        startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        Intent intent = new Intent(MyBookDisplayActivity.this, UploadActivity.class);
                        //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());

                        //Make AutoFocus always True
                        startActivity(intent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
                        Log.d("aldsk", "myBooks intent");
                        Intent myBooksIntent = new Intent(MyBookDisplayActivity.this, MyBooksActivity.class);
                        startActivity(myBooksIntent);
                        return true;
                    case R.id.navigation_messages:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent messageIntent = new Intent(MyBookDisplayActivity.this, MessagesActivity.class);
                        messageIntent.putExtra("login email", loginEmail);
                        startActivity(messageIntent);
                        return true;


                }
                return false;
            }
        });


        //} onCreate ends


        db = FirebaseFirestore.getInstance();
        CollectionReference books = db.collection("Books");
        books.whereEqualTo("title", title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> bookMatches = task.getResult().getDocuments();
                    if (bookMatches.size() > 0) {
                        //TextView bookInfo = (TextView) findViewById(R.id.bookDisplayInfo);
                        book = bookMatches.get(0).getData();
                        //TODO: add authors to text
                        String text = "Title: " + book.get("title").toString() + "\n" +
                                "ISBN: " + book.get("isbn").toString() + "\n\n" +
                                "Description: " + book.get("description") + "\n";
                        Log.d(TAG, "book detail" + text);
                        Log.d(TAG, book.get("authors").toString());


//                            bookInfo.setText("Book added!" +
//                                    "\n" + "isbn:" + book.isbn +
//                                    "\n" + "title:" + book.title +
//                                    "\n" + "Author:" + book.authors.get(0) +
//                                    "\n" + "Details:" + book.message +
//                                    "\n" + "WebLink:" + book.webLink);
                        //bookInfo.setText(text);


                        mBookDisplayTask = new MyBookDisplayActivity.UserBookDisplayTask();
                        mBookDisplayTask.execute((Void) null);

                    }

                }
            }
        });

    }

    private void setText(final TextView text,final String value, final Drawable thumbnail, final ImageView iv){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
                iv.setImageDrawable(thumbnail);
            }});
    }

    /**
     * Represents an asynchronous book display task
     */
    public class UserBookDisplayTask extends AsyncTask<Void, Void, Boolean> {

        //public FirebaseFirestore db;


        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //TextView bookInfo = (TextView) findViewById(R.id.bookInfo);


            Log.d(TAG, "title read in async task: " + title);

            if (book != null) {
                // Display Book details

                Log.d(TAG, "Book displayed in async task: " + book.get("title").toString());

                String bookDetails = "Title: " + book.get("title").toString() +"\n" +
                        "ISBN: " + book.get("isbn").toString() + "\n\n" +
                        "Authors: " + book.get("authors").toString() + "\n\n" +
                        "Description: " + book.get("description") + "\n";


                String thumbnail = book.get("thumbnail").toString();

                //BookDatabase.writeRecord(book);
/*
                Drawable d = null;
                try {
                    URL url = new URL(thumbnail);
                    InputStream content = (InputStream) url.getContent();
                    d = Drawable.createFromStream(content, "src");
                    setText((TextView) findViewById(R.id.myBookDisplayInfo), bookDetails, d, (ImageView) findViewById(R.id.myBookImage));
                }
                catch(MalformedURLException e){

                }
                catch(IOException e){}
*/
                //ImageView iv =  (ImageView) findViewById(R.id.bookImage);

            }


            try {
                // Simulate network access.
                Thread.sleep(1);


            } catch (InterruptedException e) {
                return false;
            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    Log.d(TAG, mEmail);
//                    Log.d(TAG, mPassword);
//                    //return pieces[1].equals(mPassword);
//                }
//            }

            // TODO: register the new account here.
//            Log.d(TAG, "success" + success);
//            while(!success){
//                try{Thread.sleep(1000);}
//                catch (InterruptedException e) {
//                    return false;
//                }
//                Log.d(TAG, "SUCCESS = " + success);
//            }
            return true;
        }

    }
}
