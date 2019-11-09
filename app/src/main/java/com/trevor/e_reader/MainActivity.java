package com.trevor.e_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Books books = new Books(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.deleteDatabase("Library");
    }

    // system is ready to create the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public static String EXTRA_BOOK_ID = "books";
    public static String EXTRA_BOOK_IDS_ID = "ids";
    // respond to a menu item click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // which item did they click?
        Intent intent;
        ArrayList<Book> listOfBooks;
        String[] bookArray;
        String[] bookIds;
        switch ( item.getItemId() ) {
            // view the library of downloaded books
            case R.id.view_library:
                intent = new Intent(MainActivity.this, LibraryActivity.class);
                listOfBooks = books.getBooks("", Books.DOWNLOADED_TABLE_NAME);
                bookArray = new String[listOfBooks.size()];
                bookIds = new String[listOfBooks.size()];
                for (int i = 0; i < bookArray.length; i++) {
                    bookArray[i] = listOfBooks.get(i).getTitle() + " - " + listOfBooks.get(i).getAuthor();
                    bookIds[i] = listOfBooks.get(i).getId();
                }
                intent.putExtra(EXTRA_BOOK_ID, bookArray);
                intent.putExtra(EXTRA_BOOK_IDS_ID, bookIds);
                startActivity(intent);
                return true;

            // go to activity to download books
            case R.id.download_books:
                intent = new Intent(MainActivity.this, DownloadBooksActivity.class);
                listOfBooks = books.getBooks("", Books.AVAILABLE_TABLE_NAME);
                bookArray = new String[listOfBooks.size()];
                bookIds = new String[listOfBooks.size()];
                for (int i = 0; i < bookArray.length; i++) {
                    bookArray[i] = listOfBooks.get(i).getTitle() + " - " + listOfBooks.get(i).getAuthor();
                    bookIds[i] = listOfBooks.get(i).getTitle();
                }
                intent.putExtra(EXTRA_BOOK_ID, bookArray);
                intent.putExtra(EXTRA_BOOK_IDS_ID, bookIds);
                startActivity(intent);
                return true;

            // go to settings activity
            case R.id.settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                // unknown item
                return false;
        }
    }
}
