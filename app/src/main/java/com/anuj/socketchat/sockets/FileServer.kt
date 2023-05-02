package com.anuj.socketchat.sockets

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.anuj.socketchat.adapter.ChatAdapterRecycler
import com.anuj.socketchat.model.Message
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class FileServer internal constructor(
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
    private val TAG = "FILE SERVER"
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
    }

    override fun run() {
        try {
            val fileSocket = ServerSocket(port + 1)
            Log.d(TAG, "run: " + fileSocket.localPort)
            fileSocket.reuseAddress = true
            println(TAG + "started")
            while (!interrupted()) {
                val connectFileSocket = fileSocket.accept()
                Log.d(TAG, "run: File Opened")
                val handleFile: receiveFiles = receiveFiles()
                handleFile.execute(connectFileSocket)
            }
            fileSocket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class receiveFiles :
        AsyncTask<Socket?, Void?, String?>() {
        var text: String? = null
//        protected override fun doInBackground(vararg sockets: Socket): String? {
//            try {
//                val testDirectory = Environment.getExternalStorageDirectory()
//                if (!testDirectory.exists()) testDirectory.mkdirs()
//                try {
//                    val inputStream = sockets[0].getInputStream()
//                    val dataInputStream = DataInputStream(inputStream)
//                    val fileName = dataInputStream.readUTF()
//                    val outputFile = File("$testDirectory/Download/", fileName)
//                    text = fileName
//                    val outputStream: OutputStream =
//                        BufferedOutputStream(FileOutputStream(outputFile))
//                    var fileSize = dataInputStream.readLong()
//                    var bytesRead: Int
//                    val byteArray = ByteArray(8192 * 16)
//                    while (fileSize > 0 && dataInputStream.read(
//                            byteArray,
//                            0,
//                            Math.min(byteArray.size.toLong(), fileSize).toInt()
//                        ).also {
//                            bytesRead = it
//                        } != -1
//                    ) {
//                        outputStream.write(byteArray, 0, bytesRead)
//                        fileSize -= bytesRead.toLong()
//                    }
//                    inputStream.close()
//                    dataInputStream.close()
//                    outputStream.flush()
//                    outputStream.close()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return text
//        }

        override fun doInBackground(vararg sockets: Socket?): String? {
            try {
                val testDirectory = Environment.getExternalStorageDirectory()
                if (!testDirectory.exists()) testDirectory.mkdirs()
                try {
                    val inputStream = sockets[0]!!.getInputStream()
                    val dataInputStream = DataInputStream(inputStream)
                    val fileName = dataInputStream.readUTF()
                    val outputFile = File("$testDirectory/Download/", fileName)
                    text = fileName
                    val outputStream: OutputStream =
                        BufferedOutputStream(FileOutputStream(outputFile))
                    var fileSize = dataInputStream.readLong()
                    var bytesRead: Int? = null
                    val byteArray = ByteArray(8192 * 16)
                    while (fileSize > 0 && dataInputStream.read(
                            byteArray,
                            0,
                            Math.min(byteArray.size.toLong(), fileSize).toInt()
                        ).also {
                            bytesRead = it
                        } != -1
                    ) {
                        outputStream.write(byteArray, 0, bytesRead!!)
                        fileSize -= bytesRead!!.toLong()
                    }
                    inputStream.close()
                    dataInputStream.close()
                    outputStream.flush()
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return text
        }

        override fun onPostExecute(result: String?) {
            Log.d(TAG, "onPostExecute: Result$result")
            if (result != null) {
                messageArray.add(
                    Message(
                        "New File Received: $result",
                        1,
                        Calendar.getInstance().time
                    )
                )
                messageList.adapter = mAdapter
            }
        }


    }
}