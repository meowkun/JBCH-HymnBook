package com.example.jbchhymnbook.hymn.domain.usecase

import com.example.jbchhymnbook.core.domain.Result
import com.example.jbchhymnbook.hymn.domain.model.Hymn
import com.example.jbchhymnbook.hymn.domain.repository.HymnRepository

class GetHymnDetailUseCase(
    private val hymnRepository: HymnRepository
) {
    suspend operator fun invoke(hymnId: String): Result<Hymn> {
        return hymnRepository.getHymnById(hymnId)
    }
}

