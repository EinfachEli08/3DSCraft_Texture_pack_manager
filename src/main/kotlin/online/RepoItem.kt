package online

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RepoItem(repo: Repo, onDelete: () -> Unit, onRepoChange: (String) -> Unit) {
    var text by remember { mutableStateOf(repo.url) }

    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        TextField(
            placeholder = { Text(repo.placeholder) }, // Use repo.placeholder directly
            value = text,
            onValueChange = {
                text = it
                onRepoChange(it)
            },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onDelete) {
            Text("Delete")
        }
    }
}