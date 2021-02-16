package ru.elesta.jupiter_lkselfguard.AccountFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection.AccountFragmentDeviceConnectionBarcode
import ru.elesta.jupiter_lkselfguard.DevicesFragment.AccountFragmentDeviceConnection.AccountFragmentDeviceConnectionBarcode.Companion.textFromBarcodeScanner
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity.Companion.barcodeFinished
import ru.elesta.jupiter_lkselfguard.R

class BarcodeScanner : Fragment() {

    private lateinit var codeScanner: CodeScanner
    lateinit var scanner: CodeScannerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.activity_barcode_scanner, null)
        scanner = v.findViewById(R.id.scanner_view)
        startScanning()
        return v
    }

    private fun startScanning() {
        // Parameters (default values)
        codeScanner = CodeScanner(activity!!.applicationContext, scanner)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            var resultText = ""
            var soedinit = ""
            var parseToString = it.text.toMutableList()
            for(i in parseToString){
                Log.v("EachElement", i.toString())
            }
            Log.v("SIZE", parseToString.count().toString())
            if(parseToString.count() == 12){
                parseToString.removeAt(11)
                for(i in parseToString){
                    soedinit += i
                }
                resultText = "0" + soedinit
            }
            if(parseToString.count() == 13){
                parseToString.removeAt(12)
                for(i in parseToString){
                    resultText += i
                }
            }
            barcodeFinished = true
            textFromBarcodeScanner = resultText
            val accountFrag =
                AccountFragmentDeviceConnectionBarcode()
            val ft = activity?.supportFragmentManager?.beginTransaction()
            ft?.replace(R.id.fragmentContainer, accountFrag)
            ft?.addToBackStack(null)
            ft?.commit()
        }
        codeScanner.errorCallback = ErrorCallback {

        }

        scanner.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        if(::codeScanner.isInitialized) {
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if(::codeScanner.isInitialized) {
            codeScanner?.releaseResources()
        }
        super.onPause()
    }
}
