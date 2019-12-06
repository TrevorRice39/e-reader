package com.trevor.e_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {
    Books books = new Books(this, MainActivity.DATABASE_NAME, R.raw.books);
    // downloaded book titles passed from main
    String[] bookTitles;

    // downloaded book ids passed from main
    String[] bookIds;

    // code for if a book was deleted while in library
    static final int RESULT_DELETE_CODE = 134718;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        setTitle("Library");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // creating the adapter for the list of downloaded books
        getBookInfo();

        // set the adapter to the titles of the books
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_book_list, bookTitles);

        ListView listView = (ListView) findViewById(R.id.list_books);

        listView.setAdapter(adapter);
        // if they select a book
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                // first would you like to read or delete this book?
                new AlertDialog.Builder(LibraryActivity.this)
                        .setTitle("Library")
                        .setMessage("Would you like to read or delete " + bookTitles[i] + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Read", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                // you'd like to read it

                                // make a toast
                                Toast.makeText(getApplicationContext(), bookTitles[i] + " selected.", Toast.LENGTH_LONG).show();
                                Intent returnIntent = new Intent();
                                // return the book id to main activity
                                returnIntent.putExtra(MainActivity.EXTRA_BOOK_ID, bookIds[i]);
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }})
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // you'd like to delete the book

                                // are you sure you would like to delete it? final chance
                                new AlertDialog.Builder(LibraryActivity.this)
                                        .setTitle("Delete")
                                        .setMessage("Are you sure you would like to delete " + bookTitles[i] + "?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // delete the book
                                                books.deleteBook(bookIds[i]);
                                                Intent returnIntent = new Intent();
                                                // set the result to delete code
                                                setResult(RESULT_DELETE_CODE,returnIntent);
                                                finish();
                                                startActivity(getIntent());
                                                // toast that book was deleted
                                                Toast.makeText(getApplicationContext(), bookTitles[i] + " deleted.", Toast.LENGTH_LONG).show();
                                                getBookInfo();
                                            }})
                                        .setNegativeButton("Cancel", null).show(); // didn't want to delete
                            }
                        }).show();
            }
        });
    }

    // respond to a menu item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // which item did they click?
        Intent intent;
        switch ( item.getItemId() ) {
            // return home
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    // get the book info and update bookTitles and bookIds
    public void getBookInfo() {
        // get the list of books downloaded
        ArrayList<Book> listOfBooks = books.getBooks("", Books.DOWNLOADED_TABLE_NAME);

        // update the size of bookTitles and bookIds
        bookTitles = new String[listOfBooks.size()];
        bookIds = new String[listOfBooks.size()];
        // pass in the book ids and the title and author
        for (int i = 0; i < bookTitles.length; i++) {
            bookTitles[i] = listOfBooks.get(i).getTitle() + " - " + listOfBooks.get(i).getAuthor();
            bookIds[i] = listOfBooks.get(i).getId();
        }
    }

}
