package cryogenetics.logistics.ui.taskmanager

import androidx.fragment.app.Fragment

/**
 *  Stores the data necessary for each "tab" in the task manager.
 */
data class TaskItem(
        val name: String,
        val fragment: Fragment,
        val picRef : Int
)