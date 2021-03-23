package com.voxeet

import android.app.Activity
import android.view.View
import android.widget.Toast

fun View.visible() {
   visibility =  View.VISIBLE
}

fun View.hide() {
   visibility =  View.GONE
}

fun View.inVisible() {
   visibility = View.INVISIBLE
}

fun Activity.toast(message : String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}