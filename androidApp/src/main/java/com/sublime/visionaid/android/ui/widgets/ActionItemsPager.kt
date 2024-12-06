package com.sublime.visionaid.android.ui.widgets

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sublime.visionaid.android.ui.helpers.ActionItem
import com.sublime.visionaid.android.ui.helpers.ActionType
import com.sublime.visionaid.android.ui.viewmodel.CameraViewModel
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
internal fun ActionItemsPager(
    items: List<ActionItem>,
    pagerState: PagerState,
    isLoading: Boolean,
    onCaptureImage: (Uri, ActionType) -> Unit,
    viewModel: CameraViewModel,
    context: Context,
    onCaptureError: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier =
            Modifier
                .fillMaxWidth()
                .height(90.dp),
            pageSpacing = 32.dp,
            contentPadding = PaddingValues(horizontal = 140.dp),
            pageSize = PageSize.Fixed(72.dp),
            key = { it },
        ) { page ->
            ActionItemButton(
                item = items[page],
                isSelected = pagerState.currentPage == page,
                isLoading = isLoading,
                onClick = {
                    if (pagerState.currentPage == page) {
                        coroutineScope.launch {
                            takePhoto(
                                context = context,
                                imageCapture = viewModel.imageCapture.value,
                                onCaptureImage = { uri -> onCaptureImage(uri, items[page].type) },
                                onCaptureError = onCaptureError,
                            )
                        }
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isLoading) "Analyzing scene..." else items[pagerState.currentPage].name,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}
