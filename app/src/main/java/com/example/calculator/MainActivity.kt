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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)//linking XML
        //Connecting XML IDs to Kotlin variables
        tvResult = findViewById(R.id.tvResult)
        // digits 1-6
        val digitIds = listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8 , R.id.btn9)
        digitIds.forEach{
            id-> findViewById<Button>(id).setOnClickListener{
                tvResult.append((it as Button).text.toString())     // it means which element is clicked  at this point button is clicked so it is telling to compiler that button is clicked .text -> the text which is written on the button  .tostring() convert that into string
        }
        }
        // map UI for internal symbols
        val ops = mapOf( R.id.btnPlus to "+", R.id.btnMinus to "-", R.id.btnMultiply to "x", R.id.btnDivide to "/")
        ops.forEach{(id,op)-> // every operator has two parts id and symbol
            findViewById<Button>(id).setOnClickListener{tvResult.append(op)} // find button fom UI that id is matched and append that symbol to screen

        }
        //
        findViewById<Button>(R.id.btnClear).setOnClickListener { tvResult.text = "" }
       // findViewById<Button>(R.id.btnBack).setOnClickListener {backspace() } //backspace function to be created
        findViewById<Button>(R.id.btnEqual).setOnClickListener{onEqual()}

        //dropLast(1) → shortcut hai jo last ek character delete kar deta hai.
        //
        //substring(0, text.length - 1) → same kaam karta hai, bas lamba likhna padta hai




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun onEqual() {
        val expr = tvResult.text.toString()
        try {
            val tokens = tokenize(expr)
            val postfix = infixToPostfix(tokens)
            val result = evaluatePostfix(postfix)
            tvResult.text = formatResult(result)
        } catch (e: Exception) {
            tvResult.text = "Error"
        }
    }

    // --- parsing: string -> tokens (handles 12.5, unary -, × ÷) ---
    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        val num = StringBuilder()

        fun flush() {
            if (num.isNotEmpty()) {
                tokens.add(num.toString())
                num.setLength(0)
            }
        }

        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> num.append(c)

                c == ' ' -> { /* ignore spaces if any */ }

                c == '-' -> {
                    // unary minus? (start OR after operator or '(')
                    val prev = tokens.lastOrNull()
                    val isUnary = (num.isEmpty() && (tokens.isEmpty() || prev in listOf("+","-","*","/","(")))
                    if (isUnary) {
                        num.append('-')
                    } else {
                        flush()
                        tokens.add("-")
                    }
                }

                else -> {
                    flush()
                    val mapped = when (c) {
                        '×' -> "*"
                        '÷' -> "/"
                        else -> c.toString()
                    }
                    tokens.add(mapped)
                }
            }
            i++
        }
        flush()
        return tokens
    }

    // --- shunting-yard: infix -> postfix ---
    private fun precedence(op: String) = when (op) {
        "+","-" -> 1
        "*","/" -> 2
        else -> 0
    }

    private fun infixToPostfix(expression: List<String>): List<String> {
        val stack = mutableListOf<String>()
        val result = mutableListOf<String>()

        for (token in expression) {
            when (token) {
                "+","-","*","/" -> {
                    while (stack.isNotEmpty() && precedence(stack.last()) >= precedence(token)) {
                        result.add(stack.removeAt(stack.size - 1))
                    }
                    stack.add(token)
                }
                "(" -> stack.add(token)
                ")" -> {
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        result.add(stack.removeAt(stack.size - 1))
                    }
                    if (stack.isNotEmpty() && stack.last() == "(") stack.removeAt(stack.size - 1)
                }
                else -> result.add(token) // number
            }
        }
        while (stack.isNotEmpty()) result.add(stack.removeAt(stack.size - 1))
        return result
    }

    // --- postfix evaluate ---
    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = mutableListOf<Double>()
        for (token in postfix) {
            when (token) {
                "+","-","*","/" -> {
                    if (stack.size < 2) throw IllegalArgumentException("Bad expression")
                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)
                    val v = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> a / b
                        else -> 0.0
                    }
                    stack.add(v)
                }
                else -> stack.add(token.toDouble())
            }
        }
        if (stack.size != 1) throw IllegalStateException("Bad expression")
        return stack.last()
    }

    // numbers ko pretty print (12.0 -> 12)
    private fun formatResult(value: Double): String {
        val asLong = value.toLong()
        return if (value.isFinite() && kotlin.math.abs(value - asLong) < 1e-9) asLong.toString()
        else value.toString()
    }
}

