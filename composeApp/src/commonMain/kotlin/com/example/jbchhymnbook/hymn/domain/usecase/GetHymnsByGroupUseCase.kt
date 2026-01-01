package com.example.jbchhymnbook.hymn.domain.usecase

import com.example.jbchhymnbook.core.domain.Result
import com.example.jbchhymnbook.hymn.domain.model.Hymn
import com.example.jbchhymnbook.hymn.domain.repository.HymnRepository

class GetHymnsByGroupUseCase(
    private val hymnRepository: HymnRepository
) {
    suspend operator fun invoke(groupId: String): Result<List<Hymn>> {
        return hymnRepository.getHymnsByGroup(groupId)
    }
}

