package com.google.android.gms.samples.vision.barcodereader;

/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

//package com.google.api.services.samples.books.cmdline;

        import android.util.Log;

        import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
        import com.google.api.client.http.LowLevelHttpRequest;
        import com.google.api.client.json.JsonFactory;
        import com.google.api.client.json.jackson2.JacksonFactory;
        import com.google.api.services.books.Books;
        import com.google.api.services.books.BooksRequestInitializer;
        import com.google.api.services.books.Books.Volumes.List;
        import com.google.api.services.books.model.Volume;
        import com.google.api.services.books.model.Volumes;
        //import com.google.api.client.extensions.android.http.AndroidHttp;

        import com.google.api.client.http.HttpTransport;
        import com.google.api.client.http.javanet.NetHttpTransport;

        import java.io.IOException;
        import java.net.URLEncoder;
        import java.text.NumberFormat;

        import static android.content.ContentValues.TAG;


/**
 * A sample application that demonstrates how Google Books Client Library for
 * Java can be used to query Google Books. It accepts queries in the command
 * line, and prints the results to the console.
 *
 * $ java com.google.sample.books.BooksSample [--author|--isbn|--title] "<query>"
 *
 * Please start by reviewing the Google Books API documentation at:
 * http://code.google.com/apis/books/docs/getting_started.html
 */



public class BooksSample {

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "My Project 60201";

    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();

    private static BookRecord queryGoogleBooks(JsonFactory jsonFactory, String query) throws Exception {
        ClientCredentials.errorIfNotSpecified();

        //final Books books = new Books.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory.getDefaultInstance(), null)
        //        .setApplicationName(BuildConfig.APPLICATION_ID)
        //        .setGoogleClientRequestInitializer(new BooksRequestInitializer(ClientCredentials.API_KEY))
        //        .build();


        // Set up Books client.
        final Books books = new Books.Builder(new NetHttpTransport(), jsonFactory, null)
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(ClientCredentials.API_KEY))
                .build();
        // Set query string and filter only Google eBooks.
        //System.out.println("Query: [" + query + "]");
        Log.d(TAG, "Query: [" + query + "]");
        List volumesList = books.volumes().list(query);
        //volumesList.setFilter("ebooks");

        // Execute the query.
        Volumes volumes = volumesList.execute();
        if (volumes.getTotalItems() == 0 || volumes.getItems() == null) {
            //System.out.println("No matches found.");
            Log.d(TAG,"No matches found.");
            return null;
        }

        // Create bookRecord

        BookRecord book = new BookRecord();

        // Output results.
        for (Volume volume : volumes.getItems()) {
            Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
            Volume.SaleInfo saleInfo = volume.getSaleInfo();
            //System.out.println("==========");
            Log.d(TAG, "==========");

            // Title.
            //System.out.println("Title: " + volumeInfo.getTitle());
            Log.d(TAG, "Title: " + volumeInfo.getTitle());
            book.title = volumeInfo.getTitle();
            book.categories = volumeInfo.getCategories();

            Volume.VolumeInfo.ImageLinks links = volumeInfo.getImageLinks();
            if (links != null) {
                 book.thumbnail = links.getSmallThumbnail();
            } else {
                book.thumbnail = null;
            }
            Log.d(TAG, "URL(s): " + book.thumbnail);

            // Author(s).
            java.util.List<String> authors = volumeInfo.getAuthors();
            if (authors != null && !authors.isEmpty()) {
                //System.out.print("Author(s): ");
                Log.d(TAG, "Author(s): ");
                for (int i = 0; i < authors.size(); ++i) {
                    //System.out.print(authors.get(i));
                    Log.d(TAG, authors.get(i));
                    if (i < authors.size() - 1) {
                        //System.out.print(", ");
                        Log.d(TAG,", ");
                    }
                }
                System.out.println();
                Log.d(TAG,"");
            }
            book.authors = authors;
            // Description (if any).
            if (volumeInfo.getDescription() != null && volumeInfo.getDescription().length() > 0) {
                //System.out.println("Description: " + volumeInfo.getDescription());
                Log.d(TAG, "Description: " + volumeInfo.getDescription());
                book.descr = volumeInfo.getDescription();
            }
            // Ratings (if any).
            if (volumeInfo.getRatingsCount() != null && volumeInfo.getRatingsCount() > 0) {
                int fullRating = (int) Math.round(volumeInfo.getAverageRating().doubleValue());
                //System.out.print("User Rating: ");
                Log.d(TAG, "User Rating: ");
                for (int i = 0; i < fullRating; ++i) {
                    //System.out.print("*");
                    Log.d(TAG, "*");
                }
                //System.out.println(" (" + volumeInfo.getRatingsCount() + " rating(s))");
                Log.d(TAG, " (" + volumeInfo.getRatingsCount() + " rating(s))");
            }

            /*
            // Price (if any).
            if (saleInfo != null && "FOR_SALE".equals(saleInfo.getSaleability())) {
                double save = saleInfo.getListPrice().getAmount() - saleInfo.getRetailPrice().getAmount();
                if (save > 0.0) {
                    System.out.print("List: " + CURRENCY_FORMATTER.format(saleInfo.getListPrice().getAmount())
                            + "  ");
                }
                System.out.print("Google eBooks Price: "
                        + CURRENCY_FORMATTER.format(saleInfo.getRetailPrice().getAmount()));
                if (save > 0.0) {
                    System.out.print("  You Save: " + CURRENCY_FORMATTER.format(save) + " ("
                            + PERCENT_FORMATTER.format(save / saleInfo.getListPrice().getAmount()) + ")");
                }
                System.out.println();
            }
            */

            // Access status.
            String accessViewStatus = volume.getAccessInfo().getAccessViewStatus();
            String message = "Additional information about this book is available from Google eBooks at:";
            if ("FULL_PUBLIC_DOMAIN".equals(accessViewStatus)) {
                message = "This public domain book is available for free from Google eBooks at:";
            } else if ("SAMPLE".equals(accessViewStatus)) {
                message = "A preview of this book is available from Google eBooks at:";
            }
            //System.out.println(message);
            Log.d(TAG, message);
            book.message = message;
            // Link to Google eBooks.
            //System.out.println(volumeInfo.getInfoLink());
            Log.d(TAG, volumeInfo.getInfoLink());
            book.webLink = volumeInfo.getInfoLink();
            book.owner = MainScreenActivity.mEmail;
            book.status = "available";
        }
        //System.out.println("==========");
        Log.d(TAG, "==========");
        //System.out.println(
        //        volumes.getTotalItems() + " total results at http://books.google.com/ebooks?q="
        //                + URLEncoder.encode(query, "UTF-8"));
        Log.d(TAG, volumes.getTotalItems() + " total results at http://books.google.com/ebooks?q="
                               + URLEncoder.encode(query, "UTF-8"));
        return book;
    }

    public static BookRecord queryBuilder(String queryType, String queryData) {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        try {
            // Verify command line parameters.
            /*
            if (args.length == 0) {
                System.err.println("Usage: BooksSample [--author|--isbn|--title] \"<query>\"");
                System.exit(1);
            }*/
            // Parse command line parameters into a query.
            // Query format: "[<author|isbn|intitle>:]<query>"
            String prefix = null;
            String query = "";
            //for (String arg : args) {
                if ("author".equals(queryType)) {
                    prefix = "inauthor:";
                } else if ("isbn".equals(queryType)) {
                    prefix = "isbn:";
                } else if ("title".equals(queryType)) {
                    prefix = "intitle:";
                } else {
                    Log.d(TAG, "Unknown argument: " + queryType);
                    //System.exit(1);
                } /*else {
                    query = arg;
                } */
            //}
            if (prefix != null) {
                query = prefix + queryData;
            }
            try {
                Log.d(TAG, "starting query");
                return queryGoogleBooks(jsonFactory, query);
                //Log.d(TAG, "query done");
                // Success!
                //return;
            } catch (IOException e) {
                System.err.println(e.getMessage()); //here
            }
        } catch (Throwable t) {
            t.printStackTrace(); //here

        }
        //System.exit(0);
        return null;
    }

    /*
    public static void main(String[] args) {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        try {
            // Verify command line parameters.
            if (args.length == 0) {
                System.err.println("Usage: BooksSample [--author|--isbn|--title] \"<query>\"");
                System.exit(1);
            }
            // Parse command line parameters into a query.
            // Query format: "[<author|isbn|intitle>:]<query>"
            String prefix = null;
            String query = "";
            for (String arg : args) {
                if ("--author".equals(arg)) {
                    prefix = "inauthor:";
                } else if ("--isbn".equals(arg)) {
                    prefix = "isbn:";
                } else if ("--title".equals(arg)) {
                    prefix = "intitle:";
                } else if (arg.startsWith("--")) {
                    System.err.println("Unknown argument: " + arg);
                    System.exit(1);
                } else {
                    query = arg;
                }
            }
            if (prefix != null) {
                query = prefix + query;
            }
            try {
                queryGoogleBooks(jsonFactory, query);
                // Success!
                return;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(0);
    }
    */
}
