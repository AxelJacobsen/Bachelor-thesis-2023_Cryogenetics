package cryogenetics.logistics.ui.dash

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.actLog.mini.MiniActLogFragment
import cryogenetics.logistics.ui.inventory.mini.MiniInventoryFragment

class DashFragment : Fragment() {

    private lateinit var viewModel: DashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_dash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.miniInventory, MiniInventoryFragment())
            .commit()
        childFragmentManager.beginTransaction()
            .replace(R.id.miniLog, MiniActLogFragment(true))
            .commit()

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DashViewModel::class.java)
        // TODO: Use the ViewModel
    }

}