package accounts.functions

import com.r3.corda.lib.accounts.contracts.states.AccountInfo
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.security.PublicKey
import java.util.*

abstract class FlowFunctions : FlowLogic<SignedTransaction>()
{
    override val progressTracker = ProgressTracker(INITIALIZING, BUILDING, SIGNING, COLLECTING, FINALIZING)

    fun verifyAndSign(transaction: TransactionBuilder, keysToSign: MutableList<PublicKey>): SignedTransaction
    {
        progressTracker.currentStep = SIGNING
        transaction.verify(serviceHub)
        return serviceHub.signInitialTransaction(transaction, keysToSign)
    }

    fun getAccountInfo(id: UUID): StateAndRef<AccountInfo>
    {
        return accountService.accountInfo(id) ?: throw IllegalStateException("Can't find account to move from $id")
    }

    fun getKey(id: UUID): PublicKey
    {
        val accountInfo = accountService.accountInfo(id) ?: throw IllegalStateException("Can't find account to move from $id")
        return subFlow(RequestKeyForAccount(accountInfo.state.data)).owningKey
    }
}