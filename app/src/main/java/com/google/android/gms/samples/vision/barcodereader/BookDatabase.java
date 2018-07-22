package com.google.android.gms.samples.vision.barcodereader;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//import com.google.api.core.ApiFuture;
//import com.google.api.core.ApiFutures;


import static android.content.ContentValues.TAG;

/**
 * Created by saarthaksharma on 4/3/18.
 */

public class BookDatabase {

    final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String currentCategory;
    //public static Map<String, Object> bookDbEntry;

    public static void writeRecord(final BookRecord book) {


        // Create a book DB entry
        Map<String, Object> bookDbEntry = new HashMap<>();
        bookDbEntry.put("isbn", book.isbn);
        bookDbEntry.put("title", book.title);
        bookDbEntry.put("authors", book.authors);
        bookDbEntry.put("categories", book.categories);
        bookDbEntry.put("description", book.descr);
        bookDbEntry.put("owner", book.owner);
        bookDbEntry.put("status", book.status);

        // Add a new document with a generated ID to books collection
        db.collection("Books")
                .add(bookDbEntry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Book added to book collection with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        //Adding a new document to users collection
        db.collection("Users").document(book.owner).collection("books").add(book).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Book added to users collection with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document to users", e);
                    }
                });

        for (String category : book.categories) {

            //increase size parameter by reading, increasing, then writing
            currentCategory = category;
            Log.d(TAG, "Current Category: " + currentCategory);

            db.collection("Categories").document(category).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot categorySnapshot = task.getResult();

                        Map<String, Object> bookDbEntry = new HashMap<>();
                        bookDbEntry.put("isbn", book.isbn);
                        bookDbEntry.put("title", book.title);
                        bookDbEntry.put("authors", book.authors);
                        bookDbEntry.put("categories", book.categories);
                        bookDbEntry.put("description", book.descr);
                        bookDbEntry.put("owner", book.owner);
                        bookDbEntry.put("status", book.status);

                        if(categorySnapshot.exists()) {
                            Map<String, Object> categoryObject = categorySnapshot.getData();
                            long size = 0;
                            if (categoryObject.containsKey("size")) {
                                size = (Long) categoryObject.get("size");
                            }
                            //write
                            categoryObject.put("size", ++size);


                            db.collection("Categories").document(BookDatabase.currentCategory).set(categoryObject);
                            db.collection("Categories").document(BookDatabase.currentCategory).collection("Books").add(bookDbEntry);
                        }
                        else{
                            //create category document
                            Log.d(TAG, "Document snapshot does not exist");
                            Map<String, Object> categoryObject = new HashMap<>();
                            categoryObject.put("size", 1);
                            db.collection("Categories").document(BookDatabase.currentCategory).set(categoryObject);
                            db.collection("Categories").document(BookDatabase.currentCategory).collection("Books").add(bookDbEntry);

                        }
                    }
                    else{
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });


        }
    }

    public static void readRecord(){
        Log.d(TAG, "executing BookDatabase.readRecord()");
        db.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}
