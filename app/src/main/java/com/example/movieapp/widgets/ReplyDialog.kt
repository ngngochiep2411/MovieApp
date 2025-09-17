package com.example.movieapp.widgets

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.CommentDialogLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import gun0912.tedimagepicker.builder.TedImagePicker

class ReplyDialog(
    context: Context,
    private var userName: String = "",
    private var callback: (String, uri: Uri?) -> Unit
) : BottomSheetDialog(context) {


    private var uri: Uri? = null

    fun setImage(uri: Uri?) {
        this.uri = uri
        Glide.with(context).load(uri).into(binding.img)
        binding.imageview.visibility = View.VISIBLE
        binding.icRemove.visibility = View.VISIBLE
    }

    fun setUserName(userName: String) {
        this.userName = userName
        binding.dialogCommentEt.hint = "Trả lời $userName"
    }

    fun setCallback(callback: (String, uri: Uri?) -> Unit) {
        this.callback = callback
    }

    fun clearAll() {
        binding.dialogCommentEt.text.clear()
        binding.imageview.visibility = View.GONE
    }

    private var binding: CommentDialogLayoutBinding =
        CommentDialogLayoutBinding.inflate(LayoutInflater.from(context))


    init {
        setContentView(binding.root)
        binding.dialogCommentBt.setOnClickListener {
            callback.invoke(binding.dialogCommentEt.text.toString(), uri)
            clearAll()
            this.dismiss()
        }

        binding.dialogCommentEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) {
                    binding.dialogCommentBt.alpha = 0.5F
                    binding.dialogCommentBt.isEnabled = false
                } else {
                    binding.dialogCommentBt.alpha = 1F
                    binding.dialogCommentBt.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.dialogCommentEt.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.dialogCommentEt, InputMethodManager.SHOW_IMPLICIT)

        binding.icRemove.setOnClickListener {
            binding.imageview.visibility = View.GONE
            binding.icRemove.visibility = View.GONE
        }
        binding.pickImage.setOnClickListener {
            TedImagePicker.with(context).start { uri ->
                setImage(uri)
            }
        }
    }
}
