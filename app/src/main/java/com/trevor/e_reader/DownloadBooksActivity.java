package com.trevor.e_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DownloadBooksActivity extends AppCompatActivity {
    // new database access
    public Books books = new Books(this, MainActivity.DATABASE_NAME, R.raw.books);
    // titles of books you can download
    String[] bookTitles;
    // ids of books you can download
    String[] bookIds;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        setTitle("Download Books");

        // so you can go back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the book titles and ids passed in from main activity
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
                // are you sure you'd like to download?
                new AlertDialog.Builder(DownloadBooksActivity.this)
                        .setTitle("Download")
                        .setMessage("Would you like to download " + bookTitles[i] + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                // you are sure you want to download

                                // make a toast
                                Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

                                // get the book from the available table
                                Book book = books.getBooks(bookIds[i], books.AVAILABLE_TABLE_NAME).get(0);

                                // download the book and pass in the url and id
                                new DownloadBook().execute(book.getUrl(), bookIds[i]);

                            }})
                        .setNegativeButton(android.R.string.no, null).show(); // cancel
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

    // async task to download a book
    class DownloadBook extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String ... params) {
            String str_url = params[0];
            try {
                // assemble the string and the search request
                StringBuilder response = new StringBuilder();
                URL url = new URL(str_url);

                // make the connection
                HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();

                // did it do ok?
                if ( httpconn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(httpconn.getInputStream()), 8192);
                    String strLine = null;
                    while ((strLine = input.readLine()) != null) {
                        // have more data
                        response.append(strLine);
                        response.append("\n");
                    }
                    input.close();
                    // pass in the book and the id of the book
                    String res[] = {response.toString(), params[1]};
                    return res;
                }
                else {
                    // if it didn't work, just pass id
                    String res[] = {params[1]};
                    return res;
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                String res[] = {e.toString()};
                return res;
            }
            catch ( IOException e ) {
                e.printStackTrace();
                String res[] = {e.toString()};
                return res;
            }
            catch (Exception e) {
                e.printStackTrace();
                String res[] = {e.toString()};
                return res;
            }

        }

        @Override
        protected void onPostExecute(String[] result) {

            // no result, failed to download
            if (result == null) {
                Toast.makeText(getApplicationContext(), "Failed to download", Toast.LENGTH_LONG).show();
            }
            // no book, failed to download
            if (result.length == 1) {
                Book book = books.getBooks(result[0], books.AVAILABLE_TABLE_NAME).get(0);
                Toast.makeText(getApplicationContext(), "Failed to download " + book.getTitle(), Toast.LENGTH_LONG).show();
                return;
            }

            // get the book
            Book book = books.getBooks(result[1], books.AVAILABLE_TABLE_NAME).get(0);

            // make the file name
            String filename = getApplicationContext().getFilesDir().getPath().toString() + "/" + book.getTitle().replace(" ", "") + ".txt";
            // set the path of the book to the filename
            book.setPath(filename);

            // make params for write book, pass in book text and id
            String[] params = {result[0], filename};
            String output = "";
            try {
                // write the book to a file
                output = new WriteBook().execute(params).get();
            }
            catch (ExecutionException e) { e.printStackTrace();}
            catch (InterruptedException e) { e.printStackTrace();}

            SQLiteDatabase db = books.getWritableDatabase();
            // add the book to the db
            books.addBook(book, books.DOWNLOADED_TABLE_NAME, db);
            Toast.makeText(getApplicationContext(), "Finished downloading " + book.getTitle(), Toast.LENGTH_LONG).show();
        }
    }

    // async task to write the book to a file
    class WriteBook extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... data) {
            try {
                // open a file with the path passed in
                FileOutputStream fis = new FileOutputStream (new File(data[1]));
                // write the book text to file
                fis.write(data[0].getBytes());
                //close file
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            // all went well
            return null;
        }
    }
}
