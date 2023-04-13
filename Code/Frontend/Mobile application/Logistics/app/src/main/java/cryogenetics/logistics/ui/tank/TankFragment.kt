package cryogenetics.logistics.ui.tank

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
import cryogenetics.logistics.databinding.FragmentTankBinding
import cryogenetics.logistics.ui.actLog.mini.MiniActLogFragment
import cryogenetics.logistics.ui.inventory.InventoryFragment

class TankFragment : Fragment() {

    /*
    companion object {
        fun newInstance() = InventoryFragment()
    }*/

    private var _binding : FragmentTankBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TankViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.firstRowFirst.setOnClickListener {
            menuFunctionality("transaction")
        }
        binding.firstRowSecond.setOnClickListener {
            menuFunctionality("maintenance")
        }
        binding.secondRowFirst.setOnClickListener {
            menuFunctionality("")
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.miniLog, MiniActLogFragment())
            .commit()
    }

    @SuppressLint("SetTextI18n") // Strings could be exported to res/values/string for different language support and etc, but it is not necessary yet for this project.
    private fun menuFunctionality(type: String) = when (type) {
        "transaction" -> {
            changeMarginsAndUpdateMenu(
                View.VISIBLE,
                R.drawable.link,
                R.drawable.send_out,
                R.drawable.recieve_from_user,
                R.drawable.cryo_black_transp,
                "Link",
                "Send to Client",
                "Return from Client",
                "Internal Transfer"
            )
        }
        "maintenance" -> {
            changeMarginsAndUpdateMenu(
                View.VISIBLE,
                R.drawable.maint_color_menu,
                R.drawable.fill,
                R.drawable.dispose,
                R.drawable.manual,
                "Manage Maintenance",
                "Refill",
                "Dispose",
                "Manual Act"
            )
        }
        else -> {
            changeMarginsAndUpdateMenu()
        }
    }
    private fun changeMarginsAndUpdateMenu(
        visibility: Int = View.GONE,
        ivSecondRowFirst: Int = R.drawable.cancel,
        ivSecondRowSecond: Int = R.drawable.cancel,
        ivSecondRowThird: Int = R.drawable.cancel,
        ivSecondRowFourth: Int = R.drawable.cancel,
        tvSecondRowFirst: String = "null",
        tvSecondRowSecond: String = "null",
        tvSecondRowThird: String = "null",
        tvSecondRowFourth: String = "null"
    ) {
        var btmMargin = 0 // BottomMargin is set to 0 and does not change if 2nd-row is visible.
        if (visibility == View.GONE) // If 2nd-row is NOT visible bottomMargin is set equal to marginTop.
            btmMargin = binding.firstRowFirst.marginTop // This is done to avoid converting density-independent-pixels to pixels.

        binding.rightMenuSecondRow.visibility = visibility

        binding.firstRowFirst.updateLayoutParams<ViewGroup.MarginLayoutParams> { // Change layout Params
            setMargins(leftMargin, topMargin, rightMargin, btmMargin) // Sets layout-margins, parameters are in pixels.
        }
        binding.firstRowSecond.updateLayoutParams<ViewGroup.MarginLayoutParams> {// Change layout Params
            setMargins(leftMargin, topMargin, rightMargin, btmMargin) // Sets layout-margins, parameters are in pixels.
        }

        if (ivSecondRowFirst != R.drawable.cancel) { // Avoids this block if a first drawable is still default.
            binding.ivSecondRowFirst.setImageResource(ivSecondRowFirst)
            binding.ivSecondRowSecond.setImageResource(ivSecondRowSecond)
            binding.ivSecondRowThird.setImageResource(ivSecondRowThird)
            binding.ivSecondRowFourth.setImageResource(ivSecondRowFourth)
            binding.tvSecondRowFirst.text = tvSecondRowFirst
            binding.tvSecondRowSecond.text = tvSecondRowSecond
            binding.tvSecondRowThird.text = tvSecondRowThird
            binding.tvSecondRowFourth.text = tvSecondRowFourth
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TankViewModel::class.java)
        // TODO: Use the ViewModel
    }

}