package com.trevor.e_reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String DATABASE_NAME = "Library";
    public Books books = new Books(this, DATABASE_NAME, R.raw.books);

    // current position in the book
    int position = 0;

    // number of characters in each page
    int pageSize = 1000;

    // number of total pages in the book, initialized to 0
    int numPages = 0;

    // current book text, initialized to empty string
    String currentBook = "";

    // id of the current book, initialized to empty string
    String currentBookID = "";

    // user decided font size
    float font_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // used to debug the database
        Stetho.initializeWithDefaults(this);

        // getting the last read book
        Book book = books.getLastReadBook();

        // if there was a book in the db
        if (book != null) {
            // get the text
            currentBook = readBook(book.getId());
            // update the number of pages
            numPages = currentBook.length()/pageSize;
            // update the book (date last read has changed to now)
            updateBook(currentBookID);
        }
        // update the text on the screen
        updateBookText();

        // buttons to control the pages
        // back one page button
        Button back = findViewById(R.id.btn_back);
        // next page button
        Button next = findViewById(R.id.btn_next);
        // go to page specified by page number
        Button go_to = findViewById(R.id.btn_page_search);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBookID.length() != 0) {
                    // if the position is greater than the first page
                    if (position >= pageSize) {
                        // go back a page
                        position -= pageSize;
                    }
                    // update the screen
                    updateBookText();
                    // update the page the user is on in the db
                    updateBook(currentBookID);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBookID.length() != 0) {
                    // if we're not on the last page
                    if (position <= currentBook.length() - 1001) {
                        // go to the next page
                        position += pageSize;
                    }
                    // update the screen
                    updateBookText();
                    // update the page the user is on in the db
                    updateBook(currentBookID);
                }
            }
        });

        go_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentBookID.length() != 0) {
                    // page number the user enters
                    TextView pageNum = findViewById(R.id.et_page);

                    int newPage;
                    try {
                        // attempt to parse the page number
                        newPage = Integer.parseInt(pageNum.getText().toString());
                        // update the position
                        position = newPage * pageSize;
                    } catch (Exception e) {
                        // invalid page number
                        Toast.makeText(getApplicationContext(), "Invalid page number", Toast.LENGTH_LONG).show();
                    }
                    // update the book on the screen
                    updateBookText();
                }
            }
        });
    }

    // function to update the page number
    private void updatePageNum() {
        // find the page number edit text
        TextView pageNum = findViewById(R.id.et_page);
        // set it to the position/pagesize
        pageNum.setText("" + position/pageSize);
    }
    // system is ready to create the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    // keys for our preferences
    static String font_key = "pref_text_size";

    @Override
    public void onResume() {
        super.onResume();

        // get the font size preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.font_size = Float.parseFloat(preferences.getString(font_key, "12.0"));

        updateBookText();
    }

    // IDs to pass data to other activity
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
        switch (item.getItemId()) {
            // view the library of downloaded books
            case R.id.view_library:
                intent = new Intent(MainActivity.this, LibraryActivity.class);
                listOfBooks = books.getBooks("", Books.DOWNLOADED_TABLE_NAME);
                bookArray = new String[listOfBooks.size()];
                bookIds = new String[listOfBooks.size()];
                // pass in the book ids and the title and author
                for (int i = 0; i < bookArray.length; i++) {
                    bookArray[i] = listOfBooks.get(i).getTitle() + " - " + listOfBooks.get(i).getAuthor();
                    bookIds[i] = listOfBooks.get(i).getId();
                }
                intent.putExtra(EXTRA_BOOK_ID, bookArray);
                intent.putExtra(EXTRA_BOOK_IDS_ID, bookIds);
                startActivityForResult(intent, 100);
                return true;

            // go to activity to download books
            case R.id.download_books:
                intent = new Intent(MainActivity.this, DownloadBooksActivity.class);
                listOfBooks = books.getBooks("", Books.AVAILABLE_TABLE_NAME);
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

    // function to update the current book
    private void updateBookText() {
        // get the scroll view
        ScrollView sv_text = findViewById(R.id.sv_book_text);
        // remove all children views
        sv_text.removeAllViews();

        // make a textview
        TextView book = new TextView(this);
        // set the font size
        book.setTextSize(font_size);
        try {
            // set the text to the current book at the current page
            book.setText(currentBook.substring(position, position + pageSize));

        }
        catch (StringIndexOutOfBoundsException e) { }

        // add the view to the scroll view
        sv_text.addView(book);
        // scroll to the top
        sv_text.pageScroll(View.FOCUS_UP);
        // update the page number edit text
        updatePageNum();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            currentBook = readBook(data.getStringExtra(EXTRA_BOOK_ID));
            numPages = currentBook.length()/pageSize;

            updateBookText();
        }
        if (resultCode == LibraryActivity.RESULT_DELETE_CODE) {
            currentBook = "";
            numPages = 0;

            // update the title
            TextView tv_title = findViewById(R.id.tv_book_title);
            tv_title.setText("Title: ");

            // update the author
            TextView tv_author = findViewById(R.id.tv_author);
            tv_author.setText("Author: ");


            updateBookText();
        }
    }

    public void updateBook(String id) {
        Book book = books.getBooks(id, Books.DOWNLOADED_TABLE_NAME).get(0);
        book.setDate(new java.sql.Timestamp(System.currentTimeMillis()));
        book.setPosition(position);
        //Toast.makeText(this, "time = " + new java.sql.Date(System.currentTimeMillis()).toString(), Toast.LENGTH_LONG).show();
        books.updateBook(book, true);
    }

    // read a book by id
    public String readBook(String id) {
        // set the current book id to id
        currentBookID = id;
        // find the book
        Book book = books.getBooks(id, Books.DOWNLOADED_TABLE_NAME).get(0);
        // update the book to the current time
        updateBook(book.getId());
        // get the path
        String path = book.getPath();
        // get the position of the book
        position = book.getPosition();

        // update the title
        TextView tv_title = findViewById(R.id.tv_book_title);
        tv_title.setText("Title: " + book.getTitle());

        // update the author
        TextView tv_author = findViewById(R.id.tv_author);
        tv_author.setText("Author: " + book.getAuthor());

        // read the book
        File file = new File(path);
        // book text
        StringBuilder bookText = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                // add line to the book text
                bookText.append(line);
                bookText.append('\n');
            }
        }
        catch (FileNotFoundException fe) { fe.printStackTrace(); }
        catch (IOException ie) { ie.printStackTrace(); }

        return bookText.toString();
    }

}
