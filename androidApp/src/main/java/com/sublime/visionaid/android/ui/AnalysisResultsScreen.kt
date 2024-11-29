@file:OptIn(ExperimentalMaterial3Api::class)

package com.sublime.visionaid.android.ui

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun AnalysisResultsScreen(
    imageUri: Uri,
    analysisResult: String,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        TopAppBar(
            title = { Text("Analysis Results") },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
        )

        // Image Preview
        AsyncImage(
            model = imageUri,
            contentDescription = "Analyzed Image",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            contentScale = ContentScale.Crop,
        )

        // Analysis Results
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
            ) {
                Text(
                    text = analysisResult,
                    style = MaterialTheme.typography.bodyLarge,
                )

//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Feedback Section
//                Text(
//                    "How did you like this result?",
//                    style = MaterialTheme.typography.titleMedium,
//                )

//                Row(
//                    modifier =
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 8.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                ) {
//                    IconButton(
//                        onClick = { onFeedback(false) },
//                    ) {
//                        Icon(Icons.Default.Clear, "Negative Feedback")
//                    }
//
//                    IconButton(
//                        onClick = { onFeedback(true) },
//                    ) {
//                        Icon(Icons.Default.Check, "Positive Feedback")
//                    }
//                }

                // Action Buttons
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    /*Button(onClick = onSaveResult) {
                        Icon(Icons.Default.Save, "Save")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save")
                    }

                    Button(onClick = onShareResult) {
                        Icon(Icons.Default.Share, "Share")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }*/
                }
            }
        }
    }
}
