package com.example.randomimageapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import coil.request.ImageRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                RandomImageScreen()
            }
        }
    }
}

@Composable
fun RandomImageScreen() {
    Column(modifier = Modifier
        .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // for work flow
        var hint by remember { mutableStateOf("Let's Generate Random Image!") }
        var showImage by remember { mutableStateOf(false) }
        var link by remember { mutableStateOf("") }

        // for width field
        var inputWidth by remember { mutableStateOf("") }
        val inputW = inputWidth.toIntOrNull()

        // for height field
        var inputHeight by remember { mutableStateOf("") }
        val inputH = inputHeight.toIntOrNull()

        // for dropdown field
        val directories = listOf<String>(
            "dog",
            "paris",
            "brazil",
            "rio",
            "girl"
        )
        var expanded by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf("") }
        val icon = if (expanded) {
            Icons.Filled.KeyboardArrowUp
        } else {
            Icons.Filled.KeyboardArrowDown
        }
        var textfiledSize by remember { mutableStateOf(Size.Zero) }

        Text(text = stringResource(R.string.app_name))

        if (!showImage) {
            EditNumberField(
                label = stringResource(R.string.width),
                value = inputWidth,
                onValueChange = { inputWidth = it },
                imeAction = ImeAction.Next
            )

            EditNumberField(
                label = stringResource(R.string.height),
                value = inputHeight,
                onValueChange = { inputHeight = it },
                imeAction = ImeAction.Done
            )

            OutlinedTextField(
                value = selectedItem,
                onValueChange = { selectedItem = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textfiledSize = coordinates.size.toSize()
                    },
                label = { Text("Directory") },
                trailingIcon = {
                    Icon(icon, "", Modifier.clickable { expanded = !expanded })
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textfiledSize.width.toDp() })
            ) {
                directories.forEach { label ->
                    DropdownMenuItem(onClick = {
                        selectedItem = label
                        expanded = false
                    }) {
                        Text(text = label)
                    }
                }
            }

            if (hint != "") {
                Text(text = hint)
            }

            Button(onClick = {
                if (inputH == null || inputW == null || selectedItem == "") {
                    hint = "Please fill out all input field correctly"
                } else if (inputH <= 0 || inputW <= 0 || selectedItem !in directories) {
                    hint = "Input is invalid"
                } else {
                    hint = ""
                    showImage = true

                    link = "https://loremflickr.com/" + inputWidth + "/" + inputHeight + "/" + selectedItem
                }
            }) {
                Text(text = stringResource(R.string.generate))
            }
        }
        else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(link)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.loading_image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(inputHeight.toFloat().dp)
                    .width(inputWidth.toFloat().dp),
            )

            Button(onClick = {
                inputWidth = ""
                inputHeight = ""
                selectedItem = ""
                showImage = false
            }) {
                Text(text = stringResource(R.string.restart))
            }
        }
    }
}

@Composable
fun EditNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() })
    )
}