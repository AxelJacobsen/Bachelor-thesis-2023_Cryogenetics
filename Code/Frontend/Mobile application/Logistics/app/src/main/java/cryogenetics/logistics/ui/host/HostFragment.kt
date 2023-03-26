package cryogenetics.logistics.ui.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cryogenetics.logistics.ui.tank.TankFragment


class HostFragment : Fragment() {

    companion object {
        fun newInstance() = HostFragment()
    }

    private lateinit var viewModel: HostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(cryogenetics.logistics.R.layout.fragment_host, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //super.onViewCreated(view, savedInstanceState)

        val childFragment: Fragment = TankFragment()

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(cryogenetics.logistics.R.id.mainContent, childFragment)
                .commitNow()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}