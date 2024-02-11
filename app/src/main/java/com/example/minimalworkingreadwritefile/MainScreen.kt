package com.example.minimalworkingreadwritefile

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel
) {
    val context = LocalContext.current

    // Permission state for accompanist
    // Have to put them in a composable function
    val permissionReadStorageState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val permissionWriteStorageState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val state = viewModel.state.collectAsState()

    val launcherWrite =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            val context = context
            if (it != null) {
                val dirFile = DocumentFile.fromTreeUri(context, it)
                if (dirFile != null) {
                    val file = dirFile.createFile("text/plain", "test")
                    file?.let {
                        writeDataToContentUri(
                            contentResolver = context.contentResolver,
                            contentUri = file.uri,
                            data = "This is a test to create and amend a file\nAnd a new line just for good measure",
                        )
                    }
                    // Steps to allow other apps to use your file
                    // Doesn't seem needed when I tested
//                context.grantUriPermission(
//                    "com.example.otherapp",
//                    file?.uri,
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION
//                )
                }

            } else {
                // Handle uri null case
            }
        }

    val launcherRead = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            it?.let {
                val fileTest = DocumentFile.fromSingleUri(context, it)
                if (fileTest != null) {
                    val data = readDataToContentUri(
                        context.contentResolver,
                        fileTest.uri
                    )
                    viewModel.amendDataToState(data)
                }
            }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            if (Build.VERSION.SDK_INT >= 29) {
                launcherWrite.launch(null)
            } else {
                // Need to request permissions
                // Then the steps are the same
            }
        }) {
            Text(text = "Create file")
        }

        Button(onClick = {
            if (Build.VERSION.SDK_INT >= 29) {
                launcherRead.launch(arrayOf("text/plain"))
            } else {
                // Need to request permissions
                // Then the steps are the same
            }
        }) {
            Text(text = "Read file")
        }

        Button(onClick = { viewModel.clearState() }) {
            Text(text = "Clear state")
        }

        Text(text = state.value)

    }
}

fun writeDataToContentUri(contentResolver: ContentResolver, contentUri: Uri, data: String) {
    try {
        val outputStream: OutputStream? = contentResolver.openOutputStream(contentUri)

        outputStream?.use { stream ->
            stream.write(data.toByteArray(Charsets.UTF_8))
            stream.flush()
        }
        Log.d("WriteToContentUri", "Data written successfully.")
    } catch (e: Exception) {
        Log.e("WriteToContentUri", "Error writing data to content URI", e)
    }
}

fun readDataToContentUri(contentResolver: ContentResolver, contentUri: Uri): String {
    var output: String = "Base"
    val inputStream: InputStream? = contentResolver.openInputStream(contentUri)
    BufferedReader(InputStreamReader(inputStream)).use { reader ->
        var line: String? = reader.readLine()
        if (line != null) {
            output = line
        } else {

        }
        while (line != null) {
            line = reader.readLine()
            output += line
        }
    }
    Log.d("DIRFILE2", output)
    return output
}