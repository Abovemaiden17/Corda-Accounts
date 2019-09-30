package accounts.flows

import accounts.contract.AccountContract
import accounts.functions.*
import accounts.states.AccountState
import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.util.*

@InitiatingFlow
@StartableByRPC
class CreateAccountFlow (private val iou: Int,
                         private val lenderId: UUID,
                         private val borrowerId: UUID): FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction
    {
        progressTracker.currentStep = BUILDING
        progressTracker.currentStep = SIGNING
        // Sign with our node key AND the private key from the lender account
        // since due to design we must be hosts of that account
        val keysToSignWith = mutableListOf(ourIdentity.owningKey, getKey(lenderId))

        // Only add the borrower account if it is hosted on this node (potentially it might be on a different node)
        if (getAccountInfo(borrowerId).state.data.host == serviceHub.myInfo.legalIdentitiesAndCerts.first().party)
        {
            keysToSignWith.add(getKey(borrowerId))
        }

        val signedTransaction = verifyAndSign(transaction(), keysToSignWith)

        return if (getAccountInfo(borrowerId).state.data.host == serviceHub.myInfo.legalIdentitiesAndCerts.first().party)
        {
            // Notarise and record the transaction in just our vault.
            progressTracker.currentStep = FINALIZING
            subFlow(FinalityFlow(signedTransaction, emptyList()))
        }
        else
        {
            // Send the state to the counter party and get it back with their signature.
            progressTracker.currentStep = COLLECTING
            val borrowerSession = initiateFlow(getAccountInfo(borrowerId).state.data.host)
            val borrowerSignature = subFlow(CollectSignatureFlow(signedTransaction, borrowerSession, getKey(borrowerId)))
            val finalizedSignedTransaction = signedTransaction.withAdditionalSignatures(borrowerSignature)

            // Notarise and record the transaction in both parties' vaults.
            progressTracker.currentStep = FINALIZING
            subFlow(FinalityFlow(finalizedSignedTransaction, listOf(borrowerSession)))
        }
    }

    private fun outState(): AccountState
    {
        // Lookup the account and Public Key from the UUID
        val lenderKey = getKey(lenderId)
        val borrowerKey = getKey(borrowerId)

        return AccountState(
                value = iou,
                lender = lenderKey,
                borrower = borrowerKey,
                linearId = UniqueIdentifier()
        )
    }

    private fun transaction(): TransactionBuilder
    {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val cmd = Command(AccountContract.Commands.Create(), outState().participants.map { it.owningKey })
        val builder = TransactionBuilder(notary = notary)
        builder.addOutputState(outState(), AccountContract.IOU_CONTRACT_ID)
        builder.addCommand(cmd)
        return builder
    }
}

@InitiatedBy(CreateAccountFlow::class)
class CreateAccountFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>()
{
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(flowSession)
        {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be an IOU transaction." using (output is AccountState)
                val iou = output as AccountState
                "IOUs with a value over 100 are not accepted." using (iou.value <= 100)
            }
        }

        val txId = subFlow(signTransactionFlow).id
        return subFlow(ReceiveFinalityFlow(flowSession, expectedTxId = txId))
    }
}