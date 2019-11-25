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
    Books books = new Books(this);
    // example book titles
    String[] bookTitles;
    String[] bookIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        setTitle("Library");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // creating the adapter for the list of downloaded books
        getBookInfo();
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_book_list, bookTitles);

        ListView listView = (ListView) findViewById(R.id.list_books);

        listView.setAdapter(adapter);
        // if they select a book
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(LibraryActivity.this)
                        .setTitle("Library")
                        .setMessage("Would you like to read or delete " + bookTitles[i] + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Read", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(MainActivity.EXTRA_BOOK_ID, bookIds[i]);
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }})
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(LibraryActivity.this)
                                        .setTitle("Delete")
                                        .setMessage("Are you sure you would like to delete " + bookTitles[i] + "?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                books.deleteBook(bookIds[i]);
                                                finish();
                                                startActivity(getIntent());
                                                getBookInfo();
                                            }})
                                        .setNegativeButton(android.R.string.no, null).show();
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

    public void getBookInfo() {
        ArrayList<Book> listOfBooks = books.getBooks("", Books.DOWNLOADED_TABLE_NAME);
        bookTitles = new String[listOfBooks.size()];
        bookIds = new String[listOfBooks.size()];
        // pass in the book ids and the title and author
        for (int i = 0; i < bookTitles.length; i++) {
            bookTitles[i] = listOfBooks.get(i).getTitle() + " - " + listOfBooks.get(i).getAuthor();
            bookIds[i] = listOfBooks.get(i).getId();
        }
    }
    public String readBook(String id) {
        Book book = books.getBooks(id, Books.DOWNLOADED_TABLE_NAME).get(0);
        System.out.println(book.getTitle());
        System.out.println(book.getPath());
        String path = book.getPath();
        File file = new File(path);
        StringBuilder bookText = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                bookText.append(line);
                bookText.append('\n');
            }
        }
        catch (FileNotFoundException fe) { fe.printStackTrace(); }
        catch (IOException ie) { ie.printStackTrace(); }

        return bookText.toString();
    }
}
