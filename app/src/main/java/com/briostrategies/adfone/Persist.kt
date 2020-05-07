package com.briostrategies.adfone

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SearchesDao {
    @Query("SELECT * from searches")
    fun getAll(): LiveData<List<Places>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(places: Places)
}

@Database(entities = [Places::class], version = 1, exportSchema = false)
@TypeConverters(PlacesConverter::class)
abstract class SearchesDatabase : RoomDatabase() {

    abstract fun searchesDao(): SearchesDao

    companion object {
        @Volatile
        private var INSTANCE: SearchesDatabase? = null

        fun getDatabase(context: Context): SearchesDatabase = INSTANCE ?: synchronized(this) {

            return Room.databaseBuilder(
                context.applicationContext,
                SearchesDatabase::class.java,
                "searches"
            ).build().also {
                INSTANCE = it
            }
        }
    }
}

