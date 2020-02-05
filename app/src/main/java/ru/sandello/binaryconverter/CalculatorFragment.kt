package ru.sandello.binaryconverter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_calculator.*
import kotlinx.android.synthetic.main.fragment_calculator.view.*
import java.math.RoundingMode

@ExperimentalUnsignedTypes
class CalculatorFragment : Fragment() {
    private var a = 0.toBigDecimal()
    private var b = 0.toBigDecimal()
    private var c = 0.toBigDecimal()
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    private val listCustomBin = arrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load()
        myClipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        editTextResult.setOnClickListener { copyVal("calResult") }

        view.rootView.clear_fab.setOnClickListener {
            editTextVal1.setText("")
            editTextVal2.setText("")
            editTextResult.setText("")
            it.clear_fab.hide()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (editTextVal1?.text.toString() != "")
                    try {
                        a = ConvertTo().main(editTextVal1.text.toString(), spinner.text.toString().toInt(), 10).toBigDecimal()
                        if (togglePlus.isChecked) c = (a + b)
                        if (toggleMinus.isChecked) c = (a - b)
                        if (toggleMult.isChecked) c = (a * b)
                        if (toggleDiv.isChecked && b != 0.toBigDecimal()) c = String.format("%.${fractionCount}f", a.divide(b)).toBigDecimal()
                        editTextResult.setText(ConvertTo().main(c.toString(), 10, spinner3.text.toString().toInt()))
                        errorNull()
                    } catch (e: Exception) {
                        textInputLayoutCustom1?.error = getString(R.string.invalid_value)
                    }
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (editTextVal2?.text.toString() != "")
                    try {
                        b = ConvertTo().main(editTextVal2.text.toString(), spinner2.text.toString().toInt(), 10).toBigDecimal()
                        if (togglePlus.isChecked) c = (a + b)
                        if (toggleMinus.isChecked) c = (a - b)
                        if (toggleMult.isChecked) c = (a * b)
                        if (toggleDiv.isChecked && b != 0.toBigDecimal()) c = a.divide(b, fractionCount, RoundingMode.HALF_UP)
                        editTextResult.setText(ConvertTo().main(c.toString(), 10, spinner3.text.toString().toInt()))
                        errorNull()
                    } catch (e: Exception) {
                        if (editTextVal2?.text.toString() != "")
                            textInputLayoutCustom2?.error = getString(R.string.invalid_value)
                    }
            }
        }

        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (editTextResult?.text.toString() != "")
                    try {
                        if (togglePlus.isChecked) c = (a + b)
                        if (toggleMinus.isChecked) c = (a - b)
                        if (toggleMult.isChecked) c = (a * b)
                        if (toggleDiv.isChecked && b != 0.toBigDecimal()) c = a.divide(b, fractionCount, RoundingMode.HALF_UP)
                        editTextResult.setText(ConvertTo().main(c.toString(), 10, spinner3.text.toString().toInt()))
                        errorNull()
                    } catch (e: Exception) {
                        textInputLayoutResult?.error = getString(R.string.invalid_value)
                    }
            }
        }

        editTextVal1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var allowVal = ""
                val sym = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
                for (i in 0 until spinner.text.toString().toInt())
                    allowVal += sym[i]
                if (editTextVal1.hasFocus()) { //Если фокус на editTextVal1
                    if (s.toString() != "") { //если текст не равен нулю
                        var str = ""
                        for (i in s!!.indices) //ищем символ "-"
                            if (i == 0 && s[0].toString() == "-")
                                str += s[i]
                            else if (s != "" && s[i].toString() != "-")
                                str += s[i]

                        if (str == "-") {
                            if (str.length > 1)
                                when {
                                    str != "" -> calculate(str, editTextVal1, textInputLayoutCustom1, allowVal)
                                }
                        } else {
                            when {
                                str != "" -> calculate(str, editTextVal1, textInputLayoutCustom1, allowVal)
                                str == "" -> editTextVal1.setText("")
                            }
                        }
                    }
                    if (s.toString() == "")
                        calculate("0", editTextVal1, textInputLayoutCustom1, allowVal)
                }
            }
        })


        editTextVal2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var allowVal = ""
                val sym = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
                for (i in 0 until spinner2.text.toString().toInt())
                    allowVal += sym[i]
                if (editTextVal2.hasFocus()) {
                    if (s.toString() != "") {
                        var str = ""

                        for (i in s!!.indices)
                            if (i == 0 && s[i].toString() == "-")
                                str += s[i]
                            else if (s != "" && s[i].toString() != "-")
                                str += s[i]

                        if (str == "-") {
                            if (str.length > 1)
                                when {
                                    str != "" -> calculate(str, editTextVal2, textInputLayoutCustom2, allowVal)
                                }
                        } else {
                            when {
                                str != "" -> calculate(str, editTextVal2, textInputLayoutCustom2, allowVal)
                                str == "" -> editTextVal2.setText("")
                            }
                        }
                    }
                    if (s.toString() == "")
                        calculate("0", editTextVal2, textInputLayoutCustom2, allowVal)
                }
            }
        })

        toggleGroup.addOnButtonCheckedListener { _, _, _ ->
            calculateToggle()
        }
    }


    @SuppressLint("SetTextI18n", "DefaultLocale")
    fun calculate(string: String, editField: View?, editLayout: TextInputLayout?, allowVal: String) {
        try {
            checkClear()
            if (editField?.resources?.getResourceEntryName(editField.id) == "editTextVal1" && string != "") a = ConvertTo().main(string, spinner.text.toString().toInt(), 10).toBigDecimal()
            if (editField?.resources?.getResourceEntryName(editField.id) == "editTextVal2" && string != "") b = ConvertTo().main(string, spinner2.text.toString().toInt(), 10).toBigDecimal()
            if (togglePlus.isChecked) c = (a + b)
            if (toggleMinus.isChecked) c = (a - b)
            if (toggleMult.isChecked) c = (a * b)
            if (toggleDiv.isChecked && b != 0.toBigDecimal()) c = a.divide(b, fractionCount, RoundingMode.HALF_UP)
            if (editTextVal1.text.toString() != "" && editTextVal2.text.toString() != "")
                editTextResult.setText(ConvertTo().main(c.toString(), 10, spinner3.text.toString().toInt()))
            else
                editTextResult.setText("")
            errorNull()
            save()
        } catch (e: Exception) {
            editLayout?.error = getString(R.string.available_characters_for_input) + ": $allowVal"
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun calculateToggle() {
        try {
            checkClear()
            if (togglePlus.isChecked) c = (a + b)
            if (toggleMinus.isChecked) c = (a - b)
            if (toggleMult.isChecked) c = (a * b)
            if (toggleDiv.isChecked && b != 0.toBigDecimal()) c = a.divide(b, fractionCount, RoundingMode.HALF_UP)
            if (editTextVal1.text.toString() != "" && editTextVal2.text.toString() != "")
                editTextResult.setText(ConvertTo().main(c.toString(), 10, spinner3.text.toString().toInt()))
            else
                editTextResult.setText("")
            errorNull()
            save()
        } catch (e: Exception) {
        }
    }

    private fun errorNull() {
        view!!.rootView.textInputLayoutCustom1.error = null
        view!!.rootView.textInputLayoutCustom2.error = null
        view!!.rootView.textInputLayoutResult.error = null
    }

    private fun checkClear() {
        if (editTextVal1.text.toString() != "" || editTextVal2.text.toString() != "")
            view!!.rootView.clear_fab.show()
        else
            view!!.rootView.clear_fab.hide()
    }

    private fun copyVal(viewString: String) {
        if (viewString == "calResult" && editTextResult.text.toString() != "0" && editTextResult.text.toString() != "") {
            myClip = ClipData.newPlainText("text", editTextResult.text.toString())
            myClipboard!!.setPrimaryClip(myClip!!)
            Snackbar.make(view!!.rootView.snackbar, "Скопировано: ${editTextResult.text}", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun  load() {
        val calculatorSave = context!!.getSharedPreferences("calculator", Context.MODE_PRIVATE)
        editTextVal1.setText(calculatorSave.getString("calculator1", ""))
        editTextVal2.setText(calculatorSave.getString("calculator2", ""))
        toggleGroup.check(calculatorSave.getInt("toggle", togglePlus.id))
        val adapter = ArrayAdapter(
                context!!,
                R.layout.dropdown_menu_popup_item,
                listCustomBin)
        spinner.setText(calculatorSave.getInt("spinner1", 10).toString())
        spinner2.setText(calculatorSave.getInt("spinner2", 2).toString())
        spinner3.setText(calculatorSave.getInt("spinner3", 10).toString())
        spinner.setAdapter(adapter)
        spinner2.setAdapter(adapter)
        spinner3.setAdapter(adapter)
        a = ConvertTo().main(editTextVal1.text.toString(), spinner.text.toString().toInt(), 10).toBigDecimal()
        b = ConvertTo().main(editTextVal2.text.toString(), spinner2.text.toString().toInt(), 2).toBigDecimal()
        calculateToggle()
    }

    private fun save() {
        val sharedPref = context!!.getSharedPreferences("calculator", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("calculator1", editTextVal1.text.toString()).apply()
        editor.putString("calculator2", editTextVal2.text.toString()).apply()
        editor.putInt("toggle", toggleGroup.checkedButtonId).apply()
        editor.putInt("spinner1", spinner.text.toString().toInt()).apply()
        editor.putInt("spinner2", spinner2.text.toString().toInt()).apply()
        editor.putInt("spinner3", spinner3.text.toString().toInt()).apply()
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        load()
    }

    override fun onPause() {
        super.onPause()
        save()
    }
}
