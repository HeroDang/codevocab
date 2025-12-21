package com.group20.codevocab.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.group20.codevocab.databinding.ActivityConfirmOtpBinding

class ConfirmOtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOtpLogic()
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupOtpLogic() {
        // Logic tự động chuyển sang ô tiếp theo và tự động Confirm khi nhập đủ 6 số
        val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4, binding.etOtp5, binding.etOtp6)
        
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                    
                    // Nếu đã nhập ô cuối cùng
                    if (i == editTexts.size - 1 && s?.length == 1) {
                        val intent = Intent(this@ConfirmOtpActivity, NewPasswordActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}