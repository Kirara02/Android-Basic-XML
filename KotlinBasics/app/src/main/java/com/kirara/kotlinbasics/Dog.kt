package com.kirara.kotlinbasics

class Dog(val name: String, val bread: String) {

    init {
        bark(name)
    }
    fun bark(name: String){
        println("$name says Woof woof")
    }
}