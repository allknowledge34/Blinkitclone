
# Blinkit Clone – Grocery Delivery App

A fully functional Blinkit-style grocery delivery Android application built using **Kotlin**, **MVVM architecture**, **Firebase**, and **PhonePe UPI integration**. The app supports real-time OTP authentication, persistent cart, product browsing, live order tracking, and an admin dashboard to manage products and orders.


## Demo

![App Demo](app/src/main/res/drawable/demo.gif)


## Features

### 👤 User Side
- 📲 **OTP-based Phone Authentication** using Firebase
- 🛍️ **Browse Products** by Category, Name, or Price
- 🔎 **Search Functionality** for quick product discovery
- 🛒 **Add to Cart** with persistent storage (SharedPreferences)
- 📦 **Order Flow** with real-time status: Order -> Received -> Dispatched -> Delivered
- 💳 **UPI Payment** using PhonePe SDK
- 🏠 **Manage Address/Profile**
- 🕘 **Past Orders View**

### 🛠️ Admin Side
- ➕ Add/Edit/Delete Products (Name, Price, Stock, Category)
- 📊 View All Orders with Details
- 🔁 Update Order Status (Order, Received, Dispatched, Delivered)
- 📦 Track Inventory & Order Flow




## Admin Blinkit Clone App
#### https://github.com/allknowledge34/AdminBlinkitClone.git


## Tech Stack

- **Kotlin** – Primary language
- **MVVM Architecture** – Clean separation of concerns
- **Firebase Auth** – Phone Number OTP login
- **Firebase Realtime Database** – Dynamic data sync
- **Firebase Storage** – Store product images
- **SharedPreferences** – Persistent local cart storage
- **PhonePe SDK** – Real-time UPI Payments
- **Material UI** – Clean, modern Android design


## Installation

```bash
  git clone https://github.com/allknowledge34/Blinkitclone.git
```
    
## License

[MIT](https://choosealicense.com/licenses/mit/) :- This project is licensed under the MIT License - see the LICENSE file for details.

