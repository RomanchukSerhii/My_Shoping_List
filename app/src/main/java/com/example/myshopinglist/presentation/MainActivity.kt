package com.example.myshopinglist.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopinglist.R
import com.example.myshopinglist.databinding.ActivityMainBinding
import com.example.myshopinglist.domain.ShopItem
import com.example.myshopinglist.presentation.adapter.ShopListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener  {

    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var binding: ActivityMainBinding
    private lateinit var shopListAdapter: ShopListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }
    private val component by lazy {
        (application as App).component
    }
    private var shopItemContainer: FragmentContainerView? = null
    private var isLandscapeMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shopItemContainer = binding.shopItemContainer
        if (shopItemContainer != null) {
            isLandscapeMode = true
            launchLandscapeMode()
        }

        setupRecyclerView()
        observeViewModel()
        setupListeners()
        getDbFromContentProvider()
    }

    private fun getDbFromContentProvider() {
        scope.launch {
            val cursor = contentResolver.query(
                Uri.parse("content://com.example.myshopinglist/shop_items"),
                null,
                null,
                null,
                null,
                null,
            )

            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("count"))
                val enabled = cursor.getInt(cursor.getColumnIndexOrThrow("enabled")) > 0

                val shopItem = ShopItem(
                    id = id,
                    name = name,
                    count = count,
                    enabled = enabled
                )

                Log.d("MainActivity", shopItem.toString())
            }

            cursor?.close()
        }

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