package cryogenetics.logistics.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.sidebar.SidebarFragment
import cryogenetics.logistics.ui.sidebar.SidebarViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = SidebarFragment()
    }

    private lateinit var viewModel: SidebarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SidebarViewModel::class.java)
        // TODO: Use the ViewModel
    }

}