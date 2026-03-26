package com.thomaspayet.bleapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopAppBar(
    title: @Composable () -> Unit = {
        Text("Title", style = MaterialTheme.typography.titleLarge)
    },
    subtitle: @Composable () -> Unit = {},
    leftIcon: @Composable () -> Unit = {
        Image(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Left Icon",
            alpha = 0f
        )
    },
    rightIcon: @Composable () -> Unit = {
        Image(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Right Icon",
            alpha = 0f
        )
    }
) {
    Surface(tonalElevation = 4.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            leftIcon()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                title()
                subtitle()
            }
            rightIcon()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopAppBarPreview() {
    TopAppBar(
        title = {
            Text("Title", style = MaterialTheme.typography.titleLarge)
        },
        subtitle = {
            Text("Subtitle", style = MaterialTheme.typography.titleSmall)
        },
        leftIcon = {
            Image(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Left Icon"
            )
        },
        rightIcon = {
            Image(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Right Icon"
            )
        }
    )
}