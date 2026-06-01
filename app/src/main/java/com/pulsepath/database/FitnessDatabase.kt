package com.pulsepath.database

import android.content.Context
import androidx.room.*
import com.pulsepath.models.DailyStats
import com.pulsepath.models.WorkoutSession
import com.pulsepath.models.WorkoutType

@Database(
    entities = [WorkoutSession::class, DailyStats::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(FitnessConverters::class)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun fitnessDao(): FitnessDao

    companion object {
        @Volatile private var INSTANCE: FitnessDatabase? = null

        fun getDatabase(context: Context): FitnessDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, FitnessDatabase::class.java, "pulsepath_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

class FitnessConverters {
    @TypeConverter fun fromType(t: WorkoutType): String = t.name
    @TypeConverter fun toType(s: String): WorkoutType = WorkoutType.valueOf(s)
}
