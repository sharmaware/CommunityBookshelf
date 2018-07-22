package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

//import com.google.cloud.firestore.CollectionReference;
//import com.google.cloud.firestore.DocumentSnapshot;
//import com.google.cloud.firestore.Firestore;
//import com.google.cloud.firestore.Query;
//import com.google.cloud.firestore.Query.Direction;
//import com.google.cloud.firestore.QuerySnapshot;
//import com.google.cloud.firestore.WriteResult;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static android.content.ContentValues.TAG;

public class BookCategoryDisplayActivity extends Activity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static ListView listView;
    public static String [] documentArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_category_display);

        String category = getIntent().getStringExtra("category");
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.categoryToolbar);
        toolbar.setTitle(category);

        db.collection("Categories").document(category).collection("Books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "HERE 0");
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    if(documents.size() > 0) {
                        BookCategoryDisplayActivity.documentArray = new String[documents.size()];
                        for (int i = 0; i < documents.size(); i++) {
                            BookCategoryDisplayActivity.documentArray[i] = documents.get(i).getData().get("title").toString();

                        }
                        Log.d(TAG, "HERE 1");

                        if(BookCategoryDisplayActivity.documentArray == null){
                            Log.d(TAG, "DocumentArray is null 1");
                        }

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
        Log.d(TAG, "HERE 2");
        //listView = (ListView) findViewById(R.id.bookList);


//        while(documentArray == null ){
//            Log.d(TAG,"document array is null");
//        }



    }

}
