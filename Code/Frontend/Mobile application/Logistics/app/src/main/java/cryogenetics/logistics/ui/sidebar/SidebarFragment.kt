package cryogenetics.logistics.ui.sidebar

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cryogenetics.logistics.R

class SidebarFragment : Fragment() {

    companion object {
        fun newInstance() = SidebarFragment()
    }

    private lateinit var viewModel: SidebarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sidebar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SidebarViewModel::class.java)
        // TODO: Use the ViewModel
    }

}