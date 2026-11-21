package com.ute.studentmvvm.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ute.studentmvvm.databinding.DialogAddStudentBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.viewmodel.StudentViewModel

class AddStudentDialogFragment : DialogFragment() {

    private val viewModel: StudentViewModel by activityViewModels()
    private var _binding: DialogAddStudentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddStudentBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Thêm", null)
            .setNegativeButton("Hủy") { d, _ ->
                d.dismiss()
            }
            .create()

        return dialog
    }

    override fun onResume() {
        super.onResume()
        val alertDialog = dialog as? AlertDialog ?: return
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (validateAndSave()) {
                dismiss()
            }
        }
    }

    private fun validateAndSave(): Boolean {
        val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
        val studentCode = binding.etStudentCode.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val gpaText = binding.etGpa.text?.toString()?.trim().orEmpty()

        if (fullName.isEmpty()) {
            binding.etFullName.error = "Vui lòng nhập họ và tên"
            binding.etFullName.requestFocus()
            return false
        }

        if (studentCode.isEmpty()) {
            binding.etStudentCode.error = "Vui lòng nhập mã sinh viên"
            binding.etStudentCode.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Vui lòng nhập email"
            binding.etEmail.requestFocus()
            return false
        }

        val gpa = gpaText.toDoubleOrNull()
        if (gpa == null || gpa < 0.0 || gpa > 4.0) {
            binding.etGpa.error = "Điểm GPA phải nằm trong khoảng 0.0 - 4.0"
            binding.etGpa.requestFocus()
            return false
        }

        val student = Student(
            fullName = fullName,
            studentCode = studentCode,
            email = email,
            gpa = gpa
        )
        viewModel.insert(student)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddStudentDialogFragment"
    }
}
