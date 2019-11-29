package com.example.triviasalud

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ViewModelTrivia (  val state: SavedStateHandle): ViewModel() {


    var _ViewPuntaje= MutableLiveData<Boolean>()
    val ViewPuntaje: LiveData<Boolean>
        get()= _ViewPuntaje

    lateinit var listaIndices:MutableList<Int>
    lateinit var listaPreguntas:MutableList<Pregunta>

    var _listaPreguntas= MutableLiveData<MutableList<Pregunta>>()
    val listaPreguntasLive: LiveData<MutableList<Pregunta>>
        get() = _listaPreguntas


    var _correctas= MutableLiveData<Int>()
    val correctasLive: LiveData<Int>
        get() = _correctas


    var _incorrectas= MutableLiveData<Int>()
    val incorrectasLive: LiveData<Int>
        get() = _incorrectas


    var _actual= MutableLiveData<Int>()
    val actualLive: LiveData<Int>
        get() = _actual

    init {

        Log.d("ciclo","viewModel")

        Log.d("save keys",state.keys().toString())
        //Log.d("save valoractual",state.get<Int>("actual").toString())
//        Log.d("save valorPuntaje",state.get<Boolean>("puntaje").toString())
//        Log.d("save valorCorrectas",state.get<Int>("correctas").toString())
//        Log.d("save valorIncorrectas",state.get<Int>("incorrectas").toString())
//        Log.d("save valorActual",state.get<Int>("actual").toString())

//
//        _ViewPuntaje.value=false
//       // state.set<Boolean>("puntaje",_ViewPuntaje.value)
//        _correctas.value=0
//       // state.set<Int>("correctas",_correctas.value)
//        _incorrectas.value=0
//      //  state.set<Int>("incorrectas",_incorrectas.value)
//        _actual.value=1
       // state.set<Int>("actual",_actual.value)
       // _listaPreguntas.value?.clear()

        Log.d("save lista",state.get<String>("lista").toString())

    }


}