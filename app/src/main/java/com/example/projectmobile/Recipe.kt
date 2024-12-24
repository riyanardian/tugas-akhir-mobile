package com.example.projectmobile

import java.io.Serializable

data class Recipe(
        val id:String = "",
        val title: String = "",
        val description: String = "",
        val imageUrl: String = "",
        val timestamp: Long = 0L
) : Serializable
