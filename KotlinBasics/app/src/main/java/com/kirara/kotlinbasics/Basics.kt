package com.kirara.kotlinbasics

data class CoffeeDetails(
    val sugarCount: Int,
    val name: String,
    val size: String,
    val creamAmount: Int
)

fun main(){
    // Immutable list
    // val shoppingList = listOf("Processor", "RAM", "Graphics Card","SSD")
    // Mutable list - you can add items later modify
    // val shoppingList = mutableListOf("Processor", "RAM", "Graphics Card","SSD")

    val kiraraBankAccount = BankAccount("Kirara Bernstein", 1322.20)

    kiraraBankAccount.deposit(200.0)
    kiraraBankAccount.withdraw(1200.0)
    kiraraBankAccount.deposit(3000.0)
    kiraraBankAccount.displayTransactionHistory()

    println("${kiraraBankAccount.accountHolder}'s " + "balance is ${kiraraBankAccount.balance}")
}
