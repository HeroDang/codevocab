package com.group20.codevocab.data.repository


import com.group20.codevocab.data.local.dao.ModuleDao
import com.group20.codevocab.data.remote.ApiService
import com.group20.codevocab.model.ModuleDetailItem
import com.group20.codevocab.model.ModuleItem
import com.group20.codevocab.model.mapper.toModuleDetailItem
import com.group20.codevocab.model.toModuleItem

class ModuleRepository(
    private val api: ApiService,
    private val moduleDao: ModuleDao
) {
    suspend fun getAllModules() = moduleDao.getAllModules()
    suspend fun getGeneralModules() = moduleDao.getGeneralModules()
    suspend fun getModuleById(id: Int) = moduleDao.getModuleById(id)
    suspend fun getSubModules(parentId: Int) = moduleDao.getSubModules(parentId)

    suspend fun getModulesRemote(): List<ModuleItem> {
        return api.getModules().map { it.toModuleItem() }
    }

    suspend fun getModuleDetailRemote(
        moduleId: String
    ): ModuleDetailItem {
        val dto = api.getModuleDetail(moduleId)
        return dto.toModuleDetailItem()
    }
}
