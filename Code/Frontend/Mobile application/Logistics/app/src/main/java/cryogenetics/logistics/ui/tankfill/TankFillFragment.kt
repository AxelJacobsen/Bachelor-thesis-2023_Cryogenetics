package cryogenetics.logistics.ui.tankfill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.cameraQR.CamAccess
import cryogenetics.logistics.databinding.FragmentTankFillingBinding
import cryogenetics.logistics.ui.inventory.mini.MiniInventoryFragment
import cryogenetics.logistics.cameraQR.CameraFragment
import cryogenetics.logistics.ui.tank.OnItemClickListener

class TankFillFragment : Fragment() {

    private lateinit var viewModel: TankViewModel
    private lateinit var inventoryData: List<Map<String, Any>>
    private lateinit var camFrag: CameraFragment

    private var barcodeScannerOptions: BarcodeScannerOptions? = null
    private var barcodeScanner: BarcodeScanner? = null
    private var _binding : FragmentTankFillingBinding? = null
    private val binding get() = _binding!!
    private var qrCodes: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTankFillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inventoryData = fetchInventoryData()
        inventoryData = listOf(mapOf())
        camFrag = CameraFragment(mOnFoundProductListener)

        barcodeScannerOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions!!)

        binding.ibCamera.setOnClickListener {
            if (CamAccess.checkCameraPermission(requireContext())){
                //if (checkStoragePermission()){
                binding.flFillFragment.visibility = View.VISIBLE
                childFragmentManager.beginTransaction()
                    .replace(R.id.flFillFragment, CameraFragment(mOnFoundProductListener))
                    .commit()
                println("checkSTORagePermission success") // TODO: LOG.D
            } else {
                println("checkCameraPermission fail") // TODO: LOG.D
                CamAccess.requestCameraPermission(requireActivity())
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.flMiniInventoryRecycler, MiniInventoryFragment())
            .commit()

        // initialize the recyclerView
        binding.recyclerRefilledTanks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRefilledTanks.setHasFixedSize(true)

        //Create a list of references
        val viewIds = listOf(
            R.id.tvInventoryNr,
            R.id.tvInventoryClient,
            R.id.tvInventoryLastFill,
            R.id.tvInventoryNoti
        )
        //Create adapter
        binding.recyclerRefilledTanks.adapter = TankFillAdapter(
            inventoryData as MutableList<Map<String, Any>>, viewIds)
        binding.recyclerRefilledTanks.visibility = View.VISIBLE
    }

    private fun fetchInventoryData(): List<Map<String, Any>> {
        val urlDataString = Api.fetchJsonData(ApiUrl.urlContainer)
        return Api.parseJsonArray(urlDataString)
    }

    private val mOnFoundProductListener = object : OnItemClickListener {
        override fun onClick(model: Map<String, Any>) {
            println("MODELLO" + model)
            // add model to adapter
        }

        override fun onFoundQR(serialNr: String) {
            if (qrCodes.contains(serialNr)) {
                return
            }
            qrCodes.add(serialNr)
            if (inventoryData.isNotEmpty()) {
                for (model in inventoryData) {
                    if (model.values.toString().contains(serialNr)) {
                        /*
                        initTankData(model)
                        binding.flTankCameraFragment?.visibility = View.GONE
                        binding.bottomDetails.visibility = View.VISIBLE
                        binding.rightMenuAndContent.visibility = View.VISIBLE
                         */
                        //camFrag.onPaus()
                        break
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "No search value entered, or no Tanks added!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onStopCam() {
            binding.flFillFragment?.visibility = View.GONE
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TankViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
