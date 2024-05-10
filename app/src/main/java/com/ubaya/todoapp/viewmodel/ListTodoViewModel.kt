package com.ubaya.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ubaya.todoapp.model.Todo
import com.ubaya.todoapp.model.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ListTodoViewModel(application: Application)
    : AndroidViewModel(application), CoroutineScope{
    val todoLD = MutableLiveData<List<Todo>>()
    val todoLoadErrorLD = MutableLiveData<Boolean>()
    val loadingLD = MutableLiveData<Boolean>()
    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    fun refresh() {
        loadingLD.value = true
        todoLoadErrorLD.value = false
            viewModelScope.launch {
            val db = TodoDatabase.buildDatabase(
                getApplication()
            )

            val todos = withContext(Dispatchers.IO) {
                db.todoDao().selectAllTodo()
            }
            todoLD.value = todos
            loadingLD.value = false
        }
    }

    fun clearTask(todo: Todo) {
        launch {
            val db = TodoDatabase.buildDatabase(
                getApplication()
            )
            db.todoDao().deleteTodo(todo)

            todoLD.postValue(db.todoDao().selectAllTodo())
        }
    }
}