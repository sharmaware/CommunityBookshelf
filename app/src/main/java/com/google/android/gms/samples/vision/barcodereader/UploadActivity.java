package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class UploadActivity extends Activity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

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
                        Intent myBooksIntent = new Intent(UploadActivity.this, myBooksActivity.class);
                        startActivity(myBooksIntent);
                        return true;
                }
                return false;
            }
        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        menuItem = menu.getItem(0);
        menuItem.setChecked(false);


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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView bookInfo = (TextView) findViewById(R.id.bookInfo);

        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d(TAG, Integer.toString(R.string.barcode_success));
                    Log.d(TAG, barcode.displayValue);

                    //Fetch Record using Google Books API.

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);

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


}
