package com.example.basicapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val spMonth = findViewById<Spinner>(R.id.spMonth)
        val spDay = findViewById<Spinner>(R.id.spDay)
        val spYear = findViewById<Spinner>(R.id.spYear)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        val thisYear = Calendar.getInstance().get(Calendar.YEAR)
        fun setup(sp: Spinner, items: List<String>) {
            sp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        }
        setup(spMonth, listOf("Month") + DbHelper.MONTHS.toList())
        setup(spDay, listOf("Day") + (1..31).map { it.toString() })
        setup(spYear, listOf("Year") + (thisYear downTo 1900).map { it.toString() })

        val db = DbHelper(this)

        btnSubmit.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val m = spMonth.selectedItemPosition
            val d = spDay.selectedItemPosition
            val y = if (spYear.selectedItemPosition == 0) 0 else spYear.selectedItem.toString().toInt()

            if (name.isEmpty()) { etName.error = "Enter your name"; return@setOnClickListener }
            if (m == 0 || d == 0 || y == 0) {
                Toast.makeText(this, "Select your birth date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Reject impossible dates such as Feb 30
            val validDate = try {
                Calendar.getInstance().apply { isLenient = false; clear(); set(y, m - 1, d) }.timeInMillis
                true
            } catch (e: Exception) { false }
            if (!validDate) {
                Toast.makeText(this, "Invalid birth date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter a valid email"; return@setOnClickListener
            }

            val iso = "%04d-%02d-%02d".format(y, m, d)
            db.insertUser(name, iso, email)

            startActivity(
                Intent(this, ResultActivity::class.java)
                    .putExtra("name", name)
                    .putExtra("birthday", DbHelper.pretty(iso))
                    .putExtra("email", email)
            )
        }
    }
}
