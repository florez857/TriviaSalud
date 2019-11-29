package com.example.triviasalud

data class Pregunta constructor(var pregunta:String,var respuesta:Int,var id_pregunta:Int,var opciones:MutableList<String> )

{


    constructor() : this("",0 ,0, mutableListOf())
}