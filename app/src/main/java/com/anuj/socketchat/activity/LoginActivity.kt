package com.anuj.socketchat.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anuj.socketchat.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartChat.setOnClickListener {
            if (!TextUtils.isEmpty(binding.editName.text.toString())) {
              startActivity(Intent(this, ConnectionActivity::class.java))
            } else {
                Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}