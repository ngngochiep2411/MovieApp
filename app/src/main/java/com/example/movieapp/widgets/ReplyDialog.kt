package com.example.movieapp.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.movieapp.databinding.CommentDialogLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class ReplyDialog(
    context: Context, userName: CharSequence? = null, private val callback: (String) -> Unit
) : BottomSheetDialog(context) {

    private var binding: CommentDialogLayoutBinding =
        CommentDialogLayoutBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(binding.root)

        binding.dialogCommentBt.setOnClickListener {
            callback.invoke(binding.dialogCommentEt.text.toString())
            this.dismiss()
        }
        binding.dialogCommentEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) {
                    binding.dialogCommentBt.alpha = 0.5F
                    binding.dialogCommentBt.isEnabled = false
                } else {
                    binding.dialogCommentBt.alpha = 1F
                    binding.dialogCommentBt.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        if (userName != null) {
            binding.dialogCommentEt.setHint("Trả lời $userName")
        }

        binding.dialogCommentEt.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(binding.dialogCommentEt, InputMethodManager.SHOW_IMPLICIT)
    }

}