package com.example.movieapp.widgets

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.CommentDialogLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import gun0912.tedimagepicker.builder.TedImagePicker

class ReplyDialog(
    context: Context,
    private var userName: String = "",
    private var callback: (String, uri: Uri?) -> Unit,
    var uri: Uri? = null
) : BottomSheetDialog(context) {

    private var binding: CommentDialogLayoutBinding =
        CommentDialogLayoutBinding.inflate(LayoutInflater.from(context))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.dialogCommentEt.hint =
            if (userName.isEmpty()) "Thêm bình luận..." else "Trả lời $userName"

        binding.dialogCommentEt.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.dialogCommentEt, InputMethodManager.SHOW_IMPLICIT)
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

        binding.icRemove.setOnClickListener {
            binding.imageview.visibility = View.GONE
            binding.icRemove.visibility = View.GONE
        }
        binding.pickImage.setOnClickListener {
            TedImagePicker.with(context).start { uri ->
                setImage(uri)
            }
        }
        setImage(uri = uri)
    }

    fun setImage(uri: Uri?) {
        this.uri = uri
        Glide.with(context).load(uri).into(binding.img)
        binding.imageview.visibility = if (uri != null) View.VISIBLE else View.GONE
        binding.icRemove.visibility = View.VISIBLE
    }

    fun clearAll() {
        binding.dialogCommentEt.text.clear()
        binding.imageview.visibility = View.GONE
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.dialogCommentEt.windowToken, 0)
        super.setOnDismissListener(listener)
    }
}
