package com.ducluanxutrieu.quanlynhanvien.models

import java.io.Serializable

class Staff : Serializable {
    var name: String? = null
    var email: String? = null
    var phone: String? = null
    var position: String? = null
    var uid: String? = null
    var isAdmin: Boolean = false
    var avatarUrl: String? = null
    var password: String? = null

    constructor(name: String, email: String, phone: String, position: String, uid: String, avatarUrl: String, password: String, admin: Boolean) {
        this.name = name
        this.email = email
        this.phone = phone
        this.position = position
        this.uid = uid
        this.avatarUrl = avatarUrl
        this.password = password
        this.isAdmin = admin
    }

    constructor() {}

}
