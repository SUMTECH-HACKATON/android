package net.cleanbin.myapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.cleanbin.myapplication.data.model.Achievement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementScreen(
    achievements: List<Achievement>,
    totalAnalysisCount: Int,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "업적",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기",
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
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 통계 카드
            item {
                StatsCard(
                    totalAnalyses = totalAnalysisCount,
                    unlockedCount = achievements.count { it.isUnlocked }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 업적 리스트
            items(achievements) { achievement ->
                AchievementItem(achievement = achievement)
            }
        }
    }
}

@Composable
fun StatsCard(totalAnalyses: Int, unlockedCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F8F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 나의 활동",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "총 분석", value = totalAnalyses.toString())
                StatItem(label = "달성한 업적", value = "$unlockedCount/7")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) {
                Color(0xFFF1F8F4)
            } else {
                Color(0xFFF5F5F5)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (achievement.isUnlocked) {
                            Color(0xFF4CAF50)
                        } else {
                            Color(0xFFE0E0E0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.icon,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 내용
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) {
                        Color(0xFF2E7D32)
                    } else {
                        Color(0xFF999999)
                    }
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (achievement.isUnlocked) {
                        Color(0xFF666666)
                    } else {
                        Color(0xFF999999)
                    }
                )

                if (!achievement.isUnlocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // 진행도 바
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${achievement.currentProgress}/${achievement.requiredCount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF999999)
                            )
                            Text(
                                text = "${(achievement.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF999999)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = achievement.progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF4CAF50),
                            trackColor = Color(0xFFE0E0E0)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✓ 달성 완료!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}
