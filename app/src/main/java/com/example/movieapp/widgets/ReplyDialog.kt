package com.example.movieapp.widgets

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.CommentDialogLayoutBinding
import com.example.movieapp.ui.comment.CommentFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import gun0912.tedimagepicker.builder.TedImagePicker

class ReplyDialog(
    context: Context,
    userName: CharSequence? = null,
    private val callback: (String) -> Unit
) : BottomSheetDialog(context) {

    var uri: Uri? = null

    fun setImage(uri: Uri) {
        this.uri = uri
        Glide.with(context)
            .load(uri)
            .into(binding.img)
        binding.imageview.visibility = View.VISIBLE
        binding.icRemove.visibility = View.VISIBLE
    }

    private var binding: CommentDialogLayoutBinding =
        CommentDialogLayoutBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(binding.root)

        window?.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        // Ép bottom sheet mở full height
        setOnShowListener {
            val bottomSheet =
                findViewById<ViewGroup>(com.google.android.material.R.id.design_bottom_sheet)
//            bottomSheet?.layoutParams?.ww = ViewGroup.LayoutParams.MATCH_PARENT
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }

        binding.dialogCommentBt.setOnClickListener {
            callback.invoke(binding.dialogCommentEt.text.toString())
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

        if (userName != null) {
            binding.dialogCommentEt.hint = "Trả lời $userName"
        }

        binding.dialogCommentEt.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.dialogCommentEt, InputMethodManager.SHOW_IMPLICIT)

        binding.icRemove.setOnClickListener {
            binding.imageview.visibility = View.GONE
            binding.icRemove.visibility = View.GONE
        }
        binding.pickImage.setOnClickListener {
            TedImagePicker.with(context)
                .start { uri ->
                    setImage(uri)
                }
        }
    }
}
