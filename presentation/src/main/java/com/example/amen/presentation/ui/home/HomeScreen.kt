package com.example.amen.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.amen.domain.entity.BibleVerse
import com.example.amen.presentation.ui.components.ads.BannerAdView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBibleReading: () -> Unit,
    onNavigateToTracker: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val dailyVerse by viewModel.dailyVerse.collectAsState()
    val isTtsPlaying by viewModel.isTtsPlaying.collectAsState()
    val readingProgress by viewModel.readingProgress.collectAsState()

    var journalText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val currentTime = remember { LocalDateTime.now() }
    val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
    val timeString = currentTime.format(timeFormatter)

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E2E4E), Color(0xFF0F172A))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 상단 배너 광고
                BannerAdView(modifier = Modifier.fillMaxWidth())

                // 타이틀
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AMEN",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 6.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // ── 섹션 1: 오늘의 말씀 ──────────────────────────────────────
                DailyWordSection(
                    verse = dailyVerse,
                    isTtsPlaying = isTtsPlaying,
                    onTtsToggle = { viewModel.toggleTts() },
                    onLikeToggle = { viewModel.toggleLike() }
                )

                // ── 섹션 2: 기도 일기 입력 ────────────────────────────────────
                PrayerJournalInputSection(
                    text = journalText,
                    onTextChange = { journalText = it },
                    onSave = {
                        viewModel.saveJournal(journalText)
                        journalText = ""
                    }
                )

                // ── 섹션 3: 말씀 완독 진행률 ──────────────────────────────────
                BibleProgressSection(
                    progress = readingProgress,
                    onClick = onNavigateToTracker
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DailyWordSection(
    verse: BibleVerse?,
    isTtsPlaying: Boolean,
    onTtsToggle: () -> Unit,
    onLikeToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "오늘의 말씀",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            if (verse != null) {
                Text(
                    text = "\"${verse.content}\"",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "${verse.book} ${verse.chapter}:${verse.verse}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // TTS 버튼
                    SmallFloatingActionButton(
                        onClick = onTtsToggle,
                        containerColor = if (isTtsPlaying) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f),
                        contentColor = if (isTtsPlaying) MaterialTheme.colorScheme.onPrimary else Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = if (isTtsPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "낭독",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 좋아요 버튼
                    SmallFloatingActionButton(
                        onClick = onLikeToggle,
                        containerColor = if (verse.isLiked) Color(0xFFE53935).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                        contentColor = if (verse.isLiked) Color(0xFFE53935) else Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = if (verse.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "좋아요",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun PrayerJournalInputSection(
    text: String,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "오늘의 말씀 일기",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
            placeholder = { Text("오늘의 구절을 묵상하며 기도 제목이나 소감을 적어보세요...", fontSize = 14.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        )
        
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = text.isNotBlank()
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("일기 저장하기")
        }
    }
}

@Composable
fun BibleProgressSection(
    progress: Float,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "말씀 완독 여정",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "달성",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
