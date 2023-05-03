package cryogenetics.logistics.ui.tank

interface OnItemClickListener {
    /**
     * When the user clicks on each row this method will be called to prepare update.
     */
    fun onClick(model: Map<String, Any>)
    fun onFoundQR(serialNr: String)
    fun onStopCam()
}