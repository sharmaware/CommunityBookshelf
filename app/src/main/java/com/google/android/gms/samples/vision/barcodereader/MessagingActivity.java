package com.google.android.gms.samples.vision.barcodereader;

import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.google.firebase.firestore.Query.Direction.DESCENDING;


public class MessagingActivity extends Activity {
    public static FirebaseFirestore db;
    public static String USER = "sample_user2@example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        USER = getIntent().getStringExtra("other person");
        db = FirebaseFirestore.getInstance();


        //ChatMessage chatMessage = new ChatMessage("sample message", MainScreenActivity.mEmail).messageMap();
        Log.d(TAG, "messaging activity oncreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.messagingToolbar);
        toolbar.setTitle(USER);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageField = (EditText) findViewById(R.id.messageField);
                if(!messageField.getText().toString().equals("")) {
                    Map<String, Object> chatMap = new ChatMessage(messageField.getText().toString(), MainScreenActivity.mEmail).messageMap();
                    Log.d(TAG, "EMAIL" + MainScreenActivity.mEmail);


                    MessagingActivity.db.collection("Messages").document(MainScreenActivity.mEmail + "&" + USER).collection("messages")
                            .add(chatMap);
                    messageField.setText("");
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
                }
                else{
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
}




// String [] messages = new String [documentSnapshots.getDocuments().size()];
//                String  [] senders = new String [documentSnapshots.getDocuments().size()];
//                for(int i =  0; i < documentSnapshots.getDocuments().size(); i++){
//                    Map<String, Object> document = documentSnapshots.getDocuments().get(i).getData();
//                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                    //        android.R.layout.simple_list_item_1, myStringArray);
//                    messages[i] = document.get("text").toString();
//                    senders[i] = document.get(time)

//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_2,
//                        messages, user);
// }


