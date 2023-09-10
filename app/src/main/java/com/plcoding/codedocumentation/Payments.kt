package com.plcoding.codedocumentation

/**
 * Processes a payment between two accounts.
 *
 * Example usage:
 * ```
 * val sender = Account(
 *     id = "1",
 *     balance = 100.0,
 *     currency = Currency.EUR
 * )
 * val receiver = Account(
 *     id = "2",
 *     balance = 200.0,
 *     currency = Currency.USD
 * )
 * val result = processPayment(
 *     sender = sender,
 *     receiver = receiver,
 *     amount = 50.0,
 *     transactionMode = TransactionMode.INSTANT,
 *     notes = null,
 *     feeStrategy = null
 * )
 * ```
 *
 * @param sender The account initiating the payment.
 * @param receiver The account receiving the payment.
 * @param amount The amount to be transferred in the currency of the sender.
 * @param transactionMode The mode of the transaction. Can be either
 * [TransactionMode.REGULAR] or [TransactionMode.INSTANT].
 * @param notes Optional notes or description for the transaction.
 * @param feeStrategy Optional strategy to calculate fees. When ignored,
 * no fees will be applied.
 * @param retryCount The amount of attempts made to process this payment.
 *
 * @return A [TransactionResult] representing the outcome of the transaction.
 *
 * @throws IllegalArgumentException if the amount is invalid.
 */
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

    val fee = feeStrategy?.calculateFee(amount) ?: 0.0
    val conversionRate = getConversionRate(sender.currency, receiver.currency)
    if(!sender.hasSufficientFunds(amount, conversionRate, fee)) {
        return TransactionResult.Failure("Insufficient Funds")
    }

    if (transactionMode == TransactionMode.INSTANT && !receiver.supportsInstant()) {
        return TransactionResult.Failure("Receiver doesn't support instant transactions")
    }

    // Due to international compliance regulations, additional verification is needed
    // if the sender's balance falls under 500 after a transaction.
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

    fun hasSufficientFunds(
        paymentAmount: Double,
        conversionRate: Double = 1.0,
        fee: Double? = null
    ): Boolean {
        return if(fee != null) {
            (paymentAmount + fee) * conversionRate <= balance
        } else {
            paymentAmount * conversionRate <= balance
        }
    }
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
