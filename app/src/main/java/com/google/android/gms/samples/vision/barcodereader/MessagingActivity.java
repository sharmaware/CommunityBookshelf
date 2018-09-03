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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static com.google.firebase.firestore.Query.Direction.DESCENDING;


public class MessagingActivity extends AppCompatActivity {
    public static FirebaseFirestore db;
    public static String USER = "sample_user2@example.com";
    public static String messageRecord = "";
    final static SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        USER = getIntent().getStringExtra("other person");

        getSupportActionBar().setTitle(USER); // for set actionbar title
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //mTextMessage = (TextView) findViewById(R.id.message);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        //mTextMessage.setText(R.string.title_browse);
                    Intent browseIntent = new Intent(MessagingActivity.this, BrowseActivity.class);
                        browseIntent.putExtra("login email", MainScreenActivity.mEmail);
                    startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        Intent intent = new Intent(MessagingActivity.this, UploadActivity.class);
                        //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());

                        //Make AutoFocus always True
                        intent.putExtra("login email", MainScreenActivity.mEmail);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
                        Log.d("aldsk", "myBooks intent");
                        Intent myBooksIntent = new Intent(MessagingActivity.this, myBooksActivity.class);
                        myBooksIntent.putExtra("login email", MainScreenActivity.mEmail);
                        startActivity(myBooksIntent);
                        return true;
                    case R.id.navigation_messages:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent messageIntent = new Intent(MessagingActivity.this, MessagesActivity.class);
                        messageIntent.putExtra("login email", MainScreenActivity.mEmail);
                        startActivity(messageIntent);
                        return true;


                }

                return false;
            }
        });

        db = FirebaseFirestore.getInstance();


        //ChatMessage chatMessage = new ChatMessage("sample message", MainScreenActivity.mEmail).messageMap();
        Log.d(TAG, "messaging activity oncreate");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.messagingToolbar);
        //toolbar.setTitle(USER);
        if (MainScreenActivity.mEmail.compareTo(USER) > 0) {
            messageRecord = USER + "," + MainScreenActivity.mEmail;

        } else {
            messageRecord = MainScreenActivity.mEmail + "," + USER;
        }

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageField = (EditText) findViewById(R.id.messageField);
                if (!messageField.getText().toString().equals("")) {

                    Log.d(TAG, "EMAIL" + MainScreenActivity.mEmail);


                    Log.d(TAG, "messageRecord" + messageRecord);
                    Map<String, Object> chatMap = new ChatMessage(messageField.getText().toString(), MainScreenActivity.mEmail).messageMap();

                    Map<String, Object> userEmailHashMap = new HashMap<>();
                    userEmailHashMap.put("userEmailPair", messageRecord);

                    MessagingActivity.db.collection("Messages").document(messageRecord).set(userEmailHashMap);
                    MessagingActivity.db.collection("Messages").document(messageRecord).collection("messages").add(chatMap);
                    messageField.setText("");

                }

            //}
        //});

        //messageRecord = "extreme.saarthak@gmail.com,sample_user2@example.com";
        db.collection("Messages").document(messageRecord).collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "path exists");


                    db.collection("Messages").document(messageRecord).collection("messsages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            Log.d(TAG, "change detected");
                            Query query = db.collection("Messages").document(messageRecord).collection("messages").orderBy("time", Query.Direction.DESCENDING).limit(100);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "change detected and getting new info " + messageRecord + " size: " + task.getResult().getDocuments().size());

                                        String[] messages = new String[task.getResult().getDocuments().size()];
                                        String[] senders = new String[task.getResult().getDocuments().size()];
                                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                            Map<String, Object> document = task.getResult().getDocuments().get(i).getData();
                                            messages[i] = document.get("text") + "\n" + document.get("user").toString() + "   " + sfd.format(document.get("time"));

                                        }
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,
                                                messages);
                                        ListView messageList = (ListView) findViewById(R.id.messageList);
                                        messageList.setAdapter(adapter);
                                    }
                                }
                            });
                        }

                    });

                } else {
                    Log.d(TAG, "path does not exist");
                }
            }


        });


    }
});



    }
}

//        db.collection("Messages").document(messageRecord).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.getResult().exists()) {
//                    db.collection("Messages").document(messageRecord).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                            CollectionReference collectionReference = db.collection("Messages").document(messageRecord).collection("messages");
//                            collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    String[] messages = new String[documentSnapshots.getDocuments().size()];
//                                    String[] senders = new String[documentSnapshots.getDocuments().size()];
//                                    for (int i = 0; i < documentSnapshots.getDocuments().size(); i++) {
//                                        Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
//                                        messages[messages.length - i - 1] = document.get("text") + "\n" + document.get("user").toString() + "   " + document.get("time");
//                                        //senders[i] = document.get("time").toString();
//
//                                    }
////                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2,
////                                messages);
//
//                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
//                                            messages);
//                                    ListView messageList = (ListView) findViewById(R.id.messageList);
//                                    messageList.setAdapter(adapter);
//
//                                }
//                            });
//                        }
//                    });
//                } /*
//                else{
//                    db.collection("Messages").document(messageRecord).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                            CollectionReference collectionReference = db.collection("Messages").document(messageRecord).collection("messages");
//                            collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    String[] messages = new String[documentSnapshots.getDocuments().size()];
//                                    String[] senders = new String[documentSnapshots.getDocuments().size()];
//                                    for (int i = 0; i < documentSnapshots.getDocuments().size(); i++) {
//                                        Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
//                                        messages[messages.length - i - 1] = document.get("text") + "\n" + document.get("user").toString() + "   " + document.get("time");
//                                        //senders[i] = document.get("time").toString();
//
//                                    }
////                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2,
////                                messages);
//
//                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
//                                            messages);
//                                    ListView messageList = (ListView) findViewById(R.id.messageList);
//                                    messageList.setAdapter(adapter);
//
//                                }
//                            });
//                        }
//                    });
//                } */
//            }
//
//        });
//    }
//}






////        db.collection("Messages").document(messageRecord).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
////            @Override
////            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
////                CollectionReference collectionReference = db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).collection("messages");
////                collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
////                    @Override
////                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
////                        String[] messages = new String[documentSnapshots.getDocuments().size()];
////                        String[] senders = new String[documentSnapshots.getDocuments().size()];
////                        for (int i = 0; i < documentSnapshots.getDocuments().size(); i++) {
////                            Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
////
////                            messages[messages.length - i - 1] = document.get("user").toString() + "   " + sfd.format(document.get("time") + "\n" + document.get("text"));
////                            //senders[i] = document.get("time").toString();
////
////                        }
//////                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2,
//////                                messages);
////
////                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
////                                messages);
////                        ListView messageList = (ListView) findViewById(R.id.messageList);
////                        messageList.setAdapter(adapter);
////
////                    }
////                });
////            }
////        });
//
//
//
//        db.collection("Messages").document(messageRecord).collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//
//                    db.collection("Messages").document(messageRecord).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                            CollectionReference collectionReference = db.collection("Messages").document(messageRecord).collection("messages");
//                            collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    String[] messages = new String[documentSnapshots.getDocuments().size()];
//                                    String[] senders = new String[documentSnapshots.getDocuments().size()];
//                                    for (int i = 0; i < documentSnapshots.getDocuments().size(); i++) {
//                                        Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
//
//                                        messages[messages.length - i - 1] = document.get("user").toString() + "   " + sfd.format(document.get("time") + "\n" + document.get("text"));
//                                        //senders[i] = document.get("time").toString();
//
//                                    }
////                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2,
////                                messages);
//
//                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
//                                            messages);
//                                    ListView messageList = (ListView) findViewById(R.id.messageList);
//                                    messageList.setAdapter(adapter);
//
//                                }
//                            });
//                        }
//                    });
//                } else {
//                    Log.d(TAG, "No messageRecord found" + MainScreenActivity.mEmail);
//                }
//
//                /* {
//                    db.collection("Messages").document(USER + "&" + MainScreenActivity.mEmail).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                            CollectionReference collectionReference = db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).collection("messages");
//                            collectionReference.orderBy("time", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                                    String[] messages = new String[documentSnapshots.getDocuments().size()];
//                                    String[] senders = new String[documentSnapshots.getDocuments().size()];
//                                    for (int i = 0; i < documentSnapshots.getDocuments().size(); i++) {
//                                        Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
//                                        messages[messages.length - i - 1] = document.get("user").toString() + "   " + sfd.format(document.get("time") + "\n" + document.get("text"));
//                                        //senders[i] = document.get("time").toString();
//
//                                    }
////                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_2,
////                                messages);
//
//                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
//                                            messages);
//                                    ListView messageList = (ListView) findViewById(R.id.messageList);
//                                    messageList.setAdapter(adapter);
//
//                                }
//                            });
//                        }
//                    });
//                } */
//            }
//
//        });
//    }
//}
//
//
//
//
//// String [] messages = new String [documentSnapshots.getDocuments().size()];
////                String  [] senders = new String [documentSnapshots.getDocuments().size()];
////                for(int i =  0; i < documentSnapshots.getDocuments().size(); i++){
////                    Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
////                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
////                    //        android.R.layout.simple_list_item_1, myStringArray);
////                    messages[i] = document.get("text").toString();
////                    senders[i] = document.get(time)
//
////                }
////                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_2,
////                        messages, user);
//// }
//
//
