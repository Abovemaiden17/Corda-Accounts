package accounts.controller

import javassist.NotFoundException
import net.corda.core.contracts.TransactionVerificationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

abstract class BaseController
{
    companion object
    {
        private val logger = LoggerFactory.getLogger(BaseController::class.java)
    }

    fun handleException(ex: Exception) : ResponseEntity<Any> {
        logger.error(ex.printStackTrace().toString())
        return when (ex) {
            is NotFoundException -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message.toString())
            is TransactionVerificationException -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.localizedMessage.toString())
            else -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Action has failed, error: %s", ex.message.toString()))
        }
    }
}