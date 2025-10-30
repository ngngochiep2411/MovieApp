package com.example.movieapp.ui.authen

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityRegisterBinding
import com.example.movieapp.ui.authen.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.register.setOnClickListener {
            if (binding.edtEmail.text.isEmpty()) {
                Toast.makeText(this, "Không được để trống email", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString())
                    .matches()
            ) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.edtUserName.text.isEmpty()) {
                Toast.makeText(this, "Không được để trống tài khoản", Toast.LENGTH_SHORT).show()
            } else if (binding.edtPassword.text.isEmpty()) {
                Toast.makeText(this, "Không được để trống mật khẩu", Toast.LENGTH_SHORT).show()
            } else if (binding.edtPasswordConfirm.text.isEmpty()) {
                Toast.makeText(this, "Không được để trống mật khẩu nhắc lại", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.edtPassword.text.toString() != binding.edtPasswordConfirm.text.toString()) {
                Toast.makeText(this, "2 mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.register(
                    binding.edtEmail.text.toString(),
                    binding.edtPassword.text.toString(),
                    binding.edtUserName.text.toString()
                )
            }
        }

        binding.tooglePassword.setOnClickListener {
            val password = binding.edtPassword
            if (password.transformationMethod == PasswordTransformationMethod.getInstance()) {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.tooglePassword.setImageResource(R.drawable.ic_show_password)
            } else {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.tooglePassword.setImageResource(R.drawable.ic_hide_password)
            }
            password.setSelection(password.text.length)
        }

        binding.tooglePasswordConfirm.setOnClickListener {
            val password = binding.edtPasswordConfirm
            if (password.transformationMethod == PasswordTransformationMethod.getInstance()) {
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.tooglePasswordConfirm.setImageResource(R.drawable.ic_show_password)
            } else {
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.tooglePasswordConfirm.setImageResource(R.drawable.ic_hide_password)
            }
            password.setSelection(password.text.length)
        }


        viewModel.toastMessage.observe(this@RegisterActivity) {
            Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.login.observe(this@RegisterActivity) {
            if (it) finish()
        }
    }
}