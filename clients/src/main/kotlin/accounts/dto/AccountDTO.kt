package accounts.dto

import accounts.states.AccountState
import com.fasterxml.jackson.annotation.JsonCreator
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import java.security.PublicKey

data class AccountDTO(
        val value: Int,
        val lender: PublicKey,
        val borrower: PublicKey,
        val linearId: String
)

data class AccountStateCreateFlowDTO @JsonCreator constructor(
        val iou: Int,
        val lenderId: String,
        val borrowerId: String
)

data class AccountInfoDTO(
        val name: String,
        val host: String,
        val identifier: String,
        val linearId: String
)

data class AccountInfoCreateFlowDTO @JsonCreator constructor(
        val name: String
)

fun mapToAccountInfo(accountInfo: AccountInfo): AccountInfoDTO
{
    return AccountInfoDTO(
            name = accountInfo.name,
            host = accountInfo.host.toString(),
            identifier = accountInfo.identifier.toString(),
            linearId = accountInfo.linearId.toString()
    )
}

fun mapToAccountState(accountState: AccountState): AccountDTO
{
    return AccountDTO(
            value = accountState.value,
            lender = accountState.lender,
            borrower = accountState.borrower,
            linearId = accountState.linearId.toString()
    )
}