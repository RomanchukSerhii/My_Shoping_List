package com.example.myshopinglist.presentation

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopinglist.R
import com.example.myshopinglist.presentation.adapter.ShopListAdapter
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("setErrorInput")
fun bindSetErrorInput(editText: TextInputEditText, isHasError: Boolean) {
    if (isHasError) {
        editText.requestFocus()
        editText.error = editText.context.getString(R.string.field_error)
    } else {
        editText.error = null
    }
}

@BindingAdapter("textNumbersToString")
fun bindNumbersToString(editText: TextInputEditText, number: Int) {
    editText.setText(number.toString())
}