package com.group20.codevocab.model

data class ModuleDetailItem(
    val id: String,
    val title: String,
    val description: String?,
    val wordCount: Int? = 0,
    val learnedCount: Int = 0, // Thêm cho module cha nếU cần
    val moduleType: String?,
    val isPublic: Boolean?,
    val createdAt: String?,
    val children: List<SubModuleItem>
)

data class SubModuleItem(
    val id: String,
    val name: String,
    val description: String?,
    val wordCount: Int? = 0,
    val learnedCount: Int = 0, // ✅ Thêm số từ đã học
    val moduleType: String?,
    val isPublic: Boolean?,
    val createdAt: String?
)
