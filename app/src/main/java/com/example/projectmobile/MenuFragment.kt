package com.example.projectmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class MenuFragment : Fragment() {

    private lateinit var recipe: Recipe

    companion object {
        fun newInstance(recipe: Recipe): MenuFragment {
            val fragment = MenuFragment()
            val bundle = Bundle()
            bundle.putSerializable("recipe", recipe)  // Menyimpan recipe dalam bundle
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        // Mengambil objek Recipe dari arguments
        recipe = arguments?.getSerializable("recipe") as Recipe

        val textViewTitle: TextView = view.findViewById(R.id.textViewMenuTitle)
        val textViewDescription: TextView = view.findViewById(R.id.textViewMenuDescription)
        val imageView: ImageView = view.findViewById(R.id.imageViewRecipe)

        textViewTitle.text = recipe.title
        textViewDescription.text = recipe.description

        // Gunakan Glide atau Picasso untuk memuat gambar
        Glide.with(this).load(recipe.imageUrl).into(imageView)

        return view
    }
}
