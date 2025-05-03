package com.example.rgbcolorpicker

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar

    private lateinit var redInput: EditText
    private lateinit var greenInput: EditText
    private lateinit var blueInput: EditText

    private lateinit var redSwitch: SwitchMaterial
    private lateinit var greenSwitch: SwitchMaterial
    private lateinit var blueSwitch: SwitchMaterial

    private lateinit var colorView: View
    private lateinit var resetButton: Button

    private var redValue = 1.0f
    private var greenValue = 1.0f
    private var blueValue = 1.0f

    private var redPrevious = 1.0f
    private var greenPrevious = 1.0f
    private var bluePrevious = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        redSeekBar = findViewById(R.id.seekBarRed)
        greenSeekBar = findViewById(R.id.seekBarGreen)
        blueSeekBar = findViewById(R.id.seekBarBlue)

        redInput = findViewById(R.id.inputRed)
        greenInput = findViewById(R.id.inputGreen)
        blueInput = findViewById(R.id.inputBlue)

        redSwitch = findViewById(R.id.switchRed)
        greenSwitch = findViewById(R.id.switchGreen)
        blueSwitch = findViewById(R.id.switchBlue)

        colorView = findViewById(R.id.colorBox)
        resetButton = findViewById(R.id.resetButton)

        setupColorControls(redSeekBar, redInput, redSwitch, "red")
        setupColorControls(greenSeekBar, greenInput, greenSwitch, "green")
        setupColorControls(blueSeekBar, blueInput, blueSwitch, "blue")

        resetButton.setOnClickListener {
            redValue = 1.0f; greenValue = 1.0f; blueValue = 1.0f
            redPrevious = 1.0f; greenPrevious = 1.0f; bluePrevious = 1.0f
            updateUIFromValues()
        }

        updateColor()
    }

    private fun setupColorControls(seekBar: SeekBar, input: EditText, toggle: SwitchMaterial, color: String) {
        seekBar.max = 1000
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress / 1000f
                if (toggle.isChecked) {
                    setColorValue(color, value)
                    input.setText(String.format("%.3f", value))
                    updateColor()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toFloatOrNull()
                if (value == null || value < 0 || value > 1.0) {
                    Toast.makeText(this@MainActivity, "Value must be between 0 and 1.0", Toast.LENGTH_SHORT).show()
                    return
                }
                if (toggle.isChecked) {
                    setColorValue(color, value)
                    seekBar.progress = (value * 1000).toInt()
                    updateColor()
                }
            }
        })

        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val prev = getPreviousValue(color)
                setColorValue(color, prev)
                seekBar.progress = (prev * 1000).toInt()
                input.setText(String.format("%.3f", prev))
                seekBar.isEnabled = true
                input.isEnabled = true
            } else {
                val current = getColorValue(color)
                setPreviousValue(color, current)
                setColorValue(color, 0.0f)
                seekBar.progress = 0
                input.setText("0.000")
                seekBar.isEnabled = false
                input.isEnabled = false
            }
            updateColor()
        }
    }

    private fun updateUIFromValues() {
        listOf("red", "green", "blue").forEach { color ->
            val value = getColorValue(color)
            val seekBar = getSeekBar(color)
            val input = getInput(color)
            val toggle = getSwitch(color)
            seekBar.progress = (value * 1000).toInt()
            input.setText(String.format("%.3f", value))
            seekBar.isEnabled = true
            input.isEnabled = true
            toggle.isChecked = true
        }
        updateColor()
    }

    private fun updateColor() {
        colorView.setBackgroundColor(Color.rgb(
            (redValue * 255).toInt(),
            (greenValue * 255).toInt(),
            (blueValue * 255).toInt()
        ))
    }

    private fun getColorValue(color: String): Float = when (color) {
        "red" -> redValue
        "green" -> greenValue
        else -> blueValue
    }

    private fun setColorValue(color: String, value: Float) {
        when (color) {
            "red" -> redValue = value
            "green" -> greenValue = value
            "blue" -> blueValue = value
        }
    }

    private fun getPreviousValue(color: String): Float = when (color) {
        "red" -> redPrevious
        "green" -> greenPrevious
        else -> bluePrevious
    }

    private fun setPreviousValue(color: String, value: Float) {
        when (color) {
            "red" -> redPrevious = value
            "green" -> greenPrevious = value
            "blue" -> bluePrevious = value
        }
    }

    private fun getSeekBar(color: String): SeekBar = when (color) {
        "red" -> redSeekBar
        "green" -> greenSeekBar
        else -> blueSeekBar
    }

    private fun getInput(color: String): EditText = when (color) {
        "red" -> redInput
        "green" -> greenInput
        else -> blueInput
    }

    private fun getSwitch(color: String): SwitchMaterial = when (color) {
        "red" -> redSwitch
        "green" -> greenSwitch
        else -> blueSwitch
    }
}
