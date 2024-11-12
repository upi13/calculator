package com.example.belajarkotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var workingsTV: TextView
    private lateinit var resultsTV: TextView

    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingsTV = findViewById(R.id.workingsTV)
        resultsTV = findViewById(R.id.resultsTV)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal) {
                    workingsTV.append(view.text)
                    canAddDecimal = false
                }
            } else {
                workingsTV.append(view.text)
                canAddOperation = true
            }
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View) {
        workingsTV.text = ""
        resultsTV.text = ""
        canAddOperation = false
        canAddDecimal = true
    }

    fun backSpaceAction(view: View) {
        val length = workingsTV.length()
        if (length > 0) {
            if (workingsTV.text.last() == '.') {
                canAddDecimal = true
            }
            workingsTV.text = workingsTV.text.subSequence(0, length - 1)

            if (length > 1) {
                canAddOperation = !isOperator(workingsTV.text.last())
            } else {
                canAddOperation = false
            }
        }
    }

    fun equalsAction(view: View) {
        try {
            val expression = workingsTV.text.toString()
            if (expression.isNotEmpty() && canAddOperation) {
                val processedExpression = expression.replace('x', '*')

                val result = evaluateExpression(processedExpression)

                resultsTV.text = formatResult(result)
            }
        } catch (e: Exception) {
            resultsTV.text = "Error"
        }
    }

    private fun evaluateExpression(expression: String): Double {
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<Char>()
        var currentNumber = ""

        for (char in expression) {
            when {
                char.isDigit() || char == '.' -> currentNumber += char
                isOperator(char) -> {
                    if (currentNumber.isNotEmpty()) {
                        numbers.add(currentNumber.toDouble())
                        currentNumber = ""
                    }
                    operators.add(char)
                }
            }
        }

        if (currentNumber.isNotEmpty()) {
            numbers.add(currentNumber.toDouble())
        }

        var i = 0
        while (i < operators.size) {
            if (operators[i] == '%') {
                numbers[i] = numbers[i] / 100
                operators.removeAt(i)
                i--
            } else if (operators[i] == '*' || operators[i] == '/') {
                val result = when (operators[i]) {
                    '*' -> numbers[i] * numbers[i + 1]
                    '/' -> numbers[i] / numbers[i + 1]
                    else -> 0.0
                }
                numbers[i] = result
                numbers.removeAt(i + 1)
                operators.removeAt(i)
                i--
            }
            i++
        }

        var result = numbers[0]
        for (j in operators.indices) {
            result = when (operators[j]) {
                '+' -> result + numbers[j + 1]
                '-' -> result - numbers[j + 1]
                else -> result
            }
        }
        return result
    }

    private fun isOperator(char: Char): Boolean {
        return char in "+-*/x%"
    }


    private fun formatResult(result: Double): String {
        return if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            result.toString()
        }
    }
}
