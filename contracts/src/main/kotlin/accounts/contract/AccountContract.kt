package accounts.contract

import accounts.states.AccountState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

/**
 * For a new [AccountState] to be issued onto the ledger, a transaction is required which takes:
 * - Zero input states.
 * - One output state: the new [AccountState].
 * - A Create() command with the public keys of both the lender and the borrower.
 */

class AccountContract : Contract
{
    companion object
    {
        const val IOU_CONTRACT_ID = "accounts.contract.AccountContract"
    }

    interface Commands : CommandData
    {
        class Create : TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction)
    {
//        val command = tx.commands.requireSingleCommand<Commands>()
//        val signers = command.signers.toSet()
//
//        when (command.value) {
//            is Commands.Create -> verifyCreate(tx, signers)
//            else -> throw IllegalArgumentException("Unrecognised command.")
//        }
    }

//    private fun verifyCreate(tx: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
//        // Generic constraints around the IOU transaction.
//        "No inputs should be consumed when issuing an IOU." using (tx.inputs.isEmpty())
//        "Only one output state should be created." using (tx.outputs.size == 1)
//        val out = tx.outputsOfType<AccountState>().single()
//        "The lender and the borrower cannot be the same entity." using (out.lender != out.borrower)
//        "All of the participants must be signers." using (signers.containsAll(out.participants.map { it.owningKey }))
//
//        // IOU-specific constraints.
//        "The IOU's value must be non-negative." using (out.value > 0)
//    }

}
