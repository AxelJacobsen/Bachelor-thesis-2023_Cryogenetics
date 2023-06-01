package cryogenetics.logistics.ui.tank

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.ApiCalls
import cryogenetics.logistics.cameraQR.CamAccess
import cryogenetics.logistics.cameraQR.CameraFragment
import cryogenetics.logistics.databinding.FragmentTankBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.functions.JsonAdapter
import cryogenetics.logistics.ui.actLog.MiniActLogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class TankFragment(
    private var paramModel: Map<String, Any>? = null
) : Fragment() {

    private lateinit var inventoryData: List<Map<String, Any>>
    private lateinit var camFrag: CameraFragment

    private var swipeMALF: Fragment? = null
    private var dTank: TankData? = null
    private var _binding: FragmentTankBinding? = null
    private val binding get() = _binding!!
    private var menuOne: Boolean = false
    private var qrCodes: MutableList<String> = mutableListOf()
    private var initMiniActLog = false
    // These values are just for onResume
    private var activeSecondRow = 0
    private var xBottomDetails = false
    private var xFlTankCameraFragment = false
    private var xRightMenuAndContent = false
    private var xRightMenuSecondRow = false
    private var xSearchResult = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inventoryData = ApiCalls.fetchInventoryData() // Get Inventory data from user
        camFrag = CameraFragment(mOnFoundProductListener) // Initialize cameraFragment

        // Initialize the searchRecyclerView
        binding.recyclerSearchResult.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSearchResult.setHasFixedSize(true)

        // Create a list of references for searchRecyclerView
        val viewIds = listOf(
            R.id.tvInventoryNr,
            R.id.tvInventoryClient,
            R.id.tvInventoryLastFill,
            R.id.tvInventoryNoti
        )

        // When search button is pressed, search for input and display results.
        binding.bSearch.setOnClickListener {
            if (paramModel != null) { // This is bad solution to a weird issue :)
                initTankData(paramModel!!)
                paramModel = null
            } else
                searchAndUpdateSearchView(viewIds)
        }

        // When user makes an action which translates to 'I am done writing',
        // search for input and display results.
        binding.edSearchValue.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || event?.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                searchAndUpdateSearchView(viewIds)
                true // consume the event
            } else {
                false // don't consume the event
            }
        }

        // When cam-button is pressed, check camera permissions and start camFrag.
        binding.ibCamera.setOnClickListener {
            if (paramModel != null) { // This is bad solution to a weird issue :)
                initTankData(paramModel!!)
                paramModel = null
            } else {
                if (CamAccess.checkCameraPermission(requireContext())) {
                    binding.flTankCameraFragment.visibility = View.VISIBLE
                    qrCodes.clear()
                    if (!camFrag.onRes()) {
                        childFragmentManager.beginTransaction()
                            .replace(R.id.flTankCameraFragment, camFrag)
                            .commit()
                    }
                } else {
                    CamAccess.requestCameraPermission(requireActivity())
                }
            }
        }
        binding.ibCancelSearch.setOnClickListener {
            binding.searchResult.visibility = View.GONE
        }
        binding.firstRowFirst.setOnClickListener {
            menuFunctionality("transaction")
        }
        binding.firstRowSecond.setOnClickListener {
            menuFunctionality("maintenance")
        }
        binding.secondRowFirst.setOnClickListener {
            menuFunctionality("secondRowFirst")
        }
        binding.secondRowSecond.setOnClickListener {
            menuFunctionality("secondRowSecond")
        }
        binding.secondRowThird.setOnClickListener {
            menuFunctionality("secondRowThird")
        }
        binding.secondRowFourth.setOnClickListener {
            menuFunctionality("secondRowFourth")
        }
    }

    /**
     * Searches for value in inventoryData,
     * and creates/changes recyclerAdapter and makes result visible.
     *
     * @param viewIds - List of viewIds of the elements in the recycler.
     */
    private fun searchAndUpdateSearchView(viewIds: List<Int>) {
        // Search for value
        val searchRes = Functions.searchContainer(
            requireContext(),
            ApiCalls.fetchInventoryData(), // or: ApiCalls.fetchInventoryData()
            binding.edSearchValue.text.toString()
        )
        //Create adapter
        binding.recyclerSearchResult.adapter =
            JsonAdapter(searchRes, viewIds, R.layout.mini_inventory_recycler_item, mOnFoundProductListener)
        // Make searchResult visible
        binding.searchResult.visibility = View.VISIBLE
    }

    /**
     * Menu functionality gives different result depending on type specified.
     * The menu consists of two main categories:
     * Transaction (menuOne) [Un/Link, Send, Return, Internal]
     * Maintenance (!menuOne) [Maint, Refill, Dispose, Manual]
     * This is done to reuse the layouts in two different menus.
     *
     * Also manages the creation and replacing of childFragment - ActFragment
     * @param type - The type of menuFunctionality.
     */
    @SuppressLint("SetTextI18n") // Strings could be exported to res/values/string for different language support and etc, but it is not necessary yet for this project.
    private fun menuFunctionality(type: String) = when (type) {
        "transaction" -> { // Transaction
            changeMarginsAndUpdateMenu(
                View.VISIBLE,
                R.drawable.link,
                R.drawable.send_out,
                R.drawable.recieve_from_user,
                R.drawable.cryo_black_transp,
                "Link",
                "Send to Client",
                "Return from Client",
                "Internal Transfer"
            )
            menuOne = true
        }
        "maintenance" -> { // Maintenance
            changeMarginsAndUpdateMenu(
                View.VISIBLE,
                R.drawable.maint_color_menu,
                R.drawable.fill,
                R.drawable.dispose,
                R.drawable.manual,
                "Manage Maintenance",
                "Refill",
                "Dispose",
                "Manual Act"
            )
            menuOne = false
        }
        "secondRowFirst" -> {
            activeSecondRow = 1
            if (menuOne) // Linked / Unlinked
                bundleReplaceChild("Linked")
            else  // Manage Maintenance
                bundleReplaceChild("Maintenance")

        }
        "secondRowSecond" -> {
            activeSecondRow = 2
            if (menuOne) // Send to client
                bundleReplaceChild("Sent out")
            else // Refill
                bundleReplaceChild("Refilled")
        }
        "secondRowThird" -> {
            activeSecondRow = 3
            if (menuOne) // Return from client
                bundleReplaceChild("Returned")
            else // Dispose
                bundleReplaceChild("Dispose")
        }
        "secondRowFourth" -> {
            activeSecondRow = 4
            if (menuOne) // Internal Transfer
                bundleReplaceChild("Internal")
            else // Manual Act
                bundleReplaceChild("Manual")
        }
        else -> {
            // Go back to default view of menu
            activeSecondRow = 0
            changeMarginsAndUpdateMenu()
        }
    }

    /**
     * Common function used to start ActFragment, based on actRef.
     * @param actReference - The reference for the actFragment to configure its layout.
     */
    private fun bundleReplaceChild(actReference: String) {
        val bundle = Bundle()
        bundle.putParcelable("tankData", dTank)
        val childFragment = ActFragment(mOnFoundProductListener, actReference)
        childFragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(binding.menuInventory.id, childFragment)
            .commit()
    }

    /**
     * Changes margins of the menu and update the state of the tank-menu.
     *
     * @param visibility - 2nd-row's visibility
     * @param ivSecondRowFirst - Id/Int of the 1st drawable in 2nd-row's id.
     * @param ivSecondRowSecond - Id/Int of the 2nd drawable.
     * @param ivSecondRowThird - Id/Int of the 3rd drawable.
     * @param ivSecondRowFourth - Id/Int of the 4th drawable.
     * @param tvSecondRowFirst - String for the 1st textview.
     * @param tvSecondRowSecond - String for the 2nd textview.
     * @param tvSecondRowThird - String for the 3rd textview.
     * @param tvSecondRowFourth - String for the 4th textview.
     */
    private fun changeMarginsAndUpdateMenu(
        visibility: Int = View.GONE,
        ivSecondRowFirst: Int = R.drawable.cancel,
        ivSecondRowSecond: Int = R.drawable.cancel,
        ivSecondRowThird: Int = R.drawable.cancel,
        ivSecondRowFourth: Int = R.drawable.cancel,
        tvSecondRowFirst: String = "null",
        tvSecondRowSecond: String = "null",
        tvSecondRowThird: String = "null",
        tvSecondRowFourth: String = "null",
    ) {
        var btmMargin = 0 // BottomMargin is set to 0 and does not change if 2nd-row is visible.
        if (visibility == View.GONE) // If 2nd-row is NOT visible bottomMargin is set equal to marginTop.
            btmMargin = binding.firstRowFirst.marginTop // This is done to avoid converting density-independent-pixels to pixels.

        binding.rightMenuSecondRow.visibility = visibility

        binding.firstRowFirst.updateLayoutParams<ViewGroup.MarginLayoutParams> { // Change layout Params
            setMargins(
                leftMargin,
                topMargin,
                rightMargin,
                btmMargin
            ) // Sets layout-margins, parameters are in pixels.
        }
        binding.firstRowSecond.updateLayoutParams<ViewGroup.MarginLayoutParams> {// Change layout Params
            setMargins(
                leftMargin,
                topMargin,
                rightMargin,
                btmMargin
            ) // Sets layout-margins, parameters are in pixels.
        }

        if (ivSecondRowFirst != R.drawable.cancel) { // Avoids this block if a first drawable is still default.
            binding.ivSecondRowFirst.setImageResource(ivSecondRowFirst)
            binding.ivSecondRowSecond.setImageResource(ivSecondRowSecond)
            binding.ivSecondRowThird.setImageResource(ivSecondRowThird)
            binding.ivSecondRowFourth.setImageResource(ivSecondRowFourth)
            binding.tvSecondRowFirst.text = tvSecondRowFirst
            binding.tvSecondRowSecond.text = tvSecondRowSecond
            binding.tvSecondRowThird.text = tvSecondRowThird
            binding.tvSecondRowFourth.text = tvSecondRowFourth
        }
    }

    /**
     * Create an Listener for when the user selects a tank or is done selecting.
     */
    private val mOnFoundProductListener = object : OnItemClickListener {
        override fun onClick(model: Map<String, Any>) {
            initTankData(model)

            binding.searchResult.visibility = View.GONE
        }

        /**
         * This function is called each frame when a QR code is scanned,
         * to save resources we only search for once for each value entered.
         * It tries to search and alters the user if there is no data to search.
         * If a valid tank is found, change layout and pause camera.
         *
         * @param onFoundQR - A possible serial number found by QR-scanner.
         */
        override fun onFoundQR(serialNr: String) {
            if (qrCodes.contains(serialNr)) {
                return
            }
            qrCodes.add(serialNr)
            if (inventoryData.isNotEmpty()) { // If data is not empty,
                for (model in inventoryData) { // go through the data,
                    if (model.values.toString().contains(serialNr)) { // and search for serialNr.
                        initTankData(model) // Change selected Tank
                        // Set camera onPaus(e) and change layout by altering visibility.
                        binding.flTankCameraFragment.visibility = View.GONE
                        camFrag.onPaus()
                        break
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "No search value entered, or no Tanks added!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        /**
         * Update/init tank data with value sent as param, after act is performed.
         * @param tank - List of Map data, our common method to storing dbData.
         */
        override fun updateTankData(tank: List<Map<String, Any>>) {
            initTankData(tank[0])
        }

        /**
         * Override action when the camera is stopped, to hide cameraFrag.
         */
        override fun onStopCam() {
            binding.flTankCameraFragment.visibility = View.GONE
        }
    }

    /**
     * Initializes tankData, starts miniActLogFrag and changes values in the UI.
     *
     * @param tank - Map of data after searching after the tank.
     */
    private fun initTankData(tank: Map<String, Any>) {
        println("tankxy " + tank)
        dTank = TankData(
            address = tank.entries.find { it.key == "address" }?.value.toString(),
            client_id = tank.entries.find { it.key == "client_id" }?.value.toString(),
            client_name = tank.entries.find { it.key == "client_name" }?.value.toString(),
            comment = tank.entries.find { it.key == "comment" }?.value.toString(),
            container_model_name = tank.entries.find { it.key == "container_model_name" }?.value.toString(),
            container_sr_number = tank.entries.find { it.key == "container_sr_number" }?.value.toString(),
            container_status_name = tank.entries.find { it.key == "container_status_name" }?.value.toString(),
            invoice = getRightDate(tank.entries.find { it.key == "invoice" }?.value.toString()),
            last_filled = getRightDate(tank.entries.find { it.key == "last_filled" }?.value.toString()),
            liter_capacity = tank.entries.find { it.key == "liter_capacity" }?.value.toString(),
            location_id = tank.entries.find { it.key == "location_id" }?.value.toString(),
            location_name = tank.entries.find { it.key == "location_name" }?.value.toString(),
            maintenance_needed = tank.entries.find { it.key == "maintenance_needed" }?.value.toString(),
            production_date = tank.entries.find { it.key == "production_date" }?.value.toString(),
            refill_interval = tank.entries.find { it.key == "refill_interval" }?.value.toString(),
            id = tank.entries.find { it.key == "id" }?.value.toString()
        )

        // Start/replace fragment, if init has happened already, remove old Fragment first.
        if (initMiniActLog) {
            val swipe = childFragmentManager.findFragmentByTag("MALF")
                ?: throw RuntimeException("Could not find Tag")

            childFragmentManager.beginTransaction()
                .remove(swipe)
                .replace(R.id.miniLog, MiniActLogFragment(serialNr = dTank?.container_sr_number.toString()), "MALF")
                .commit()
            childFragmentManager.popBackStack()
        } else {
            childFragmentManager.beginTransaction()
                .replace(R.id.miniLog, MiniActLogFragment(serialNr = dTank?.container_sr_number.toString()), "MALF")
                .commit()
            initMiniActLog = true
        }
        println("TankDatax " + dTank)

        // Change the tanks values in the UI.
        binding.tvTankId.text = dTank?.id
        binding.tvTankStatus.text = dTank?.container_status_name
        binding.tvTankLocation.text = dTank?.address
        binding.tvAffiliatedLab.text = dTank?.location_name
        binding.tvTankClient.text = dTank?.client_name
        binding.tvTankLastFilled.text = dTank?.last_filled
        binding.tvTankNote.text = dTank?.comment
        binding.bottomDetails.visibility = View.VISIBLE
        binding.rightMenuAndContent.visibility = View.VISIBLE
    }

    /**
     * Translates/reformats dates from 'yyyy-MM-dd' to 'dd-MM-yyyy',
     *
     * @param inputString - The input in the format of the DB.
     * @return The result of date-reformat.
     */
    private fun getRightDate(inputString: String): String? {
        if (inputString == "0000-00-00" || inputString == "null") // Ensures that formatter doesn't fail if date = 00...
            return "null"

        val formatterDb = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatterAndroid = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = LocalDate.parse(inputString, formatterDb)
        return date.format(formatterAndroid)
    }

    /**
     * Override onPause to save values to keep the state of the fragment,
     * when switching between tabs. These values will be used when onResume is called.
     * MiniActLogFrag or MALF is removed by the fragmentManager, to then be replaced in onResume
     */
    override fun onPause() {
        super.onPause()
        xBottomDetails = binding.bottomDetails.visibility == View.VISIBLE
        xFlTankCameraFragment = binding.flTankCameraFragment.visibility == View.VISIBLE
        xRightMenuAndContent = binding.rightMenuAndContent.visibility == View.VISIBLE
        xRightMenuSecondRow = binding.rightMenuSecondRow.visibility == View.VISIBLE
        xSearchResult = binding.searchResult.visibility == View.VISIBLE

        if (initMiniActLog) {
            swipeMALF = childFragmentManager.findFragmentByTag("MALF")
                ?: throw RuntimeException("Could not find Tag")
            childFragmentManager.beginTransaction()
                .remove(swipeMALF!!)
                .commit()
            childFragmentManager.popBackStack()
        }
    }

    /**
     * Override onResume to restore the state of the fragment when switching between tabs.
     */
    override fun onResume() {
        super.onResume()
        // If a tank has been chosen, start miniLog fragment again...
        if (initMiniActLog) {
            // Restore layout by setting the visibility of these layouts.
            binding.bottomDetails.visibility = if (xBottomDetails) View.VISIBLE else View.GONE
            binding.flTankCameraFragment.visibility = if (xFlTankCameraFragment) View.VISIBLE else View.GONE
            binding.rightMenuAndContent.visibility = if (xRightMenuAndContent) View.VISIBLE else View.GONE
            binding.rightMenuSecondRow.visibility = if (xRightMenuSecondRow) View.VISIBLE else View.GONE
            binding.searchResult.visibility = if (xSearchResult) View.VISIBLE else View.GONE

            if (swipeMALF != null) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.miniLog, swipeMALF!!, "MALF")
                    .commit()
            }
            // ..and initialize tank values.
            binding.tvTankId.text = dTank?.id
            binding.tvTankStatus.text = dTank?.container_status_name
            binding.tvTankLocation.text = dTank?.address
            binding.tvTankClient.text = dTank?.client_name
            binding.tvTankLastFilled.text = dTank?.last_filled
            binding.tvAffiliatedLab.text = dTank?.location_name
            binding.tvTankNote.text = dTank?.comment
        }
        // Replicates menu by using the same pattern with stored values.
        if (menuOne)
            menuFunctionality("transaction")
         else
            menuFunctionality("maintenance")
        when (activeSecondRow) {
            0 -> menuFunctionality("else")
            1 -> menuFunctionality("secondRowFirst")
            2 -> menuFunctionality("secondRowSecond")
            3 -> menuFunctionality("secondRowThird")
            4 -> menuFunctionality("secondRowFourth")
        }
    }
}

