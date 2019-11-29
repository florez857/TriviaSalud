package com.example.triviasalud

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialogo_abandonar_partida.view.*
import kotlinx.android.synthetic.main.dialogo_ok.view.*
import kotlinx.android.synthetic.main.dialogo_resultado.view.*
import kotlinx.android.synthetic.main.fragment_trivia.view.materialButton1
import kotlinx.android.synthetic.main.fragment_trivia.view.materialButton2
import kotlinx.android.synthetic.main.fragment_trivia.view.materialButton3
import kotlinx.android.synthetic.main.fragment_trivia.view.roottrivia
import kotlinx.android.synthetic.main.fragment_trivia.view.textView

import kotlinx.coroutines.Runnable
import java.lang.reflect.Type

/**
 * A simple [Fragment] subclass.
 */
class trivia : Fragment() {

    var listaBoton = mutableListOf<MaterialButton>()
    var listaPreguntas= mutableListOf<Pregunta>()
    lateinit var vista:View
    lateinit var  viewModel:ViewModelMain
    lateinit var  viewModelFragment:ViewModelTrivia
    lateinit var builderCargando :AlertDialog.Builder
    var cronom:CountDownTimer?=null
    lateinit var vistaDialogCargando :View
    lateinit var alert1 :AlertDialog
    lateinit var pref:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        builderCargando=AlertDialog.Builder(this.context)
        vistaDialogCargando= layoutInflater.inflate(R.layout.dialogo_cargando, null)
        builderCargando.setView(vistaDialogCargando)
        alert1= builderCargando.create()
        alert1.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alert1.setCanceledOnTouchOutside(false)


        Log.d("ciclo","onCreate")
         viewModel   = activity?.let {
             ViewModelProvider(it, SavedStateVMFactory(this))
                 .get(ViewModelMain::class.java)
         }!!

         viewModel._barra.value=false
        viewModel._cron.value=false

        viewModelFragment= ViewModelProvider(this, SavedStateVMFactory(this))
                .get(ViewModelTrivia::class.java)


       // viewModel?.puntaje(true)

        pref= context?.getSharedPreferences("datos",0) as SharedPreferences

       //  Log.d( "save sharedpreference", pref.getString("preguntas","vacio"))


        cronom=object:CountDownTimer(30000,1000){
            override fun onFinish() {

                mostrarDialogoTiempoSolucion(viewModel.id_pregunta,viewModel.id_solucion)
                Log.d("dialogo","mal pregunta")
                // this.cancel()
               // siguientePregunta(false)

            }

            override fun onTick(millisUntilFinished: Long) {
                viewModel._cronometro.value=millisUntilFinished
                Log.d("dialogo",millisUntilFinished.toString())
                viewModel.tiempoCron=millisUntilFinished
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        Log.d("ciclo","onCreateView")

        vista = inflater.inflate(R.layout.fragment_trivia, container, false)


        viewModel.cargalive.observe(this, Observer {

            if (it){


                vista.roottrivia.visibility=View.GONE
                alert1.show()
                viewModel.puntaje(false)
                viewModel._cron.value=false

            }else{
               vista.roottrivia.visibility=View.VISIBLE
                viewModel.puntaje(true)
                viewModel._cron.value=true
                alert1.dismiss()
                //cronom.start()
            }
        })



        listaBoton.add(vista.materialButton1)
        listaBoton.add(vista.materialButton2)
        listaBoton.add(vista.materialButton3)

        Log.d("guardar keysinicio",viewModelFragment.state.keys().toString())
//        if (viewModelFragment.state.get<String>("lista")==null){

        if (!viewModelFragment.state.contains("lista")){

            Log.d("guardar","no contiene")
            Log.d("guardar contains",viewModelFragment.state.contains("lista").toString())

           // alert1.show()
            Log.d("mostrando",alert1.isShowing.toString())

            viewModel.cargar()

            viewModel.listaPreguntasLive.observe(this, Observer {

               // alert1.dismiss()

               if(viewModel.guardado){

                   viewModel.guardado=false

                   Log.d("guardar", " guardado true")

               }else{

                   listaPreguntas=it
                   Log.d("tiempo", " listapregunta")
                   cargarPregunta(0)

                   Log.d("guardar", " guardado false")

               }



              //  }//


            })

        }else{

            var gson=Gson()
            var listType: Type =object:TypeToken<MutableList<Pregunta>?>() {}.getType()
            var listaGuard= gson.fromJson<MutableList<Pregunta>>(viewModelFragment.state.get<String>("lista") as String,listType)

                 listaPreguntas=listaGuard

            viewModel._actual.value=viewModelFragment.state.get<Int>("actual") as Int
            viewModel._correctas.value=viewModelFragment.state.get<Int>("correctas") as Int
            viewModel._incorrectas.value=viewModelFragment.state.get<Int>("incorrectas") as Int


            Log.d("guardar listaguardada", listaPreguntas.toString())

            var act=viewModel._actual.value as Int -1
                 cargarPregunta(act)
        }






        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event

       /**cuado se inicia se oculta el cuadro de tuntaje**/

          cronom?.cancel()
       val builder1 = AlertDialog.Builder(activity)

            val vistaDialog = layoutInflater.inflate(R.layout.dialogo_abandonar_partida, null)
            builder1.setView(vistaDialog)
            val alert2 = builder1.create()
            alert2.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            alert2.setCanceledOnTouchOutside(false)
            alert2.setCancelable(false)
            alert2.show()


            vistaDialog.si.setOnClickListener {

                vista.roottrivia.visibility=View.GONE
                viewModel.guardado=false
                findNavController().navigate(R.id.action_trivia_to_homeFragment)
                viewModel?.puntaje(false)
                viewModel._barra.value=true
                viewModel._cron.value=false
                viewModelFragment.state.remove<String>("lista")
                Log.d("guardar lista keys", viewModelFragment.state.keys().toString())
                viewModel.inicializar()
                cronom?.cancel()
                alert1.cancel()
                // viewModel.inicializar()

                alert2.dismiss()
            }


            vistaDialog.no.setOnClickListener {
                alert2.dismiss()
                cronom?.cancel()
                cronom?.start()

            }


//            alert.show()
//
//
//
//
//            viewModel?.puntaje(false)
//            viewModel._barra.value=true
//            viewModelFragment.state.remove<String>("lista")
//            viewModel.inicializar()
//            alert1.cancel()
//            // viewModel.inicializar()
//            findNavController().navigate(R.id.action_trivia_to_homeFragment)
        }
        return vista
    }

    fun cargarPregunta(posicion:Int) {

      var  id_respuesta=listaPreguntas.get(posicion).respuesta

        viewModel.id_pregunta=posicion
        viewModel.id_solucion=id_respuesta
        listaBoton.shuffle()

        cronom?.start()

        for (i in 0..2) {


            if ( id_respuesta != i) {
                listaBoton.get(i).text = listaPreguntas.get(posicion).opciones.get(i)
                listaBoton.get(i).setOnClickListener {
                    mostrarDialogoError(posicion,id_respuesta)
                    cronom?.cancel()
                }
            }else{

                Log.d("data fragment",listaPreguntas.toString())
                vista.textView.text=listaPreguntas.get(posicion).pregunta
                Log.d("data pregunta",listaPreguntas.get(posicion).pregunta)
                //cargar pregunta correcta
                listaBoton.get(i).text = listaPreguntas.get(posicion).opciones.get(id_respuesta)
                listaBoton.get(i).setOnClickListener {

                    cronom?.cancel()
                    mostrarDialgoPositivo()
                }
            }
        }
       // cronom.onFinish()
        //viewModel._cron.value=true
        //cronom.cancel()

        Log.d("tiempo","iniciando cronom cargarpregunt")

//        if( !viewModelFragment.state.contains("tiempo")){
//
//         // Log.d("tiempo contiene" , (viewModelFragment?.state.get<Long>("tiempo")as Long).toString())
//            cronom.start()
//        }else{
//
//           // Log.d("tiempo nocontiene" , (viewModelFragment?.state.get<Long>("tiempo")as Long).toString())
//           // cronom.onTick(viewModelFragment?.state.get<Long>("tiempo")as Long)
//        }



       // viewModel.puntaje(true)
        //vista.root.visibility=View.VISIBLE
    }

    private fun mostrarDialogoError(idPregunta:Int,idOpcion:Int) {

        val builder = AlertDialog.Builder(activity)

        val vistaDialog = layoutInflater.inflate(R.layout.dialogo_error, null)
        builder.setView(vistaDialog)


//        builder.setMessage("debes seguir usando ganaste")
//            .setTitle("GANASTE")
//            .setPositiveButton("seguir", DialogInterface.OnClickListener { dialog, which ->
//
//            })
//            .setNegativeButton("Salir", DialogInterface.OnClickListener { dialog, which ->
//                dialog.dismiss()
//
//            })

        val alert = builder.create()

        val handler = Handler()
        val runnable = Runnable {
            alert.dismiss()

            mostrarDialogoSolucion(idPregunta,idOpcion)
        }
        handler.postDelayed(runnable, 2000)
        alert.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
       // alert.getWindow()?.attributes?.windowAnimations=R.style.DialogAnimatio;
        alert.setCanceledOnTouchOutside(false)
        alert.setCancelable(false)
        alert.show()



    }

    private fun mostrarDialogoSolucion(idPregunta:Int,idOpcion:Int ) {
        val builder = AlertDialog.Builder(activity)

        val vistaDialog = layoutInflater.inflate(R.layout.dialogo_ok, null)
        builder.setView(vistaDialog)
        val alert = builder.create()
        alert.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false)
        alert.setCancelable(false)

        vistaDialog.textView5.text=listaPreguntas.get(idPregunta).opciones.get(idOpcion)

        vistaDialog.continuar.setOnClickListener {

            siguientePregunta(false)
            alert.dismiss()
        }
        alert.show()

    }



    private fun mostrarDialogoTiempoSolucion(idPregunta:Int,idOpcion:Int ) {
        val builder3 = AlertDialog.Builder(this.context)

        val vistaDialog3 = layoutInflater.inflate(R.layout.dilogo_tiempo_terminado, null)
        builder3.setView(vistaDialog3)
        val alert3 = builder3.create()
        alert3.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alert3.setCanceledOnTouchOutside(false)
        alert3.setCancelable(false)

        vistaDialog3.textView5.text=listaPreguntas.get(idPregunta).opciones.get(idOpcion)

        vistaDialog3.continuar.setOnClickListener {

            Log.d("dialogo","iniciando dialog")
            cronom?.cancel()
            siguientePregunta(false)
            alert3.dismiss()

        }
        cronom?.cancel()
        alert3.show()

    }



    private fun mostrarDialgoPositivo() {

        val builder = AlertDialog.Builder(activity)

        val vistaDialog = layoutInflater.inflate(R.layout.dialogo_correcto, null)
        builder.setView(vistaDialog)


//        builder.setMessage("debes seguir usando ganaste")
//            .setTitle("GANASTE")
//            .setPositiveButton("seguir", DialogInterface.OnClickListener { dialog, which ->
//
//            })
//            .setNegativeButton("Salir", DialogInterface.OnClickListener { dialog, which ->
//                dialog.dismiss()
//
//            })

        val alert = builder.create()

        val handler = Handler()
        val runnable = Runnable {

            Log.d("data actual",viewModel._actual.value?.toString())



            siguientePregunta(true)

            alert.dismiss()


        }
        handler.postDelayed(runnable, 2000)
        alert.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
       // alert.getWindow()?.attributes?.windowAnimations=R.style.DialogAnimatio;
        alert.setCanceledOnTouchOutside(false)
        alert.setCancelable(false)
        alert.show()

    }

    private fun siguientePregunta(respuestaEstado:Boolean) {


        var actualVal = viewModel._actual.value as Int
        var corectVal = viewModel._correctas.value as Int
        var incorectVal = viewModel._incorrectas.value as Int



        if (actualVal < 8) {
                 if(respuestaEstado){
                     viewModel._correctas.value = corectVal + 1
                 }else{
                     viewModel._incorrectas.value = incorectVal + 1
                 }
            viewModel._actual.value = actualVal + 1


            Log.d("data cactual inc", viewModel._actual.value?.toString())
            cargarPregunta(actualVal)
            //cronom.cancel()
            // alert.dismiss()
        } else {

            if(respuestaEstado){
                viewModel._correctas.value = corectVal + 1
            }else{
                viewModel._incorrectas.value = incorectVal + 1
            }
            viewModel._actual.value = actualVal + 1
            viewModel._cron.value=false
            cronom?.cancel()
            cargarResultado()


            // viewModel.inicializar()
            // alert.dismiss()
           // findNavController().navigate(R.id.action_trivia_to_homeFragment)
        }
    }

    private fun cargarResultado() {

        val builder = AlertDialog.Builder(activity)

        val vistaDialog = layoutInflater.inflate(R.layout.dialogo_resultado, null)
        builder.setView(vistaDialog)
        val alert = builder.create()
        alert.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false)
        alert.setCancelable(false)

        vistaDialog.mostrarcorrectas.text="Respuestas Correctas "+viewModel._correctas.value.toString()
        vistaDialog.mostrarIncorrectas.text="Respuestas Incorrectas "+viewModel._incorrectas.value.toString()
        vistaDialog.mostrarTotal.text="8 Preguntas Realizadas"

       // vistaDialog.textView5.text=listaPreguntas.get(idPregunta).opciones.get(idOpcion)
          vista.roottrivia.visibility=View.GONE
          viewModel?.puntaje(false)
        cronom?.cancel()
        vistaDialog.terminar.setOnClickListener {
           // siguientePregunta(false)
            alert.dismiss()
            alert1.dismiss()
            cronom?.cancel()
            viewModel._cron.value=false
            viewModel?.puntaje(false)
            viewModel._actual.value = 1
            viewModel._correctas.value = 0
            viewModel._incorrectas.value = 0
            viewModel.guardado=false
            findNavController().navigate(R.id.action_trivia_to_homeFragment)

        }
        alert.show()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        Log.d("tiempo onsaveView",viewModel.tiempoCron.toString())
       // viewModelFragment.state.set<Long>("tiempo",viewModel.tiempoCron)

        Log.d("tiempo onsaveState",viewModelFragment.state.get<Long>("tiempo").toString())
        //cronom.onFinish()
        //cronom=null
        //viewModel.cr


        cronom?.cancel()
        if(viewModelFragment.state.get<String>("lista")==null) {

            var gson = Gson()
            val listType: Type = object : TypeToken<MutableList<Pregunta>?>() {}.getType()
            val preguntJson = gson.toJson(viewModel._listaPreguntas.value, listType)
            Log.d("save guardajsonpreg", preguntJson)
            // viewModel.guardarPregunta(preguntJson)
            viewModelFragment.state.set<String>("lista", preguntJson)
        }
            viewModelFragment.state.set<Int>("actual",viewModel._actual.value as Int)
            viewModelFragment.state.set<Int>("correctas",viewModel._correctas.value as Int)
            viewModelFragment.state.set<Int>("incorrectas",viewModel._incorrectas.value as Int)

            Log.d("viewmodel 1actual",viewModel._actual.value.toString())
            Log.d("viewmodel 1incorrectas",viewModel._incorrectas.value.toString())
            Log.d("viewmodel 1correctas",viewModel._correctas.value.toString())






    }


    override fun onResume() {
        super.onResume()
        cronom?.start()
    }


}
