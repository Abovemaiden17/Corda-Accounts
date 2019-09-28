package accounts.states

import accounts.contract.AccountContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.AnonymousParty
import java.security.PublicKey

@BelongsToContract(AccountContract::class)
data class AccountState(val value: Int,
                        val lender: PublicKey,
                        val borrower: PublicKey,
                        override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState
{
    //The public keys of the involved parties.
    override val participants: List<AbstractParty> get() = listOf(lender, borrower).map { AnonymousParty(it) }
}