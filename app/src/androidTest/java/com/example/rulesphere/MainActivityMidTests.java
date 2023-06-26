package com.example.rulesphere;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.search.SearchView;

@RunWith(AndroidJUnit4.class)
public class MainActivityMidTests {

    // Custom ViewAction to perform setText on a SearchView
    public class SearchViewSetTextAction implements ViewAction {
        private final String text;

        public SearchViewSetTextAction(String text) {
            this.text = text;
        }

        @Override
        public Matcher<View> getConstraints() {
            return isAssignableFrom(SearchView.class);
        }

        @Override
        public String getDescription() {
            return "Set text on a SearchView";
        }

        @Override
        public void perform(UiController uiController, View view) {
            SearchView searchView = (SearchView) view;
            searchView.setText(text);

            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
            searchView.dispatchKeyEvent(keyEvent);
        }
    }

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void SearchTest() throws InterruptedException {
        String searchTerm = "People living deeply have no fear of death";

        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.searchViewInput)).perform(click());
        onView(withId(R.id.searchViewInput)).perform(typeText(searchTerm));

        pressBack();

        Thread.sleep(1000);

        onView(withId(R.id.recycler_view_search))
                .check(matches(hasDescendant(allOf(
                        isDisplayed(),
                        hasDescendant(withText(containsString(searchTerm)))
                ))));
    }

    String ruleOfDayQuote = "";

    @Test
    public void LikeTest() {
        onView(withId(R.id.ruleCard)).perform(doubleClick());

        onView(withId(R.id.ruleOfDayQuote))
                .check(new ViewAssertion() {
                    @Override
                    public void check(View view, NoMatchingViewException noViewFoundException) {
                        if (view instanceof TextView) {
                            ruleOfDayQuote = ((TextView) view).getText().toString();
                        }
                    }
                });

        onView(withId(R.id.myRules)).perform(click());

        onView(withId(R.id.recycler_view_myRules))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(containsString(ruleOfDayQuote)))));

        onView(withText(containsString(ruleOfDayQuote)))
                .check(matches(isDisplayed()));
    }

    String ruleOfDayDay, ruleOfDayAuthor, ruleOfDayQuote2;

    @Test
    public void copyQuoteToClipboard() throws Exception {
        onView(withId(R.id.ruleOfDayDay))
                .check(new ViewAssertion() {
                    @Override
                    public void check(View view, NoMatchingViewException noViewFoundException) {
                        if (view instanceof TextView) {
                            ruleOfDayDay = ((TextView) view).getText().toString();
                        }
                    }
                });

        onView(withId(R.id.ruleOfDayAuthor))
                .check(new ViewAssertion() {
                    @Override
                    public void check(View view, NoMatchingViewException noViewFoundException) {
                        if (view instanceof TextView) {
                            ruleOfDayAuthor = ((TextView) view).getText().toString();
                        }
                    }
                });

        onView(withId(R.id.ruleOfDayQuote))
                .check(new ViewAssertion() {
                    @Override
                    public void check(View view, NoMatchingViewException noViewFoundException) {
                        if (view instanceof TextView) {
                            ruleOfDayQuote2 = ((TextView) view).getText().toString();
                        }
                    }
                });

        onView(withId(R.id.extended_fab_share)).perform(click());

        onView(withId(R.id.copyQuote)).perform(click());

        ClipboardManager clipboard = (ClipboardManager) ApplicationProvider.getApplicationContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);

        String copiedText = clipboard.getPrimaryClip().getItemAt(0).getText().toString();

        String expectedText = "Rule nr. " + ruleOfDayDay + "\n" +
                ruleOfDayQuote2 + "\n" +
                ruleOfDayAuthor;

        assertEquals(expectedText, copiedText);
    }

    String profileNameText;

    @Test
    public void changeProfileName() throws Exception {
        onView(withId(R.id.profile)).perform(click());

        onView(withId(R.id.profileNameInput)).perform(click());

        onView(withId(R.id.usedForMidTest)).perform(typeText("Proba"));

        pressBack();

        onView(withId(R.id.updateProfileName)).perform(click());

        onView(withId(R.id.profileNameText))
                .check(new ViewAssertion() {
                    @Override
                    public void check(View view, NoMatchingViewException noViewFoundException) {
                        if (view instanceof TextView) {
                            profileNameText = ((TextView) view).getText().toString();
                        }
                    }
                });

        assertEquals("Proba.", profileNameText);
    }

    @Test
    public void addNewQuote() throws Exception {
        onView(withId(R.id.myRules)).perform(click());

        onView(withId(R.id.myRulesQuotes)).perform(click());

        onView(withId(R.id.fab_addQuote)).perform(click());

        onView(withId(R.id.ruleInput)).perform(click());
        onView(withId(R.id.usedForMidTest2)).perform(typeText("Probni rule."));

        pressBack();

        onView(withId(R.id.addRule)).perform(click());

        onView(withText(containsString("Probni rule.")))
                .check(matches(isDisplayed()));
    }
}
