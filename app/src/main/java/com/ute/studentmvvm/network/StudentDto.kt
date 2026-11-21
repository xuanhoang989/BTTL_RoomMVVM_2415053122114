package com.ute.studentmvvm.network

import com.google.gson.annotations.SerializedName
import com.ute.studentmvvm.model.Student

data class StudentDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("student_code")
    val studentCode: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("gpa")
    val gpa: Double,

    @SerializedName("major")
    val major: String = "Công Nghệ Thông Tin"
)

fun StudentDto.toEntity(): Student {
    return Student(
        id = this.id,
        fullName = this.fullName,
        studentCode = this.studentCode,
        email = this.email,
        gpa = this.gpa,
        major = this.major
    )
}
