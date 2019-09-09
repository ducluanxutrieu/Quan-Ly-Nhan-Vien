package com.ducluanxutrieu.quanlynhanvien.models

class Auth {
    var email: String? = null
    var name: String? = null
    var uid: String? = null

    constructor() {}

    constructor(email: String, name: String, uid: String) {
        this.email = email
        this.name = name
        this.uid = uid
    }

    override fun toString(): String {
        return "Auth{" +
                "email='" + email + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", uid='" + uid + '\''.toString() +
                '}'.toString()
    }
}
