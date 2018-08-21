package com.google.android.gms.samples.vision.barcodereader;

import java.util.List;

/**
 * Created by saarthaksharma on 4/3/18.
 */

public class BookRecord {
    public String isbn;
    public String title;
    public List<String> authors;
    public List<String> categories;
    public String descr;
    public String message;
    public String webLink;
    public String owner;
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getDescr() {
        return descr;
    }

    public String getMessage() {
        return message;
    }

    public String getWebLink() {
        return webLink;
    }

    public String getOwner() {
        return owner;
    }

    public String getStatus() {
        return status;
    }

    public String status;

}
