package com.anuj.socketchat.sockets

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anuj.socketchat.R
import com.anuj.socketchat.adapter.ChatAdapterRecycler
import com.anuj.socketchat.model.Message
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class ChatServer internal constructor(
    private val ownIp: String,
    activity: Activity,
    context: Context,
    mAdapter: ChatAdapterRecycler,
    messageList: RecyclerView,
    messageArray: ArrayList<Message>,
    port: Int,
    serverIpAddress: String
) :
    Thread() {
    private val context: Context
    private val serverIpAddress: String
    private val activity: Activity
    private val TAG = "CHATSERVER"
    private val messageList: RecyclerView
    private val messageArray: ArrayList<Message>
    private val mAdapter: ChatAdapterRecycler
    private val port: Int

    init {
        this.messageArray = messageArray
        this.messageList = messageList
        this.mAdapter = mAdapter
        this.port = port
        this.context = context
        this.serverIpAddress = serverIpAddress
        this.activity = activity
    }

    @SuppressLint("SetTextI18n")
    override fun run() {
        try {
            val initSocket = ServerSocket(port)
            initSocket.reuseAddress = true
            var textView: TextView
            //textView = activity.findViewById(R.id.textView);
            // textView.setText("Server Socket Started at IP: " + ownIp + " and Port: " + port);
            // textView.setBackgroundColor(Color.parseColor("#39FF14"));
            println(TAG + "started")
            while (!interrupted()) {
                val connectSocket = initSocket.accept()
                val handle: receiveTexts = receiveTexts()
                handle.execute(connectSocket)
            }
            initSocket.close()
        } catch (e: IOException) {
            var textView: TextView
            /*  textView = activity.findViewById(R.id.textView);
            textView.setText("Server Socket initialization failed. Port already in use.");
            textView.setBackgroundColor(Color.parseColor("#FF0800"));*/e.printStackTrace()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class receiveTexts :
        AsyncTask<Socket?, Void?, String?>() {
        var text: String? = null
//        protected override fun doInBackground(vararg sockets: Socket): String? {
//
//        }
        override fun doInBackground(vararg sockets: Socket?): String? {
            try {
                val input = BufferedReader(InputStreamReader(sockets[0]!!.getInputStream()))
                text = input.readLine()
                Log.i(TAG, "Received => $text")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return text
        }

        override fun onPostExecute(result: String?) {
            var result = result
            Log.d(TAG, "onPostExecute: Result$result")
            if (result!![0] == '1' && result[1] == ':') {
                val stringBuilder = StringBuilder(result)
                stringBuilder.deleteCharAt(0)
                stringBuilder.deleteCharAt(0)
                result = stringBuilder.toString()
                messageArray.add(Message(result, 1, Calendar.getInstance().time))
                messageList.adapter = mAdapter
            } else {
                val stringBuilder = StringBuilder(result)
                stringBuilder.deleteCharAt(0)
                stringBuilder.deleteCharAt(0)
                result = stringBuilder.toString()
                val message_List: RecyclerView
                message_List = activity.findViewById(R.id.message_list)
                val layerDrawable = message_List.background as LayerDrawable
            }
        }




    }
}