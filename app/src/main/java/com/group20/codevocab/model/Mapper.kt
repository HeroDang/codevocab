package com.group20.codevocab.model

import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.data.remote.dto.*

// Existing mappers...
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

fun ModuleItem.toDtoExt(): ModuleDto {
    return ModuleDto(
        id = this.id,
        name = this.name,
        description = this.description,
        is_public = this.isPublic,
        created_at = this.createdAt ?: "",
        module_type = "personal"
    )
}

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
        wordCount = this.count_word ?: 0,
        moduleType = this.module_type,
        isPublic = this.is_public,
        createdAt = this.created_at,
        children = this.children.map { child ->
            SubModuleItem(
                id = child.id ?: "",
                name = child.name ?: "",
                description = child.description ?: "",
                wordCount = child.count_word ?: 0,
                moduleType = child.module_type,
                isPublic = child.is_public,
                createdAt = child.created_at
            )
        }
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
        audioUrl = this.audioUrl
    )
}

// Speaking Analysis Mappers
fun SpeakingAnalysisResponse.toResult(): SpeakingAnalysisResult {
    return SpeakingAnalysisResult(
        originalSentence = this.originalSentence,
        recognizedSentence = this.recognizedSentence,
        analysis = this.analysis.map { it.toModel() },
        mispronouncedPhonemes = this.mispronouncedPhonemes
    )
}

fun WordAnalysisDto.toModel(): WordAnalysis {
    return WordAnalysis(
        word = this.word,
        status = this.status,
        segments = this.segments.map {
            SpeakingSegment(
                text = it.text,
                isCorrect =  it.isCorrect,
                phoneticError = it.phoneticError?.let {
                    PhoneticError(it.expected, it.actual, it.note)
                }
            )
        }
    )
}
