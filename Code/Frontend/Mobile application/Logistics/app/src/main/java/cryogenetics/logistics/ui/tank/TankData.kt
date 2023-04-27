package cryogenetics.logistics.ui.tank

import android.os.Parcel
import android.os.Parcelable

data class TankData(
    val address: String?,
    val client_id: String?,
    val client_name: String?,
    val comment: String?,
    val container_model_name: String?,
    val container_sr_number: String?,
    val container_status_name: String?,
    val invoice: String?,
    val last_filled: String?,
    val liter_capacity: String?,
    val location_id: String?,
    val location_name: String?,
    val maintenance_needed: String?,
    val production_date: String?,
    val refill_interval: String?,
    val temp_id: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<TankData> {
        override fun createFromParcel(parcel: Parcel): TankData {
            return TankData(parcel)
        }

        override fun newArray(size: Int): Array<TankData?> {
            return arrayOfNulls(size)
        }
    }
}