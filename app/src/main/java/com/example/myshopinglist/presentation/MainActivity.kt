package com.example.myshopinglist.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopinglist.R
import com.example.myshopinglist.databinding.ActivityMainBinding
import com.example.myshopinglist.presentation.adapter.ShopListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener  {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter
    private var shopItemContainer: FragmentContainerView? = null
    private var isLandscapeMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shopItemContainer = binding.shopItemContainer
        if (shopItemContainer != null) {
            isLandscapeMode = true
            launchLandscapeMode()
        }

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setupRecyclerView()
        observeViewModel()
        setupListeners()
        contentResolver.query(
            Uri.parse("content://com.example.myshopinglist/shop_items"),
            null,
            null,
            null,
            null,
            null,
        )
    }

    private fun launchLandscapeMode() {
        launchFragment(ShopItemFragment.newInstanceAddItem())
    }

    private fun setupRecyclerView() {
        with(binding.recyclerView) {
            shopListAdapter = ShopListAdapter()
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.ENABLED_TYPE,
                ShopListAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.DISABLED_TYPE,
                ShopListAdapter.MAX_POOL_SIZE
            )
        }
    }

    private fun observeViewModel() {
        viewModel.shopItemList.observe(this) {
            shopListAdapter.submitList(it)
        }
    }

    private fun setupListeners() {
        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(binding.recyclerView)
        setupAddButtonListener()
    }

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = { shopItem ->
            if (isLandscapeMode) {
                launchFragment(ShopItemFragment.newInstanceEditItem(shopItem.id))
            } else {
                val intent = ShopItemActivity.newIntentEditItem(this, shopItem.id)
                startActivity(intent)
            }
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = { shopItem ->
            viewModel.changeEnabledState(shopItem)
        }
    }

    private fun setupSwipeListener(recyclerView: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val shopItem = shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(shopItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupAddButtonListener() {
        if (isLandscapeMode) {
            binding.addButton.setOnClickListener {
                launchFragment(ShopItemFragment.newInstanceAddItem())
            }
        } else {
            binding.addButton.setOnClickListener {
                val intent = ShopItemActivity.newIntentAddItem(this)
                startActivity(intent)
            }
        }
    }

    private fun launchFragment(fragment: ShopItemFragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onEditingFinished() {
        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack()
    }
}