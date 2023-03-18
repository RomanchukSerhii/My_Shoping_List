package com.example.myshopinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myshopinglist.R
import com.example.myshopinglist.databinding.FragmentShopItemBinding
import com.example.myshopinglist.domain.ShopItem
import kotlin.concurrent.fixedRateTimer

class ShopItemFragment(
    private val screenMode: String = MODE_UNKNOWN,
    private val shopItemId: Int = ShopItem.UNDEFINED_ID
): Fragment() {
    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding == null")

    private lateinit var viewModel: ShopItemViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseParams()
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        addTextChangedListeners()
        launchRightMode()
        observeViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            viewModel.shopItem.observe(viewLifecycleOwner) { shopItem ->
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
            viewModel.errorInputName.observe(viewLifecycleOwner) { isHasError ->
                if (isHasError) {
                    etName.requestFocus()
                    etName.error = getString(R.string.field_error)
                } else {
                    etName.error = null
                }
            }

            viewModel.errorInputCount.observe(viewLifecycleOwner) { isHasError ->
                if (isHasError) {
                    etCount.requestFocus()
                    etCount.error = getString(R.string.field_error)
                } else {
                    etCount.error = null
                }
            }

            viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
                activity?.onBackPressed()
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

    private fun parseParams() {
        if (screenMode != MODE_ADD && screenMode != MODE_EDIT) {
            throw RuntimeException("Param screen mode is absent")
        }
        if (screenMode == MODE_EDIT && shopItemId == ShopItem.UNDEFINED_ID) {
                throw RuntimeException("Shop item id is absent")
        }
    }

    companion object {
        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment(MODE_ADD)
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment(MODE_EDIT, shopItemId)
        }

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