package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.ModuleDao

class ModuleRepository(private val moduleDao: ModuleDao) {
    suspend fun getAllModules() = moduleDao.getAllModules()
    suspend fun getModuleById(id: Int) = moduleDao.getModuleById(id)
}
