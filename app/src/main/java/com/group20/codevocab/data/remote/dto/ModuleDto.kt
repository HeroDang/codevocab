package com.group20.codevocab.data.remote.dto

data class ModuleDto(
    val id: String,
    val name: String,
    val description: String?,
    val module_type: String,
    val is_public: Boolean,
    val created_at: String
)