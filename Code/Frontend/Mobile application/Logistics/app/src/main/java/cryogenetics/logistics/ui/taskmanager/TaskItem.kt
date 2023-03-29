package cryogenetics.logistics.ui.taskmanager

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment

/**
 *  Stores the data necessary for each "tab" in the task manager.
 */
data class TaskItem(
        var mTestText : String
        ) : Parcelable {

        // Parcelable default constructor
        constructor(parcel: Parcel) : this(
                parcel.readString().toString()
        ) {
        }

        // Parcelable default write
        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(mTestText)
        }

        // Parcelable default describe
        override fun describeContents(): Int {
                return 0
        }

        // Setup
        companion object CREATOR : Parcelable.Creator<TaskItem> {
                override fun createFromParcel(parcel: Parcel): TaskItem {
                        return TaskItem(parcel)
                }

                override fun newArray(size: Int): Array<TaskItem?> {
                        return arrayOfNulls(size)
                }
        }
}