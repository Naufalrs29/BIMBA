package com.example.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppState
import com.example.data.AppStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Structures for the interactive screens
data class LetterInfo(
    val letter: String,
    val wordName: String,
    val imageUrl: String,
    val syllables: List<String>
)

data class TebakItem(
    val name: String,
    val borderClr: Color,
    val imageUrl: String,
    val isCorrect: Boolean
)

data class JumbledGame(
    val correctWord: String,
    val imageUrl: String,
    val lettersPool: List<String>,
    val placeholderHint: String
)

data class DrawingStroke(
    val points: List<androidx.compose.ui.geometry.Offset>,
    val color: Color,
    val strokeWidth: Float
)

class BimbaViewModel(private val repository: AppStateRepository) : ViewModel() {

    // Persistent State
    val appState: StateFlow<AppState?> = repository.appStateFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppState()
        )

    // Letters Learning State
    private val lettersList = listOf(
        LetterInfo(
            "B",
            "BOLA",
            "https://lh3.googleusercontent.com/aida-public/AB6AXuAyNLECbDWV_N2kmN5YiK8WioWm5Pud6oVqN11iDJVxZKSKudK-lNbq_Z1iTBzkoVfjAFpOa95omNc2yW-qMPk9ixGs43TxUcLBGiL5u4-K1SxgHaWpUM5bgmt0Rvpi6_4jQHDzb8MmwrBtTJslUw3nW9y5vrU30_D-scErQr4G3ZKVL22u2PL8mV0Bcl_i4wFL4zxmmUnh7W97j9mr58IC7ygHjknqthh7Z_eWyPEwLiM9DAVtDFb8r24u0A1yyRhkR5Gvqj3M1VA",
            listOf("BO", "LA")
        ),
        LetterInfo(
            "A",
            "APEL",
            "https://lh3.googleusercontent.com/aida-public/AB6AXuCSL_Cm-QugYQggC-1oqNgBuSIE5FxseEzGSAt21siq4P98VGcifObdm67jEA29X1OUP8qo8wzT5RCnV_s5kyZ4-ZjuuIv-cAa5gnanMU8AnjoeQxiJe-WTYCWXonN9YqHdg6m-sw-EfKwhK5ZzVI4lYi3OYZ-WoYl-ESvXVlroFayxefpRmXpOGQY73RQKa73m3pPpcKG-1pyX6f_0CiRR0VmRRb_ComyEK1lbGclvm_v-UdlPvY8l070evxneup5vM9O_-KA8J6Y",
            listOf("A", "PEL")
        ),
        LetterInfo(
            "C",
            "CERI",
            "https://lh3.googleusercontent.com/aida-public/AB6AXuB_fhFZt4TXgmVPHuHVBDxpTiF0QFI1m6W8B2_0agc2CzDSFvH6py-uyQctkqdFEq71_HaFqoYyrQA9Am74cVZMWVS2oiWuoXqDsR2hxO6q3dv2MvlPrvnaWHNLBDws3hLJo2gtDrrYbFqgGYybQXRkenfSnINwCwywvpeYbtULbU37ykurJDSM_66EhnjhIPhSG1aEwr1BVGmSe5CNzTl3Xmgd1G-Ar3NAkkiaGn6byyG6T8vIECqradO-qFSslb6qW4QWjTvtJA4",
            listOf("CE", "RI")
        )
    )

    private val _currentLetterIdx = MutableStateFlow(0)
    val currentLetterIdx = _currentLetterIdx.asStateFlow()

    val activeLetterInfo: StateFlow<LetterInfo> = MutableStateFlow(lettersList[0]).apply {
        viewModelScope.launch {
            _currentLetterIdx.collect { idx ->
                value = lettersList[idx % lettersList.size]
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), lettersList[0])

    fun nextLetter() {
        _currentLetterIdx.value = (_currentLetterIdx.value + 1) % lettersList.size
    }

    fun previousLetter() {
        var prev = _currentLetterIdx.value - 1
        if (prev < 0) prev = lettersList.size - 1
        _currentLetterIdx.value = prev
    }

    // Interactive Quiz Game (Tebak kata "Mana gambar BOLA?")
    val tebakItemsList = listOf(
        TebakItem(
            "Apel",
            Color(0xFFCC4548),
            "https://lh3.googleusercontent.com/aida-public/AB6AXuAzdNlYgXuIGZewCb1hBvL6QCENEA1cmY_OZa_AmeuHZddmcoy3jhopW0eSKvxAO6MeW00LxIl6HoHG75YKMNW-IJt9TcT9a6T6dm4ilKBTSSGJmSPwGdD8zRd3IoiSuvqe14jJh1IbdwCSXpmaKGVMJ5Xl-xXsWa4qstRzc6S7F5t3e9vSs9hhUA3_yY46WlbUChS2GuIHn0TGz2u6OQvSWhvyQrvlXs_5CpuCi6iJ4Orp1Og9LaVd3ERpzCyBksJutIryAp9MLhg",
            false
        ),
        TebakItem(
            "Bola",
            Color(0xFF2976C7),
            "https://lh3.googleusercontent.com/aida-public/AB6AXuAvv2kQ9H9DQldFFSlqihaEFpOax0sPAx66p-dyUB3u8VE-MqIJPwrLkxFVOFED9zuH83WHaQZ7kHp_8l8BEtBLxoIomGdmg8jCk1AIVGP7aKTtMVxaXRxZCpOgNLYVpbQe0QaIJXT8uoFJKu3u0k9SFXChoXfpwmmGo4FdQd1woxWIblehHRPgSe_f6krw267edbDfyLrMa4bzIN_CBmuFBw6B_WDq8TXjgrtN3oy0_wxNxGjoU-o39uhh19TttfyPbzBCzuCq7_g",
            true
        ),
        TebakItem(
            "Ceri",
            Color(0xFF705D00),
            "https://lh3.googleusercontent.com/aida-public/AB6AXuB_fhFZt4TXgmVPHuHVBDxpTiF0QFI1m6W8B2_0agc2CzDSFvH6py-uyQctkqdFEq71_HaFqoYyrQA9Am74cVZMWVS2oiWuoXqDsR2hxO6q3dv2MvlPrvnaWHNLBDws3hLJo2gtDrrYbFqgGYybQXRkenfSnINwCwywvpeYbtULbU37ykurJDSM_66EhnjhIPhSG1aEwr1BVGmSe5CNzTl3Xmgd1G-Ar3NAkkiaGn6byyG6T8vIECqradO-qFSslb6qW4QWjTvtJA4",
            false
        )
    )

    private val _tebakProgress = MutableStateFlow(2) // out of 3
    val tebakProgress = _tebakProgress.asStateFlow()

    private val _showTebakSuccessOverlay = MutableStateFlow(false)
    val showTebakSuccessOverlay = _showTebakSuccessOverlay.asStateFlow()

    fun selectTebakAnswer(item: TebakItem) {
        if (item.isCorrect) {
            _showTebakSuccessOverlay.value = true
            viewModelScope.launch {
                val current = appState.value ?: AppState()
                repository.updateState(current.copy(starsCount = current.starsCount + 5, levelProgressPercent = 90))
            }
        }
    }

    fun dismissTebakSuccess() {
        _showTebakSuccessOverlay.value = false
    }

    // Jumbled Letters Game (Menyusun Kata "Susun Huruf: A-P-E-L")
    private val defaultJumbledGame = JumbledGame(
        correctWord = "APEL",
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCSL_Cm-QugYQggC-1oqNgBuSIE5FxseEzGSAt21siq4P98VGcifObdm67jEA29X1OUP8qo8wzT5RCnV_s5kyZ4-ZjuuIv-cAa5gnanMU8AnjoeQxiJe-WTYCWXonN9YqHdg6m-sw-EfKwhK5ZzVI4lYi3OYZ-WoYl-ESvXVlroFayxefpRmXpOGQY73RQKa73m3pPpcKG-1pyX6f_0CiRR0VmRRb_ComyEK1lbGclvm_v-UdlPvY8l070evxneup5vM9O_-KA8J6Y",
        lettersPool = listOf("P", "L", "A", "E"),
        placeholderHint = "Susun huruf: A - P - E - L"
    )

    private val _jumbledProgress = MutableStateFlow(0.6f) // 60%
    val jumbledProgress = _jumbledProgress.asStateFlow()

    private val _showJumbledSuccessOverlay = MutableStateFlow(false)
    val showJumbledSuccessOverlay = _showJumbledSuccessOverlay.asStateFlow()

    // Users can click on pool letters, placing them in order in the target slots
    val targetSlots = mutableStateListOf<String>("", "", "", "") // stores whatever letters got placed
    val poolLettersVisibility = mutableStateListOf<Boolean>(true, true, true, true) // visibility state of Letter Pool tiles

    fun placeLetterInNextSlot(poolIdx: Int, letter: String) {
        // Find first empty target slot
        val firstEmptyIdx = targetSlots.indexOfFirst { it.isEmpty() }
        if (firstEmptyIdx != -1) {
            // Check if letter is valid for that slot index
            val expectedLetterForThisSlot = defaultJumbledGame.correctWord[firstEmptyIdx].toString()
            if (letter == expectedLetterForThisSlot) {
                targetSlots[firstEmptyIdx] = letter
                poolLettersVisibility[poolIdx] = false

                // Check final completion
                if (targetSlots.all { it.isNotEmpty() && it == defaultJumbledGame.correctWord[targetSlots.indexOf(it)].toString() }) {
                    _showJumbledSuccessOverlay.value = true
                    viewModelScope.launch {
                        val current = appState.value ?: AppState()
                        repository.updateState(current.copy(
                            starsCount = current.starsCount + 10,
                            levelProgressPercent = 100,
                            masteredLetters = "A,B,C,D,E"
                        ))
                    }
                }
            }
        }
    }

    fun resetJumbledGame() {
        for (i in 0 until targetSlots.size) {
            targetSlots[i] = ""
            poolLettersVisibility[i] = true
        }
        _showJumbledSuccessOverlay.value = false
    }

    // Interaktif Mewarnai Canvas Drawing State
    private val _selectedDrawColor = MutableStateFlow(Color(0xFFFF5252)) // Default red
    val selectedDrawColor = _selectedDrawColor.asStateFlow()

    private val _isEraserActive = MutableStateFlow(false)
    val isEraserActive = _isEraserActive.asStateFlow()

    val drawingStrokes = mutableStateListOf<DrawingStroke>()

    fun setDrawColor(color: Color) {
        _isEraserActive.value = false
        _selectedDrawColor.value = color
    }

    fun toggleEraser() {
        _isEraserActive.value = !_isEraserActive.value
    }

    fun addStroke(stroke: DrawingStroke) {
        drawingStrokes.add(stroke)
    }

    fun clearDrawing() {
        drawingStrokes.clear()
    }

    // Live Video & Karaoke Lyrics State
    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying = _isVideoPlaying.asStateFlow()

    private val _videoProgress = MutableStateFlow(0.3f) // preset progress slider starts at 45m out of 2.30
    val videoProgress = _videoProgress.asStateFlow()

    private val _isMicrophoneActive = MutableStateFlow(false)
    val isMicrophoneActive = _isMicrophoneActive.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled = _isRepeatEnabled.asStateFlow()

    fun toggleVideoPlay() {
        _isVideoPlaying.value = !_isVideoPlaying.value
    }

    fun setVideoProgress(progress: Float) {
        _videoProgress.value = progress
    }

    fun toggleMicrophone() {
        _isMicrophoneActive.value = !_isMicrophoneActive.value
    }

    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }

    // Parent Laporan share success
    private val _isShareSuccessActive = MutableStateFlow(false)
    val isShareSuccessActive = _isShareSuccessActive.asStateFlow()

    fun triggerShareHappiness() {
        _isShareSuccessActive.value = true
        viewModelScope.launch {
            val current = appState.value ?: AppState()
            repository.updateState(current.copy(starsCount = current.starsCount + 10))
        }
    }

    fun dismissShareSuccess() {
        _isShareSuccessActive.value = false
    }
}

class BimbaViewModelFactory(private val repository: AppStateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BimbaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BimbaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
