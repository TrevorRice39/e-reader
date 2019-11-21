package com.trevor.e_reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.opencsv.CSVReader;

public class Books extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Library";

    public static final String AVAILABLE_TABLE_NAME = "available"; // books that can be downloaded
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_URL = "url";
    public static final String KEY_IS_DOWNLOADED = "is_downloaded";


    public static final String DOWNLOADED_TABLE_NAME = "downloaded"; // downloaded books
    // downloaded table uses the keys above as well as these
    public static final String KEY_POSITION = "position";
    public static final String KEY_PATH = "file_path";
    public static final String KEY_DATE_LAST_READ = "date_read";

    Context context;
    // constructor
    public Books(Context c){
        super(c,DATABASE_NAME, null, DATABASE_VERSION);

        context = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + AVAILABLE_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT,"
                + KEY_AUTHOR + " TEXT,"
                + KEY_URL + " TEXT,"
                + KEY_IS_DOWNLOADED + " TEXT )");

        db.execSQL("CREATE TABLE " + DOWNLOADED_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_AUTHOR + " TEXT,"
                + KEY_URL + " TEXT,"
                + KEY_POSITION + " TEXT,"
                + KEY_PATH + " TEXT,"
                + KEY_DATE_LAST_READ + " TEXT)" );

        initializeAvailableBooks(db);
    }

    // get available books from resources
    public void initializeAvailableBooks(SQLiteDatabase db) {

        ArrayList<Book> books = readAvailableBooks();
        for (Book b : books) {
            addBook(b, AVAILABLE_TABLE_NAME, db);
        }
    }

    private ArrayList<Book> readAvailableBooks() {
        ArrayList<Book> books = new ArrayList<>();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.books);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                Book book = new Book();
                book.setTitle(columns[0]);
                book.setAuthor(columns[1]);
                book.setUrl(columns[2]);
                books.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }
    public void addBook(Book book, String TABLE_NAME, SQLiteDatabase db) { ;
        ContentValues values = new ContentValues();

        if (TABLE_NAME.equals(DOWNLOADED_TABLE_NAME)) {
            values.put(KEY_ID, book.getId());
            values.put(KEY_DATE_LAST_READ, book.getDateLastRead());
            values.put(KEY_POSITION, book.getPosition());
            values.put(KEY_PATH, book.getPath());
        }
        else {
            values.put(KEY_IS_DOWNLOADED, "false");
        }

        values.put(KEY_TITLE, book.getTitle());
        values.put(KEY_AUTHOR, book.getAuthor());
        values.put(KEY_URL, book.getUrl());
        db.insert(TABLE_NAME, null, values);
    }
    // user is upgrading to a new version of the app with
    // a higher DATABASE_VERSION
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // ideally should copy data over, but ignore for now
        db.execSQL("DROP TABLE IF EXISTS "+ DOWNLOADED_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ AVAILABLE_TABLE_NAME);
        onCreate(db);
    }

    // download a book

    // add a new transaction
//    public void newTransaction( long date, String category, double amount, String payee ) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_DATE, date);
//        values.put(KEY_CATEGORY, category);
//        values.put(KEY_AMOUNT, amount);
//        values.put(KEY_PAYEE, payee);
//        db.insert(TABLE_NAME, null, values);
//
//        db.close();
//    }
//
//    // edit a transaction
//    public void updateTransaction( int id, long date, String category, double amount, String payee ) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String strFilter =  KEY_ID + "=?";
//        ContentValues values = new ContentValues();
//        values.put(KEY_DATE, date);
//        values.put(KEY_CATEGORY, category);
//        values.put(KEY_AMOUNT, amount);
//        values.put(KEY_PAYEE, payee);
//
//        // record to update
//        String[] whereArgs = { ""+id };
//
//        // do the update
//        db.update(TABLE_NAME, values, strFilter, whereArgs );
//
//        db.close();
//    }

//    // delete a transaction
//    public void deleteTransaction( int id ) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String strFilter =  KEY_ID + "=?";
//
//        // record to delete
//        String[] whereArgs = { ""+id };
//
//        // delete the record
//        db.delete(TABLE_NAME, strFilter, whereArgs );
//
//        db.close();
//    }

    // get a list of transactions matching a filter
    public ArrayList<Book> getBooks(String filter, String TABLE_NAME ) {
        ArrayList<Book> books = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;

        // no filter?
        if (filter == null || filter.length() == 0) {
            // no filter
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            // --  try db.query instead? --
        } else {
            // convert our filter string into an "array" for the query params

            String[] params = {filter};
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                    " WHERE " + KEY_ID + " = ?", params);

        }

        // anything to display?
        if(cursor.moveToFirst()){
            do {
                String id = cursor.getString(cursor.getColumnIndex(KEY_ID));
                String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                String author = cursor.getString(cursor.getColumnIndex(KEY_AUTHOR));
                String url = cursor.getString(cursor.getColumnIndex(KEY_URL));
                String path = "";
                Date date = new Date();
                String position = "";
                if (TABLE_NAME.equals("downloaded")) {
                    //date = cursor.getString(cursor.getColumnIndex(KEY_DATE_LAST_READ));
                    position = cursor.getString(cursor.getColumnIndex(KEY_POSITION));
                    path = cursor.getString(cursor.getColumnIndex(KEY_PATH));
                }
                Book book = new Book();
                book.setId(id);
                book.setTitle(title);
                book.setAuthor(author);
                book.setUrl(url);
                book.setDate(date);
                book.setPosition(position);
                book.setPath(path);
                books.add(book);
            } while (cursor.moveToNext());

        }
        cursor.close();

        return books;
    }
}
