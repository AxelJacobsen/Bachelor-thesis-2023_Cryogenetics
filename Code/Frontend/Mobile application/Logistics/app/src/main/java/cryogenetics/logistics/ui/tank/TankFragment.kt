package cryogenetics.logistics.ui.tank

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.ApiCalls
import cryogenetics.logistics.cameraQR.CamAccess
import cryogenetics.logistics.cameraQR.CameraFragment
import cryogenetics.logistics.databinding.FragmentTankBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.ui.actLog.mini.MiniActLogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TankFragment : Fragment() {

    private lateinit var viewModel: TankViewModel
    private lateinit var inventoryData: List<Map<String, Any>>
    private lateinit var dTank: TankData
    private lateinit var camFrag: CameraFragment

    private var _binding: FragmentTankBinding? = null
    private val binding get() = _binding!!
    private var menuOne: Boolean = false
    private var qrCodes: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inventoryData = ApiCalls.fetchInventoryData()
        camFrag = CameraFragment(mOnFoundProductListener)
        binding.bSearch.setOnClickListener {
            val searchRes = Functions.searchContainer(
                requireContext(),
                inventoryData,
                binding.edSearchValue.text.toString()
            )

            // initialize the recyclerView
            binding.recyclerSearchResult.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerSearchResult.setHasFixedSize(true)

            //Create a list of references
            val viewIds = listOf(
                R.id.tvInventoryNr,
                R.id.tvInventoryClient,
                R.id.tvInventoryLastFill,
                R.id.tvInventoryNoti
            )
            //Create adapter
            binding.recyclerSearchResult.adapter =
                SearchAdapter(searchRes, viewIds, mOnFoundProductListener)
            binding.searchResult.visibility = View.VISIBLE
        }

        binding.ibCamera.setOnClickListener {
            if (CamAccess.checkCameraPermission(requireContext())) {
                binding.flTankCameraFragment.visibility = View.VISIBLE
                qrCodes.clear()
                if (!camFrag.onRes()) {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.flTankCameraFragment, camFrag)
                        .commit()
                }
                println("checkCameraPermission success") // TODO: LOG.D
            } else {
                println("checkCameraPermission fail") // TODO: LOG.D
                CamAccess.requestCameraPermission(requireActivity())
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
        childFragmentManager.beginTransaction()
            .replace(R.id.miniLog, MiniActLogFragment())
            .commit()
    }

    /**
     * This is a bit (:
     */
    @SuppressLint("SetTextI18n") // Strings could be exported to res/values/string for different language support and etc, but it is not necessary yet for this project.
    private fun menuFunctionality(type: String) = when (type) {
        "transaction" -> {
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
        "maintenance" -> {
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
            if (menuOne) { // Linked / Unlinked
                println("Linked / Unlinked")
                val bundle = Bundle()
                bundle.putParcelable("tankData", dTank)
                val childFragment2 = ActFragment(mOnFoundProductListener, "Linked")
                childFragment2.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment2)
                    .commit()

                println("Linked / Unlinked$dTank")

            } else { // Manage Maintenance
                println("Manage Maintenance")
                val bundle = Bundle()
                bundle.putParcelable("tankData", dTank)
                val childFragment1 = ActFragment(mOnFoundProductListener, "Maintenance")
                childFragment1.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment1)
                    .commit()

                println("Manage Maintenance$dTank")
            }
        }
        "secondRowSecond" -> {
            if (menuOne) { // Send to client
                println("Send to client")
                val bundle = Bundle()
                bundle.putParcelable("tankData", dTank)
                val childFragment2 = ActFragment(mOnFoundProductListener, "Sent out")
                childFragment2.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment2)
                    .commit()

                println("Send to client $dTank")

            } else { // Refill
                println("RefiRefilledll")
                val bundle = Bundle()
                bundle.putParcelable("tankData", dTank)
                val childFragment1 = ActFragment(mOnFoundProductListener, "Refilled")
                childFragment1.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment1)
                    .commit()

                println("Refilled $dTank")
            }
        }
        "secondRowThird" -> {
            if (menuOne) { // Return from client
                println("Return from client")
                val bundle = Bundle()
                bundle.putParcelable("tankData", dTank)
                val childFragment2 = ActFragment(mOnFoundProductListener, "Returned")
                childFragment2.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment2)
                    .commit()

                println("Return from client $dTank")

            } else { // Dispose
                println("Dispose")
                val bundle = Bundle()
                bundle.putParcelable("tankData", dTank)
                val childFragment1 = ActFragment(mOnFoundProductListener, "Dispose")
                childFragment1.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment1)
                    .commit()

                println("Dispose $dTank")
            }
        }
        "secondRowFourth" -> {
            if (menuOne) { // Internal Transfer
                println("Internal Transfer")
                val bundle = Bundle()
                bundle.putParcelable("tankData", dTank)
                val childFragment1 = ActFragment(mOnFoundProductListener, "Internal")
                childFragment1.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment1)
                    .commit()

                println("Internal Transfer $dTank")

            } else { // Manual Act
                println("Manual")
                val bundle = Bundle()
                // TODO: LOCK DOWN THIS MENU UNTIL A TANK IS CHOSEN, to avoid:
                // TODO: lateinit property dTank has not been initialized
                bundle.putParcelable("tankData", dTank)
                val childFragment = ActFragment(mOnFoundProductListener, "Manual")
                childFragment.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment)
                    .commit()

                println("Manual act$dTank")
            }
        }
        else -> {
            changeMarginsAndUpdateMenu()
        }
    }

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
            btmMargin =
                binding.firstRowFirst.marginTop // This is done to avoid converting density-independent-pixels to pixels.

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

    private val mOnFoundProductListener = object : OnItemClickListener {
        override fun onClick(model: Map<String, Any>) {
            println("MODELLO" + model)
            initTankData(model)

            binding.tvTankId.text = dTank.id
            binding.tvTankStatus.text = dTank.container_status_name
            binding.tvTankLocation.text = dTank.address
            binding.tvTankClient.text = dTank.client_name
            binding.tvTankLastFilled.text = dTank.last_filled
            binding.tvTankNote.text = dTank.comment
            binding.searchResult.visibility = View.GONE
            binding.bottomDetails.visibility = View.VISIBLE
            binding.rightMenuAndContent.visibility = View.VISIBLE
        }

        override fun onFoundQR(serialNr: String) {
            if (qrCodes.contains(serialNr)) {
                return
            }
            qrCodes.add(serialNr)
            if (inventoryData.isNotEmpty()) {
                for (model in inventoryData) {
                    if (model.values.toString().contains(serialNr)) {
                        initTankData(model)
                        binding.flTankCameraFragment.visibility = View.GONE
                        binding.bottomDetails.visibility = View.VISIBLE
                        binding.rightMenuAndContent.visibility = View.VISIBLE
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

        override fun updateTankData(tank: List<Map<String, Any>>) {
            initTankData(tank[0])
        }

        override fun onStopCam() {
            binding.flTankCameraFragment.visibility = View.GONE
        }
    }

    private fun initTankData(tank: Map<String, Any>) {
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

        binding.tvTankId.text = dTank.id
        binding.tvTankStatus.text = dTank.container_status_name
        binding.tvTankLocation.text = dTank.address
        binding.tvTankClient.text = dTank.client_name
        binding.tvTankLastFilled.text = dTank.last_filled
        binding.tvAffiliatedLab.text = dTank.location_name
        binding.tvTankNote.text = dTank.comment
    }

    private fun getRightDate(string: String): String? {
        if (string == "0000-00-00") { // Ensures that formatter doesn't fail if date = 00...
            Toast.makeText(
                requireContext(),
                "Failed to parse date: $string , 'Last filled' was set as today!", Toast.LENGTH_LONG
            ).show()
            return LocalDate.now().toString()
        }
        val formatterDb = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatterAndroid = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = LocalDate.parse(string, formatterDb)
        return date.format(formatterAndroid)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[TankViewModel::class.java]
        // TODO: Use the ViewModel
    }

}

