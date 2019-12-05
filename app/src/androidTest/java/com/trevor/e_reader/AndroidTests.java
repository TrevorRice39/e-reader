package com.trevor.e_reader;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

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



    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void initializeDB() {
        Books books = new Books(mActivityRule.getActivity(), "test", R.raw.test_books);
        ArrayList<Book> booksFromDB = books.getBooks("", Books.AVAILABLE_TABLE_NAME);
        Book b1 = booksFromDB.get(0);
        Book b2 = booksFromDB.get(1);

        assertEquals("http://styere.xyz/class/TimeMachine.txt", b1.getUrl());
        assertEquals("The Time Machine", b1.getTitle());
        assertEquals("H.G. Wells", b1.getAuthor());

        assertEquals("http://styere.xyz/class/ChristmasCarol.txt", b2.getUrl());
        assertEquals("A Christmas Carol", b2.getTitle());
        assertEquals("Charles Dickens", b2.getAuthor());
    }

    @Test
    public void downloadBooks() {
        Books books = new Books(mActivityRule.getActivity(), "test", R.raw.test_books);


        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());

        onView(withText("Download Books")).perform(click());
        onView(withText("The Time Machine - H.G. Wells")).perform(click());

        mActivityRule.finishActivity();

    }

}