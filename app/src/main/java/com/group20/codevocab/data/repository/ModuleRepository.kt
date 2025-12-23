package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.ModuleDao
import com.group20.codevocab.data.local.dao.FlashcardProgressDao
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

data class ModuleProgressInfo(
    val processedCount: Int, // Số từ đã tương tác (Know/Hard/Review)
    val totalCount: Int      // Tổng số từ trong module
) {
    val percentage: Int 
        get() = if (totalCount > 0) (processedCount * 100 / totalCount) else 0
}

class ModuleRepository(
    private val api: ApiService,
    private val moduleDao: ModuleDao,
    private val flashcardProgressDao: FlashcardProgressDao,
    private val wordDao: WordDao
) {
    suspend fun getAllModules() = moduleDao.getAllModules()
    suspend fun getGeneralModules() = moduleDao.getGeneralModules()
    suspend fun getModuleById(id: String) = moduleDao.getModuleById(id)

    suspend fun getWordCountForModule(moduleId: String): Int {
        return wordDao.getWordsByModule(moduleId).size
    }

    suspend fun getModulesRemote(): List<ModuleItem> {
        val remoteModules = api.getModules()
        return remoteModules.map { it.toModuleItem() }
    }

    suspend fun getMyModules(): List<ModuleItem> {
        val remoteModules = api.getMyModules()
        return remoteModules.map { it.toModuleItem() }
    }

    suspend fun getSharedWithMeModules(): List<ModuleItem> {
        val remoteModules = api.getSharedWithMeModules()
        return remoteModules.map { it.toModuleItem() }
    }

    suspend fun getUserModulesRemote(userId: String): List<ModuleItem> {
        val remoteModules = api.getUserModules(userId)
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

    suspend fun acceptShareModule(shareId: String): Boolean {
        return try {
            val response = api.acceptShareModule(shareId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getInProgressModules(): List<ModuleEntity> {
        val inProgressIds = flashcardProgressDao.getInProgressModuleIds()
        return inProgressIds.mapNotNull { id ->
            moduleDao.getModuleById(id)
        }
    }

    suspend fun getModuleProgressInfo(moduleId: String): ModuleProgressInfo {
        // Lấy tổng số từ thực tế của module từ DB local
        val words = wordDao.getWordsByModule(moduleId)
        val totalCount = words.size
        
        // Đếm số từ đã được người dùng nhấn nút (Know/Hard/Review)
        val processedCount = flashcardProgressDao.countByModule(moduleId)
        
        // Fallback: nếu words chưa sync local, dùng processedCount làm mốc
        val finalTotal = if (totalCount > 0) totalCount else processedCount
        
        return ModuleProgressInfo(processedCount, finalTotal)
    }

    suspend fun getModuleProgress(moduleId: String): Int {
        return getModuleProgressInfo(moduleId).percentage
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