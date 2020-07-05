package ua.geekhub.antosiukoleksii.fitchallenge.entity

data class GroupEntity (
    val admin:String,
    var startDate: Long,
    var endDate: Long,
    var users: List<String>
)
