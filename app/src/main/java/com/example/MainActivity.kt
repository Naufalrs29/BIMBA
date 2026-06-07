package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.AppState
import com.example.ui.*
import java.util.*

data class BimbaMenuItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val bgColor: Color,
    val accentColor: Color
)

enum class BimbaScreen {
    BERANDA,
    HURUF,
    MAIN_GAME,
    MENYUSUN_KATA,
    MEWARNAI,
    LAPORAN,
    KARAOKE,
    SUMMARY
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as BimbaApplication
            val factory = BimbaViewModelFactory(app.appStateRepository)
            val vModel: BimbaViewModel = viewModel(factory = factory)

            BimbaInteractiveApp(vModel)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BimbaInteractiveApp(viewModel: BimbaViewModel) {
    var activeScreen by remember { mutableStateOf(BimbaScreen.BERANDA) }
    val context = LocalContext.current

    val appState by viewModel.appState.collectAsStateWithLifecycle()
    val state = appState ?: AppState()

    // Screen navigation container with material colors
    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            BimbaTopAppBar(
                userStars = state.starsCount,
                avatarUrl = when (activeScreen) {
                    BimbaScreen.MEWARNAI -> "https://lh3.googleusercontent.com/aida-public/AB6AXuCFD5A-_aNjnGG0wEGkHL8qCwWj-uB42XNy8rmQio2Cj0LJTNSpOiIsb4reofpAobvKbxb7uyiFbjy_vViXwlYOo74n6OM4HjIF3gJEV36EXRpFGm01H2yqG0Hnx64iabBL-eFuoAOWlprz6RX44MWU-iajqrdJZ1MevV3xsqBjozPqzXqUay2qZ2Vulx9NwT9f1mXhqm46a3jM_IWwltuQCL5R8fuWDp5C4rHKMbw-XetLpHuHMOGaAhSZt14A0pkOcTlUk9Z_Y-I"
                    BimbaScreen.MAIN_GAME -> "https://lh3.googleusercontent.com/aida-public/AB6AXuCOgb7NWAxseKHT5RphU34LWKAM2sIO70_SEqWW4SkvtsIiUUiAW9Tg5I2RDjNLiceU6ljwklxk8xtgz38SFJ53GZWaX03QVg1LqycuwQZMsoUJzjU_CS0SehPCNvGjovllkTE0JHWiVhKLMtiWKqaELbgDX_oRyYiLRFQKih7r1LXN5GL-nugDScDsGXGreyUVzGS5nubIXMpCZirTPpY82SfUkw4Okigqwl2OpbU9U-f_MUBIpNIDmlaMPmmsmGngcUp7MAkvhqU"
                    else -> "https://lh3.googleusercontent.com/aida-public/AB6AXuCmUB5ZMwQlKv8v9GkwUDRbigHXvYEMKl8n8wqxf1V-9H5t6KJktYTA_LDFWTaZ95vJs6Hzt7tWjpI1TGp9fxBll5wRR5c91WgW7zoZZwuEUGXvgmxUeMCFIqw6hqctpK6LnOQWhLQ-d4f_YmwTupa8-tCmUnSoqede9iIuhhxlSElZJAjnclFD6XmSqsiB_8dl5vm7jp4FexpU3UsTmQ9dORm8xIvRyQ8eonmuRT-D86qQVm1fYbdWTI7F35SJM5Zso7b-aJmsXIQ"
                },
                onBackClicked = {
                    if (activeScreen != BimbaScreen.BERANDA) {
                        activeScreen = BimbaScreen.BERANDA
                    }
                },
                showBackBtn = activeScreen == BimbaScreen.KARAOKE || activeScreen == BimbaScreen.SUMMARY
            )
        },
        bottomBar = {
            BimbaBottomNavigationBar(
                activeScreen = activeScreen,
                onScreenSelected = { screen ->
                    activeScreen = screen
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen transition animator
            AnimatedContent(
                targetState = activeScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    BimbaScreen.BERANDA -> BerandaScreen(
                        viewModel = viewModel,
                        appState = state,
                        onMenuItemClicked = { item ->
                            when (item) {
                                "Belajar Huruf" -> activeScreen = BimbaScreen.HURUF
                                "Main Game" -> activeScreen = BimbaScreen.MAIN_GAME
                                "Bernyanyi" -> activeScreen = BimbaScreen.KARAOKE
                                "Mewarnai" -> activeScreen = BimbaScreen.MEWARNAI
                            }
                        },
                        onPlayAnimasiVideo = {
                            activeScreen = BimbaScreen.KARAOKE
                        }
                    )
                    BimbaScreen.HURUF -> BelajarHurufScreen(
                        viewModel = viewModel,
                        onPlayLettersGame = {
                            activeScreen = BimbaScreen.MENYUSUN_KATA
                        }
                    )
                    BimbaScreen.MAIN_GAME -> TebakKataScreen(
                        viewModel = viewModel,
                        onNextLevel = {
                            viewModel.dismissTebakSuccess()
                            activeScreen = BimbaScreen.SUMMARY
                        }
                    )
                    BimbaScreen.MENYUSUN_KATA -> JumbledLettersScreen(
                        viewModel = viewModel,
                        onFinishGame = {
                            viewModel.resetJumbledGame()
                            activeScreen = BimbaScreen.SUMMARY
                        }
                    )
                    BimbaScreen.MEWARNAI -> MewarnaiScreen(
                        viewModel = viewModel,
                        onDrawingFinish = {
                            activeScreen = BimbaScreen.SUMMARY
                        }
                    )
                    BimbaScreen.LAPORAN -> LaporanScreen(viewModel = viewModel, appState = state)
                    BimbaScreen.KARAOKE -> KaraokeScreen(viewModel = viewModel)
                    BimbaScreen.SUMMARY -> SummaryRewardScreen(
                        viewModel = viewModel,
                        onMainLagiClicked = {
                            activeScreen = BimbaScreen.MAIN_GAME
                        },
                        onBerandaClicked = {
                            activeScreen = BimbaScreen.BERANDA
                        }
                    )
                }
            }
        }
    }
}

// Top App Bar
@Composable
fun BimbaTopAppBar(
    userStars: Int,
    avatarUrl: String,
    onBackClicked: () -> Unit,
    showBackBtn: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showBackBtn) {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF005DA7)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD4E3FF))
                            .border(2.dp, Color(0xFF005DA7), CircleShape)
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Child Avatar Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Text(
                    text = "biMBA AIUEO",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF005DA7)
                )
            }

            // Star Points Badge
            Box(
                modifier = Modifier
                    .background(Color(0xFFFDD73B), shape = RoundedCornerShape(9999.dp))
                    .border(2.dp, Color(0xFF715D00), RoundedCornerShape(9999.dp))
                    .clickable { }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$userStars ⭐",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF715D00)
                    )
                }
            }
        }
    }
}

// Bottom Navigation Bar matching screenshot design
@Composable
fun BimbaBottomNavigationBar(
    activeScreen: BimbaScreen,
    onScreenSelected: (BimbaScreen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                Triple("Beranda", Icons.Default.Home, BimbaScreen.BERANDA),
                Triple("Huruf", Icons.Default.Abc, BimbaScreen.HURUF),
                Triple("Main", Icons.Default.SportsEsports, BimbaScreen.MAIN_GAME),
                Triple("Laporan", Icons.Default.ShowChart, BimbaScreen.LAPORAN)
            )

            navItems.forEach { (label, icon, screen) ->
                val isActive = activeScreen == screen || (screen == BimbaScreen.MAIN_GAME && activeScreen == BimbaScreen.MENYUSUN_KATA)
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onScreenSelected(screen) }
                        .let {
                            if (isActive) {
                                it
                                    .background(Color(0xFFFDD73B))
                                    .border(1.5.dp, Color(0xFF705D00), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            } else {
                                it.padding(horizontal = 16.dp, vertical = 8.dp)
                            }
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isActive) Color(0xFF705D00) else Color(0xFF717783),
                            modifier = Modifier.size(24.dp)
                        )
                        if (isActive) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF705D00),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Clean elegant squishy custom card shadow
@Composable
fun SquishyCard(
    borderColor: Color,
    bottomShadowColor: Color,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
    ) {
        // Base solid bottom shadow layout
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 6.dp)
                .background(bottomShadowColor, shape = RoundedCornerShape(24.dp))
        )
        // Main content foreground
        Column(
            modifier = Modifier
                .background(fillColor, shape = RoundedCornerShape(24.dp))
                .border(2.dp, borderColor, shape = RoundedCornerShape(24.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

// 1. Beranda Dashboard
@Composable
fun BerandaScreen(
    viewModel: BimbaViewModel,
    appState: AppState,
    onMenuItemClicked: (String) -> Unit,
    onPlayAnimasiVideo: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Video Activity Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onPlayAnimasiVideo() }
            ) {
                // Background image loaded via AsyncImage hotlink
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuD2qnYO-iXFeANqic9njhIqE-prNae_q395AcJ9HyOaNu3N3c7wK1mYdA8K3_S8FR8DWxi9tR7SOBEhg83s-rbZt1Y-uL2vZ_o8dvBjp9FTd-9Z81Lep7z1o75ka3U7HFvaktYQreyUC0vLrWfuRe63-58f0lLemaOdo4bqqvcUYjVSzV32WHlVQ8mp5pDgQ5nq28fG5J1l5EdSuzfj-1PQd466akrAqTc9KD3g4zxI4nQE4CU0sP_jlkd3b_-TNIutJqg06TWFnQU",
                    contentDescription = "Cheerful sun anim",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Black semi-transparent scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )
                // Center big soft play button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFFDD73B), CircleShape)
                        .border(3.dp, Color(0xFF715D00), CircleShape)
                        .align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video Lagu",
                        tint = Color(0xFF715D00),
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center)
                    )
                }

                // Banner bottom gradient caption titles
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Lagu biMBA Animasi",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Ayo bernyanyi bersama!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Learning Menu 2x2 Grid using customized visual designs
        item {
            val items = listOf(
                BimbaMenuItem("Belajar Huruf", Icons.Default.Abc, Color(0xFFD4E3FF), Color(0xFF005DA7)),
                BimbaMenuItem("Main Game", Icons.Default.SportsEsports, Color(0xFFFFE173), Color(0xFF705D00)),
                BimbaMenuItem("Bernyanyi", Icons.Default.MusicNote, Color(0xFFFFDAD8), Color(0xFFAA2D32)),
                BimbaMenuItem("Mewarnai", Icons.Default.Palette, Color(0xFFC8E6C9), Color(0xFF2E7D32))
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Item 1: Belajar Huruf
                    Box(modifier = Modifier.weight(1f)) {
                        SquishyGridMenuButton(
                            title = items[0].title,
                            icon = items[0].icon,
                            bgColor = items[0].bgColor,
                            accentClr = items[0].accentColor,
                            onClick = { onMenuItemClicked(items[0].title) }
                        )
                    }
                    // Item 2: Main Game
                    Box(modifier = Modifier.weight(1f)) {
                        SquishyGridMenuButton(
                            title = items[1].title,
                            icon = items[1].icon,
                            bgColor = items[1].bgColor,
                            accentClr = items[1].accentColor,
                            onClick = { onMenuItemClicked(items[1].title) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Item 3: Bernyanyi
                    Box(modifier = Modifier.weight(1f)) {
                        SquishyGridMenuButton(
                            title = items[2].title,
                            icon = items[2].icon,
                            bgColor = items[2].bgColor,
                            accentClr = items[2].accentColor,
                            onClick = { onMenuItemClicked(items[2].title) }
                        )
                    }
                    // Item 4: Mewarnai
                    Box(modifier = Modifier.weight(1f)) {
                        SquishyGridMenuButton(
                            title = items[3].title,
                            icon = items[3].icon,
                            bgColor = items[3].bgColor,
                            accentClr = items[3].accentColor,
                            onClick = { onMenuItemClicked(items[3].title) }
                        )
                    }
                }
            }
        }

        // Stats progress card
        item {
            SquishyCard(
                borderColor = Color(0xFFC1C7D3),
                bottomShadowColor = Color(0xFFE1E3E4),
                fillColor = Color(0xFFEDEEEF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Semicircular / Symmetrical percent bubble badge with dynamic tracker icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.White, CircleShape)
                            .border(3.dp, Color(0xFFFDD73B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${appState.levelProgressPercent}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF705D00)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .background(Color(0xFF005DA7), CircleShape)
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Upward score chart metrics",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Progres Belajar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF191C1D)
                        )
                        Text(
                            text = "Hebat! Kamu sudah hampir menyelesaikan level ini.",
                            fontSize = 14.sp,
                            color = Color(0xFF414751)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom thick slider progress bar with bubbled rounded fill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(Color(0xFFFFFBFE), RoundedCornerShape(9999.dp))
                                .border(1.5.dp, Color(0xFFC1C7D3), RoundedCornerShape(9999.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.85f)
                                    .background(Color(0xFF705D00), RoundedCornerShape(9999.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SquishyGridMenuButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    accentClr: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 6.dp)
                .background(bgColor, shape = RoundedCornerShape(20.dp))
                .border(2.dp, accentClr, shape = RoundedCornerShape(20.dp))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .border(2.dp, accentClr, shape = RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(bgColor, shape = RoundedCornerShape(12.dp))
                    .border(2.dp, accentClr, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentClr,
                    modifier = Modifier.size(36.dp)
                )
            }
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = accentClr,
                textAlign = TextAlign.Center
            )
        }
    }
}

// 2. Belajar Huruf Card Activity
@Composable
fun BelajarHurufScreen(
    viewModel: BimbaViewModel,
    onPlayLettersGame: () -> Unit
) {
    val context = LocalContext.current
    val letterIdx by viewModel.currentLetterIdx.collectAsStateWithLifecycle()
    val activeItem by viewModel.activeLetterInfo.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Progress Bubble Header indicator
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(Color(0xFFE7E8E9), RoundedCornerShape(9999.dp))
                    .border(2.dp, Color(0xFFC1C7D3), RoundedCornerShape(9999.dp))
            ) {
                // mascot sliding track position calculations
                val percentageProgress = (letterIdx + 1).toFloat() / 3f
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(percentageProgress)
                        .background(Color(0xFF705D00), RoundedCornerShape(9999.dp))
                )
                Icon(
                    imageVector = Icons.Default.ChildCare,
                    contentDescription = "Mascot child indicator",
                    tint = Color(0xFFAA2D32),
                    modifier = Modifier
                        .offset(x = (200 * percentageProgress).dp)
                        .size(32.dp)
                        .align(Alignment.CenterStart)
                )
            }
            Text(
                text = "Huruf ${letterIdx + 1} dari 3",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF717783)
            )
        }

        // Large alphabet learning canvas card
        SquishyCard(
            borderColor = Color(0xFF005DA7),
            bottomShadowColor = Color(0xFFC1C7D3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Speaker audio sound card selector button top-right and clickable speech trigger
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Mengeja bunyi huruf: ${activeItem.letter}", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp)
                        .background(Color(0xFFFFE173), CircleShape)
                        .border(2.dp, Color(0xFF715D00), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Read Letter Audio Spelling",
                        tint = Color(0xFF715D00)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big capital styled letter
                    Text(
                        text = activeItem.letter,
                        fontSize = 110.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF005DA7),
                        lineHeight = 110.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dashed illustration image card loaded from AIDA URL
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(Color(0xFFF3F4F5), RoundedCornerShape(24.dp))
                            .border(2.dp, Color(0xFFC1C7D3), RoundedCornerShape(24.dp))
                            .clickable {
                                Toast.makeText(context, "Objek: ${activeItem.wordName}", Toast.LENGTH_SHORT).show()
                            }
                            .padding(12.dp)
                    ) {
                        AsyncImage(
                            model = activeItem.imageUrl,
                            contentDescription = activeItem.wordName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Text spells labels
                    Text(
                        text = activeItem.wordName.map { "$it " }.joinToString("").trim(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF005DA7)
                    )
                }
            }
        }

        // letters switching control arrow keys
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.previousLetter() },
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFAA2D32), CircleShape)
                    .border(2.dp, Color(0xFF410006), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous alphabet character icon",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Big yellow letters game transition player trigger
            IconButton(
                onClick = onPlayLettersGame,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFFDD73B), CircleShape)
                    .border(3.dp, Color(0xFF715D00), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Compile spell puzzle matching game",
                    tint = Color(0xFF715D00),
                    modifier = Modifier.size(44.dp)
                )
            }

            IconButton(
                onClick = { viewModel.nextLetter() },
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF005DA7), CircleShape)
                    .border(2.dp, Color(0xFF001C39), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Alphabet letter screen card",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Text(
            text = "Ayo dengarkan bunyi huruf '${activeItem.letter}' dan tekan gambarnya!",
            textAlign = TextAlign.Center,
            color = Color(0xFF414751),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

// 3. Main Game: Tebak Kata ("Mana Gambar Bola?") SCREEN
@Composable
fun TebakKataScreen(
    viewModel: BimbaViewModel,
    onNextLevel: () -> Unit
) {
    val context = LocalContext.current
    val progress by viewModel.tebakProgress.collectAsStateWithLifecycle()
    val showSuccess by viewModel.showTebakSuccessOverlay.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Slider tracker track progress bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(Color(0xFFE1E3E4), RoundedCornerShape(9999.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.6f)
                            .background(Color(0xFFFDD73B), RoundedCornerShape(9999.dp))
                    )
                }
            }

            // Central auditory vocal question button and title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Mana gambar BOLA?", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFF2976C7), CircleShape)
                        .border(3.dp, Color(0xFF004883), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Voicing Question spelling",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "Mana gambar BOLA?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF005DA7)
                )
                Text(
                    text = "Dengarkan dan pilih jawabanmu!",
                    fontSize = 16.sp,
                    color = Color(0xFF717783)
                )
            }

            // Choice 3-rows grid / List of cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                viewModel.tebakItemsList.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .border(2.dp, item.borderClr, RoundedCornerShape(20.dp))
                            .clickable {
                                if (item.isCorrect) {
                                    viewModel.selectTebakAnswer(item)
                                } else {
                                    Toast.makeText(context, "Coba lagi ya!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF8F9FA))
                                .border(1.5.dp, item.borderClr, CircleShape)
                                .padding(8.dp)
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Text(
                            text = item.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191C1D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Lewati & Bantuan action footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Materi dilewati", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE7E8E9)),
                    shape = RoundedCornerShape(9999.dp)
                ) {
                    Text("Lewati", color = Color(0xFF414751), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "Tips: Tekan tombol speaker untuk bantuan mengeja!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB3B0)),
                    shape = RoundedCornerShape(9999.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Help tips", tint = Color(0xFF8C1520))
                        Text("Bantuan", color = Color(0xFF8C1520), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // Success dialog popup with stars
        if (showSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "",
                                tint = Color(0xFFFDD73B),
                                modifier = Modifier.size(48.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "",
                                tint = Color(0xFFFDD73B),
                                modifier = Modifier.size(64.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "",
                                tint = Color(0xFFFDD73B),
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Text(
                            text = "Hebat!",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF705D00),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Kamu pintar sekali!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF191C1D),
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = onNextLevel,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005DA7)),
                            shape = RoundedCornerShape(9999.dp)
                        ) {
                            Text("Lanjut", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

// 4. Game Menyusun Kata Jumbled Letters Grid
@Composable
fun JumbledLettersScreen(
    viewModel: BimbaViewModel,
    onFinishGame: () -> Unit
) {
    val context = LocalContext.current
    val progress by viewModel.jumbledProgress.collectAsStateWithLifecycle()
    val showSuccess by viewModel.showJumbledSuccessOverlay.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Task spelling card
            SquishyCard(
                borderColor = Color(0xFF005DA7),
                bottomShadowColor = Color(0xFFD4E3FF),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Progress tracker bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(Color(0xFFEDEEEF), RoundedCornerShape(9999.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(Color(0xFF2976C7), RoundedCornerShape(9999.dp))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subject Image loads Apel Aida illustration
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color(0xFFF3F4F5), RoundedCornerShape(20.dp))
                        .padding(12.dp)
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCSL_Cm-QugYQggC-1oqNgBuSIE5FxseEzGSAt21siq4P98VGcifObdm67jEA29X1OUP8qo8wzT5RCnV_s5kyZ4-ZjuuIv-cAa5gnanMU8AnjoeQxiJe-WTYCWXonN9YqHdg6m-sw-EfKwhK5ZzVI4lYi3OYZ-WoYl-ESvXVlroFayxefpRmXpOGQY73RQKa73m3pPpcKG-1pyX6f_0CiRR0VmRRb_ComyEK1lbGclvm_v-UdlPvY8l070evxneup5vM9O_-KA8J6Y",
                        contentDescription = "Apel",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Word Slots (Target letters placing container)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val filledLetter = viewModel.targetSlots[i]
                        val expectedLetter = "APEL"[i].toString()

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (filledLetter.isNotEmpty()) Color(0xFFFDD73B) else Color(0xFFF3F4F5),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    3.dp,
                                    if (filledLetter.isNotEmpty()) Color(0xFF715D00) else Color(0xFFC1C7D3),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filledLetter,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF005DA7)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hint instructions
                Box(
                    modifier = Modifier
                        .background(Color(0xFF2976C7).copy(alpha = 0.1f), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Susun huruf: A - P - E - L",
                        color = Color(0xFF005DA7),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Jumbled letters Pool list (Tappable source letters)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val poolLetters = listOf("P", "L", "A", "E")
                poolLetters.forEachIndexed { index, letter ->
                    val visible = viewModel.poolLettersVisibility[index]
                    if (visible) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFFFFE173), RoundedCornerShape(16.dp))
                                .border(2.dp, Color(0xFF705D00), RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.placeLetterInNextSlot(index, letter)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // bottom shadow 3d feedback is achieved via offset design inside custom graphics
                            Text(
                                text = letter,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF705D00)
                            )
                        }
                    } else {
                        // Empty spacer preserving dimensions
                        Box(modifier = Modifier.size(64.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Helper text tip
            Text(
                text = "Sentuh huruf di atas sesuai urutan ejaan!",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                color = Color(0xFF717783),
                fontWeight = FontWeight.Medium
            )
        }

        // Win popup reward overlay trigger
        if (showSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = Color(0xFFFDD73B), modifier = Modifier.size(48.dp))
                            Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = Color(0xFFFDD73B), modifier = Modifier.size(64.dp))
                            Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = Color(0xFFFDD73B), modifier = Modifier.size(48.dp))
                        }

                        Text(
                            text = "HEBAT!\nKAMU PINTAR",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF005DA7),
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = onFinishGame,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD73B)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(9999.dp)
                        ) {
                            Text("MAIN LAGI", color = Color(0xFF715D00), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

// 5. Mewarnai Screen with interactive line art canvas draw paths
@Composable
fun MewarnaiScreen(
    viewModel: BimbaViewModel,
    onDrawingFinish: () -> Unit
) {
    val context = LocalContext.current
    val activeClr by viewModel.selectedDrawColor.collectAsStateWithLifecycle()
    val eraserActive by viewModel.isEraserActive.collectAsStateWithLifecycle()

    var activeStrokePoints = remember { mutableStateListOf<Offset>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Interactive Drawing Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFC1C7D3), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
        ) {
            // Background dotted radial grid pattern is simulated on the draw behind canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                activeStrokePoints.clear()
                                activeStrokePoints.add(startOffset)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                activeStrokePoints.add(change.position)
                            },
                            onDragEnd = {
                                val finalColor = if (eraserActive) Color.White else activeClr
                                val strokeWidth = if (eraserActive) 48f else 16f
                                viewModel.addStroke(
                                    DrawingStroke(
                                        points = activeStrokePoints.toList(),
                                        color = finalColor,
                                        strokeWidth = strokeWidth
                                    )
                                )
                                activeStrokePoints.clear()
                            }
                        )
                    }
            ) {
                // 1. Dotted pattern
                val dotRadius = 2f
                val spacing = 40f
                for (x in 0..size.width.toInt() step spacing.toInt()) {
                    for (y in 0..size.height.toInt() step spacing.toInt()) {
                        drawCircle(
                            color = Color(0xFFC1C7D3).copy(alpha = 0.35f),
                            radius = dotRadius,
                            center = Offset(x.toFloat(), y.toFloat())
                        )
                    }
                }

                // 2. Clear Draw Strokes compiled on history
                viewModel.drawingStrokes.forEach { stroke ->
                    if (stroke.points.size > 1) {
                        for (i in 0 until stroke.points.size - 1) {
                            drawLine(
                                color = stroke.color,
                                start = stroke.points[i],
                                end = stroke.points[i + 1],
                                strokeWidth = stroke.strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                // 3. Current active stroke path drawing
                if (activeStrokePoints.size > 1) {
                    val strokeClr = if (eraserActive) Color.White else activeClr
                    val width = if (eraserActive) 48f else 16f
                    for (i in 0 until activeStrokePoints.size - 1) {
                        drawLine(
                            color = strokeClr,
                            start = activeStrokePoints[i],
                            end = activeStrokePoints[i + 1],
                            strokeWidth = width,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // Grayscale outline of the Apple placed over
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC0ORJSBexMREgB6GOW6axCE6cinNrcxTJGVRQikYW4oZB2ize8sG2Oe6clS0jQ8FTCSjxPbwfUXVfURBjHSbKQ9LMQa1f8PhurYP4X3Q3dvpfddqA1z4ueKEoRzigheLRWNy4zD0XidAnrfQvrmPX3sSFu3oKb6HpLtmpb_Rrsk95y3k4EN8ZD0AfNnj49XD5Hic5Sk1no251sGw13Khp2C9-LvRHXeQFtn_LM9KBWqv4-aAYKkNuiGMS5scYtabqpYptVxmYv4vY",
                contentDescription = "Grayscale apple drawing layout guide",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentScale = ContentScale.Fit,
                alpha = 0.4f // subtle overlay so child can paint behind it! This represents a high-key immersive design!
            )

            // Dynamic float guide box
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color(0xFFFFDAD8), RoundedCornerShape(9999.dp))
                    .border(2.dp, Color(0xFFCC4548), RoundedCornerShape(9999.dp))
                    .align(Alignment.TopStart)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Lightbulb hint", tint = Color(0xFFCC4548), modifier = Modifier.size(20.dp))
                    Text("Ayo Mewarnai! ap-e-l", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFCC4548))
                }
            }

            // Word spelling footer inside outline
            Text(
                text = "A - P - E - L",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF005DA7).copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )

            // Drawing Tool indicators bottom-right
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { viewModel.setDrawColor(activeClr) },
                    modifier = Modifier
                        .background(if (!eraserActive) Color(0xFF005DA7) else Color(0xFFE1E3E4), shape = RoundedCornerShape(12.dp))
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = "Brush Drawing Tool",
                        tint = if (!eraserActive) Color.White else Color(0xFF717783)
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleEraser() },
                    modifier = Modifier
                        .background(if (eraserActive) Color(0xFF005DA7) else Color(0xFFE1E3E4), shape = RoundedCornerShape(12.dp))
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalMall, // visual substitute for eraser
                        contentDescription = "Eraser active toggle",
                        tint = if (eraserActive) Color.White else Color(0xFF717783)
                    )
                }
            }
        }

        // Color palette drawer panel with Selesai trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE7E8E9), RoundedCornerShape(20.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val colorChoices = listOf(
                Color(0xFFFF5252), // Red
                Color(0xFFFFD740), // Yellow
                Color(0xFF448AFF), // Blue
                Color(0xFF69F0AE), // Green
                Color(0xFFFFAB40), // Orange
                Color.Black
            )

            colorChoices.forEach { color ->
                val isSelected = activeClr == color && !eraserActive
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 1.5.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            viewModel.setDrawColor(color)
                        }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDrawingFinish,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC4548)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Default.Grade, contentDescription = "Finished illustration badge", tint = Color.White, modifier = Modifier.size(20.dp))
                    Text("SELESAI", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// 6. Parental Reports / Laporan Dashboard Screen
@Composable
fun LaporanScreen(
    appState: AppState,
    viewModel: BimbaViewModel
) {
    val context = LocalContext.current
    val isShared by viewModel.isShareSuccessActive.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Parent encouragement card with dynamic hotlinked rocket
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF005DA7), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hebat, Ayah & Bunda! 🌟",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ananda menunjukkan kemajuan yang luar biasa minggu ini. Tetap semangati perjalanannya mengenal dunia huruf ya!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }

                    Box(modifier = Modifier.size(80.dp)) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBcqVQDxBJWj5BvzHtdajgZlE76NpwqLuu0C65XgAxKRXBs5-wE6jYGNKP9qWzJifWiuPbgBU8YZFMMA6yUKbAGuNU8j-bNnobE5XNEbeaIHZ_vA4KfQCRGxtJaIbrpFtFi9_mkWx6wIojYXo6owmlPsFEUXSQCC7w9EvYE7pT2jGtc4NeryQuKB78sK0OFzv2ysAkq3kL3TU_FIS37uCux1Gt6AUL7W8EqaUcQAWTZrkCYdYCIXK4XsY0jmqgV7HZQQzxNK4J6yfU",
                            contentDescription = "Celebration rocket learning stats indicator",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        // Stats & Progress Overview Bento Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Learning Bar Chart Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(2.dp, Color(0xFFC1C7D3), RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Kemajuan\nBelajar",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF005DA7),
                                fontSize = 16.sp,
                                lineHeight = 20.sp
                            )
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "",
                                tint = Color(0xFF717783)
                            )
                        }

                        // Bar values list
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val values = listOf("Sen" to 0.6f, "Sel" to 0.4f, "Rab" to 0.85f, "Kam" to 0.7f)
                            values.forEach { (day, hPercent) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .fillMaxHeight(hPercent)
                                            .background(Color(0xFF2976C7), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(day, fontSize = 10.sp, color = Color(0xFF717783), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Mastered Letters Check card metrics
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(2.dp, Color(0xFFFDD73B), RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Huruf yang\nDikuasai",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF705D00),
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.height(80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val mastered = appState.masteredLetters.split(",")
                            items(mastered) { l ->
                                Row(
                                    modifier = Modifier
                                        .background(Color(0xFFFFE173), RoundedCornerShape(8.dp))
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        l,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF705D00),
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success check",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Achievement card with parents sharing Happiness toggle
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFDAD8), RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFFCC4548), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Emoji trophies and rewards card",
                            tint = Color(0xFFCC4548),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pencapaian Baru!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF410006)
                        )
                        Text(
                            text = "Lulus Modul Membaca Level 1",
                            fontSize = 14.sp,
                            color = Color(0xFF8C1520)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.triggerShareHappiness()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC4548)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bagikan", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Detailed Stats metrics (study durations and study target)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.8f)
                        .background(Color(0xFFEDEEEF), RoundedCornerShape(24.dp))
                        .border(1.5.dp, Color(0xFFC1C7D3), RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Waktu Belajar",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF005DA7),
                        fontSize = 15.sp
                    )
                    // Study bar metric indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(Color.White, RoundedCornerShape(9999.dp))
                            .border(1.5.dp, Color(0xFFC1C7D3), RoundedCornerShape(9999.dp)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.75f)
                                .background(Color(0xFF2976C7), RoundedCornerShape(9999.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "45 Menit",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Text(
                        text = "Target harian: 60 Menit",
                        fontSize = 12.sp,
                        color = Color(0xFF717783),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Daily study consecutive streak metrics
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .background(Color(0xFFE7E8E9), RoundedCornerShape(24.dp))
                        .border(1.5.dp, Color(0xFFC1C7D3), RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Streak rewards badge",
                        tint = Color(0xFF705D00),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "7 Hari",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF191C1D)
                    )
                    Text(
                        text = "Belajar Beruntun",
                        fontSize = 11.sp,
                        color = Color(0xFF717783),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Share happy toast
    if (isShared) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissShareSuccess() },
            title = { Text("Membagikan Kebahagiaan! 🎉") },
            text = { Text("Pencapaian ananda belajar biMBA AIUEO sukses dibagikan ke Ayah, Bunda & Educators! Anda memperoleh bonus +10 ⭐.") },
            confirmButton = {
                Button(onClick = { viewModel.dismissShareSuccess() }) {
                    Text("Keren!")
                }
            }
        )
    }
}

// 7. Video Karaoke Screen (Karaoke player with slide controls & text highlighting)
@Composable
fun KaraokeScreen(viewModel: BimbaViewModel) {
    val playState by viewModel.isVideoPlaying.collectAsStateWithLifecycle()
    val progressSlider by viewModel.videoProgress.collectAsStateWithLifecycle()
    val micActive by viewModel.isMicrophoneActive.collectAsStateWithLifecycle()
    val repeatActive by viewModel.isRepeatEnabled.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Video section container showing simulated hills backdrop and interactive bunny mascot
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .border(4.dp, Color(0xFF2976C7), RoundedCornerShape(24.dp))
        ) {
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBFtCpVSizxhxSEGb4hwV-wq7izf12zd0NikZXYlRRSpdaeDaMVBWi3feoSEQfkveIzgstjZTWsWMf5W5NC2TLGcFVPXzA1uMRNR5Vx231M28H3qEuRDHXEsN9xFyYP83c6R_d6UW8nnOVqG7La6zPrrvF66jBPlOwGEFhxfKDoQKkI6DSll_K2wuMx31n_HtcR5zg8MQnkwjbcXExHPogIPhOIv2llqb6cwUIOXj1SRtt2PrWehKSJ82mEIw5VPkQfuV6PKlU5wAU",
                contentDescription = "Simulated hills video background layout",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlaid round bunny character illustration on bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(90.dp)
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBG7HslzP9If-Kwt5JAx18Nm_hT5mW4UMixeav5ZModqAUTml6qaTHXzl-_ktkLh_y9GtmcBCquvLr_IzJPQk8bdySnZ-QklUoAZ4AWG49G9ju4jQoHJIVdoX8IUWLBiHFvnz-7OG28Ijl3w2Q6cT9n5B2GQnb2Uv8kzUY9HXdeSu6jhscLJEOmzst_3PVfLH0bBsLr0-AoA5w8dmigY3uVGTp_fL9A1zSJtwzJOAyNH0i8s_kMcBbIAc8O-d6phwLLWikBkNpyPYc",
                    contentDescription = "Immersive bunny character",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            // Big centered pause play toggle overlay
            IconButton(
                onClick = { viewModel.toggleVideoPlay() },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .background(Color(0xFFFDD73B), CircleShape)
                    .border(2.dp, Color(0xFF715D00), CircleShape)
            ) {
                Icon(
                    imageVector = if (playState) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Toggle video player play pause",
                    tint = Color(0xFF715D00),
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Subtitle lyrics highlight container section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3F4F5), RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFFC1C7D3), RoundedCornerShape(20.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFDAD8), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Sedang Bernyanyi: Lagu AIUEO",
                        color = Color(0xFFAA2D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "A I U E O",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (playState) Color(0xFF005DA7) else Color(0xFF717783)
                )
                Text(
                    text = "Ayo kita belajar huruf",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (playState) Color(0xFF005DA7).copy(alpha = 0.7f) else Color(0xFF717783).copy(alpha = 0.5f)
                )
            }
        }

        // Detailed progress slider controller row
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0:45", fontWeight = FontWeight.Bold, color = Color(0xFF717783))
                Text("2:30", fontWeight = FontWeight.Bold, color = Color(0xFF717783))
            }
            Slider(
                value = progressSlider,
                onValueChange = { viewModel.setVideoProgress(it) },
                colors = SliderDefaults.colors(
                    activeTrackColor = Color(0xFF005DA7),
                    thumbColor = Color(0xFFFDD73B)
                )
            )
        }

        // Action controls buttons (Skip, Speak Toggle, Repeat loops)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .background(Color(0xFFE1E3E4), CircleShape)
                    .size(48.dp)
            ) {
                Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Prev", tint = Color(0xFF005DA7))
            }

            IconButton(
                onClick = { viewModel.toggleVideoPlay() },
                modifier = Modifier
                    .background(Color(0xFFFDD73B), CircleShape)
                    .border(2.dp, Color(0xFF715D00), CircleShape)
                    .size(72.dp)
            ) {
                Icon(
                    imageVector = if (playState) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color(0xFF715D00),
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .background(Color(0xFFE1E3E4), CircleShape)
                    .size(48.dp)
            ) {
                Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = Color(0xFF005DA7))
            }

            IconButton(
                onClick = { viewModel.toggleMicrophone() },
                modifier = Modifier
                    .background(if (micActive) Color(0xFF2976C7) else Color(0xFFE1E3E4), RoundedCornerShape(12.dp))
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Toggle mic micActive",
                    tint = if (micActive) Color.White else Color(0xFF005DA7)
                )
            }

            IconButton(
                onClick = { viewModel.toggleRepeat() },
                modifier = Modifier
                    .background(if (repeatActive) Color(0xFF2976C7) else Color(0xFFE1E3E4), RoundedCornerShape(12.dp))
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Toggle repeatEnabled loops",
                    tint = if (repeatActive) Color.White else Color(0xFF005DA7)
                )
            }
        }

        // Bento playlist track indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFE173).copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                .border(2.dp, Color(0xFF705D00).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFDD73B), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC3LCfOPZy0sk97oV3-uX4erhEbnh_pv_1RoNBhbpi1DGu9Ywx_eXPJOl9ixyWC27WICx03qyl0Vaw_a4SH2GUHXlrywSjmUgGDmRbCPh-oQHy9tzeyy9AbHrRtJvEcuvNmD1GjKAppvhxagrCo2uCTg2kXysaSGK1v3buesFhqT2YHYiGMDsX6t0FY9Gu-LgdsbqYUXkFLtoHIXXoeXGeQFQbo9EtZr2_MOetf9OQCqH7Hq7Wmjif1z_zov3rcAUkPdVVMY5PKLNw",
                    contentDescription = "Next track playlist layout cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Selanjutnya: Huruf B-D",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF705D00),
                    fontSize = 15.sp
                )
                Text(
                    text = "Belajar mengeja kata sederhana bersama Kakak Animasi.",
                    color = Color(0xFF705D00).copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF005DA7), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Stars features",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// 8. Reward & Finished Materis Summary Screen
@Composable
fun SummaryRewardScreen(
    viewModel: BimbaViewModel,
    onMainLagiClicked: () -> Unit,
    onBerandaClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Popping celebration stars header top reward
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = Color(0xFFFDD73B), modifier = Modifier.size(48.dp))
                Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = Color(0xFFFDD73B), modifier = Modifier.size(64.dp))
                Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = Color(0xFFFDD73B), modifier = Modifier.size(48.dp))
            }

            Text(
                text = "Hebat! Kamu Pintar Sekali!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF005DA7),
                textAlign = TextAlign.Center
            )
        }

        // Mascot orange animal illustration
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color(0xFFD4E3FF).copy(alpha = 0.35f))
        ) {
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBDxPyhZP52xpwKhcRCsGgcgwzxeueIdToLDBRd0XyvpYn-aFgI3g7TqsOosdTBNF5QjUSgZjmZBrOleGeN9ZQGO0xBJqWXuzsjIjIwhZEdeLsJ5zihb18bvbBBFzdOU9IXt6bs1lyMtO1VZKVcrpR_Mij5CL2G3555EoVVJwYG5H6iC6El8B-3phhl00lWCEjWHJzP2z1av2ZFfaRLAd3SSecydbQkxU86wUQYbFILlNUkuekwyz0Wje6RjFUAugF0UoA2qHkY-3s",
                contentDescription = "Encouragement Mascot thumbs-up biMBA illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Bento Summary cards show mastered letters: A (Apel), B (Buku)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Materi Hari Ini:",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF717783),
                fontSize = 18.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Letter A card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(3.dp, Color(0xFF005DA7), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("A", fontSize = 54.sp, fontWeight = FontWeight.Bold, color = Color(0xFF005DA7))
                        Text("Apel", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1D))
                    }
                }

                // Letter B card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .border(3.dp, Color(0xFFAA2D32), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("B", fontSize = 54.sp, fontWeight = FontWeight.Bold, color = Color(0xFFAA2D32))
                        Text("Buku", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1D))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Play Again & Home layout action buttons at footer matching screenshot
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onMainLagiClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD740)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(0xFF715D00))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "", tint = Color(0xFF715D00))
                    Text("Main Lagi", color = Color(0xFF715D00), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Button(
                onClick = onBerandaClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDEEEF)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(0xFFC1C7D3))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "", tint = Color(0xFF414751))
                    Text("Beranda", color = Color(0xFF414751), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}
