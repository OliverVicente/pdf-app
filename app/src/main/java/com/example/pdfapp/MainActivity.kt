package com.example.pdfapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.compose.AndroidFragment
import androidx.pdf.viewer.fragment.PdfViewerFragment
import com.example.pdfapp.ui.theme.PdfAppTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PdfAppTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeContentPadding()
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        DocumentSelectionScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentSelectionScreen() {
    val context = LocalContext.current
    var documentUri: Uri? by remember { mutableStateOf(null) }
    val documentSelectionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (Build.VERSION.SDK_INT >= 31) {
                documentUri = uri
            } else {
                val openPdfIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(openPdfIntent)
            }
        }

    if (documentUri == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = { documentSelectionLauncher.launch(arrayOf("application/pdf")) }
            ) {
                Text(stringResource(R.string.select_document))
            }
        }
    } else {
        documentUri?.let { PdfRenderer(uri = it, modifier = Modifier.fillMaxSize()) }
    }
}

@Composable
private fun PdfRenderer(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    AndroidFragment<PdfViewerFragment>(
        arguments = bundleOf(
            "documentUri" to uri
        ),
        modifier = modifier
    )
}
