package com.example.triviasalud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var  viewModel:ViewModelMain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // supportActionBar?.hide()

      viewModel   = ViewModelProvider(this, SavedStateVMFactory(this))
            .get(ViewModelMain::class.java)


        viewModel.ViewPuntaje.observe(this, Observer {

            if (it){
                 materialCardView3.visibility= View.VISIBLE

            }else{

                materialCardView3.visibility=View.GONE
            }
        })


        viewModel.barralive.observe(this, Observer {

            if(it){
                supportActionBar?.show()
            }else{
                supportActionBar?.hide()
            }
        })

        viewModel.correctasLive.observe(this, Observer {
            correctas.text="correctas \n"+it.toString()
        })

        viewModel.incorrectasLive.observe(this, Observer {
            incorrectas.text="incorrectas \n"+it.toString()
        })

        viewModel.actualLive.observe(this, Observer {
            actual.text="pregunta \n"+it.toString()+"/8"
        })


        viewModel._cron.observe(this, Observer {
            if(it){
                cronometro.visibility=View.VISIBLE
            }else{

                cronometro.visibility=View.GONE
            }

        })


        viewModel.cronometrolive.observe(this, Observer {

            cronometro.text=(String.format(Locale.getDefault(),"Tiempo Restante %d segundos",it/1000L))

            viewModel.tiempoCron=it
        })



        val navController=this.findNavController(R.id.fragment)

        NavigationUI.setupActionBarWithNavController(this,navController)



    }


    override fun onSupportNavigateUp(): Boolean {

        val nacController=this.findNavController(R.id.fragment)
        return nacController.navigateUp()
    }
}
