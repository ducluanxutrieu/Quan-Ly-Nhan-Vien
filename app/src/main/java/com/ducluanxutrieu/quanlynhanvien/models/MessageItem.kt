package com.ducluanxutrieu.quanlynhanvien.models

class MessageItem {
    var text: String? = null
    var email: String? = null
    var name: String? = null
    var uid: String? = null
    var picture: String? = null
    var timeStamp: String? = null

    constructor() {}

    constructor(text: String, email: String, name: String, uid: String, picture: String, timeStamp: String) {
        this.text = text
        this.email = email
        this.name = name
        this.uid = uid
        this.picture = picture
        this.timeStamp = timeStamp
    }

    override fun toString(): String {
        return "MessageItem{" +
                "text='" + text + '\''.toString() +
                ", email='" + email + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", uid='" + uid + '\''.toString() +
                ", picture='" + picture + '\''.toString() +
                ", timeStamp='" + timeStamp + '\''.toString() +
                '}'.toString()
    }
}

