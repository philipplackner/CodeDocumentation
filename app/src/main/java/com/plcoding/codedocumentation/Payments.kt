package com.plcoding.codedocumentation

fun processPayment(
    sender: Account,
    receiver: Account,
    amount: Double,
    transactionMode: TransactionMode,
    notes: String? = null,
    feeStrategy: FeeStrategy? = null,
    retryCount: Int = 0
): TransactionResult {
    if (amount <= 0) {
        throw IllegalArgumentException("Invalid amount")
    }

    if (receiver.currency != sender.currency) {
        val conversionRate = getConversionRate(sender.currency, receiver.currency)
        if (amount * conversionRate > sender.balance) {
            return TransactionResult.Failure("Insufficient Funds")
        }
    } else if(amount > sender.balance) {
        return TransactionResult.Failure("Insufficient Funds")
    }

    if (transactionMode == TransactionMode.INSTANT && !receiver.supportsInstant()) {
        return TransactionResult.Failure("Receiver doesn't support instant transactions")
    }

    val fee = feeStrategy?.calculateFee(amount) ?: 0.0
    if (fee + amount > sender.balance) {
        return TransactionResult.Failure("Insufficient funds")
    }

    val remainingBalance = sender.balance - (amount + fee)
    if (remainingBalance < 500) {
        if (!performAdditionalVerification(sender)) {
            return TransactionResult.Failure("Additional verification failed.")
        }
    }

    val finalAmount = if (sender.currency == receiver.currency) {
        amount
    } else {
        amount * getConversionRate(sender.currency, receiver.currency)
    }

    if (!updateAccounts(sender, receiver)) {
        if (retryCount < 3) {
            return processPayment(
                sender,
                receiver,
                amount,
                transactionMode,
                notes,
                feeStrategy,
                retryCount + 1
            )
        }
        return TransactionResult.Failure("Transaction failed after multiple attempts")
    }

    return TransactionResult.Success(finalAmount, fee)
}

data class Account(
    val id: String,
    var balance: Double,
    val currency: Currency,
) {
    fun supportsInstant(): Boolean = true
}

enum class Currency {
    USD, EUR, GBP, INR
}

enum class TransactionMode {
    INSTANT, REGULAR
}

interface FeeStrategy {
    fun calculateFee(amount: Double): Double
}

sealed class TransactionResult {
    data class Success(val transferredAmount: Double, val fee: Double) : TransactionResult()
    data class Failure(val reason: String) : TransactionResult()
}

fun getConversionRate(from: Currency, to: Currency): Double = 1.0
fun updateAccounts(sender: Account, receiver: Account) = true
fun performAdditionalVerification(account: Account) = true
