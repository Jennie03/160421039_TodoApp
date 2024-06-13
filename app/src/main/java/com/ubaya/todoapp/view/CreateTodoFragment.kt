package com.ubaya.todoapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.ubaya.todoapp.R
import com.ubaya.todoapp.TodoWorker
import com.ubaya.todoapp.databinding.FragmentCreateTodoBinding
import com.ubaya.todoapp.model.Todo
import com.ubaya.todoapp.util.NotificationHelper
import com.ubaya.todoapp.viewmodel.DetailTodoViewModel
import java.util.concurrent.TimeUnit


class CreateTodoFragment : Fragment(), RadioClick, TodoEditClick {
    private lateinit var viewModel: DetailTodoViewModel
    private lateinit var binding: FragmentCreateTodoBinding

    override fun onTodoEditClick(v: View) {
        val workRequest = OneTimeWorkRequestBuilder<TodoWorker>()
            .setInitialDelay(30, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(
                    "title" to "Todo created",
                    "message" to "Stay focus"
                ))
            .build()
        WorkManager.getInstance(requireContext()).enqueue(workRequest)

        val list = listOf(binding.todo!!)
        viewModel.addTodo(list)
        Toast.makeText(view?.context, "Data added", Toast.LENGTH_LONG).show()
        Navigation.findNavController(v).popBackStack()
    }

    override fun onRadioClick(v: View, priority: Int, obj: Todo) {
        binding.todo?.priority = v.tag.toString().toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),NotificationHelper.REQUEST_NOTIF)
        }

        binding.todo = Todo("", "", 3, 0, 0)
        binding.radioListener = this
        binding.addListener = this

        viewModel = ViewModelProvider(this).get(DetailTodoViewModel::class.java)

        binding.btnAdd.setOnClickListener {
            var radio =
                view.findViewById<RadioButton>(binding.radioGroupPriority.checkedRadioButtonId)

            var todo = Todo(
                binding.txtTitle.text.toString(),
                binding.txtNotes.text.toString(),
                radio.tag.toString().toInt(), 0, 0
            )

            val list = listOf(todo)
            viewModel.addTodo(list)
            Toast.makeText(view.context, "Data added", Toast.LENGTH_LONG).show()
            Navigation.findNavController(it).popBackStack()

            val notif = NotificationHelper(view.context)
            notif.createNotification("Todo Created",
                "A new todo has been created! Stay focus!")

            val workRequest = OneTimeWorkRequestBuilder<TodoWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .setInputData(
                    workDataOf(
                        "title" to "Todo created",
                        "message" to "Stay focus"
                    ))
                .build()
            WorkManager.getInstance(requireContext()).enqueue(workRequest)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NotificationHelper.REQUEST_NOTIF) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                NotificationHelper(requireContext())
                    .createNotification(
                        "Todo Created",
                        "A new todo has been created! Stay focus!"
                    )

            }
        }
    }
}