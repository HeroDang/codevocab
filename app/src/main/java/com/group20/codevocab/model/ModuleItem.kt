package com.group20.codevocab.model

import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.remote.dto.ModuleDto

data class ModuleItem(
    val id: String,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val isLocal: Boolean = false // Default to false for backward compatibility or ease of use
)

fun ModuleDto.toModuleItem(): ModuleItem {
    return ModuleItem(
        id = id,
        name = name,
        description = description,
        isPublic = is_public,
        isLocal = false // Data from DTO (Server) is not local
    )
}

fun ModuleItem.toEntity(): ModuleEntity {
    return ModuleEntity(
        id = id,
        name = name,
        description = description,
        moduleType = if (isLocal) "personal" else "general", // Simplified mapping
        isPublic = isPublic,
        createdAt = "" // Should be handled properly, for update it's fine if ignored by logic
    )
}

fun ModuleItem.toDto(): ModuleDto {
    return ModuleDto(
        id = id,
        name = name,
        description = description,
        is_public = isPublic,
        module_type = "personal", // Default for now
        created_at = ""
    )
}
