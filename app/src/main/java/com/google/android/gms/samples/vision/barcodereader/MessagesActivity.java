package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MessagesActivity extends AppCompatActivity {

    final static int MAX_USER_CONVERSATIONS = 1;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static ListView listView;
    public static String [] userEmailArray = new String[MAX_USER_CONVERSATIONS];

    public static String loginEmail = "sample_user3@example.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().setTitle("Messages"); // for set actionbar title
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar

        //loginEmail = getIntent().getStringExtra("login email");

        MyApplication loginEmailObject = (MyApplication)getApplication();
        loginEmail = loginEmailObject.getUserEmail();

        Log.d(TAG, "login Email in onCreate: " + loginEmail);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent browseIntent = new Intent(MessagesActivity.this, BrowseActivity.class);
                        browseIntent.putExtra("login email", loginEmail);
                        startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        //mTextMessage.setText(R.string.title_upload);
                        Intent uploadIntent = new Intent(MessagesActivity.this, BarcodeCaptureActivity.class);
                        uploadIntent.putExtra("login email", loginEmail);
                        startActivity(uploadIntent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
                        Intent myBooksIntent = new Intent(MessagesActivity.this, MyBooksActivity.class);
                        myBooksIntent.putExtra("login email", loginEmail);
                        startActivity(myBooksIntent);
                        return true;
                    case R.id.navigation_messages:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent messageIntent = new Intent(MessagesActivity.this, MessagesActivity.class);
                        messageIntent.putExtra("login email", loginEmail);
                        startActivity(messageIntent);
                        return true;
                    case R.id.navigation_home:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent dashBoardIntent = new Intent(MessagesActivity.this, DashBoardActivity.class);
                        startActivity(dashBoardIntent);
                        return true;


                }
                return false;
            }
        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        for (int i = 0; i <= 3; i++) {
            if (i != 2) {
                menuItem = menu.getItem(i);
                menuItem.setChecked(false);
            }
        }


        /*
         Compare emails combinations. If compareTo returns positive keep the order else reverse the order
         and create record
         */


        Log.d(TAG, "Reading userEmails: ");

        db.collection("Messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        Log.d(TAG, "userEmail task completed: ");
                        if (task.isSuccessful()) {
                            int userConversations = 0;

                            Log.d(TAG, "userEmail task " + task.getResult().size());
                            //for(DocumentSnapshot document: task.getResult()){
                            //    Log.d(TAG, "userEmail: " + document.getId());
                            //}


                            Log.d(TAG, "task successful: " + loginEmail);
                            //task.getResult().getDocuments();
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();

                            for (int i = 0; i < documents.size(); i++) {

                                if (documents.get(i).getId().contains(loginEmail)) {
                                    userEmailArray[userConversations] = documents.get(i).getId().toString();
                                    Log.d(TAG, "userEmails: " + userEmailArray[userConversations]);

                                    userConversations++;

                                    if (userConversations >= MAX_USER_CONVERSATIONS) {
                                        Log.d(TAG, "user conversations exceeded, handling only 64 ");
                                        break;
                                    }

                                }
                            }




                            if (userConversations > 0 ) {


                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, MessagesActivity.userEmailArray);
                                MessagesActivity.listView = (ListView) findViewById(R.id.userEmailList);
                                MessagesActivity.listView.setAdapter(adapter);


                                if (MessagesActivity.listView != null) {
                                    MessagesActivity.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String title = ((TextView) view).getText().toString();
                                            Intent intent = new Intent(MessagesActivity.this, MessagingConversationActivity.class);
                                            intent.putExtra("name", title);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }


                        } else{
                                Log.d(TAG, "Error getting documents: ", task.getException());
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

}
