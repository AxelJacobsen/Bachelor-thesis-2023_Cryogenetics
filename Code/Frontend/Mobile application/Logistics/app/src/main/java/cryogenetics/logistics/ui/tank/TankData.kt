package cryogenetics.logistics.ui.tank

import android.os.Parcel
import android.os.Parcelable

data class TankData(
    var address: String?,
    var client_id: String?,
    val client_name: String?,
    var comment: String?,
    val container_model_name: String?,
    val container_sr_number: String?,
    var container_status_name: String?,
    var invoice: String?,
    var last_filled: String?,
    val liter_capacity: String?,
    var location_id: String?,
    val location_name: String?,
    var maintenance_needed: String?,
    val production_date: String?,
    val refill_interval: String?,
    val id: String?,
    var act: String? = "",
    var comment_for_act: String? = ""
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
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(client_id)
        parcel.writeString(client_name)
        parcel.writeString(comment)
        parcel.writeString(container_model_name)
        parcel.writeString(container_sr_number)
        parcel.writeString(container_status_name)
        parcel.writeString(invoice)
        parcel.writeString(last_filled)
        parcel.writeString(liter_capacity)
        parcel.writeString(location_id)
        parcel.writeString(location_name)
        parcel.writeString(maintenance_needed)
        parcel.writeString(production_date)
        parcel.writeString(refill_interval)
        parcel.writeString(id)
        parcel.writeString(act)
        parcel.writeString(comment_for_act)
    }

    override fun describeContents(): Int {
        return 0
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
