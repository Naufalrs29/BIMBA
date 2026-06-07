package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "app_state")
data class AppState(
    @PrimaryKey val id: Int = 1,
    val starsCount: Int = 128,
    val studyStreakDays: Int = 7,
    val learnedMinutes: Int = 45,
    val dailyTargetMinutes: Int = 60,
    val masteredLetters: String = "A,B,C,D", // Comma-separated or similar representation
    val levelProgressPercent: Int = 85
)

@Dao
interface AppStateDao {
    @Query("SELECT * FROM app_state WHERE id = 1 LIMIT 1")
    fun getAppState(): Flow<AppState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppState(appState: AppState)
}

@Database(entities = [AppState::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appStateDao(): AppStateDao
}

class AppStateRepository(private val dao: AppStateDao) {
    val appStateFlow: Flow<AppState?> = dao.getAppState()

    suspend fun updateState(appState: AppState) {
        dao.saveAppState(appState)
    }

    suspend fun addStars(amount: Int) {
        // Run as a helper if needed by loading current state first
    }
}
