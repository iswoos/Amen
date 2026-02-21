package com.example.amen.presentation.ui.relax

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.amen.domain.entity.BibleVerse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelaxScreen(
    onBackClick: () -> Unit,
    viewModel: RelaxViewModel = hiltViewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val backgroundVolume by viewModel.backgroundVolume.collectAsState()
    val ttsVolume by viewModel.ttsVolume.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredBooks by viewModel.filteredBooks.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val selectedBook by viewModel.selectedBook.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val selectedVerses by viewModel.selectedVerses.collectAsState()

    val sleepTimerActive by viewModel.sleepTimerActive.collectAsState()
    val sleepTimerRemainingSecs by viewModel.sleepTimerRemainingSecs.collectAsState()

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showBookDialog by remember { mutableStateOf(false) }
    var showChapterDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("휴식과 수면", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "설정", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(colors = listOf(Color(0xFF1A2A4A), Color(0xFF0A0F1E))))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 60.dp, top = 8.dp)
            ) {
                // 상단 헤더
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "평온한 말씀과 함께하는 밤",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // ── 수면 타이머 섹션 (신규 위치: 헤더와 성경 선택 사이) ──────────────
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (sleepTimerActive && sleepTimerRemainingSecs > 0) {
                            ActiveTimerCard(
                                remainingSecs = sleepTimerRemainingSecs,
                                onCancel = { viewModel.cancelSleepTimer() }
                            )
                        } else {
                            SleepTimerInputCard(
                                onStartTimer = { viewModel.startSleepTimer(it) }
                            )
                        }
                    }
                }

                // ── 성경 낭독 선택 카드 ──────────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "성경 선택",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                SelectionButton(
                                    label = selectedBook ?: "성경 권",
                                    modifier = Modifier.weight(1f),
                                    onClick = { showBookDialog = true }
                                )
                                SelectionButton(
                                    label = selectedChapter?.let { "${it}장" } ?: "장",
                                    modifier = Modifier.weight(0.6f),
                                    enabled = selectedBook != null,
                                    onClick = { showChapterDialog = true }
                                )
                            }

                            // 구절 목록 미리보기
                            AnimatedVisibility(
                                visible = selectedVerses.isNotEmpty(),
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(modifier = Modifier.padding(top = 20.dp)) {
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // 낭독 재생 버튼 (여기 배치)
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        IconButton(
                                            onClick = { viewModel.togglePlayStatus() },
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(if (isPlaying) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.15f))
                                        ) {
                                            Icon(
                                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp),
                                                tint = if (isPlaying) Color.White else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // 구절 리스트 (클릭 시 해당 절부터 재생)
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        for ((index, verse) in selectedVerses.withIndex()) {
                                            VerseItem(
                                                verse = verse,
                                                onClick = { viewModel.playFromVerse(index) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── 설정 BottomSheet ──────────────────────────────────────────
        if (showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSettingsSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF161C2C),
                contentColor = Color.White
            ) {
                SettingsSheetContent(
                    backgroundVolume = backgroundVolume,
                    ttsVolume = ttsVolume,
                    onBackgroundVolumeChange = { viewModel.setBackgroundVolume(it) },
                    onTtsVolumeChange = { viewModel.setTtsVolume(it) }
                )
            }
        }

        // ── 성경 권 선택 다이얼로그 (검색 포함) ──────────────────────────
        if (showBookDialog) {
            BookSelectionDialog(
                searchQuery = searchQuery,
                books = filteredBooks,
                onSearchChange = { viewModel.setSearchQuery(it) },
                onBookSelect = { 
                    viewModel.selectBook(it)
                    showBookDialog = false
                },
                onDismiss = { showBookDialog = false }
            )
        }

        // ── 장 선택 다이얼로그 ──────────────────────────────────────────
        if (showChapterDialog) {
            ChapterSelectionDialog(
                chapters = chapters,
                onChapterSelect = {
                    viewModel.selectChapter(it)
                    showChapterDialog = false
                },
                onDismiss = { showChapterDialog = false }
            )
        }
    }
}

@Composable
fun SelectionButton(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.2f), // 색상 강화 (0.1f -> 0.2f)
            disabledContainerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, color = Color.White)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
        }
    }
}

@Composable
fun VerseItem(
    verse: BibleVerse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "${verse.verse}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = verse.content,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActiveTimerCard(remainingSecs: Long, onCancel: () -> Unit) {
    val mins = remainingSecs / 60
    val secs = remainingSecs % 60
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${String.format("%02d:%02d", mins, secs)} 후 종료",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = onCancel) {
                Text("취소", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun SleepTimerInputCard(onStartTimer: (Int) -> Unit) {
    var timerInput by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "수면 타이머 설정",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = timerInput,
                    onValueChange = { if (it.all { char -> char.isDigit() }) timerInput = it },
                    label = { Text("시간 입력 (분)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Button(
                    onClick = { onStartTimer(timerInput.toIntOrNull() ?: 0) },
                    enabled = timerInput.isNotBlank(),
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("시작")
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (mins in listOf(10, 30, 60)) {
                    AssistChip(
                        onClick = { onStartTimer(mins) },
                        label = { Text("${mins}분") },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        border = null // 타입 충돌 방지 및 투명 보더 유지
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSheetContent(
    backgroundVolume: Float,
    ttsVolume: Float,
    onBackgroundVolumeChange: (Float) -> Unit,
    onTtsVolumeChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Text("오디오 설정", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // 볼륨 섹션
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("볼륨 설정", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            VolumeControl(label = "🎵 배경 음악", volume = backgroundVolume, onValueChange = onBackgroundVolumeChange)
            VolumeControl(label = "📖 말씀 낭독", volume = ttsVolume, onValueChange = onTtsVolumeChange)
        }
    }
}

@Composable
fun VolumeControl(label: String, volume: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${(volume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
        Slider(
            value = volume,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSelectionDialog(
    searchQuery: String,
    books: List<String>,
    onSearchChange: (String) -> Unit,
    onBookSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161C2C))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("성경 선택", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("검색 (예: 창세)") },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(books) { book ->
                        ListItem(
                            headlineContent = { Text(book, color = Color.White) },
                            modifier = Modifier.clickable { onBookSelect(book) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterSelectionDialog(
    chapters: List<Int>,
    onChapterSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161C2C))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("장 선택", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(60.dp),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chapters) { chapter ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .clickable { onChapterSelect(chapter) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${chapter}", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
