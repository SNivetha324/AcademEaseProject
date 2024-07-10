package com.ac.maduidesigns.Model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

data class todomodel(
    var id: String = "",
    var taskId: String? = null,
    var name: String? = null,
    var dueDate: String? = null,
    var dueTime: String? = null,
    var repeatType: String? = null,
    var status: Boolean = false
)




