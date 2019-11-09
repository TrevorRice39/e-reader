package com.trevor.e_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DownloadBooksActivity extends AppCompatActivity {
    public Books books = new Books(this);
    String[] bookTitles;
    String[] bookIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        setTitle("Download Books");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        bookTitles = intent.getStringArrayExtra(MainActivity.EXTRA_BOOK_ID);
        bookIds = intent.getStringArrayExtra(MainActivity.EXTRA_BOOK_IDS_ID);
        // creating the adapter for the list of downloaded books
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_book_list, bookTitles);

        ListView listView = (ListView) findViewById(R.id.list_books);
        listView.setAdapter(adapter);
        // if they select a book
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(DownloadBooksActivity.this)
                        .setTitle("Download")
                        .setMessage("Would you like to download " + bookTitles[i] + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                                System.out.println("id is " + bookIds[i]);
                                books.downloadBook(bookIds[i]);
                                // add to library and show downloading
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
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
}
