package cryogenetics.logistics.ui.TestFragmentA

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cryogenetics.logistics.R

class TestFragmentA : Fragment() {

    companion object {
        fun newInstance() = TestFragmentA()
    }

    private lateinit var viewModel: TestFragmentAViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TestFragmentAViewModel::class.java)
        // TODO: Use the ViewModel
    }

}