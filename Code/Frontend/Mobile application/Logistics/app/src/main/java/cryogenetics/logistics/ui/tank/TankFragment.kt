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
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.databinding.FragmentTankBinding
import cryogenetics.logistics.ui.actLog.mini.MiniActLogFragment
import cryogenetics.logistics.ui.tank.tankMenu.ManualActFragment

class TankFragment : Fragment() {

    private var _binding : FragmentTankBinding? = null
    private val binding get() = _binding!!
    private var menuOne : Boolean = false
    private lateinit var viewModel: TankViewModel
    private lateinit var dTank : TankData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bSearch.setOnClickListener {
            val searchRes = searchContainer(fetchInventoryData(), binding.edSearchValue.text.toString())

            // initialize the recyclerView
            binding.searchResult?.layoutManager = LinearLayoutManager(requireContext())
            binding.searchResult?.setHasFixedSize(true)

            //Create a list of references
            val viewIds = listOf(
                R.id.tvInventoryNr,
                R.id.tvInventoryClient,
                R.id.tvInventoryLastFill,
                R.id.tvInventoryNoti
            )
            //Create adapter
            binding.searchResult?.adapter = SearchAdapter(searchRes, viewIds, mOnProductClickListener)
            binding.searchResult?.visibility = View.VISIBLE
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
        binding.secondRowFourth.setOnClickListener {
            menuFunctionality("secondRowFourth")
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.miniLog, MiniActLogFragment())
            .commit()
    }

    private fun searchContainer(
        fetchedData: List<Map<String, Any>>,
        searchValue: String = ""
    ): MutableList<Map<String, Any>> {
        val searchResults = mutableListOf<Map<String, Any>>()
        if (fetchedData.isNotEmpty() && searchValue != ""){
            for (model in fetchedData) {
                for (value in model.values) {
                    val result = value.toString().contains(binding.edSearchValue.text)
                    if (result) {
                        searchResults.add(model)
                        break
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "No search value entered, or no Tanks added!", Toast.LENGTH_LONG).show()
        }
        return searchResults
    }

    private fun fetchInventoryData() :  List<Map<String, Any>>{
        val urlDataString = Api.fetchJsonData("http://10.0.2.2:8080/api/container")
        return Api.parseJsonArray(urlDataString)
    }

    @SuppressLint("SetTextI18n") // Strings could be exported to res/values/string for different language support and etc, but it is not necessary yet for this project.
    private fun menuFunctionality(type: String) = when (type) {
        // TODO: LOCK DOWN THIS MENU UNTIL A TANK IS CHOSEN, to avoid:
        // TODO: lateinit property dTank has not been initialized
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
        "secondRowFourth" -> {
            if (menuOne) {
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, MiniActLogFragment())
                    .commit()
                println("internal transfer")

            } else {
                println("manueel")
                val bundle = Bundle()
                bundle.putParcelable("tankData", dTank)
                //bundle.putString("key", "value")
                val childFragment = ManualActFragment()
                childFragment.arguments = bundle
                childFragmentManager.beginTransaction()
                    .replace(binding.menuInventory.id, childFragment)
                    .commit()

                println("manual act$dTank")

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
        tvSecondRowFourth: String = "null"
    ) {
        var btmMargin = 0 // BottomMargin is set to 0 and does not change if 2nd-row is visible.
        if (visibility == View.GONE) // If 2nd-row is NOT visible bottomMargin is set equal to marginTop.
            btmMargin = binding.firstRowFirst.marginTop // This is done to avoid converting density-independent-pixels to pixels.

        binding.rightMenuSecondRow.visibility = visibility

        binding.firstRowFirst.updateLayoutParams<ViewGroup.MarginLayoutParams> { // Change layout Params
            setMargins(leftMargin, topMargin, rightMargin, btmMargin) // Sets layout-margins, parameters are in pixels.
        }
        binding.firstRowSecond.updateLayoutParams<ViewGroup.MarginLayoutParams> {// Change layout Params
            setMargins(leftMargin, topMargin, rightMargin, btmMargin) // Sets layout-margins, parameters are in pixels.
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

    private val mOnProductClickListener = object :OnItemClickListener {
        override fun onClick(model: Map<String, Any>) {
            println("MODELLO" + model)
            dTank = TankData(
                address = model.entries.find { it.key == "address" }?.value.toString(),
                client_id = model.entries.find { it.key == "client_id" }?.value.toString(),
                client_name = model.entries.find { it.key == "client_name" }?.value.toString(),
                comment = model.entries.find { it.key == "comment" }?.value.toString(),
                container_model_name = model.entries.find { it.key == "container_model_name" }?.value.toString(),
                container_sr_number = model.entries.find { it.key == "container_sr_number" }?.value.toString(),
                container_status_name = model.entries.find { it.key == "container_status_name" }?.value.toString(),
                invoice = model.entries.find { it.key == "invoice" }?.value.toString(),
                last_filled = model.entries.find { it.key == "last_filled" }?.value.toString(),
                // TODO:
                liter_capacity = model.entries.find { it.key == "liter_capacity" }?.value.toString(),
                location_id = model.entries.find { it.key == "location_id" }?.value.toString(),
                location_name = model.entries.find { it.key == "location_name" }?.value.toString(),
                maintenance_needed = model.entries.find { it.key == "maintenance_needed" }?.value.toString(),
                production_date = model.entries.find { it.key == "production_date" }?.value.toString(),
                refill_interval = model.entries.find { it.key == "refill_interval" }?.value.toString(),
                temp_id = model.entries.find { it.key == "temp_id" }?.value.toString()
            )
            binding.tvTankId.text = dTank.temp_id
            binding.tvTankStatus.text = dTank.container_status_name
            binding.tvTankLocation.text = dTank.address
            binding.tvTankClient.text = dTank.client_name
            binding.tvTankLastFilled.text = dTank.last_filled
            binding.tvTankNote.text = dTank.comment
            binding.searchResult?.visibility = View.INVISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TankViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

