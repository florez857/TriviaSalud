package com.example.triviasalud


import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.fragment_home.view.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event

            /**cuado se inicia se oculta el cuadro de tuntaje**/
            activity?.finish()
        }

        val  viewModel   = activity?.let {
            ViewModelProvider(it, SavedStateVMFactory(this))
                .get(ViewModelMain::class.java)
        }
        // Inflate the layout for this fragment

   val view=inflater.inflate(R.layout.fragment_home, container, false)
        viewModel?._barra?.value=true

   view.materialButton.setOnClickListener {

       findNavController().navigate(R.id.action_homeFragment_to_trivia)
       //viewModel?._barra?.value=false
       //viewModel?.puntaje(false)
   }

        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item!!,view!!.findNavController())|| super.onOptionsItemSelected(item)
    }



}
