package org.flauschhaus.broccoli;

import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.flauschhaus.broccoli.category.Category;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.NavigationViewActions.navigateTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.flauschhaus.broccoli.util.RecyclerViewAssertions.hasItemsCount;
import static org.flauschhaus.broccoli.util.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FilteringAndSearchingIntegrationTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        scenario = launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void filter_and_search() throws InterruptedException {
        onView(withId(R.id.recycler_view)).check(hasItemsCount(0));

        // create category
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(open());
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_categories));

        Thread.sleep(1000); // TODO why is that necessary?

        onView(withId(R.id.recycler_view)).check(hasItemsCount(0));

        onView(withId(R.id.fab_categories)).perform(click());
        onView(withId(R.id.category_name)).perform(typeText("Test"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_category_name)).check(matches(withText("Test")));

        // create and assign recipe
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(open());
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_recipes));

        Thread.sleep(1000);

        onView(withId(R.id.fab_recipes)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText("Lauchkuchen"));
        onView(withId(R.id.new_categories)).perform(click());
        onView(withText("Test")).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.button_save_recipe)).perform(click());

        // check show only this recipe
        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Lauchkuchen")));

        // create another recipe
        onView(withId(R.id.fab_recipes)).perform(click());
        onView(withId(R.id.new_title)).perform(typeText("Tofucurry"));
        onView(withId(R.id.button_save_recipe)).perform(click());

        // check show only this recipe
        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Tofucurry")));

        // favor this recipe
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).perform(click());
        onView(withId(R.id.action_details_like)).perform(click());
        pressBack();
        pressBack();
        pressBack();

        // check all recipes
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(Category.class)), is(Category.ALL))).perform(click());
        onView(withId(R.id.recycler_view)).check(hasItemsCount(2));

        // check unassigned recipes
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(Category.class)), is(Category.UNASSIGNED))).perform(click());
        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Tofucurry")));

        // check favorite recipes
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(Category.class)), is(Category.FAVORITES))).perform(click());
        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Tofucurry")));

        // check category
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(Category.class)), withCategoryName("Test"))).perform(click());
        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Lauchkuchen")));

        // search for something
        onView(withId(R.id.action_search)).perform(click());
        onView(is(instanceOf(SearchView.class))).perform(typeSearchViewText("Lau"));
        onView(withId(R.id.recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.recycler_view).atPositionOnView(0, R.id.card_text_view_title)).check(matches(withText("Lauchkuchen")));
        pressBack();
        pressBack();

        // delete recipes
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(Category.class)), is(Category.ALL))).perform(click());

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.recycler_view)).check(hasItemsCount(0));

        // delete category
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(open());
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_categories));

        Thread.sleep(1000);

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(android.R.id.button3)).perform(click());
        onView(withId(android.R.id.button3)).perform(click());
        onView(withId(R.id.recycler_view)).check(hasItemsCount(0));
    }

    static Matcher<Category> withCategoryName(String categoryName) {
        return new CategoryNameMatcher(categoryName);
    }

    static class CategoryNameMatcher extends TypeSafeMatcher<Category> {

        private String categoryName;

        CategoryNameMatcher(String categoryName) {
            this.categoryName = categoryName;
        }

        @Override
        protected boolean matchesSafely(Category category) {
           return categoryName.equals(category.getName());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("category with name: " + categoryName);
        }

    }

    static ViewAction typeSearchViewText(final String text){
        return new ViewAction(){
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text,false);
            }
        };
    }

}