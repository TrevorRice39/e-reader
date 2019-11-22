package com.trevor.e_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Books books = new Books(this);
    int position = 0;
    int pageSize = 1000;
    String currentBook = "Go select a book from the library please!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.deleteDatabase("Library");

        Button back = findViewById(R.id.btn_back);
        Button next = findViewById(R.id.btn_next);

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
        try {
            book.setText(currentBook.substring(position, position + pageSize));
            EditText pageNum = findViewById(R.id.et_page);
        }
        catch (StringIndexOutOfBoundsException e) {

        }

        sv_text.addView(book);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            currentBook = readBook(data.getStringExtra(EXTRA_BOOK_ID));
            updateBookText();
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
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
