package com.example.triviasalud

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ViewModelMain(  val state: SavedStateHandle):ViewModel() {

     var _ViewPuntaje=MutableLiveData<Boolean>()
     val ViewPuntaje:LiveData<Boolean>
        get()= _ViewPuntaje

    // lateinit var listaIndices:MutableList<Int>
    // lateinit var listaPreguntas:MutableList<Pregunta>

    var _listaPreguntas=MutableLiveData<MutableList<Pregunta>>()
    val listaPreguntasLive:LiveData<MutableList<Pregunta>>
    get() = _listaPreguntas


    var _correctas=MutableLiveData<Int>()
    val correctasLive:LiveData<Int>
        get() = _correctas


    var _incorrectas=MutableLiveData<Int>()
    val incorrectasLive:LiveData<Int>
        get() = _incorrectas


    var _actual=MutableLiveData<Int>()
    val actualLive:LiveData<Int>
        get() = _actual


    var _carga=MutableLiveData<Boolean>()
    val cargalive:LiveData<Boolean>
        get() = _carga

    var _barra=MutableLiveData<Boolean>()
    val barralive:LiveData<Boolean>
        get() = _barra


    var _cron=MutableLiveData<Boolean>()
    val cronalive:LiveData<Boolean>
        get() = _cron


    var _cronometro=MutableLiveData<Long>()
    val cronometrolive:LiveData<Long>
        get() = _cronometro


    var tiempoCron:Long=0L

    var id_pregunta:Int=-1
     var id_solucion:Int=-1

    var guardado:Boolean=false

init {
    _cron.value=false
    _barra.value=true
     _carga.value=false
    _ViewPuntaje.value=false
    _correctas.value=0
    _incorrectas.value=0
    _actual.value=1
}

    fun puntaje(Puntaje:Boolean){
        _ViewPuntaje.value=Puntaje
    }


    fun carga(car:Boolean){
        _carga.value=car

    }

    fun inicializar(){
        _ViewPuntaje.value=false
        _correctas.value=0
        _incorrectas.value=0
        _actual.value=1
    }

    fun cargar(){
        // _ViewPuntaje.value=false

        Log.d("guardar", "cargando.....")
         _carga.value=true
        val db = FirebaseFirestore.getInstance()

        db.collection("indices")
            .document("id_indices")
            .get().addOnSuccessListener {
              var  listaIndices= it.data?.get("lista") as MutableList<Int>

                Log.d("data listaindiori", listaIndices.toString())
                listaIndices.shuffle()

                Log.d("data listaindimez", listaIndices.toString())
                Log.d("guardar listaindice", listaIndices.toString())



              var  listaPreguntas= mutableListOf<Pregunta>()

                var query= db.collection("preguntas")

                for(i in 0..7){

                    query.whereEqualTo("id_pregunta",listaIndices.get(i))
                    Log.d("data indico", listaIndices.get(i).toString())

                }

                query.get()
                    .addOnCompleteListener {
                        var documentos= it.result?.documents
                        Log.d("data tamaño", documentos?.size.toString())
                        var i=0

                        for (dc in documentos!!){


                            var pregunta= dc.toObject(Pregunta::class.java)

                            pregunta?.let { it1 -> listaPreguntas.add(i,it1) }
                            Log.d("data opciones", pregunta?.opciones.toString())
                            Log.d("data respuesta", pregunta?.respuesta.toString())
                            Log.d("data pregunta", pregunta?.pregunta.toString())
                            Log.d("data id_pregunta", pregunta?.id_pregunta.toString())
                            Log.d("data listaPreguntastemp", listaPreguntas.toString())
                           i=i.inc()
                        }
                      //  listaPreguntas.removeAt(0)

                        listaPreguntas.shuffle()

                        Log.d("data listaPreguntascon", listaPreguntas.toString())
                        Log.d("data listaPrTamaño", listaPreguntas.size.toString())
                        Log.d("guardar listafinal", listaPreguntas.size.toString())



                        _listaPreguntas.value=listaPreguntas
                        Log.d("guardar listaViewModel",_listaPreguntas.value.toString())

                       // _cron.value=true
                        //_ViewPuntaje.value=true
                        _carga.value=false

                    }
            }
    }

}