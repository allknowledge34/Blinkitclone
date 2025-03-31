package com.sachin.blinkitclone

interface CartListener {

    fun showCartLayout(itemCount : Int)

    fun savinCartItemCount(itemCount: Int)

    fun hideCartLayout()

}