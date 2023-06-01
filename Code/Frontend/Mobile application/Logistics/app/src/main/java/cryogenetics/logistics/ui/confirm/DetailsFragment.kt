package cryogenetics.logistics.ui.confirm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.databinding.FragmentDetailsBinding
import cryogenetics.logistics.functions.JsonAdapter
import cryogenetics.logistics.ui.actLog.ActLogFragment
import cryogenetics.logistics.ui.host.HostFragment
import cryogenetics.logistics.ui.tank.OnItemClickListener
import cryogenetics.logistics.ui.tank.TankFragment


class DetailsFragment(
    private val detailData: List<Map<String, Any>>,
    private val fragTag: String,
    private val mOnProductClickListener: OnItemClickListener? = null
) : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clComment.visibility = View.GONE

        binding.bCancel.setOnClickListener {
            mOnProductClickListener?.onCloseFragment(fragTag)
        }
        binding.bClose.setOnClickListener {
            mOnProductClickListener?.onCloseFragment(fragTag)
        }

        // initialize the recyclerView
        binding.inventoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.inventoryRecycler.setHasFixedSize(true)

        binding.clOpenTabsRow.visibility = View.VISIBLE
        binding.clConfirmCancelRow.visibility = View.GONE
        val serialNr = detailData[0].entries.find { it.key == "container_sr_number" }?.value.toString()
        val tankId = detailData[0].entries.find { it.key == "id" }?.value.toString()
        binding.tvTitle.text = "Details of $tankId"
        binding.tvDescription.text = "A more detailed view, at tank $serialNr"

        binding.bActLog.setOnClickListener {
            HostFragment.openAndAddFragment(HostFragment.returnHostFragment(), ActLogFragment(serialNr), "Log[$serialNr]", R.drawable.recent_transactions)
        }

        binding.bTankPage.setOnClickListener {
            HostFragment.openAndAddFragment(HostFragment.returnHostFragment(), TankFragment(detailData[0]), "Tank", R.drawable.tank)
        }

        //Create a list of references
        val viewIds = listOf(
            R.id.tvInventoryNr,
            R.id.tvInventoryClient,
            R.id.tvInventoryLocation,
            R.id.tvInventoryInvoice,
            R.id.tvInventoryLastFill,
            R.id.tvInventorySerialNr,
            R.id.tvInventoryNoti,
            R.id.tvInventoryStatus
        )
        //Create adapter
        binding.inventoryRecycler.adapter = JsonAdapter(
            detailData as MutableList<Map<String, Any>>, viewIds, R.layout.inventory_recycler_item_long)
        binding.inventoryData.visibility = View.VISIBLE
        binding.actLogData.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}