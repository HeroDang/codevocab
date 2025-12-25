package com.group20.codevocab.model.mapper

import com.group20.codevocab.data.remote.dto.ModuleDetailDto
import com.group20.codevocab.model.ModuleDetailItem
import com.group20.codevocab.model.SubModuleItem

fun ModuleDetailDto.toModuleDetailItem(): ModuleDetailItem {
    return ModuleDetailItem(
        id = id,
        title = name,
        description = description,
        wordCount = count_word ?: 0,
        moduleType = module_type,
        isPublic = is_public,
        createdAt = created_at,
        children = children.map {
            SubModuleItem(
                id = it.id,
                name = it.name,
                description = it.description,
                wordCount = it.count_word ?: 0,
                moduleType = it.module_type,
                isPublic = it.is_public,
                createdAt = it.created_at
            )
        }
    )
}
