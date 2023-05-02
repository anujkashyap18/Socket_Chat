package com.anuj.socketchat.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anuj.socketchat.R
import com.anuj.socketchat.model.Message
import com.bumptech.glide.Glide
import java.io.File
import java.text.SimpleDateFormat

class ChatAdapterRecycler internal constructor(
    private val context: Context,
    arrayList: ArrayList<Message>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val arrayList: ArrayList<Message>

    init {
        this.arrayList = arrayList
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message = arrayList[position]
        if (message.isSent() && position != arrayList.size - 1) return VIEW_TYPE_MESSAGE_SENT else if (!message.isSent() && position != arrayList.size - 1) return VIEW_TYPE_MESSAGE_RECEIVED else if (message.isSent() && position == arrayList.size - 1) return LATEST_TYPE_MESSAGE_SENT else if (!message.isSent() && position == arrayList.size - 1) return LATEST_TYPE_MESSAGE_RECEIVED
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
//        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_from_bottom)
        when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                return SentMessageHolder(view)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                return ReceivedMessageHolder(view)
            }
            LATEST_TYPE_MESSAGE_SENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
    //            view.startAnimation(animation)
                return SentMessageHolder(view)
            }
            LATEST_TYPE_MESSAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
    //            view.startAnimation(animation)
                return ReceivedMessageHolder(view)
            }
            else -> return null!!
        }
    }

//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val message: Message = arrayList[position]
//        when (holder.itemViewType) {
//            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
//            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
//            LATEST_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
//            LATEST_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
//        }
//    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private inner class ReceivedMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageText: TextView
        var timeText: TextView
        var messageImage: ImageView
        var playButton: ImageView
        var pauseButton: ImageView
        var clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        init {
            messageText = itemView.findViewById(R.id.text_message_body)
            timeText = itemView.findViewById(R.id.text_message_time)
            messageImage = itemView.findViewById(R.id.received_image)
            playButton = itemView.findViewById(R.id.play_button)
            pauseButton = itemView.findViewById(R.id.pause_button)
            itemView.setOnClickListener { view: View? ->
                onClick(
                    view
                )
            }
            itemView.setOnLongClickListener { view: View? ->
                onLongclick(
                    view
                )
            }
        }

        fun bind(message: Message) {
            val sdf = SimpleDateFormat("hh:mm a")
            val currentDateTimeString = sdf.format(message.dateType)
            messageText.setText(message.message)
            timeText.text = currentDateTimeString
            Log.d(ContentValues.TAG, "bind: " + message.message)
            if (message.message.contains("New File Received: ") &&
                (message.message.contains("png") ||
                        message.message.contains("jpg") ||
                        message.message.contains("jpeg"))
            ) {
                val fileName: List<String> = message.message.split(":")
                Log.d(ContentValues.TAG, "bind: Received Message" + fileName[1])
                val stringBuilder = StringBuilder(fileName[1])
                stringBuilder.deleteCharAt(0)
                val path = stringBuilder.toString()
                val directory =
                    Environment.getExternalStorageDirectory().toString() + "/Download/" + path
                Log.d(ContentValues.TAG, "bind: Print Directory:$directory")
                val imgFile = File(directory)
                if (imgFile.exists()) {
                    Log.d(ContentValues.TAG, "bind: +Yeah Exists")
                    Glide.with(context)
                        .load(directory)
                        .override(500, 500)
                        .into(messageImage)
                }
            } else if (message.message.contains("New File Received: ") &&
                message.message.contains("mp3")
            ) {
                if (mediaPlayer.isPlaying) {
                    playButton.visibility = View.INVISIBLE
                    pauseButton.visibility = View.VISIBLE
                } else {
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.INVISIBLE
                }
            }
        }

        fun onClick(view: View?) {
            if (messageText.text.toString().contains(".mp3") && messageText.text.toString()
                    .contains("New File Received: ")
            ) {
                val message = messageText.text.toString().split(":").toTypedArray()
                Log.d(ContentValues.TAG, "onClick: " + message[1])
                var filename = message[1]
                filename = filename.trim { it <= ' ' }
                val path = Environment.getExternalStorageDirectory().toString() + "/Download/"
                println(ContentValues.TAG + "path and filename => " + path + filename)
                val uri = Uri.parse(path + filename)
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.INVISIBLE
                } else {
                    mediaPlayer = MediaPlayer.create(context, uri)
                    //Log.d(TAG, "onClick: " + context.getObbDir() + "/downloadFolder/" + path);
                    mediaPlayer.start()
                    playButton.visibility = View.INVISIBLE
                    pauseButton.visibility = View.VISIBLE
                }
            }
        }

        fun onLongclick(view: View?): Boolean {
            val clip = ClipData.newPlainText("Copied Text", messageText.text)
            clipboard.setPrimaryClip(clip)
            return true
        }
    }

    private inner class SentMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageText: TextView
        var timeText: TextView
        var messageImage: ImageView
        var playButton: ImageView
        var pauseButton: ImageView
        var clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        init {
            messageText = itemView.findViewById(R.id.send_message_body)
            timeText = itemView.findViewById(R.id.text_message_time)
            messageImage = itemView.findViewById(R.id.sent_image)
            playButton = itemView.findViewById(R.id.sent_play_button)
            pauseButton = itemView.findViewById(R.id.sent_pause_button)
            itemView.setOnClickListener { view: View? ->
                onClick(
                    view
                )
            }
            itemView.setOnLongClickListener { view: View? ->
                onLongclick(
                    view
                )
            }
        }

        fun bind(message: Message) {
            var newMessage: String = message.message
            val sdf = SimpleDateFormat("hh:mm a")
            val currentDateTimeString = sdf.format(message.dateType)
            if (message.message.contains("New File Sent: ") &&
                (message.message.contains("png") ||
                        message.message.contains("jpg") ||
                        message.message.contains("jpeg"))
            ) {
                val fileName: List<String> = message.message.split(":")
                Log.d(ContentValues.TAG, "bind: Received Message" + fileName[2])
                val newStringArr = newMessage.split(":").toTypedArray()
                newMessage = newStringArr[0] + newStringArr[1]
                Log.d(ContentValues.TAG, "bind: Directory:$newMessage")
                val directory = newStringArr[2]
                Log.d(ContentValues.TAG, "bind: Print Directory:$directory")
                val imgFile = File(directory)
                if (imgFile.exists()) {
                    Log.d(ContentValues.TAG, "bind: +Yeah Exists")
                    Glide.with(context)
                        .load(directory)
                        .override(500, 500)
                        .into(messageImage)
                }
            } else if (message.message.contains("New File Sent: ") &&
                message.message.contains("mp3")
            ) {
                if (mediaPlayer.isPlaying) {
                    playButton.visibility = View.INVISIBLE
                    pauseButton.visibility = View.VISIBLE
                } else {
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.INVISIBLE
                }
            }
            messageText.text = newMessage
            timeText.text = currentDateTimeString
        }

        fun onClick(view: View?) {
            if (messageText.text.toString().contains(".mp3") && messageText.text.toString()
                    .contains("New File Sent: ")
            ) {
                val message = messageText.text.toString().split(":").toTypedArray()
                Log.d(ContentValues.TAG, "onClick: " + message[2])
                var path = message[2]
                path = path.trim { it <= ' ' }
                val uri = Uri.parse(path)
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.INVISIBLE
                } else {
                    mediaPlayer = MediaPlayer.create(context, uri)
                    mediaPlayer.start()
                    playButton.visibility = View.INVISIBLE
                    pauseButton.visibility = View.VISIBLE
                }
            }
        }

        fun onLongclick(view: View?): Boolean {
            val clip = ClipData.newPlainText("Copied Text", messageText.text)
            clipboard.setPrimaryClip(clip)
            return true
        }
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
        private const val LATEST_TYPE_MESSAGE_SENT = 3
        private const val LATEST_TYPE_MESSAGE_RECEIVED = 4
        private var mediaPlayer = MediaPlayer()
    }

//    override fun onBindViewHolder(holder: Any, position: Int) {
//        val message: Message = arrayList[position]
//        when (holder) {
//            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
//            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
//            LATEST_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
//            LATEST_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
//        }
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message = arrayList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
            LATEST_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            LATEST_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }
}