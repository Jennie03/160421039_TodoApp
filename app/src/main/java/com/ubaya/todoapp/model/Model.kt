package com.ubaya.todoapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    @ColumnInfo(name = "title")
    var title:String,
    @ColumnInfo(name = "notes")
    var notes:String,
    @ColumnInfo(name="priority")
    var priority:Int,
    @ColumnInfo(name="is_done")
    var is_done:Int,
    @ColumnInfo(name="todo_date")
    var todo_date:Int
    //when we add an attribute with boolean data type in database, it will change itself into tinyint.
    //might be because int consist of 0 and 1, which is easier to process than strings of chars.
)
{
    @PrimaryKey(autoGenerate = true)
    var uuid:Int = 0
}