package com.group20.codevocab.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.group20.codevocab.data.local.entity.ModuleEntity

@Dao
interface ModuleDao {
    @Query("SELECT * FROM modules")
    suspend fun getAllModules(): List<ModuleEntity>

    @Query("SELECT * FROM modules WHERE id = :id")
    suspend fun getModuleById(id: Int): ModuleEntity
}
