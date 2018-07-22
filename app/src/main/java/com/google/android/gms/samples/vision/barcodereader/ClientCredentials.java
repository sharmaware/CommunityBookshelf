package com.google.android.gms.samples.vision.barcodereader;

//package com.google.api.services.samples.books.cmdline;


/**
 * API key found in the <a href="https://code.google.com/apis/console?api=books">Google apis
 * console</a>.
 *
 */
public class ClientCredentials {

    /** Value of the "API key" shown under "Simple API Access". */
    static final String API_KEY =
            "AIzaSyAEkQlfuwEAirqESobBbmp--WUU2-VXn1w";

    static void errorIfNotSpecified() {
        if (API_KEY.startsWith("Enter ")) {
            System.err.println(API_KEY);
            System.exit(1);
        }
    }
}

