package com.sachin.blinkitclone.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.sachin.blinkitclone.CartListener
import com.sachin.blinkitclone.R
import com.sachin.blinkitclone.Utils
import com.sachin.blinkitclone.adapters.AdapterCartProducts
import com.sachin.blinkitclone.databinding.ActivityOrderPlaceBinding
import com.sachin.blinkitclone.databinding.AddressLayoutBinding
import com.sachin.blinkitclone.models.Orders
import com.sachin.blinkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject

class OrderPlaceActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityOrderPlaceBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private var cartListener : CartListener?=null
    private var grandTotalAmount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor()
        getAllCartProducts()
        backToUserMainActivity()
        onPlaceOrderClicked()

        Checkout.preload(applicationContext)
        val co = Checkout()

        co.setKeyID("You Api Key")
    }

    private fun onPlaceOrderClicked() {
        binding.btnNext.setOnClickListener {
            viewModel.getAddressStatus().observe(this){status->
                if (status){

                    initPayment()
                }
                else{
                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))
                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun initPayment() {

        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","BlinkitClone")
            options.put("description","Total Amount")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image",R.drawable.app_icon)
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
            options.put("amount",(grandTotalAmount*100).toString())

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 9);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","allknowledge34@gmail.com")
            prefill.put("contact","9999999999")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun saveOrder() {
        viewModel.getAll().observe(this){cartProductsList->
            if (cartProductsList.isNotEmpty()){
                viewModel.getUserAddress { address->
                    val order= Orders(
                        orderId = Utils.getRandomId(),
                        orderList = cartProductsList,
                        userAddress = address, orderStatus = 0, orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUserId()
                    )
                    viewModel.saveOrderedProducts(order)
                }
                for (products in cartProductsList){
                    val count = products.productCount
                    val stock = products.productStock?.minus(count!!)
                    if (stock != null){
                        viewModel.saveProductsAfterOrder(stock, products)
                    }
                    lifecycleScope.launch {
                        Utils.showToast(this@OrderPlaceActivity, message = "Order Saved")
                        viewModel.deleteCartProducts()
                        viewModel.savinCartItemCount(0)
                        cartListener?.hideCartLayout()

                        Utils.hideDialog()
                        startActivity(Intent(this@OrderPlaceActivity, UsersMainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {

        Utils.showDialog(this, "Processing..")
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etDescriptiveAddress.text.toString()

        val address = "$userPinCode, $userDistrict($userState), $userAddress, $userPhoneNumber"

        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
        }
        Utils.showToast(this, "Address Saved..")
        alertDialog.dismiss()

        initPayment()
    }

    private fun backToUserMainActivity() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this){cartProductList->
            adapterCartProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0

            for (products in cartProductList){
                val price = products.productPrice?.substring(1)?.toInt()

                val itemCount = products.productCount!!
                totalPrice += (price?. times(itemCount)!!)
            }
            binding.tvSubTotal.text = totalPrice.toString()

            if (totalPrice > 200){
                binding.tvDeliveryCharge.text = "₹20"
                totalPrice +=20
            }else{
                binding.tvDeliveryCharge.text ="₹50"
                totalPrice +=50
            }
            grandTotalAmount = totalPrice
            binding.tvGrandTotal.text = "₹$totalPrice"
        }
    }
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderPlaceActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
        saveOrder()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this, "Error: ${p1}", Toast.LENGTH_SHORT).show()
    }
}