package com.anuj.socketchat.activity

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anuj.socketchat.R
import com.anuj.socketchat.adapter.ChatAdapterRecycler
import com.anuj.socketchat.databinding.ActivityChatBinding
import com.anuj.socketchat.model.Message
import com.anuj.socketchat.sockets.ChatServer
import com.anuj.socketchat.sockets.FileServer
import java.io.PrintWriter
import java.net.Socket
import java.util.*

class ChatActivity : AppCompatActivity() {

    companion object{
        lateinit var binding : ActivityChatBinding
        var messageArray: ArrayList<Message>? = null

         lateinit var mMessageAdapter: ChatAdapterRecycler

         lateinit var mMessageRecycler: RecyclerView


        var serverIpAddress = ""

        var myport = 0
        var sendPort = 0
    }
    var TAG = "CLIENT ACTIVITY"

    var fileUp: ImageButton? = null
    var s: ChatServer? = null
    var f: FileServer? = null
    lateinit var ownIp: String
    var toolbar: Toolbar? = null
    private val exit = false
    private val REQUEST_CODE = 200
    lateinit var tv_title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageArray = java.util.ArrayList()
        mMessageRecycler = findViewById(R.id.message_list)
        mMessageAdapter = ChatAdapterRecycler(this, messageArray!!)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.stackFromEnd = true
        layoutManager.isSmoothScrollbarEnabled = true

        mMessageRecycler.layoutManager = layoutManager

        val bundle = intent.extras
        if (bundle != null) {
            val info = bundle.getString("ip&port")!!
            val infos = info!!.split(" ").toTypedArray()
            serverIpAddress = infos[0]
            sendPort = infos[1].toInt()
            myport = infos[2].toInt()
        }
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        ownIp = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)

        tv_title = findViewById(R.id.tv_title)
        tv_title.text = "Connection to $serverIpAddress"

        if (serverIpAddress != "") {
            s = ChatServer(
                ownIp,
                this,
                applicationContext,
                mMessageAdapter,
                mMessageRecycler,
                messageArray!!,
                myport,
                serverIpAddress
            )
            s!!.start()
            f = FileServer(
                applicationContext,
                mMessageAdapter,
                mMessageRecycler,
                messageArray!!,
                myport,
                serverIpAddress
            )
            f!!.start()
        }

        binding.ivsendmesaage.setOnClickListener { v ->
            if (!binding.etMessagetext.text.toString().isEmpty()) {
                val user:ChatActivity.User =
                    ChatActivity.User(
                        "1:" + binding.etMessagetext.text.toString()
                    )
                user.execute()
            } else {
                val toast = Toast.makeText(
                    applicationContext,
                    "Please write something",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class User internal constructor(var msg: String) :
        AsyncTask<Void?, Void?, String>() {
//        protected override fun doInBackground(vararg voids: Void): String {
//            try {
//                val ipadd: String = serverIpAddress
//                val portr: Int = sendPort
//                val clientSocket = Socket(ipadd, portr)
//                val outToServer = clientSocket.getOutputStream()
//                val output = PrintWriter(outToServer)
//                output.println(msg)
//                output.flush()
//                clientSocket.close()
//                runOnUiThread { binding.ivsendmesaage.setEnabled(false) }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return msg
//        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                val ipadd: String = serverIpAddress
                val portr: Int = sendPort
                val clientSocket = Socket(ipadd, portr)
                val outToServer = clientSocket.getOutputStream()
                val output = PrintWriter(outToServer)
                output.println(msg)
                output.flush()
                clientSocket.close()
//                runOnUiThread { binding.ivsendmesaage.setEnabled(false) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return msg
        }

        override fun onPostExecute(result: String) {
            var result = result

//            runOnUiThread { binding.ivsendmesaage.isEnabled = true }
//            Log.i(TAG, "on post execution result => $result")
            val stringBuilder = StringBuilder(result)
            if (stringBuilder[0] == '1' && stringBuilder[1] == ':') {
                stringBuilder.deleteCharAt(0)
                stringBuilder.deleteCharAt(0)
                result = stringBuilder.toString()
                messageArray!!.add(Message(result, 0, Calendar.getInstance().time))
                mMessageRecycler.adapter = mMessageAdapter
                binding.etMessagetext.setText("")
            }
        }


    }
}