package com.example.simpletodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FileUtils.writeLines
import org.apache.commons.io.IOUtils.readLines
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    var listOfTasks = mutableListOf<String>()
    lateinit var adapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onLongClickListener = object: TaskItemAdapter.OnLongClickListener {
            override fun onItemLongClicked(position: Int) {
                //1. remove the item from the list
                listOfTasks.removeAt(position)
                adapter.notifyDataSetChanged()
                saveItems()
            }
        }
        loadItems()

        //look up recyclerView in layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        //create adapter passing in the sample user data
        adapter = TaskItemAdapter(listOfTasks, onLongClickListener)


        //Attach the adapter to the recyclerview to populate items
        recyclerView.adapter = adapter

        //Set layout manager to position the items
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputTextField = findViewById<EditText>(R.id.addTaskField)

        findViewById<Button>(R.id.button).setOnClickListener {
            //1. Grab the text the user has inputted into @id/addTaskField

            findViewById<Button>(R.id.button).setOnClickListener {
                val userInputtedTask = inputTextField.text.toString()
                //2. Add string to our list of tasks: listOfTasks
                listOfTasks.add(userInputtedTask)

                //notify the adapter that our data has been updated
                adapter.notifyItemInserted(listOfTasks.size-1)

                //3. Reset text field
                inputTextField.setText("")

                saveItems()

            }

        }


    }
    // Save the data that the user has inputted
    // Save data by writing and reading from a file

    //Get the file we need
    fun getDataFile() : File {
        return File(filesDir, "data.txt")
    }

    fun loadItems() {
        try {

            listOfTasks = FileUtils.readLines(getDataFile(), Charset.defaultCharset())
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun saveItems(){
        try {

            FileUtils.writeLines(getDataFile(), listOfTasks)

        } catch(ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}