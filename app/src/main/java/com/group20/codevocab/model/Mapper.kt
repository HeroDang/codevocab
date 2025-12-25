package com.group20.codevocab.model

import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.data.remote.dto.ModuleDetailDto
import com.group20.codevocab.data.remote.dto.ModuleDto
import com.group20.codevocab.data.remote.dto.WordDto

// Removed conflicting overload ModuleDto.toModuleItem() because it is already defined in ModuleItem.kt
// But I will create a wrapper or alternative if needed, but since it's already there and correct, we can skip it here.
// However, the user said "chi them code moi khong xoa code cua, doc config hay dat ten khac di"
// So I will rename these extension functions to avoid conflict.

fun ModuleDto.toModuleItemExt(): ModuleItem {
    return ModuleItem(
        id = this.id ?: "",
        name = this.name ?: "",
        description = this.description,
        isPublic = this.is_public,
        wordCount = null, 
        ownerName = null,
        isLocal = false,
        createdAt = this.created_at,
        status = null
    )
}

// Renamed to avoid conflict with ModuleItem.kt
fun ModuleItem.toDtoExt(): ModuleDto {
    return ModuleDto(
        id = this.id,
        name = this.name,
        description = this.description,
        is_public = this.isPublic,
        created_at = this.createdAt ?: "",
        module_type = "personal" // Default for local
    )
}

// Renamed to avoid conflict with ModuleItem.kt
fun ModuleItem.toEntityExt(): ModuleEntity {
    return ModuleEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        moduleType = if (this.isLocal) "personal" else "market",
        isPublic = this.isPublic,
        createdAt = this.createdAt ?: "",
        isDeleted = false
    )
}

fun ModuleDetailDto.toModuleDetailItemExt(): ModuleDetailItem {
    return ModuleDetailItem(
        id = this.id ?: "",
        title = this.name ?: "",
        description = this.description,
        children = this.children?.map { child ->
            SubModuleItem(
                id = child.id ?: "",
                name = child.name ?: "",
                description = child.description ?: ""
            )
        } ?: emptyList()
    )
}

fun WordEntity.toWordDto(): WordDto {
    return WordDto(
        id = this.id,
        textEn = this.textEn,
        meaningVi = this.meaningVi,
        partOfSpeech = this.partOfSpeech,
        ipa = this.ipa,
        exampleSentence = this.exampleSentence,
        audioUrl = this.audioUrl // Added audioUrl parameter
    )
}
