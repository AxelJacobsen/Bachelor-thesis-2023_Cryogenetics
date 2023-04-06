package cryogenetics.logistics.ui.tank

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cryogenetics.logistics.R
import cryogenetics.logistics.databinding.MiniActLogRecyclerItemBinding
import cryogenetics.logistics.ui.actLog.mini.MiniActLogFragment
import cryogenetics.logistics.ui.inventory.InventoryFragment
import cryogenetics.logistics.ui.inventory.InventoryViewModel

class TankFragment : Fragment() {

    companion object {
        fun newInstance() = InventoryFragment()
    }

    private lateinit var viewModel: TankViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_tank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.miniLog, MiniActLogFragment())
            .commit()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TankViewModel::class.java)
        // TODO: Use the ViewModel
    }

}