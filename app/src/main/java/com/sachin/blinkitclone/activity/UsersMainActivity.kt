package com.sachin.blinkitclone.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sachin.blinkitclone.CartListener
import com.sachin.blinkitclone.adapters.AdapterCartProducts
import com.sachin.blinkitclone.databinding.ActivityMainBinding
import com.sachin.blinkitclone.databinding.BsCartProductBinding
import com.sachin.blinkitclone.roomdb.CartProducts
import com.sachin.blinkitclone.viewmodels.UserViewModel

class UsersMainActivity : AppCompatActivity() , CartListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var cartProductList: List<CartProducts>
    private lateinit var adapterCartProducts: AdapterCartProducts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllCartProducts()
        getTotalItemCountInCart()
        onCartClicked()
        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this){
            cartProductList = it
        }
    }

    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener {
            var bsCartProductBinding = BsCartProductBinding.inflate(LayoutInflater.from(this))
            bsCartProductBinding.btnNext.setOnClickListener {
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }

            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductBinding.root)

            bsCartProductBinding.tvNumaberOfProductCount.text = binding.tvNumaberOfProductCount.text

            adapterCartProducts = AdapterCartProducts()
            bsCartProductBinding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            bs.show()
        }
    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this){
            if (it > 0){
                binding.llCart.visibility= View.VISIBLE
                binding.tvNumaberOfProductCount.text = it.toString()
            }else{
                binding.llCart.visibility= View.GONE
            }
        }
    }

    override fun showCartLayout(itemCount : Int) {
        val previousCount = binding.tvNumaberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if (updatedCount > 0){
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumaberOfProductCount.text = updatedCount.toString()
        }
        else{
            binding.llCart.visibility = View.GONE
            binding.tvNumaberOfProductCount.text = "0"
        }
    }

    override fun savinCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this){
            viewModel.savinCartItemCount(it + itemCount)
        }

    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNumaberOfProductCount.text = "0"
    }

}