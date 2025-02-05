package com.example.easycalculator // Replace this with your package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Black),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Screen
        Text(
            text = input,
            fontSize = 48.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.End)
        )

        // Calculator Buttons
        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "x"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    CalculatorButton(label) {
                        input = handleButtonClick(label, input)
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
    ) {
        Text(text = label, fontSize = 24.sp, color = Color.White)
    }
}

fun handleButtonClick(label: String, currentInput: String): String {
    return when (label) {
        "C" -> "0"
        "=" -> evaluateExpression(currentInput)
        else -> if (currentInput == "0") label else currentInput + label
    }
}

fun evaluateExpression(expression: String): String {
    return try {
        // Replace 'x' with '*' for multiplication
        val sanitizedExpression = expression.replace("x", "*")

        // Use a simple parser for basic arithmetic operations
        val result = object : Any() {
            fun parse(): Double {
                val tokens = sanitizedExpression.toMutableList()
                return parseExpression(tokens)
            }

            private fun parseExpression(tokens: MutableList<Char>): Double {
                var value = parseTerm(tokens)
                while (tokens.isNotEmpty() && (tokens[0] == '+' || tokens[0] == '-')) {
                    val op = tokens.removeAt(0)
                    val nextValue = parseTerm(tokens)
                    value = if (op == '+') value + nextValue else value - nextValue
                }
                return value
            }

            private fun parseTerm(tokens: MutableList<Char>): Double {
                var value = parseFactor(tokens)
                while (tokens.isNotEmpty() && (tokens[0] == '*' || tokens[0] == '/')) {
                    val op = tokens.removeAt(0)
                    val nextValue = parseFactor(tokens)
                    value = if (op == '*') value * nextValue else value / nextValue
                }
                return value
            }

            private fun parseFactor(tokens: MutableList<Char>): Double {
                if (tokens.isNotEmpty() && tokens[0] == '(') {
                    tokens.removeAt(0)
                    val value = parseExpression(tokens)
                    tokens.removeAt(0) // Remove closing parenthesis ')'
                    return value
                }
                val number = StringBuilder()
                while (tokens.isNotEmpty() && (tokens[0].isDigit() || tokens[0] == '.')) {
                    number.append(tokens.removeAt(0))
                }
                return number.toString().toDouble()
            }
        }.parse()

        // Check if the result is a whole number (integer) and format accordingly
        if (result == result.toInt().toDouble()) {
            result.toInt().toString()  // Return as integer
        } else {
            result.toString()  // Return as decimal
        }
    } catch (e: Exception) {
        "Error"
    }
}
