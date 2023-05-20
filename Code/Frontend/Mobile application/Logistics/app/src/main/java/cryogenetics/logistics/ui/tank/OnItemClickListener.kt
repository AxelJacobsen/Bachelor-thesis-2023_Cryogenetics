package cryogenetics.logistics.ui.tank

interface OnItemClickListener {
    /**
     * When the user clicks on each row this method will be called to prepare update.
     */
    fun onClick(model: Map<String, Any>) {}
    fun onFoundQR(serialNr: String) {}
    fun onStopCam() {}
    fun onChecked(map: Map<String, Any>, itemBool: Boolean) {}
    fun onClickTankFill(model: Map<String, Any>, ref: String) {}
    fun onCloseFragment(tag: String) {}
    fun displayActData(tag: String) {}
    fun updateTankData(tank: List<Map<String, Any>>) {}
}