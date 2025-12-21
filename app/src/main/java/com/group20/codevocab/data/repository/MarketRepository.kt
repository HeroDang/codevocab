package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.model.ModuleItem
import com.group20.codevocab.model.toModuleItem


class MarketRepository {

    private val apiService = ApiClient.api

    suspend fun getMarketModules(): List<ModuleItem> {
        // Calling the new function and mapping the DTOs to domain models
        return apiService.getAllModules().map { it.toModuleItem() }
    }
}