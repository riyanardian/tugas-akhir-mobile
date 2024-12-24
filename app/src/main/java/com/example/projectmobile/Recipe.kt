package com.example.projectmobile

data class Recipe(
        val id:String = "",
        val title: String = "",        // Nama resep
        val description: String = "",  // Deskripsi resep
        val imageUrl: String = "",     // URL gambar resep
        val timestamp: Long = 0        // Timestamp untuk urutan resep
)
