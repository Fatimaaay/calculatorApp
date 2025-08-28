package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
//variables for screen and values
    private lateinit var tvResult:TextView
    private var number1:Int =0
    private var number2:Int =0
    private var operator:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)//linking XML
        //Connecting XML IDs to Kotlin variables
        tvResult = findViewById(R.id.tvResult)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btnAdd: Button = findViewById(R.id.btnAdd)
        val btnEqual: Button = findViewById(R.id.btnEquals)
//check
        btn1.setOnClickListener {
            tvResult.text = tvResult.text.toString() + "1"
        }
        btn2.setOnClickListener {
            tvResult.text = tvResult.text.toString() + "2"

        }
        btn3.setOnClickListener{
            tvResult.text = tvResult.text.toString()+"3"
        }
        btn4.setOnClickListener{
            tvResult.text= tvResult.text.toString()+"4"
        }


        btnAdd.setOnClickListener {
            number1 = tvResult.text.toString().toInt() // store first number
            operator = "+" // store operator
            tvResult.text = "" // clear screen for second number
        }


        btnEqual.setOnClickListener {
            number2 = tvResult.text.toString().toInt() // second number
            var result = 0

            if (operator == "+") {
                result = number1 + number2
            }

            tvResult.text = result.toString() // show result
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}