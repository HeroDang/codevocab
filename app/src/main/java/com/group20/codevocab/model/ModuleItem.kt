package com.group20.codevocab.model

import com.group20.codevocab.data.remote.dto.ModuleDto

data class ModuleItem(
    val id: String,
    val name: String,
    val description: String?,
    val isPublic: Boolean
)

fun ModuleDto.toModuleItem(): ModuleItem {
    return ModuleItem(
        id = id,
        name = name,
        description = description,
        isPublic = is_public
    )
}
