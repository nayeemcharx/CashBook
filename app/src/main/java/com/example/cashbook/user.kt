package com.example.cashbook

class user(val email:String)
{
    private lateinit var firstName:String
    private lateinit var lastName:String
    private lateinit var pin:String

    fun setFirstName(firstName:String)
    {
        this.firstName=firstName
    }
    fun setLastName(lastName:String)
    {
        this.lastName=lastName
    }
    fun setPin(pin:String)
    {
        this.pin=pin
    }

}