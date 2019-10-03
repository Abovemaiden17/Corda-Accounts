package accounts.service.`interface`

import accounts.dto.AccountDTO
import accounts.dto.AccountInfoCreateFlowDTO
import accounts.dto.AccountInfoDTO
import accounts.dto.AccountStateCreateFlowDTO

interface IAccountService : IService
{
    fun createAccountInfo(request: AccountInfoCreateFlowDTO): AccountInfoDTO
    fun getAllAccountStates(): List<AccountDTO>
    fun getAccountState(linearId: String): AccountDTO
    fun createAccountState(request: AccountStateCreateFlowDTO): AccountDTO
}