import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.zip.ZipFile

sealed class ZipContent {
    data class Directory(val name: String, val contents: List<ZipContent>) : ZipContent()
    data class File(val name: String) : ZipContent()
}

@Composable
fun DirectoryItem(directory: ZipContent.Directory, zipFilePath: String, onFileClick: (String, String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(4.dp) // Add padding to make clickable area larger
        ) {
            Icon(
                if (expanded) Icons.Filled.ArrowDropDown else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null
            )
            Text(directory.name)
        }
        if (expanded) {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                ZipDirectoryView(directory.contents, zipFilePath, onFileClick)
            }
        }
    }
}


@Composable
fun ZipDirectoryView(contents: List<ZipContent>, zipFilePath: String, onFileClick: (String, String) -> Unit) {
    Column {
        contents.forEachIndexed {_, content ->
            when (content) {
                is ZipContent.Directory -> DirectoryItem(content, zipFilePath, onFileClick)
                is ZipContent.File -> FileItem(content, zipFilePath, onFileClick)
            }
        }
    }
}

@Composable
fun ZipFileContent(zipContent: ZipContent, zipFilePath: String, onFileClick: (String, String) -> Unit) {
    LazyColumn {
        zipContent.let { content ->
            item {
                ZipDirectoryView(listOf(content), zipFilePath, onFileClick)
            }
        }
    }
}

@Composable
fun FileItem(file: ZipContent.File, zipFilePath: String, onFileClick: (String, String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                onFileClick(zipFilePath, file.name)
            }
            .padding(4.dp) // Add padding to make clickable area larger
    ) {
        Text(file.name) // Display file name
    }
}

fun parseZipFile(zipFilePath: String): List<ZipContent> {
    val zipFile = ZipFile(zipFilePath)
    val rootContents = mutableListOf<ZipContent>()
    val directoryMap = mutableMapOf<String, MutableList<ZipContent>>()

    zipFile.use { zip ->
        zip.entries().asSequence().forEach { entry ->
            val entryName = entry.name
            val parts = entryName.split("/")

            var currentDirectoryContents = directoryMap.getOrPut("", { mutableListOf() })

            for (i in parts.indices) {
                val part = parts[i]
                if (i == parts.size - 1 && !entry.isDirectory) {
                    // It's a file
                    currentDirectoryContents.add(ZipContent.File(part))
                } else {
                    // It's a directory
                    val path = parts.subList(0, i ).joinToString("/")
                    val nextDirectoryContents = directoryMap.getOrPut(path, { mutableListOf() })
                    if (!currentDirectoryContents.any { it is ZipContent.Directory && it.name == part }) {
                        currentDirectoryContents.add(ZipContent.Directory(part, nextDirectoryContents))
                    }
                    currentDirectoryContents = nextDirectoryContents
                }
            }
        }
    }

    return directoryMap[""] ?: rootContents
}
