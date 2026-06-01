package com.pulsepath.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.pulsepath.R
import com.pulsepath.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val dest = if (FirebaseAuth.getInstance().currentUser != null)
                MainActivity::class.java else AuthActivity::class.java
            startActivity(Intent(this, dest))
            finish()
        }, 1800)
    }
}
