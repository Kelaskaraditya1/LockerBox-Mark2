package com.starkindustries.lockerboxmark2.Models
class FileStructure
{
    lateinit var name:String
    lateinit var fileType:String
    lateinit var fileUri:String
    constructor(name_:String,fileType_:String,fileUri_:String)
    {
        this.name=name_
        this.fileType=fileType_
        this.fileUri=fileUri_
    }
    constructor(name_:String,filetype_:String)
    {
        this.name=name_
        this.fileType=filetype_
    }
    constructor()
    {

    }
}