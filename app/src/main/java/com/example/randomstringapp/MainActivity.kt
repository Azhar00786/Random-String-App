package com.example.randomstringapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.randomstringapp.database.GeneratedStringEntity
import com.example.randomstringapp.ui.theme.RandomStringAppTheme
import com.example.randomstringapp.utils.AppConstants
import com.example.randomstringapp.viewmodel.RandomStringAppViewModel
import com.example.randomstringapp.viewmodel.RandomStringAppViewModelFactory
import java.text.SimpleDateFormat

private var showPermissionDialog: Boolean = false
private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: RandomStringAppViewModel by viewModels {
            RandomStringAppViewModelFactory(
                (application as RandomStringAppApplication).repository
            )
        }
        enableEdgeToEdge()
        setContent {
            RandomStringAppTheme {
                RandomStringAppMainScreen(viewModel)
            }
        }
    }
}


@Composable
fun RandomStringAppMainScreen(viewModel: RandomStringAppViewModel) {
    val context = LocalContext.current
    var stringLength by remember {
        mutableStateOf("")
    }
    val generatedStrings by viewModel.generatedStrings.collectAsState()
    val error by viewModel.error.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[AppConstants.CONTENT_PROVIDER_READ_PERMISSION] == true
        val writeGranted = permissions[AppConstants.CONTENT_PROVIDER_WRITE_PERMISSION] == true

        viewModel.setReadPermission(readGranted)
        viewModel.setWritePermission(writeGranted)
    }

    LaunchedEffect(Unit) {
        val readGranted = checkReadRuntimePermission(context)
        val writeGranted = checkWriteRuntimePermission(context)

        viewModel.setReadPermission(readGranted)
        viewModel.setWritePermission(writeGranted)

        if (!readGranted || !writeGranted) {
            permissionLauncher.launch(
                arrayOf(
                    AppConstants.CONTENT_PROVIDER_READ_PERMISSION,
                    AppConstants.CONTENT_PROVIDER_WRITE_PERMISSION
                )
            )
        } else {
            Log.d(
                TAG,
                "Permission to read and write from Content provider is granted"
            )
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = stringLength, onValueChange = { stringLength = it },
            label = { Text(stringResource(id = R.string.button_one_heading)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Button(
            onClick = {
                if (stringLength.isNotBlank()) {
                    Log.d(TAG, "stringLength is not blank")
                    val length = stringLength.toIntOrNull() ?: 0
                    if (length > 0) {
                        val readGranted = checkReadRuntimePermission(context)
                        val writeGranted = checkWriteRuntimePermission(context)

                        if (readGranted && writeGranted) {
                            viewModel.generateString(stringLength.toInt())
                            stringLength = ""
                        } else {
                            showPermissionDialog = true
                        }
                    } else {
                        Toast.makeText(
                            context,
                            R.string.string_error_message_one,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(context, R.string.string_error_message_two, Toast.LENGTH_SHORT)
                        .show()
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = stringResource(id = R.string.button_one_label))
        }

        Button(
            onClick = {
                viewModel.clearTable()
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = stringResource(id = R.string.button_two_label))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(generatedStrings) { item ->
                GeneratedStringItem(item, onDelete = {
                    viewModel.deleteOneRecord(item)
                })
                HorizontalDivider()
            }
        }
    }

    if (showPermissionDialog) {
        Log.d(TAG, "Showing dialog box")
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(id = R.string.alert_dialog_heading)) },
            text = {
                Text(stringResource(R.string.alert_dialog_body))
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    permissionLauncher.launch(
                        arrayOf(
                            AppConstants.CONTENT_PROVIDER_READ_PERMISSION,
                            AppConstants.CONTENT_PROVIDER_WRITE_PERMISSION
                        )
                    )
                }) {
                    Text(stringResource(id = R.string.alert_dialog_button_allow))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                }) {
                    Text(stringResource(id = R.string.alert_dialog_button_cancel))
                }
            }
        )
    }
}

@Composable
fun GeneratedStringItem(item: GeneratedStringEntity, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat(AppConstants.TIME_FORMAT) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = stringResource(R.string.generated_string_item_text_one, item.value))
        Text(text = stringResource(R.string.generated_string_item_text_two, item.length))
        Text(
            text = stringResource(
                id = R.string.generated_string_item_text_three,
                dateFormat.format(java.util.Date(item.timestamp))
            )
        )
        Button(
            onClick = { onDelete() },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(stringResource(id = R.string.record_button_heading))
        }
    }
}

private fun checkReadRuntimePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, AppConstants.CONTENT_PROVIDER_READ_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED
}

private fun checkWriteRuntimePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, AppConstants.CONTENT_PROVIDER_WRITE_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED
}
