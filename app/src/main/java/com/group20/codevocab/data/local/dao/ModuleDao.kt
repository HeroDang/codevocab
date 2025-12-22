package com.group20.codevocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.group20.codevocab.data.local.entity.ModuleEntity

@Dao
interface ModuleDao {
    @Query("SELECT * FROM modules")
    suspend fun getAllModules(): List<ModuleEntity>

    @Query("SELECT * FROM modules WHERE id = :id")
    suspend fun getModuleById(id: String): ModuleEntity? // Đã thêm dấu ? để tránh crash

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ModuleEntity>)

    @Query("DELETE FROM modules")
    suspend fun clearModules()

    @Query("SELECT * FROM modules WHERE module_type = 'general'")
    suspend fun getGeneralModules(): List<ModuleEntity>

    @Update
    suspend fun updateModule(module: ModuleEntity)
}