package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private UploadActivity.UserBookUploadTask mBookUploadTask = null;
    public Barcode barcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getSupportActionBar().setTitle("Scan and Upload"); // for set actionbar title
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_browse:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent browseIntent = new Intent(UploadActivity.this, BrowseActivity.class);
                        startActivity(browseIntent);
                        return true;
                    case R.id.navigation_upload:
                        //mTextMessage.setText(R.string.title_upload);
//                        Intent uploadIntent = new Intent(UploadActivity.this, BarcodeCaptureActivity.class);
//                        startActivity(uploadIntent);
                        return true;
                    case R.id.navigation_myBooks:
                        //mTextMessage.setText(R.string.title_myBooks);
                        Intent myBooksIntent = new Intent(UploadActivity.this, MyBooksActivity.class);
                        startActivity(myBooksIntent);
                        return true;
                    case R.id.navigation_messages:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent messageIntent = new Intent(UploadActivity.this, MessagesActivity.class);
                        startActivity(messageIntent);
                        return true;
                    case R.id.navigation_home:
                        //mTextMessage.setText(R.string.title_browse);
                        Intent dashBoardIntent = new Intent(UploadActivity.this, DashBoardActivity.class);
                        startActivity(dashBoardIntent);
                        return true;


                }
                return false;
            }
        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        for(int i = 0; i <= 3; i++) {
            if(i != 1) {
                menuItem = menu.getItem(i);
                menuItem.setChecked(false);
            }
        }



        Button scanBarcodeButton = (Button) findViewById(R.id.scanBarcodeButton);

        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                Log.d(TAG, "Scaning Barcode");
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

//        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        toolbar.setTitle("Upload");

        findViewById(R.id.navigation_upload).performClick();
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


                    mBookUploadTask = new UploadActivity.UserBookUploadTask();
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

    /**
     * Represents an asynchronous book upload task
     */
    public class UserBookUploadTask extends AsyncTask<Void, Void, Boolean> {

        //public FirebaseFirestore db;


        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //TextView bookInfo = (TextView) findViewById(R.id.bookInfo);

            //Fetch Record using Google Books API.

            Log.d(TAG, "Barcode read: " + barcode.displayValue);

            BookRecord book = BooksSample.queryBuilder("isbn",barcode.displayValue );


            if (book != null) {
                // Display Book details

                book.isbn = barcode.displayValue;

                String bookDetails = "Book added!" +
                        "\n" + "isbn:" + book.isbn +
                        "\n" + "title:" + book.title +
                        "\n" + "Author:" + book.authors.get(0) +
                        "\n" + "Details:" + book.message +
                        "\n" + "WebLink:" + book.webLink +
                        "\n" + "Owner:" + book.getOwner()
                        ;


                String thumbnail = book.thumbnail;
                //upload book to firestore

                BookDatabase.writeRecord(book);

                Drawable d = null;
                try {
                    URL url = new URL(thumbnail);
                    InputStream content = (InputStream) url.getContent();
                    d = Drawable.createFromStream(content, "src");
                    setText((TextView) findViewById(R.id.bookInfo), bookDetails, d, (ImageView) findViewById(R.id.bookImage));
                }
                catch(MalformedURLException e){

                }
                catch(IOException e){}



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
