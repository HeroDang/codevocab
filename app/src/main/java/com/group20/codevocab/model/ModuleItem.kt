package com.group20.codevocab.model

import android.os.Parcelable
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.remote.dto.ModuleDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModuleItem(
    val id: String,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val isLocal: Boolean = false, // Default to false for backward compatibility or ease of use
    val ownerName: String? = null,
    val wordCount: Int? = 0,
    val status: String? = null,
    val shareId: String? = null,
    val createdAt: String? = null
) : Parcelable

fun ModuleDto.toModuleItem(): ModuleItem {
    return ModuleItem(
        id = id,
        name = name,
        description = description,
        isPublic = is_public,
        isLocal = false, // Data from DTO (Server) is not local
        ownerName = owner_name,
        wordCount = count_word,
        status = status,
        createdAt = created_at
    )
}

fun ModuleItem.toEntity(): ModuleEntity {
    return ModuleEntity(
        id = id,
        name = name,
        description = description,
        moduleType = if (isLocal) "personal" else "general", // Simplified mapping
        isPublic = isPublic,
        createdAt = createdAt ?: System.currentTimeMillis().toString()
    )
}

fun ModuleItem.toDto(): ModuleDto {
    return ModuleDto(
        id = id,
        name = name,
        description = description,
        is_public = isPublic,
        module_type = "personal", // Default for now
        created_at = createdAt ?: "",
        owner_name = ownerName,
        count_word = wordCount,
        status = status
    )
}
