package cryogenetics.logistics.ui.TestFragmentB

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import cryogenetics.logistics.R

class TestFragmentB : Fragment() {

    companion object {
        fun newInstance() = TestFragmentB()
    }

    private lateinit var viewModel: TestFragmentBViewModel
    private lateinit var mEditText: EditText
    private lateinit var mTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test_fragment_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Test text field
        mTextView = view.findViewById(cryogenetics.logistics.R.id.textView2)
        mEditText = view.findViewById(cryogenetics.logistics.R.id.editText)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TestFragmentBViewModel::class.java)
        // TODO: Use the ViewModel
    }

}