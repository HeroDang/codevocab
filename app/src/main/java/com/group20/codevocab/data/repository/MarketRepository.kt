package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.model.ModuleItem
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.model.toModuleItem
import com.group20.codevocab.model.mapper.toWordItem

class MarketRepository {

    private val apiService = ApiClient.api

    suspend fun getMarketModules(): List<ModuleItem> {
        return apiService.getAllModules().map { it.toModuleItem() }
    }

    suspend fun getWordsOfModule(moduleId: String): List<WordItem> {
        return apiService.getWordsBySubmodule(moduleId).map { it.toWordItem() }
    }
}