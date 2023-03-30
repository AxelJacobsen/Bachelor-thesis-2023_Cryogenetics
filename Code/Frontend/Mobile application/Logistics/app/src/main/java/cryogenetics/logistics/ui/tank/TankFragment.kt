package cryogenetics.logistics.ui.tank

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.actLog.ActLogFragment
import cryogenetics.logistics.ui.inventory.ActLogViewModel

class TankFragment : Fragment() {

    companion object {
        fun newInstance() = ActLogFragment()
    }

    private lateinit var viewModel: ActLogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_tank, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ActLogViewModel::class.java)
        // TODO: Use the ViewModel
    }

}