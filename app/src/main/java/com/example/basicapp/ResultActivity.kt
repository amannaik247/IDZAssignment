package com.example.basicapp

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Data passed from Activity 1
        val name = intent.getStringExtra("name")
        val birthday = intent.getStringExtra("birthday")
        val email = intent.getStringExtra("email")
        Toast.makeText(this, "Added: $name | $birthday | $email", Toast.LENGTH_LONG).show()

        val table = findViewById<LinearLayout>(R.id.table)
        addRow(table, "Name", "Birthday", "Email Id", bold = true)
        for ((n, b, e) in DbHelper(this).getAllUsers()) {
            addRow(table, n, DbHelper.pretty(b), e, bold = false)
        }
    }

    private fun addRow(parent: LinearLayout, a: String, b: String, c: String, bold: Boolean) {
        val row = LayoutInflater.from(this).inflate(R.layout.row_user, parent, false)
        listOf(R.id.tvName to a, R.id.tvBirthday to b, R.id.tvEmail to c).forEach { (id, text) ->
            row.findViewById<TextView>(id).apply {
                this.text = text
                if (bold) setTypeface(null, Typeface.BOLD)
            }
        }
        parent.addView(row)
    }
}
