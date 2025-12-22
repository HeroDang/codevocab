package com.group20.codevocab.model

data class ModuleDetailItem(
    val id: String,
    val title: String,
    val description: String?,
    val children: List<SubModuleItem>
)

data class SubModuleItem(
    val id: String,
    val name: String,
    val description: String?
    // TODO (sau): wordCount, progress
)
