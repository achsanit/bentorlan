package com.example.bengkolan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(this,
                    MainActivity::class.java)
            )
            finish()
        }, 2500)
    }
}