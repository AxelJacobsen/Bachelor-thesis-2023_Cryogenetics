package cryogenetics.logistics.ui.tank

data class TankData(
    val address : String,
    val client_id : String,
    val client_name : String,
    val comment : String,
    val container_model_name : String,
    val container_sr_number : String,
    val container_status_name : String,
    val invoice : String,
    val last_filled : String,
    val liter_capacity : String,
    val location_id : String,
    val location_name : String,
    val maintenance_needed : String,
    val production_date : String,
    val refill_interval : String,
    val temp_id : String
)