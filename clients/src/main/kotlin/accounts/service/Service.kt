package accounts.service

import accounts.dto.*
import accounts.flows.CreateAccountFlow
import accounts.service.`interface`.IAccountService
import accounts.states.AccountState
import accounts.webserver.NodeRPCConnection
import accounts.webserver.utilities.FlowHandlerCompletion
import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount
import org.springframework.stereotype.Service

@Service
class Service(private val rpc: NodeRPCConnection, private val fhc: FlowHandlerCompletion) : IAccountService
{
    // Get all account info

    override fun getAll(): Any
    {

        val accountInfoStates = rpc.proxy.vaultQuery(AccountInfo::class.java).states
        return accountInfoStates.map { mapToAccountInfo(it.state.data) }
    }

    // Get an account info
    override fun get(linearId: String): Any
    {
        val accountInfoStates = rpc.proxy.vaultQuery(AccountInfo::class.java).states
        val accountInfoStateRef = accountInfoStates.find { it.state.data.linearId.toString() == linearId } ?: throw Exception("Account info not found")
        return mapToAccountInfo(accountInfoStateRef.state.data)
    }

    override fun createAccountInfo(request: AccountInfoCreateFlowDTO): AccountInfoDTO
    {
        val flowReturn = rpc.proxy.startFlowDynamic(
                CreateAccount::class.java,
                request.name
        )
        val flowResult = flowReturn.returnValue.get().state.data
        return mapToAccountInfo(flowResult)
    }

    override fun getAllAccountStates(): List<AccountDTO>
    {
        val accountStates = rpc.proxy.vaultQuery(AccountState::class.java).states
        return accountStates.map { mapToAccountState(it.state.data) }
    }

    override fun getAccountState(linearId: String): AccountDTO
    {
        val accountStates = rpc.proxy.vaultQuery(AccountState::class.java).states
        val accountStateRef = accountStates.find { it.state.data.linearId.toString() == linearId } ?: throw Exception("Account state not found")
        return mapToAccountState(accountStateRef.state.data)
    }

    override fun createAccountState(request: AccountStateCreateFlowDTO): AccountDTO
    {
        val flowReturn = rpc.proxy.startFlowDynamic(
                CreateAccountFlow::class.java,
                request.iou,
                request.lenderId,
                request.borrowerId
        )
        fhc.flowHandlerCompletion(flowReturn)
        val flowResult = flowReturn.returnValue.get().coreTransaction.outputStates.first() as AccountState
        return mapToAccountState(flowResult)
    }
}