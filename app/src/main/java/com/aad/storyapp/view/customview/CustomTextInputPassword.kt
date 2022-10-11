package com.aad.storyapp.view.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent
import com.aad.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/****************************************************
 * Created by Indra Muliana
 * On Wednesday, 28/09/2022 19.51
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

class CustomTextInputPassword : TextInputEditText {

    private var inputLayout: TextInputLayout? = null
    var isValid: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        inputLayout = getTextInputLayout()
    }

    private fun getTextInputLayout(): TextInputLayout? {
        var parent: ViewParent = parent
        while (parent is View) {
            if (parent is TextInputLayout) {
                return parent
            }
            parent = parent.getParent()
        }

        return null
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // TODO
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                validate(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                // TODO
            }

        })
    }

    fun validate(password: String): Boolean {
        isValid = when {
            password.isEmpty() -> {
                inputLayout?.error = context.getString(R.string.validation_password_required)
                false
            }
            password.length < 6 -> {
                inputLayout?.error = context.getString(R.string.validation_password_length)
                false
            }
            else -> {
                inputLayout?.error = ""
                true
            }
        }

        return isValid
    }

}