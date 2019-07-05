package com.test.fan;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void test1() throws Exception {
        Intent intent = new Intent();
        loginActivityActivityTestRule.launchActivity(intent);

        onView(withId(R.id.btn_login))
                .perform(ViewActions.click());
        onView(withText(R.string.toast_username))
                .inRoot(RootMatchers.withDecorView(not(is(loginActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        Thread.sleep(1000);
        assertEquals(1, 1);
    }

    @Test
    public void test2() throws Exception {
        Intent intent = new Intent();
        loginActivityActivityTestRule.launchActivity(intent);

        onView(withId(R.id.et_user_name))
                .perform(clearText(), typeText("aaa"), closeSoftKeyboard());
        onView(withId(R.id.btn_login))
                .perform(ViewActions.click());
        onView(withText(R.string.toast_password))
                .inRoot(RootMatchers.withDecorView(not(is(loginActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        Thread.sleep(1000);
        assertEquals(1, 1);
    }

    @Test
    public void test3() throws Exception {
        Intent intent = new Intent();
        loginActivityActivityTestRule.launchActivity(intent);

        onView(withId(R.id.et_psw))
                .perform(clearText(), typeText("aaa"), closeSoftKeyboard());
        onView(withId(R.id.btn_login))
                .perform(ViewActions.click());
        onView(withText(R.string.toast_username))
                .inRoot(RootMatchers.withDecorView(not(is(loginActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        Thread.sleep(1000);
        assertEquals(1, 1);
    }

    @Test
    public void test4() throws Exception {
        Intent intent = new Intent();
        loginActivityActivityTestRule.launchActivity(intent);
        onView(withId(R.id.et_user_name))
                .perform(clearText(), typeText("aaa"), closeSoftKeyboard());
        onView(withId(R.id.et_psw))
                .perform(clearText(), typeText("aaa"), closeSoftKeyboard());
        onView(withId(R.id.btn_login))
                .perform(ViewActions.click());
        onView(withText(R.string.toast_mismatch))
                .inRoot(RootMatchers.withDecorView(not(is(loginActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        Thread.sleep(1000);
        assertEquals(1, 1);
    }

    @Test
    public void test5() throws Exception {
        Intent intent = new Intent();
        loginActivityActivityTestRule.launchActivity(intent);
        onView(withId(R.id.et_user_name))
                .perform(clearText(), typeText("zzk"), closeSoftKeyboard());
        onView(withId(R.id.et_psw))
                .perform(clearText(), typeText("aaa"), closeSoftKeyboard());
        onView(withId(R.id.btn_login))
                .perform(ViewActions.click());
        onView(withText(R.string.toast_mismatch))
                .inRoot(RootMatchers.withDecorView(not(is(loginActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        Thread.sleep(1000);
        assertEquals(1, 1);
    }

    @Test
    public void test6() throws Exception {
        Intent intent = new Intent();
        loginActivityActivityTestRule.launchActivity(intent);
        onView(withId(R.id.et_user_name))
                .perform(clearText(), typeText("zzk"), closeSoftKeyboard());
        onView(withId(R.id.et_psw))
                .perform(clearText(), typeText("orzorz"), closeSoftKeyboard());
        onView(withId(R.id.btn_login))
                .perform(ViewActions.click());
        assertTrue(loginActivityActivityTestRule.getActivity().isFinishing());

        Thread.sleep(1000);
        assertEquals(1, 1);
    }

    @Test
    public void test7() throws Exception {
        Intent intent = new Intent();
        loginActivityActivityTestRule.launchActivity(intent);

        onView(withId(R.id.tv_register))
                .perform(click());

        intended(hasComponent(RegisterActivity.class.getName()));

        Thread.sleep(1000);
        assertEquals(1, 1);
    }
}
