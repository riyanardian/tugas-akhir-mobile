package com.example.projectmobile

data class Recipe(
        val id:String = "",
        val title: String = "",
        val description: String = "",
        val imageUrl: String = "",
        val timestamp: Long = 0L
): Serializable
