package com.trevor.e_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DownloadBooksActivity extends AppCompatActivity {


    String[] bookTitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        setTitle("Download Books");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        bookTitles = intent.getStringArrayExtra("books");
        // creating the adapter for the list of downloaded books
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_book_list, bookTitles);

        ListView listView = (ListView) findViewById(R.id.list_books);
        listView.setAdapter(adapter);
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
}
