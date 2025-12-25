package com.group20.codevocab.data.remote.dto

data class ModuleDetailDto(
    val id: String,
    val name: String,
    val description: String?,
    val module_type: String?,
    val is_public: Boolean?,
    val created_at: String?,
    val count_word: Int? = 0,
    val children: List<ModuleChildDto> = emptyList()
)

data class ModuleChildDto(
    val id: String,
    val name: String,
    val description: String?,
    val module_type: String?,
    val is_public: Boolean?,
    val created_at: String?,
    val count_word: Int? = 0
)
