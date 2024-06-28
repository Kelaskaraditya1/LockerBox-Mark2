package com.starkindustries.lockerboxmark2.Models
class User
{
    lateinit var name:String
    lateinit var username:String
    lateinit var phoneNo:String
    lateinit var password:String
    lateinit var email:String
    constructor(name_:String,username_:String,phoneNo_:String,password_:String,email_:String)
    {
        this.name=name_
        this.username=username_
        this.phoneNo=phoneNo_
        this.password=password_
        this.email=email_
    }
    constructor(name_:String,username_:String,phoneNo_:String,email_:String)
    {
        this.name=name_
        this.username=username_
        this.phoneNo=phoneNo_
        this.email=email_
    }
    constructor(name_:String,username_:String,email_:String)
    {
        this.name=name_
        this.username=username_
        this.email=email_
    }
    constructor()
    {

    }
}