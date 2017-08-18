package com.example.android.baketime;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RecipeListInstrumentedTest {
    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityTestRule = new ActivityTestRule<RecipeListActivity>(RecipeListActivity.class);
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.example.android.baketime", appContext.getPackageName());
    }
    @Test
    public void testIngredientsView() throws Exception {
        onView(allOf(withId(R.id.recipe), withText("Nutella Pie"))).perform(click());
        onView(allOf(withId(R.id.id), withText("Ingredients"))).check(matches(withText("Ingredients")));
    }
}
