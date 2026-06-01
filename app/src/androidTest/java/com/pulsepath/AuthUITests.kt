package com.pulsepath

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.pulsepath.ui.auth.AuthActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AuthUITests {

    private lateinit var scenario: ActivityScenario<AuthActivity>

    @Before
    fun setUp() {
        try { FirebaseAuth.getInstance().signOut() } catch (e: Exception) { }
        Thread.sleep(800)
        scenario = ActivityScenario.launch(AuthActivity::class.java)
        Thread.sleep(1000)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun test1_emailField_isVisible() {
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(R.id.etEmail)
            assertEquals("Email field should be visible",
                View.VISIBLE, view.visibility)
        }
    }

    @Test
    fun test2_passwordField_isVisible() {
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(R.id.etPassword)
            assertEquals("Password field should be visible",
                View.VISIBLE, view.visibility)
        }
    }

    @Test
    fun test3_loginButton_isVisible() {
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(R.id.btnLogin)
            assertEquals("Login button should be visible",
                View.VISIBLE, view.visibility)
        }
    }

    @Test
    fun test4_registerLink_isVisible() {
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(R.id.tvRegister)
            assertEquals("Register link should be visible",
                View.VISIBLE, view.visibility)
        }
    }

    @Test
    fun test5_forgotPasswordLink_isVisible() {
        scenario.onActivity { activity ->
            val view = activity.findViewById<View>(R.id.tvForgotPassword)
            assertEquals("Forgot password link should be visible",
                View.VISIBLE, view.visibility)
        }
    }
}
