package com.example.movieapp.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.example.movieapp.R

class LoadingDialog(context: Context) : Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        setContentView(view)

        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(true)


    }

}