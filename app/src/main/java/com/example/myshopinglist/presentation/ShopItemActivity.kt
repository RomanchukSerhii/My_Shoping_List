package com.example.myshopinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myshopinglist.R
import com.example.myshopinglist.databinding.ActivityShopItemBinding
import com.example.myshopinglist.domain.ShopItem

class ShopItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopItemBinding
    private lateinit var viewModel: ShopItemViewModel

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        parseIntent()
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        addTextChangedListeners()
        launchRightMode()
        observeViewModels()
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun launchAddMode() {
        binding.apply {
            saveButton.setOnClickListener {
                viewModel.addShopItem(etName.text.toString(), etCount.text.toString())
            }
        }
    }

    private fun launchEditMode() {
        binding.apply {
            viewModel.getShopItem(shopItemId)
            viewModel.shopItem.observe(this@ShopItemActivity) { shopItem ->
                etName.setText(shopItem.name)
                etCount.setText(shopItem.count.toString())
            }
            saveButton.setOnClickListener {
                viewModel.editShopItem(etName.text?.toString(), etCount.text?.toString())
            }
        }
    }

    private fun observeViewModels() {
        binding.apply {
            viewModel.errorInputName.observe(this@ShopItemActivity) { isHasError ->
                Log.d("ShopItemActivity", isHasError.toString())
                if (isHasError) {
                    etName.requestFocus()
                    etName.error = getString(R.string.field_error)
                } else {
                    etName.error = null
                }
            }

            viewModel.errorInputCount.observe(this@ShopItemActivity) { isHasError ->
                if (isHasError) {
                    etCount.requestFocus()
                    etCount.error = getString(R.string.field_error)
                } else {
                    etCount.error = null
                }
            }

            viewModel.shouldCloseScreen.observe(this@ShopItemActivity) {
                finish()
            }
        }
    }

    private fun addTextChangedListeners() {
        binding.apply {
            etName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.resetInputNameError()
                }

                override fun afterTextChanged(s: Editable?) {}

            })

            etCount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.resetInputCountError()
                }

                override fun afterTextChanged(s: Editable?) {}

            })
        }
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode: $mode")
        }
        screenMode = mode

        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Shop item id is absent")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

    companion object {
        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }
    }
}