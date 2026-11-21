package com.ute.studentmvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ute.studentmvvm.R
import com.ute.studentmvvm.data.AppDatabase
import com.ute.studentmvvm.databinding.FragmentStudentListBinding
import com.ute.studentmvvm.model.Student
import com.ute.studentmvvm.repository.StudentRepository
import com.ute.studentmvvm.ui.adapter.StudentAdapter
import com.ute.studentmvvm.viewmodel.StudentViewModel
import com.ute.studentmvvm.viewmodel.StudentViewModelFactory
import java.util.Locale

class StudentListFragment : Fragment() {

    private val viewModel: StudentViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = StudentRepository(database.studentDao())
        StudentViewModelFactory(repository)
    }
    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupToolbar()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            onStudentClick = { student ->
                showStudentDetailDialog(student)
            },
            onDeleteClick = { student ->
                showDeleteConfirmDialog(student)
            }
        )
        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sync -> {
                    viewModel.syncFromCloud()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            AddStudentDialogFragment().show(childFragmentManager, AddStudentDialogFragment.TAG)
        }
    }

    private fun observeViewModel() {
        viewModel.allStudents.observe(viewLifecycleOwner) { students ->
            val count = students.size
            studentAdapter.submitList(students)
            binding.tvStudentCountBadge.text = "$count SV"
            binding.layoutEmpty.isVisible = students.isEmpty()
            binding.rvStudents.isVisible = students.isNotEmpty()
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showStudentDetailDialog(student: Student) {
        val detailMessage = """
            Họ và tên: ${student.fullName}
            MSSV: ${student.studentCode}
            Email: ${student.email}
            Điểm GPA: ${String.format(Locale.US, "%.2f", student.gpa)}
            Ngành học: ${student.major}
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Chi Tiết Sinh Viên")
            .setMessage(detailMessage)
            .setPositiveButton("Đóng", null)
            .show()
    }

    private fun showDeleteConfirmDialog(student: Student) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xác Nhận Xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sinh viên \"${student.fullName}\" (MSSV: ${student.studentCode})?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.delete(student)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = StudentListFragment()
    }
}
