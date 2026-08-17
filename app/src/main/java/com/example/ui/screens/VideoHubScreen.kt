package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.model.VideoTemplate

@Composable
fun VideoHubScreen(
    templates: List<VideoTemplate>,
    modifier: Modifier = Modifier
) {
    var selectedTemplate by remember { mutableStateOf<VideoTemplate?>(templates.firstOrNull()) }
    var isEditingInWebView by remember { mutableStateOf(false) }
    val selectedPhotoIndices = remember { mutableStateListOf(0, 1, 2) }

    if (isEditingInWebView && selectedTemplate != null) {
        // Full screen webview video editor interface
        FullScreenWebViewEditor(
            template = selectedTemplate!!,
            onClose = { isEditingInWebView = false }
        )
    } else {
        // Template Picker and Editor Overview
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            // Top Header Bar
            Surface(
                color = Color.White,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "Video Studio",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Video Studio Hub",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color(0xFF1E293B)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFE0F2FE)
                    ) {
                        Text(
                            text = "Cloud Render",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Color(0xFF0369A1),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Active Selected Template Preview Hero Card
                item {
                    selectedTemplate?.let { template ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Selected Template",
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                    color = Color(0xFF0284C7)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = template.title,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.8f)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = template.thumbnailResId ?: R.drawable.img_video_template_love),
                                        contentDescription = template.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Play icon overlay button
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .align(Alignment.Center)
                                            .clip(CircleShape)
                                            .background(Color(0xCC000000)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play Template",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    // Duration tag
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xCC000000),
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .align(Alignment.BottomStart)
                                    ) {
                                        Text(
                                            text = "Duration: ${template.duration}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Photo selector preview (Select 3 photos for video)
                                Text(
                                    text = "Select Family Photos (3/3)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF334155)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val sampleRes = listOf(
                                        R.drawable.img_family_hero,
                                        R.drawable.img_modern_house,
                                        R.drawable.img_ai_robot,
                                        R.drawable.img_app_icon
                                    )

                                    sampleRes.forEachIndexed { idx, resId ->
                                        val isSelected = selectedPhotoIndices.contains(idx)
                                        Box(
                                            modifier = Modifier
                                                .size(68.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(
                                                    width = if (isSelected) 3.dp else 0.dp,
                                                    color = if (isSelected) Color(0xFF0284C7) else Color.Transparent,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable {
                                                    if (isSelected && selectedPhotoIndices.size > 1) {
                                                        selectedPhotoIndices.remove(idx)
                                                    } else if (!isSelected) {
                                                        selectedPhotoIndices.add(idx)
                                                    }
                                                }
                                        ) {
                                            Image(
                                                painter = painterResource(id = resId),
                                                contentDescription = "Photo $idx",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .align(Alignment.TopEnd)
                                                        .background(Color(0xFF0284C7), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Action Button: Open WebView Video Editor
                                Button(
                                    onClick = { isEditingInWebView = true },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0284C7),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Movie,
                                            contentDescription = "Render Video",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Create & Render Video",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Grid of Video Templates
                item {
                    Text(
                        text = "Browse Video Templates",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E293B)
                    )
                }

                items(templates.chunked(2)) { rowTemplates ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowTemplates.forEach { t ->
                            TemplateItemCard(
                                template = t,
                                isSelected = t.id == selectedTemplate?.id,
                                onSelect = { selectedTemplate = t },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowTemplates.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateItemCard(
    template: VideoTemplate,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color(0xFF0284C7) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
            ) {
                Image(
                    painter = painterResource(id = template.thumbnailResId ?: R.drawable.img_video_template_love),
                    contentDescription = template.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(Color(0x99000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = template.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = template.duration,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun FullScreenWebViewEditor(
    template: VideoTemplate,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Toolbar
        Surface(color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Cloud Video Editor - ${template.title}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Rendering without local CPU strain",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // WebView pointing to cloud video editor studio HTML canvas
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    webViewClient = WebViewClient()

                    // HTML Canvas for Video Rendering Studio
                    val htmlData = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <style>
                                body {
                                    margin: 0;
                                    padding: 0;
                                    background: #020617;
                                    color: #f8fafc;
                                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                                    display: flex;
                                    flex-direction: column;
                                    align-items: center;
                                    justify-content: center;
                                    min-height: 100vh;
                                    text-align: center;
                                }
                                .container {
                                    width: 90%;
                                    max-width: 480px;
                                    background: #0f172a;
                                    padding: 24px;
                                    border-radius: 20px;
                                    box-shadow: 0 10px 25px rgba(0,0,0,0.5);
                                    border: 1px solid #1e293b;
                                }
                                .preview-box {
                                    width: 100%;
                                    height: 220px;
                                    background: linear-gradient(135deg, #0284c7, #6366f1);
                                    border-radius: 16px;
                                    display: flex;
                                    align-items: center;
                                    justify-content: center;
                                    margin: 16px 0;
                                    position: relative;
                                    overflow: hidden;
                                }
                                .pulse {
                                    width: 80px;
                                    height: 80px;
                                    background: rgba(255,255,255,0.2);
                                    border-radius: 50%;
                                    animation: pulse 1.5s infinite;
                                }
                                @keyframes pulse {
                                    0% { transform: scale(0.95); opacity: 0.8; }
                                    50% { transform: scale(1.1); opacity: 0.4; }
                                    100% { transform: scale(0.95); opacity: 0.8; }
                                }
                                .badge {
                                    background: #22c55e;
                                    color: #022c22;
                                    font-weight: bold;
                                    padding: 4px 12px;
                                    border-radius: 12px;
                                    font-size: 12px;
                                    display: inline-block;
                                }
                                button {
                                    background: #0284c7;
                                    color: white;
                                    border: none;
                                    padding: 12px 24px;
                                    border-radius: 12px;
                                    font-weight: bold;
                                    font-size: 14px;
                                    cursor: pointer;
                                    width: 100%;
                                    margin-top: 12px;
                                }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <span class="badge">TEMPLATE: ${template.title}</span>
                                <h2>Cloud Video Renderer</h2>
                                <p style="color:#94a3b8; font-size:13px;">Applying cinematic filters, audio transitions, and text overlays.</p>
                                <div class="preview-box">
                                    <div class="pulse"></div>
                                    <span style="position:absolute; font-weight:bold; color:white;">Rendering HD Video...</span>
                                </div>
                                <p style="font-size: 12px; color: #64748b;">Estimated processing time: 2-3 seconds</p>
                                <button onclick="alert('Video rendered successfully! Saved to family gallery.')">Download HD Video</button>
                            </div>
                        </body>
                        </html>
                    """.trimIndent()

                    loadDataWithBaseURL("https://videostudio.local", htmlData, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
