package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.ModuleDao
import com.group20.codevocab.data.local.dao.WordDao
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.data.local.entity.toEntity
import com.group20.codevocab.data.remote.ApiService
import com.group20.codevocab.model.ModuleDetailItem
import com.group20.codevocab.model.ModuleItem
import com.group20.codevocab.model.mapper.toModuleDetailItem
import com.group20.codevocab.model.toDto
import com.group20.codevocab.model.toEntity
import com.group20.codevocab.model.toModuleItem
import java.util.UUID

class ModuleRepository(
    private val api: ApiService,
    private val moduleDao: ModuleDao,
    private val wordDao: WordDao
) {
    suspend fun getAllModules() = moduleDao.getAllModules()
    suspend fun getGeneralModules() = moduleDao.getGeneralModules()
    suspend fun getModuleById(id: String) = moduleDao.getModuleById(id)

    suspend fun getModulesRemote(): List<ModuleItem> {
        val remoteModules = api.getModules()
        // Removed local caching as requested
        return remoteModules.map { it.toModuleItem() }
    }

    suspend fun getUserModulesRemote(userId: String): List<ModuleItem> {
        val remoteModules = api.getUserModules(userId)
        // Removed local caching as requested
        return remoteModules.map { it.toModuleItem() }
    }

    suspend fun getModuleDetailRemote(
        moduleId: String
    ): ModuleDetailItem {
        val dto = api.getModuleDetail(moduleId)
        return dto.toModuleDetailItem()
    }
    
    suspend fun createModuleLocal(name: String) {
        val module = ModuleEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            description = null,
            moduleType = "personal",
            isPublic = false,
            createdAt = System.currentTimeMillis().toString()
        )
        moduleDao.insertModules(listOf(module))
    }

    suspend fun updateModule(item: ModuleItem) {
        if (item.isLocal) {
            moduleDao.updateModule(item.toEntity())
        } else {
            api.updateModule(item.id, item.toDto())
        }
    }

    suspend fun insertWords(words: List<WordEntity>) {
        wordDao.insertAll(words)
    }

    suspend fun copyModuleToLocal(moduleItem: ModuleItem): String {
        val newId = UUID.randomUUID().toString()
        val newModule = ModuleEntity(
            id = newId,
            name = "Copy " + moduleItem.name,
            description = moduleItem.description,
            moduleType = "personal", // Set as personal
            isPublic = false, // Set as private
            createdAt = System.currentTimeMillis().toString()
        )
        moduleDao.insertModules(listOf(newModule))
        return newId
    }
}