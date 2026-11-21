package com.ute.studentmvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {

    val allStudents: LiveData<List<Student>> = repository.allStudents.asLiveData(Dispatchers.IO)

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    fun insert(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insert(student)
                _statusMessage.postValue("Thêm sinh viên thành công: ${student.fullName}")
            } catch (e: Exception) {
                _statusMessage.postValue("Lỗi khi thêm sinh viên: ${e.message}")
            }
        }
    }

    fun delete(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.delete(student)
                _statusMessage.postValue("Đã xóa sinh viên: ${student.fullName}")
            } catch (e: Exception) {
                _statusMessage.postValue("Lỗi khi xóa sinh viên: ${e.message}")
            }
        }
    }

    fun syncFromCloud() {
        viewModelScope.launch(Dispatchers.IO) {
            _statusMessage.postValue("Đang đồng bộ dữ liệu từ đám mây...")
            val result = repository.syncFromCloud()
            result.onSuccess { count ->
                _statusMessage.postValue("Đồng bộ thành công $count sinh viên từ Cloud")
            }.onFailure { error ->
                _statusMessage.postValue("Đồng bộ thất bại: ${error.localizedMessage ?: error.message}")
            }
        }
    }
}
