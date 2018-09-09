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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static android.content.ContentValues.TAG;

public class DashBoardActivity extends AppCompatActivity {

    private TextView mTextMessage;

//    private TextView statusMessage;
//    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static int size = 0;
    List<String> categories;
    public static String loginEmail = "sample_user3@example.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setTitle("Welcome to Community Bookshelf");

        MyApplication loginEmailObject = (MyApplication) getApplication();
        loginEmail = loginEmailObject.getUserEmail();

        Log.d(TAG, "Started dashBoard activity for user" + loginEmail);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "Started dashBoard activity for user 1" + loginEmail);
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        Log.d(TAG, "Started dashBoard activity for user 2" + loginEmail);
  //                      mTextMessage.setText(R.string.title_browse);
                    Intent browseIntent = new Intent(DashBoardActivity.this, BrowseActivity.class);
                    startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        Intent intent = new Intent(DashBoardActivity.this, UploadActivity.class);
                        //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());

                        //Make AutoFocus always True
                        startActivity(intent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
                        Log.d("aldsk", "myBooks intent");
                        Intent myBooksIntent = new Intent(DashBoardActivity.this, MyBooksActivity.class);
                        startActivity(myBooksIntent);
                        return true;

                    case R.id.navigation_messages:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent messageIntent = new Intent(DashBoardActivity.this, MessagesActivity.class);
                        messageIntent.putExtra("login email", loginEmail);
                        startActivity(messageIntent);
                        return true;


                }
                return false;
            }
        });




        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        for(int i = 0; i < 3; i++) {
            if(i != 0) {
                menuItem = menu.getItem(i);
                menuItem.setChecked(false);
            }
        }


        setContentView(R.layout.activity_dashboard);
//        TextView tView = (TextView) findViewById(R.id.totalBooksAndTraded);
        //tView.setText("hi papa");
//        tView.setEnabled(false);


        db.collection("Books").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   Log.d(TAG, "Fetching all books");

//                                                   TextView tView = (TextView) findViewById(R.id.totalBooksAndTraded);
//                                                   tView.setText("Number of books available in bookshelf " + "\n" +task.getResult().size() );
//                                                   tView.setEnabled(false);
                                               }
                                           }
                                       });

/*

        View toolbar1 = findViewById(R.id.totalBooksAndTraded);


        ((TextView) toolbar1.setText("Custom1");


        setContentView(R.layout.activity_dashboard);
        View linearLayout =  findViewById(R.id.totalBooksAndTraded);
        //LinearLayout layout = (LinearLayout) findViewById(R.id.info);

        TextView valueTV = new TextView(this);
        valueTV.setText("Books in Community Bookshelf");
        valueTV.setId(5);
        valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

        ((LinearLayout) linearLayout).addView(valueTV);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mylayout);
        TextView txt1 = new TextView(MyClass.this);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.addView(txt1);
*/

    }
}





