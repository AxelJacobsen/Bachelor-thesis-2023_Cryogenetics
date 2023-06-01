package cryogenetics.logistics.api

import android.os.Parcel
import android.os.Parcelable

data class ContainerModel(
    val container_model_name: String?,
    val liter_capacity: String?,
    val refill_interval: String?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(container_model_name)
        parcel.writeString(liter_capacity)
        parcel.writeString(refill_interval)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContainerModel> {
        override fun createFromParcel(parcel: Parcel): ContainerModel {
            return ContainerModel(parcel)
        }

        override fun newArray(size: Int): Array<ContainerModel?> {
            return arrayOfNulls(size)
        }
    }

}
