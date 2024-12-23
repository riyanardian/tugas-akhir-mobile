package com.example.projectmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class MenuFragment(private val Recipe: Recipe) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val textViewTitle: TextView = view.findViewById(R.id.textViewMenuTitle)
        val textViewDescription: TextView = view.findViewById(R.id.textViewMenuDescription)
        val imageView: ImageView = view.findViewById(R.id.imageViewRecipe)

        textViewTitle.text = Recipe.title
        textViewDescription.text = Recipe.description

        // Gunakan Glide atau Picasso untuk memuat gambar
        Glide.with(this).load(Recipe.imageUrl).into(imageView)

        return view
    }
}
