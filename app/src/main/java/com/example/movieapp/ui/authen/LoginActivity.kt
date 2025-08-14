package com.example.movieapp.ui.authen

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityLoginBinding
import com.example.movieapp.databinding.ActivityMain2Binding
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import com.example.movieapp.ui.auth.login.LoginViewModel
import com.example.movieapp.widgets.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        val spanned = Html.fromHtml(getString(R.string.dang_ky_text))
        val spannable = SpannableString(spanned)

        val target = "Đăng ký"
        val start = spanned.indexOf(target)
        val end = start + target.length

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = "#27c153".toColorInt()
            }

        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvRegister.text = spannable
        binding.tvRegister.movementMethod = LinkMovementMethod.getInstance()
        binding.tvRegister.highlightColor = Color.TRANSPARENT

        binding.btnLogin.setOnClickListener {
            viewModel.login(binding.edtEmail.text.toString(), binding.edtPassword.text.toString())
        }
        initObserver()
    }

    private fun initObserver() {
        viewModel.toastMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.login.observe(this) {
            if (it) {
                this.finish()
            }
        }
        viewModel.loading.observe(this) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
    }
}