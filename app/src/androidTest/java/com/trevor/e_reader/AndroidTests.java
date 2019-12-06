package com.trevor.e_reader;

import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AndroidTests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    // is the database initialized
    @Test
    public void initializeDB() {
        // delete the database
        getApplicationContext().deleteDatabase("test");

        // make a new database instance
        Books books = new Books(mActivityRule.getActivity(), "test", R.raw.test_books);

        // get the books from the available table
        ArrayList<Book> booksFromDB = books.getBooks("", Books.AVAILABLE_TABLE_NAME);
        Book b1 = booksFromDB.get(0); // first book
        Book b2 = booksFromDB.get(1); // second book

        // check if the url, title, and author is correct
        assertEquals("http://styere.xyz/class/TimeMachine.txt", b1.getUrl());
        assertEquals("The Time Machine", b1.getTitle());
        assertEquals("H.G. Wells", b1.getAuthor());

        // check if url, title, and author is correct
        assertEquals("http://styere.xyz/class/ChristmasCarol.txt", b2.getUrl());
        assertEquals("A Christmas Carol", b2.getTitle());
        assertEquals("Charles Dickens", b2.getAuthor());
    }

    // test if you can download books
    @Test
    public void downloadBooks() {
        // new db instance
        Books books = new Books(mActivityRule.getActivity(), "test", R.raw.test_books);

        // open options
        openActionBarOverflowOrOptionsMenu(getApplicationContext());
        // select download books
        onView(withText("Download Books")).perform(click());
        // select time machine
        onView(withText("The Time Machine - H.G. Wells")).perform(click());
        // select ok
        onView(withText("OK")).perform(click());
        // give it time to download
        try {
            Thread.sleep(5000);
        }
        catch (Exception e) {

        }

        // the actual path
        String path = getApplicationContext().getFilesDir().getPath().toString() + "/" + "The Time Machine".replace(" ", "") + ".txt";
        File f = new File(path);

        // check if that file exists
        assert(f.exists());
        mActivityRule.finishActivity();

    }

    // checks if the user can select a book and display it to screen
    @Test
    public void canSelectBook() {
        Books books = new Books(mActivityRule.getActivity(), "test", R.raw.test_books);

        // open options
        openActionBarOverflowOrOptionsMenu(getApplicationContext());

        // go to library
        onView(withText("View Library")).perform(click());
        // select time machine
        onView(withText("The Time Machine - H.G. Wells")).perform(click());
        // read the book
        onView(withText("READ")).perform(click());

        // clear the page number
        onView(withId(R.id.et_page)).perform(clearText());
        // type 0
        onView(withId(R.id.et_page)).perform(typeText("0"));
        // go to page 0
        onView(withId(R.id.btn_page_search)).perform(click());

        // actual text that should be in the book
        String actualBookText = "The Project Gutenberg EBook of The Time Machine, by H. G. Wells";

        // get the text from the book textview
        TextView book = mActivityRule.getActivity().findViewById(R.id.tv_book);

        // get first 300 characters
        String bookText = book.getText().toString().substring(0, 300);

        // find the index of wells
        bookText = bookText.substring(0, bookText.indexOf("Wells") + 6);

        // replace all line breaks in both strings
        bookText = bookText.replaceAll("(\\r|\\n)", "");
        actualBookText = actualBookText.replaceAll("(\\r|\\n)", "");

        // test if they equal
        assertEquals(bookText, actualBookText);

        mActivityRule.finishActivity();
    }

    // test if the program saves the last book read
    @Test
    public void savesBook() {
        Books books = new Books(mActivityRule.getActivity(), "test", R.raw.test_books);

        // open the option menu
        openActionBarOverflowOrOptionsMenu(getApplicationContext());

        // click library
        onView(withText("View Library")).perform(click());
        // click the time machine
        onView(withText("The Time Machine - H.G. Wells")).perform(click());
        // read it
        onView(withText("READ")).perform(click());
        // close the activity
        mActivityRule.finishActivity();
        // open it again
        mActivityRule.launchActivity(new Intent());

        // go to the first page
        onView(withId(R.id.et_page)).perform(clearText());
        onView(withId(R.id.et_page)).perform(typeText("0"));
        onView(withId(R.id.btn_page_search)).perform(click());

        // check if the book is there

        // actual book text
        String actualBookText = "The Project Gutenberg EBook of The Time Machine, by H. G. Wells";

        TextView book = mActivityRule.getActivity().findViewById(R.id.tv_book);

        // 300 chars from book text view
        String bookText = book.getText().toString().substring(0, 300);

        // substring until wells
        bookText = bookText.substring(0, bookText.indexOf("Wells") + 6);

        // replace line breaks
        bookText = bookText.replaceAll("(\\r|\\n)", "");
        actualBookText = actualBookText.replaceAll("(\\r|\\n)", "");

        // test if they equal
        assertEquals(bookText, actualBookText);

        mActivityRule.finishActivity();
    }

    // test if the page is saved
    @Test
    public void savesPage() {
        Books books = new Books(mActivityRule.getActivity(), "test", R.raw.test_books);

        // open the options
        openActionBarOverflowOrOptionsMenu(getApplicationContext());

        // view library
        onView(withText("View Library")).perform(click());
        // select the time machine
        onView(withText("The Time Machine - H.G. Wells")).perform(click());
        // read it
        onView(withText("READ")).perform(click());

        // go to page 5
        onView(withId(R.id.et_page)).perform(clearText());
        onView(withId(R.id.et_page)).perform(typeText("5"));
        closeSoftKeyboard();
        onView(withId(R.id.btn_page_search)).perform(click());
        onView(withId(R.id.btn_page_search)).perform(click());
        
        // close activity
        mActivityRule.finishActivity();

        // relaunch activity
        mActivityRule.launchActivity(new Intent());

        // should be on page 5
        onView(withId(R.id.et_page)).check(matches(withText("5")));
    }
}