package cryogenetics.logistics.ui.tankfill

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.ApiCalls
import cryogenetics.logistics.cameraQR.CamAccess
import cryogenetics.logistics.cameraQR.CameraFragment
import cryogenetics.logistics.databinding.FragmentTankFillingBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.functions.Functions.Companion.enforceNumberFormat
import cryogenetics.logistics.functions.JsonAdapter
import cryogenetics.logistics.ui.confirm.ConfirmFragment
import cryogenetics.logistics.ui.confirm.DetailsFragment
import cryogenetics.logistics.ui.inventory.MiniInventoryFragment
import cryogenetics.logistics.ui.tank.OnItemClickListener

class TankFillFragment : Fragment() {

    private lateinit var inventoryData: List<Map<String, Any>>
    private lateinit var camFrag: CameraFragment
    private lateinit var mAdapter: TankFillAdapter
    private lateinit var mListener: OnItemClickListener

    private val tankData: MutableList<Map<String, Any>> = mutableListOf()
    private var dList: MutableList<Map<String, Any>> = mutableListOf()
    private var _binding: FragmentTankFillingBinding? = null
    private val binding get() = _binding!!
    private var qrCodes: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTankFillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inventoryData = ApiCalls.fetchInventoryData()
        camFrag = CameraFragment(mOnFoundProductListener)
        mListener = mOnFoundProductListener

        binding.bSearch.setOnClickListener {
            val searchRes = Functions.searchContainer(
                requireContext(),
                ApiCalls.fetchInventoryData(),
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
                JsonAdapter(searchRes, viewIds, R.layout.mini_inventory_recycler_item, mOnFoundProductListener)
            binding.searchResult.visibility = View.VISIBLE
        }

        binding.ibCamera.setOnClickListener {
            if (CamAccess.checkCameraPermission(requireContext())) {
                binding.flFillFragment.visibility = View.VISIBLE
                childFragmentManager.beginTransaction()
                    .replace(R.id.flFillFragment, CameraFragment(mOnFoundProductListener))
                    .commit()
                println("checkSTORagePermission success") // TODO: LOG.D
            } else {
                println("checkCameraPermission fail") // TODO: LOG.D
                // TODO: TOAST
                CamAccess.requestCameraPermission(requireActivity())
            }
        }

        binding.clMenuFirst.setOnClickListener {
            tankData.clear()
            dataSetChange()
        }
        binding.clMenuSecond.setOnClickListener {
            tankData.remove(tankData.last())
            dataSetChange()
        }
        binding.clMenuThird.setOnClickListener {
            removeSelected()
        }
        binding.clMenuForth.setOnClickListener {

            if (tankData.size > 0) {
                childFragmentManager.beginTransaction()
                    .replace(
                        R.id.flConfirm, ConfirmFragment(
                            tankData, mOnFoundProductListener, "TankFillOverView"
                        ), "Conf"
                    )
                    .commit()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No tanks has been added yet.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.ibCancelSearch.setOnClickListener {
            binding.searchResult.visibility = View.GONE
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.flMiniInventoryRecycler, MiniInventoryFragment())
            .commit()

        // initialize the recyclerView
        binding.recyclerRefilledTanks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRefilledTanks.setHasFixedSize(true)

        //Create a list of references
        val viewIds = listOf(
            R.id.tvInventoryNr,
            R.id.tvInventoryClient,
            R.id.tvInventoryLastFill,
            R.id.tvInventoryNoti
        )
        //Create adapter
        mAdapter = TankFillAdapter(tankData, viewIds, mOnFoundProductListener)
        binding.recyclerRefilledTanks.adapter = mAdapter
        binding.recyclerRefilledTanks.visibility = View.VISIBLE
    }


    private fun addTankToList(model: Map<String, Any>) {
        binding.clRefilledTanks.visibility = View.VISIBLE
        if (tankData.contains(model) || model.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Could not add tank to list, check if it is already added!",
                Toast.LENGTH_LONG
            ).show()
            Log.d("TAG", "Could not add tank to list! Model: $model")
        } else {
            tankData.add(model)
            dataSetChange()
        }
    }

    private fun removeSelected() {
        for (item in dList)
            tankData.remove(item)

        dList.clear()
        dataSetChange()
    }

    private val mOnFoundProductListener = object : OnItemClickListener {
        override fun onClick(model: Map<String, Any>) {
            addTankToList(model)
        }

        override fun onClickTankFill(model: Map<String, Any>, ref: String) = when (ref) {
            "bComment" -> {
                println("onCommentClick bComment " + model)
            }
            "bDetails" -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.flConfirm, DetailsFragment(listOf( model ),
                        "Dtai", mListener), "Dtai")
                    .commit()
                println("onCommentClick bDetails " + model)
            }
            else -> {}
        }

        override fun onFoundQR(serialNr: String) {
            if (qrCodes.contains(serialNr))
                return

            qrCodes.add(serialNr)
            if (inventoryData.isNotEmpty()) {
                for (model in inventoryData) {
                    if (model.values.toString().contains(serialNr)) {
                        addTankToList(enforceNumberFormat(model))
                        
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

        override fun onStopCam() {
            binding.flFillFragment.visibility = View.GONE
        }

        override fun onChecked(map: Map<String, Any>, itemBool: Boolean) {
            if (itemBool)
                if (!dList.contains(map))
                    dList.add(map)
                else
                    if (dList.contains(map))
                        dList.remove(map)
        }

        override fun onCloseFragment(tag: String) {
            val swipe = childFragmentManager.findFragmentByTag(tag)
                ?: throw RuntimeException("Could not find Tag: $tag")

            childFragmentManager.beginTransaction()
                .remove(swipe)
                .commit()
            childFragmentManager.popBackStack()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun dataSetChange() {
        mAdapter.notifyDataSetChanged()
    }
}
