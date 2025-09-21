package com.example.movieapp.ui.authen

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityLoginBinding
import com.example.movieapp.ui.auth.login.LoginViewModel
import com.example.movieapp.widgets.LoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id)) // Web client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

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
            val email = binding.edtEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Email không được để trống", Toast.LENGTH_SHORT)
                    .show()

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                Toast.makeText(this@LoginActivity, "Email không hợp lệ", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.edtPassword.text.toString().isEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Mật khẩu không được để trống",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.login(
                    binding.edtEmail.text.toString(),
                    binding.edtPassword.text.toString()
                )
            }

        }

        binding.loginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        binding.toogle.setOnClickListener {
            val password = binding.edtPassword
            if (password.transformationMethod == PasswordTransformationMethod.getInstance()) {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.toogle.setImageResource(R.drawable.ic_show_password)
            } else {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.toogle.setImageResource(R.drawable.ic_hide_password)
            }
            password.setSelection(password.text.length)
        }

        binding.back.setOnClickListener {
            finish()
        }
        initObserver()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                // Lấy email
                val email = account?.email
                val name = account?.displayName

                Log.d("GoogleSignIn", "User email: $email")
                Log.d("GoogleSignIn", "User name: $name")

                // Lấy idToken nếu muốn gửi server
                val idToken = account?.idToken
                Log.d("GoogleSignIn", "idToken: $idToken")
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
            }
        }
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