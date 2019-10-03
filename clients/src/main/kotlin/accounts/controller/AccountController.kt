package accounts.controller

import accounts.dto.AccountInfoCreateFlowDTO
import accounts.dto.AccountStateCreateFlowDTO
import accounts.service.Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val CONTROLLER_NAME = "api/v1/accounts"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(CONTROLLER_NAME) // The paths for HTTP requests are relative to this base path.
class AccountController(private val accountService: Service) : BaseController()
{
    /**
     * Get all accounts
     */
    @GetMapping(value = ["/all"], produces = ["application/json"])
    private fun getAllAccountInfo(): ResponseEntity<Any>
    {
        return try {
            val response = accountService.getAll()
            ResponseEntity.ok(response)
        } catch (ex: Exception) {
            return this.handleException(ex)
        }
    }

    /**
     * Get an account
     */
    @GetMapping(value = ["/{linearId}"], produces = ["application/json"])
    private fun getAccountInfo(@PathVariable linearId: String): ResponseEntity<Any>
    {
        return try {
            val response = accountService.get(linearId)
            ResponseEntity.ok(response)
        } catch (ex: Exception) {
            return this.handleException(ex)
        }
    }

    /**
     * Create an account info
     */
    @PostMapping(value = [], produces = ["application/json"])
    private fun createAccountInfo(@RequestBody request: AccountInfoCreateFlowDTO): ResponseEntity<Any>
    {
        return try
        {
            val response = accountService.createAccountInfo(request)
            ResponseEntity.created(URI("/" + CONTROLLER_NAME + "/" + response.linearId)).body(response)
        } catch (ex: Exception) {
            return this.handleException(ex)
        }
    }

    /**
     * Get all account states
     */
    @GetMapping(value = ["/state/all"], produces = ["application/json"])
    private fun getAllAccountStates(): ResponseEntity<Any>
    {
        return try {
            val response = accountService.getAllAccountStates()
            ResponseEntity.ok(response)
        } catch (ex: Exception) {
            return this.handleException(ex)
        }
    }

    /**
     * Get an account
     */
    @GetMapping(value = ["/state/{linearId}"], produces = ["application/json"])
    private fun getAccountState(@PathVariable linearId: String): ResponseEntity<Any>
    {
        return try {
            val response = accountService.getAccountState(linearId)
            ResponseEntity.ok(response)
        } catch (ex: Exception) {
            return this.handleException(ex)
        }
    }

    /**
     * Create an account state
     */
    @PostMapping(value = ["/state"], produces = ["application/json"])
    private fun createAccountState(@RequestBody request: AccountStateCreateFlowDTO): ResponseEntity<Any>
    {
        return try
        {
            val response = accountService.createAccountState(request)
            ResponseEntity.created(URI("/" + CONTROLLER_NAME + "/state/" + response.linearId)).body(response)
        } catch (ex: Exception) {
            return this.handleException(ex)
        }
    }
}