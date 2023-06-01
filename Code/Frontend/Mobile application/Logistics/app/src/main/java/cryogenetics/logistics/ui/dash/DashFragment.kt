package cryogenetics.logistics.ui.dash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.actLog.MiniActLogFragment
import cryogenetics.logistics.ui.inventory.MiniInventoryFragment

class DashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_dash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.miniInventory, MiniInventoryFragment( tvInventoryClient = true ))
            .commit()
        childFragmentManager.beginTransaction()
            .replace(R.id.miniLog, MiniActLogFragment(tvActLogRNrVisible = true))
            .commit()

    }
}