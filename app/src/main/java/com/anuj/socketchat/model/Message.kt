package com.anuj.socketchat.model

import java.util.*

data class Message(val message:String,val dateType : Int, val sentAt: Date){
    fun isSent(): Boolean {
        return dateType == 0
    }
}
