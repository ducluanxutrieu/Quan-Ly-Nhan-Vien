package com.ducluanxutrieu.quanlynhanvien.models

import java.io.Serializable

class RequestItem : Serializable {
    var startDate: String? = null
    var endDate: String? = null
    var thanDays: Int = 0
    var timeRequest: String? = null
    var name: String? = null
    var text: String? = null
    var offType: String? = null
    var isAccept: Boolean = false
    var requestKey: String? = null
    var uid: String? = null
    var note: String? = null

    constructor() {}

    constructor(startDate: String, endDate: String, thanDays: Int, timeRequest: String, name: String, text: String, offType: String, accept: Boolean, uid: String) {
        this.startDate = startDate
        this.endDate = endDate
        this.thanDays = thanDays
        this.timeRequest = timeRequest
        this.name = name
        this.text = text
        this.offType = offType
        this.isAccept = accept
        this.uid = uid
    }
}
