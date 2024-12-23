package com.example.projectmobile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class DashboardAdapter(
    fragmentActivity: FragmentActivity,
    private val recipeList: List<Recipe>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = recipeList.size

    override fun createFragment(position: Int): Fragment {
        val Recipe = recipeList[position]
        return MenuFragment(Recipe) // Mengirim data resep ke fragment
    }
}
