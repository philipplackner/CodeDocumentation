package com.plcoding.codedocumentation

/**
 * Processes a payment transaction between two accounts.
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
 * @param transactionMode The mode of the transaction.
 * Can be [TransactionMode.REGULAR] or [TransactionMode.REGULAR]
 * @param notes Optional notes or description for the transaction.
 * @param feeStrategy Optional strategy to calculate transaction fees.
 * If no strategy is passed, no fees will be applied.
 * @param retryCount The number of attempts made to process the transaction.
 *
 * @return A [TransactionResult] representing the outcome of the transaction.
 *
 * @throws IllegalArgumentException if the transaction amount is invalid.
 */
//fun processPayment2(
//    sender: Account,
//    receiver: Account,
//    amount: Double,
//    transactionMode: TransactionMode,
//    notes: String? = null,
//    feeStrategy: FeeStrategy? = null,
//    retryCount: Int = 0
//): TransactionResult {
//    if (amount <= 0) throw IllegalArgumentException("Invalid amount")
//
//    val fee = feeStrategy?.calculateFee(amount) ?: 0.0
//    val conversionRate = getConversionRate(sender.currency, receiver.currency)
//    if (!sender.hasSufficientFunds(amount, conversionRate, fee)) {
//        return TransactionResult.Failure("Insufficient Funds")
//    }
//
//    if (transactionMode == TransactionMode.INSTANT && !receiver.supportsInstant()) {
//        return TransactionResult.Failure("Receiver doesn't support instant transactions")
//    }
//
//    val remainingBalance = sender.balance - (amount + fee)
//
//    if (remainingBalance < 500) {
//        // Due to an international compliance regulation, additional verification
//        // is needed if sender's balance goes below 500 after transaction.
//        if (!performAdditionalVerification(sender)) {
//            return TransactionResult.Failure("Additional verification failed.")
//        }
//    }
//
//    val finalAmount = if (sender.currency == receiver.currency) {
//        amount
//    } else {
//        amount * getConversionRate(sender.currency, receiver.currency)
//    }
//
//    if (!updateAccounts(sender, receiver)) {
//        if (retryCount < 3) {
//            return processPayment2(
//                sender,
//                receiver,
//                amount,
//                transactionMode,
//                notes,
//                feeStrategy,
//                retryCount + 1
//            )
//        }
//        return TransactionResult.Failure("Transaction failed after multiple attempts")
//    }
//
//    return TransactionResult.Success(finalAmount, fee)
//}
//
///**
// * @receiver The account to check the funds for.
// */
//fun Account.hasSufficientFunds(
//    paymentAmount: Double,
//    conversionRate: Double = 1.0,
//    fee: Double? = null
//): Boolean {
//    return if (fee != null) {
//        (paymentAmount + fee) * conversionRate > balance
//    } else {
//        paymentAmount * conversionRate > balance
//    }
//}

