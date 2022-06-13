package com.example.bengkolan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        firebaseAuth = FirebaseAuth.getInstance()

        val getUser = firebaseAuth.currentUser

        if (getUser != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(
                    Intent(this,
                        MainActivity::class.java)
                )
                finish()
            }, 2500)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(
                    Intent(this,
                        AuthActivity::class.java)
                )
                finish()
            }, 2500)
        }

    }
}