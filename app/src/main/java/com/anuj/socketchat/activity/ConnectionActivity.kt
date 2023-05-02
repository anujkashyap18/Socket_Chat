package com.anuj.socketchat.activity

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anuj.socketchat.databinding.ActivityConnectionBinding

class ConnectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectionBinding

    var showIPaddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        showIPaddress = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)

        binding.showIPtextId.text = showIPaddress

        binding.connectButton.setOnClickListener { view ->
            if (Patterns.IP_ADDRESS.matcher(binding.ipEditText.text!!).matches()) {
                val info: String = getInfo()
                val intent = Intent(this@ConnectionActivity, ChatActivity::class.java)
                intent.putExtra("ip&port", info)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            } else {
                val toast =
                    Toast.makeText(this, "Please Enter a Valid IP Address", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    private fun getInfo(): String {
        return binding.ipEditText.text.toString() + " " + binding.portEditText.text.toString() + " " + binding.myPortEditText.text.toString()
    }
}