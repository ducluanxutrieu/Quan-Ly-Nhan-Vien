package com.ducluanxutrieu.quanlynhanvien.models

import java.io.Serializable

class Task : Serializable {
    var taskContent: String? = null
    var taskTitle: String? = null
    var keyTask: String? = null

    constructor() {}

    constructor(taskTitle: String, taskContent: String) {
        this.taskContent = taskContent
        this.taskTitle = taskTitle
    }
}
