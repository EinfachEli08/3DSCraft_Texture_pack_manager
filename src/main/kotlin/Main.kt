
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

data class NavigationItem(val title: String, val icon: ImageVector, val badgeCount: Int? = null)

@Composable
@Preview



fun App() {
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var showAlert by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableIntStateOf(0) }
    var zipContent by remember { mutableStateOf<List<ZipContent>?>(null) }
    val selectedFileContent by remember { mutableStateOf<String?>(null) }
    val navigationItems = listOf(
        NavigationItem("Home", Icons.Filled.Home, badgeCount = 3),
        NavigationItem("Pack Gallery", Icons.Filled.ShoppingCart),
        NavigationItem("Settings", Icons.Filled.Settings)
    )



    val content: @Composable () -> Unit = when (selectedItem) {
        0 -> {
            {
                if (selectedFileName == null) {
                    Text("Press + to import a new resource pack")
                } else {
                    selectedFileName?.let {
                        Text(
                            text = "Selected file: $it",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IndeterminateCircularIndicator()

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement  = Arrangement.SpaceBetween
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {

                                zipContent?.let { content ->
                                    ZipFileContent(content[0], it) { zipFilePath, filePath ->
                                        println(fileName)
                                        println(selectedFileContent)
                                        println(zipFilePath)
                                        println(filePath)
                                    }
                                }

                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "File Viewer",
                                    textAlign = TextAlign.Center,

                                )
                            }



                        }
                        LinearDeterminateIndicator()
                    }
                }
            }
        }

        1 -> {
            { Text("Resource pack gallery") }
        }

        2 -> {
            { Text("Settings") }
        }

        else -> {
            { Text("Unknown Content") }
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Row {
            NavigationRail {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(onClick = {
                            openFilePicker { fileName, isZip ->
                                if (isZip) {
                                    selectedFileName = fileName
                                    fileName?.let { nonNullFileName ->
                                        zipContent = parseZipFile(nonNullFileName)
                                    }
                                } else {
                                    showAlert = true
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    navigationItems.forEachIndexed { index, item ->
                        NavigationRailItem(
                            icon = {
                                BadgedBox(badge = {
                                    if (item.badgeCount != null) {
                                        Badge { Text(item.badgeCount.toString()) }
                                    }
                                }) {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            },
                            label = { Text(item.title) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            alwaysShowLabel = false
                        )
                    }
                }
            }

            MainContent(content)

            if (showAlert) {
                AlertDialog(
                    onDismissRequest = { showAlert = false },
                    title = { Text(text = "Invalid File") },
                    text = { Text(text = "The selected file is not a ZIP file.") },
                    confirmButton = {
                        TextButton(onClick = { showAlert = false }) {
                            Text("Dismiss")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showAlert = false
                                openFilePicker { fileName, isZip ->
                                    if (isZip) {
                                        selectedFileName = fileName
                                        fileName?.let { nonNullFileName ->
                                            zipContent = parseZipFile(nonNullFileName)
                                        }
                                    } else {
                                        showAlert = true
                                    }
                                }
                            }
                        ) {
                            Text("Open different file")
                        }
                    }
                )
            }
        }
    }
}

var fileName: String? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp, bottom = 16.dp, start = 0.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

fun openFilePicker(onFileSelected: (String?, Boolean) -> Unit) {
    val frame = Frame()
    val fileDialog = FileDialog(frame, "Select a file", FileDialog.LOAD)
    fileDialog.isVisible = true
    val selectedFile = fileDialog.file
    val isCanceled =
        selectedFile == null && fileDialog.directory == null // Check if selection was canceled
    if (isCanceled) {
        onFileSelected(null, false) // Invoke callback with null values
    } else {
        if (selectedFile != null) {
            fileName = "${fileDialog.directory}${File.separator}$selectedFile"

            val isZip = fileName!!.endsWith(".zip")
            onFileSelected(fileName, isZip)
        } else {
            onFileSelected(null, false)
        }
    }
    frame.dispose()
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication, title = "3DSCraft Texture manager"
    ) {
        App()
    }
}
