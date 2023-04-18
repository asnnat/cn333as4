package com.example.randomimageapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.materialIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.randomimageapp.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colors.background) {
                RandomImageWithBackground()
            }
        }
    }
}

@Composable
fun RandomImageScreen() {
    Column(modifier = Modifier
        .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
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

        Text(
            text = stringResource(R.string.app_name),
            fontSize = 48.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 20.dp, top = 30.dp)
        )

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

            TextField(
                value = selectedItem,
                onValueChange = { selectedItem = it },
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(
                            width = 4.dp,
                            brush = Brush.horizontalGradient(listOf(Purple200, Purple500))
                        ),
                        shape = RoundedCornerShape(25.dp),
                    )
                    .onGloballyPositioned { coordinates ->
                        textfiledSize = coordinates.size.toSize()
                    },
                label = { Text("Directory", style = TextStyle(color = Color.White)) },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    leadingIconColor = Color.White,
                    textColor = Color.White
                ),
                trailingIcon = {
                    Icon(icon, "", Modifier.clickable { expanded = !expanded }, tint = Color.White)
                }
            )
            MaterialTheme(shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(16.dp))) {
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
            }


            if (hint != "") {
                Text(
                    text = hint,
                    color = Color.White
                )
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
            },
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.elevation(5.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Pink500,
                    contentColor = White
                )
            ) {
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
            },
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.elevation(5.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Pink500,
                    contentColor = White
                )
            ) {
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
        label = {Text(label, style = TextStyle(color = LightBlue))},
        shape = RoundedCornerShape(25.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            textColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    width = 4.dp,
                    brush = Brush.horizontalGradient(listOf(Cyan200, Cyan800))
                ),
                shape = RoundedCornerShape(25.dp),
            ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction,),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() })
    )
}

@Composable
fun RandomImageWithBackground() {
    val image = painterResource(R.drawable.background)
    Box {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        RandomImageScreen()
    }
}

@Preview
@Composable
fun RandomImage() {
    RandomImageWithBackground()
}