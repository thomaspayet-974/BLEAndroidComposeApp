package com.thomaspayet.bleapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaspayet.bleapp.ui.theme.BLEAppTheme

@Composable
fun ListElement(
    title: String = "Title",
    content: String = "Content",
    image: ImageVector? = Icons.Filled.Android,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            image?.let {
                Icon(
                    imageVector = image,
                    modifier = Modifier.size(50.dp),
                    contentDescription = content
                )
            }

            Column(modifier = Modifier.padding(start = 5.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = content,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListElementPreview() {
    BLEAppTheme() {
        ListElement()
    }
}