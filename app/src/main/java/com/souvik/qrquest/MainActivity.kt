package com.souvik.qrquest

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.souvik.qrquest.ui.theme.QRQuestTheme

class MainActivity : ComponentActivity() {

    private var textResult = mutableStateOf("")

    private val barCodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            textResult.value = result.contents
        }
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setCameraId(0)
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a QR code")
        options.setBeepEnabled(false)
        options.setOrientationLocked(false)

        barCodeLauncher.launch(options)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        isGranted ->
        if (isGranted){
            showCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            QRQuestTheme {
                Scaffold(
                    modifier = Modifier.padding(4.dp),
                    bottomBar = {
                        BottomAppBar(
                            actions = {},
                            floatingActionButton = {
                                FloatingActionButton(onClick = { checkCameraPermission(this@MainActivity) }, containerColor = colorResource(
                                    id = R.color.lightPurple
                                )) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.qr_scan),
                                        contentDescription = "QR Scan"

                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.qr_scan),
                            modifier = Modifier.size(100.dp),
                            contentDescription = "QR"
                        )
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(text = textResult.value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

//            fun String.copyToClipboard(context: Context) {
//                val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                val clipData = ClipData.newPlainText("label", this)
//                clipBoard.setPrimaryClip(clipData)
//            }
//                )

        }
    }

    private fun checkCameraPermission(context: Context) {
            if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED){
                showCamera()
            }else if (
                shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)
            ){
                Toast.makeText(this@MainActivity, "Camera Required", Toast.LENGTH_SHORT).show()
            }else{
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
    }
}
