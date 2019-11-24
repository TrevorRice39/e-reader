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
    public Books books = new Books(this);
    int position = 0;
    int pageSize = 1000;
    int numPages = 0;
    String currentBook = "Go select a book from the library please!";
    float font_size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this.deleteDatabase("Library");
        Stetho.initializeWithDefaults(this);
        Book book = books.getLastReadBook();
        if (book != null) {
            Toast.makeText(this, "id = " + book.getId(), Toast.LENGTH_LONG).show();
            currentBook = readBook(book.getId());
        }
        updateBookText();


        Button back = findViewById(R.id.btn_back);
        Button next = findViewById(R.id.btn_next);
        Button go_to = findViewById(R.id.btn_page_search);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position >= pageSize) {
                    position -= pageSize;
                }
                updateBookText();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= currentBook.length()-1001) {
                    position += pageSize;
                }
                updateBookText();
            }
        });

        go_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView pageNum = findViewById(R.id.et_page);


                int newPage = Integer.parseInt(pageNum.getText().toString());

                if (newPage <= numPages && newPage >= 0) {
                    position = newPage * 1000;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Invalid page number", Toast.LENGTH_LONG).show();
                }
                updateBookText();

            }
        });
    }

    private void updatePageNum() {
        TextView pageNum = findViewById(R.id.et_page);
        pageNum.setText("" + position/1000);
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
        Toast.makeText(this, "resumed", Toast.LENGTH_SHORT).show();
        ScrollView text = findViewById(R.id.sv_book_text);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.font_size = Float.parseFloat(preferences.getString(font_key, "12.0"));
        Toast.makeText(this, "" + this.font_size, Toast.LENGTH_LONG).show();
        updateBookText();

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
        switch (item.getItemId()) {
            // view the library of downloaded books
            case R.id.view_library:
                intent = new Intent(MainActivity.this, LibraryActivity.class);
                listOfBooks = books.getBooks("", Books.DOWNLOADED_TABLE_NAME);
                bookArray = new String[listOfBooks.size()];
                bookIds = new String[listOfBooks.size()];
                for (int i = 0; i < bookArray.length; i++) {
                    bookArray[i] = listOfBooks.get(i).getTitle() + " - " + listOfBooks.get(i).getAuthor() + " " + listOfBooks.get(i).getId();
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

    private void updateBookText() {
        ScrollView sv_text = findViewById(R.id.sv_book_text);
        sv_text.removeAllViews();

        TextView book = new TextView(this);
        book.setTextSize(font_size);
        try {
            book.setText(currentBook.substring(position, position + pageSize));
            // EditText pageNum = findViewById(R.id.et_page);
        }
        catch (StringIndexOutOfBoundsException e) {

        }

        sv_text.addView(book);
        sv_text.pageScroll(View.FOCUS_UP);
        updatePageNum();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            currentBook = readBook(data.getStringExtra(EXTRA_BOOK_ID));
            numPages = currentBook.length()/1000;

            updateBookText();
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }
    public String readBook(String id) {
        Book book = books.getBooks(id, Books.DOWNLOADED_TABLE_NAME).get(0);
        book.setDate(new java.sql.Timestamp(System.currentTimeMillis()));
        //Toast.makeText(this, "time = " + new java.sql.Date(System.currentTimeMillis()).toString(), Toast.LENGTH_LONG).show();
        books.updateBook(book, true);
        String path = book.getPath();
        position = book.getPosition();
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
