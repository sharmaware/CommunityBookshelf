package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MessagesActivity extends Activity {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static ListView listView;
    public static String [] documentArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent browseIntent = new Intent(MessagesActivity.this, BrowseActivity.class);
                        startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        //mTextMessage.setText(R.string.title_upload);
                        Intent uploadIntent = new Intent(UploadActivity.this, BarcodeCaptureActivity.class);
                        startActivity(uploadIntent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
                        Intent myBooksIntent = new Intent(MessagesActivity.this, myBooksActivity.class);
                        startActivity(myBooksIntent);
                        return true;
                    case R.id.navigation_messages:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent messageIntent = new Intent(MessagesActivity.this, MessagingActivity.class);
                        startActivity(messageIntent);
                        return true;
                }
                return false;
            }
        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        for (int i = 0; i <= 3; i++) {
            if (i != 3) {
                menuItem = menu.getItem(i);
                menuItem.setChecked(false);
            }
        }


        /*
         Compare emails combinations. If compareTo returns positive keep the order else reverse the order
         and create record
         */


        db.collection("Messages").document().collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                Log.d(TAG, "HERE 0");
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                if (documents.size() > 0) {
                    Log.d(TAG, "Fetching user combinations for messages");
                    MessagesActivity.documentArray = new String[documents.size()];
                    for (int i = 0; i < documents.size(); i++) {
                        MessagesActivity.documentArray[i] = documents.get(i).getData().toString();
                        if (MessagesActivity.documentArray[i].contains(MainScreenActivity.mEmail)) {
                            Log.d(TAG, "Found user in the Messages record user" + i);

                        }

                    }
                }
            }
        });



        db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        CollectionReference collectionReference = db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).collection("messages");
                                        collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                                String[] messages = new String[documentSnapshots.getDocuments().size()];
                                                String[] senders = new String[documentSnapshots.getDocuments().size()];
                                                for (int i = 0; i < documentSnapshots.getDocuments().size(); i++) {
                                                    Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
                                                    messages[messages.length - i - 1] = document.get("text") + "\n" + document.get("user").toString() + "   " + document.get("time");
                                                    //senders[i] = document.get("time").toString();

                                                }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2,
//                                messages);

                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
                                                        messages);
                                                ListView messageList = (ListView) findViewById(R.id.messageList);
                                                messageList.setAdapter(adapter);

                                            }
                                        });
                                    }
                                });
















                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, BookCategoryDisplayActivity.documentArray);
                    BookCategoryDisplayActivity.listView = (ListView) findViewById(R.id.bookCategoryList);
                    BookCategoryDisplayActivity.listView.setAdapter(adapter);


                    if(BookCategoryDisplayActivity.listView != null) {
                        BookCategoryDisplayActivity.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String title = ((TextView) view).getText().toString();
                                Intent intent = new Intent(BookCategoryDisplayActivity.this, BookDisplayActivity.class);
                                intent.putExtra("name", title);
                                startActivity(intent);
                            }
                        });
                    }
                    else{
                        Log.d(TAG, "ListView is null");
                    }
//                        }
                }
                else{
                    Log.d(TAG, "documents.size() = 0");
                }
            }
            else{
                Log.d(TAG, "task is unsuccessful");
            }
        }
    });



















        db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            CollectionReference collectionReference = db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).collection("messages");
                            collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                    String[] messages = new String[documentSnapshots.getDocuments().size()];
                                    String[] senders = new String[documentSnapshots.getDocuments().size()];
                                    for (int i = 0; i < documentSnapshots.getDocuments().size(); i++) {
                                        Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
                                        messages[messages.length - i - 1] = document.get("text") + "\n" + document.get("user").toString() + "   " + document.get("time");
                                        //senders[i] = document.get("time").toString();

                                    }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2,
//                                messages);

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
                                            messages);
                                    ListView messageList = (ListView) findViewById(R.id.messageList);
                                    messageList.setAdapter(adapter);

                                }
                            });
                        }
                    });
                } else {
                    db.collection("Messages").document(USER + "&" + MainScreenActivity.mEmail).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            CollectionReference collectionReference = db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).collection("messages");
                            collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                    String[] messages = new String[documentSnapshots.getDocuments().size()];
                                    String[] senders = new String[documentSnapshots.getDocuments().size()];
                                    for (int i = 0; i < documentSnapshots.getDocuments().size(); i++) {
                                        Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
                                        messages[messages.length - i - 1] = document.get("text") + "\n" + document.get("user").toString() + "   " + document.get("time");
                                        //senders[i] = document.get("time").toString();

                                    }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2,
//                                messages);

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
                                            messages);
                                    ListView messageList = (ListView) findViewById(R.id.messageList);
                                    messageList.setAdapter(adapter);

                                }
                            });
                        }
                    });
                }
            }

        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TextView bookInfo = (TextView) findViewById(R.id.bookInfo);

        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d(TAG, Integer.toString(R.string.barcode_success));
                    Log.d(TAG, barcode.displayValue);

                    //Fetch Record using Google Books API.

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);


                    mBookUploadTask = new MessagesActivity.UserBookUploadTask();
                    mBookUploadTask.execute((Void) null);

                    /*
                    BookRecord book = BooksSample.queryBuilder("isbn",barcode.displayValue );


                    if (book != null) {
                        // Display Book details

                        book.isbn = barcode.displayValue;
                        bookInfo.setText("Book added!" +
                                "\n" + "isbn:" + book.isbn +
                                "\n" + "title:" + book.title +
                                "\n" + "Author:" + book.authors.get(0) +
                                "\n" + "Details:" + book.message +
                                "\n" + "WebLink:" + book.webLink);
                        //upload book to firestore
                        BookDatabase.writeRecord(book);
                    }
                    */


                } else {
                    Log.d(TAG,"ERROR");
                    Log.d(TAG, Integer.toString(R.string.barcode_failure));
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Log.d(TAG,"ERROR");
                Log.d(TAG, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setText(final TextView text,final String value, final Drawable thumbnail, final ImageView iv){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
                iv.setImageDrawable(thumbnail);
        }});
    }

}
