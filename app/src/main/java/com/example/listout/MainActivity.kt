package com.example.listout

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {


    private lateinit var editTextTask: EditText
    private lateinit var buttonAdd: Button
    private lateinit var listViewTasks: ListView
    private lateinit var tasks: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBarColor)

        // Initialize views
        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        listViewTasks = findViewById(R.id.listViewTasks)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("tasks", Context.MODE_PRIVATE)

        // Load tasks from SharedPreferences
        tasks = ArrayList(sharedPreferences.getStringSet("tasks", HashSet()) ?: HashSet())

        // Initialize adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        listViewTasks.adapter = adapter

        // Add task button click listener
        buttonAdd.setOnClickListener {
            val task = editTextTask.text.toString().trim()
            if (task.isNotEmpty()) {
                addTask(task)
                editTextTask.text.clear()
            }
        }

        // ListView item click listener (for editing tasks)
        listViewTasks.setOnItemClickListener { _, _, position, _ ->
            showEditDialog(position)
        }

        // ListView item long click listener (for deleting tasks)
        listViewTasks.setOnItemLongClickListener { _, _, position, _ ->
            showDeleteDialog(position)
            true // Indicates that the long click event is consumed
        }
    }

    override fun onStop() {
        super.onStop()
        // Save tasks to SharedPreferences when the activity is stopped
        val editor = sharedPreferences.edit()
        editor.putStringSet("tasks", HashSet(tasks))
        editor.apply()
    }

    private fun addTask(task: String) {
        tasks.add(task)
        adapter.notifyDataSetChanged()
    }

    // Function to show edit dialog
    private fun showEditDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Task")

        val input = EditText(this)
        input.setText(tasks[position])
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val editedTask = input.text.toString().trim()
            if (editedTask.isNotEmpty()) {
                tasks[position] = editedTask
                adapter.notifyDataSetChanged()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    // Function to show delete dialog
    private fun showDeleteDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { dialog, _ ->
                tasks.removeAt(position)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}