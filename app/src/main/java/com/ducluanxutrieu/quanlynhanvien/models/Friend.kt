package com.ducluanxutrieu.quanlynhanvien.models

import java.io.Serializable

class Friend : Serializable {
    var name: String? = null
    var uid: String? = null
    var recentMessage: String? = null
    var avatarUrl: String? = null

    constructor() {}

    constructor(name: String, uid: String, recentMessage: String, avatarUrl: String) {
        this.name = name
        this.uid = uid
        this.recentMessage = recentMessage
        this.avatarUrl = avatarUrl
    }
}
