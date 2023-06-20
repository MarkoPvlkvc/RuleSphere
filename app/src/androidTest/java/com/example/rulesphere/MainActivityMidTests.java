package com.example.rulesphere;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
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

import android.view.KeyEvent;
import android.view.View;
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
    public void SearchTest() {
        String searchTerm = "People living deeply have no fear of death";

        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search_view))
                .perform(new SearchViewSetTextAction(searchTerm));

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
}
