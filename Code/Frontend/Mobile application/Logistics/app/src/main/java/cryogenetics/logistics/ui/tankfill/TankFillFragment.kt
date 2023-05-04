package cryogenetics.logistics.ui.tankfill

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cryogenetics.logistics.R
import cryogenetics.logistics.databinding.FragmentTankFillingBinding
import cryogenetics.logistics.ui.inventory.mini.MiniInventoryFragment

class TankFillFragment : Fragment() {

    /*
    companion object {
        fun newInstance() = InventoryFragment()
    }*/

    private var _binding : FragmentTankFillingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TankViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTankFillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        binding.firstRowFirst.setOnClickListener {
            menuFunctionality("transaction")
        }
        binding.firstRowSecond.setOnClickListener {
            menuFunctionality("maintenance")
        }
        binding.secondRowFirst.setOnClickListener {
            menuFunctionality("")
        }
        */
        childFragmentManager.beginTransaction()
            .replace(R.id.flMiniInventoryRecycler, MiniInventoryFragment())
            .commit()

        childFragmentManager.beginTransaction() // TODO: Change Fragment to refilled tanks
            .replace(R.id.flRefilledTanksRecycler, MiniInventoryFragment())
            .commit()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TankViewModel::class.java)
        // TODO: Use the ViewModel
    }

}