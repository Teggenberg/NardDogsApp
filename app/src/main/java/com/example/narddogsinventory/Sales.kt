package com.example.narddogsinventory

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class Sales : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)

        val origin = LocalDateTime.parse("2023-01-01T20:00:00.0000")
        val current = LocalDateTime.now()

        val days = Duration.between(origin,current).toDays().toString()

        findViewById<TextView>(R.id.etDate).text = days
    }
}