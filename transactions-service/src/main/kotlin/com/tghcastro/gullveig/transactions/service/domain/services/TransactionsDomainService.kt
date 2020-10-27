package com.tghcastro.gullveig.transactions.service.domain.services

import com.tghcastro.gullveig.transactions.service.domain.interfaces.TransactionsRepository
import com.tghcastro.gullveig.transactions.service.domain.interfaces.TransactionsService
import com.tghcastro.gullveig.transactions.service.domain.models.Transactions
import com.tghcastro.gullveig.transactions.service.domain.results.DomainResult
import org.springframework.stereotype.Service

@Service
class TransactionsDomainService(val transactionsRepository: TransactionsRepository) : TransactionsService {

    override fun create(transaction: Transactions): DomainResult<Transactions> {
        return transaction.validate().onSuccess { internalCreate(transaction) }
    }

    override fun getById(id: Long): Transactions? {
        return transactionsRepository.getById(id)
    }

    private fun internalCreate(transaction: Transactions): DomainResult<Transactions> {
        val createdTransaction = this.transactionsRepository.saveAndFlush(transaction)
        return DomainResult.success(createdTransaction)
    }
}