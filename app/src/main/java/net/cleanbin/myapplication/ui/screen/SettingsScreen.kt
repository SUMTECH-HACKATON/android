package net.cleanbin.myapplication.ui.screen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.cleanbin.myapplication.data.model.RecyclingType
import net.cleanbin.myapplication.notification.RecyclingNotificationManager
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val notificationManager = remember { RecyclingNotificationManager(context) }

    // 알림 권한 요청 launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            notificationManager.showTestNotification()
        }
    }

    // 각 쓰레기 종류별로 선택된 요일을 저장
    var recycleDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var generalDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var foodDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var largeDays by remember { mutableStateOf(setOf<DayOfWeek>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "분리수거 일정 설정",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color(0xFF2E7D32)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF2E7D32)
                )
            )
        },
        containerColor = Color.White,
        floatingActionButton = {
            // 테스트용 알림 버튼
            FloatingActionButton(
                onClick = {
                    // Android 13 이상에서는 알림 권한 요청
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        notificationManager.showTestNotification()
                    }
                },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "알림 테스트",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 안내 카드
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF1F8F4)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "📅 분리수거 일정",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "각 쓰레기 종류별로 배출일을 설정하세요.\n오른쪽 아래 벨 버튼을 눌러 알림을 테스트해보세요!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.5f)
                        )
                    }
                }
            }

            // 재활용 쓰레기
            item {
                RecyclingTypeCard(
                    type = RecyclingType.RECYCLABLE,
                    selectedDays = recycleDays,
                    onDaysChange = { recycleDays = it }
                )
            }

            // 일반 쓰레기
            item {
                RecyclingTypeCard(
                    type = RecyclingType.GENERAL,
                    selectedDays = generalDays,
                    onDaysChange = { generalDays = it }
                )
            }

            // 음식물 쓰레기
            item {
                RecyclingTypeCard(
                    type = RecyclingType.FOOD,
                    selectedDays = foodDays,
                    onDaysChange = { foodDays = it }
                )
            }

            // 대형 폐기물
            item {
                RecyclingTypeCard(
                    type = RecyclingType.LARGE,
                    selectedDays = largeDays,
                    onDaysChange = { largeDays = it }
                )
            }

            // 전체 일정 요약
            item {
                if (recycleDays.isNotEmpty() || generalDays.isNotEmpty() ||
                    foodDays.isNotEmpty() || largeDays.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF9E6)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "📋 설정된 일정 요약",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (recycleDays.isNotEmpty()) {
                                ScheduleSummaryItem(RecyclingType.RECYCLABLE, recycleDays)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (generalDays.isNotEmpty()) {
                                ScheduleSummaryItem(RecyclingType.GENERAL, generalDays)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (foodDays.isNotEmpty()) {
                                ScheduleSummaryItem(RecyclingType.FOOD, foodDays)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (largeDays.isNotEmpty()) {
                                ScheduleSummaryItem(RecyclingType.LARGE, largeDays)
                            }
                        }
                    }
                }
            }

            // 하단 여백
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun RecyclingTypeCard(
    type: RecyclingType,
    selectedDays: Set<DayOfWeek>,
    onDaysChange: (Set<DayOfWeek>) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 제목
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type.emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = type.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 요일 버튼들 (가로 스크롤)
            val daysOfWeek = listOf(
                DayOfWeek.MONDAY to "월",
                DayOfWeek.TUESDAY to "화",
                DayOfWeek.WEDNESDAY to "수",
                DayOfWeek.THURSDAY to "목",
                DayOfWeek.FRIDAY to "금",
                DayOfWeek.SATURDAY to "토",
                DayOfWeek.SUNDAY to "일"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                daysOfWeek.forEach { (day, shortName) ->
                    FilterChip(
                        selected = selectedDays.contains(day),
                        onClick = {
                            onDaysChange(
                                if (selectedDays.contains(day)) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }
                            )
                        },
                        label = {
                            Text(
                                text = shortName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4CAF50),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF5F5F5),
                            labelColor = Color(0xFF666666)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 선택된 요일 표시
            if (selectedDays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                val dayText = selectedDays.sortedBy { it.value }.joinToString(", ") { day ->
                    when (day) {
                        DayOfWeek.MONDAY -> "월요일"
                        DayOfWeek.TUESDAY -> "화요일"
                        DayOfWeek.WEDNESDAY -> "수요일"
                        DayOfWeek.THURSDAY -> "목요일"
                        DayOfWeek.FRIDAY -> "금요일"
                        DayOfWeek.SATURDAY -> "토요일"
                        DayOfWeek.SUNDAY -> "일요일"
                    }
                }
                Text(
                    text = "선택: $dayText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ScheduleSummaryItem(type: RecyclingType, days: Set<DayOfWeek>) {
    val dayNames = days.sortedBy { it.value }.joinToString(", ") { day ->
        when (day) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
        }
    }
    Text(
        text = "${type.emoji} ${type.displayName}: 매주 ${dayNames}요일",
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF5D4037)
    )
}
