package com.example.projectmobile

import LihatAdaptor
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class LihatResepActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var adapter: LihatAdaptor
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_resep)

        // Mengatur RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        recipeList = ArrayList()

        // Mengatur adapter
        adapter = LihatAdaptor(
            recipeList = recipeList,
            onEditClicked = { recipe ->
                val intent = Intent(this, EditDataActivity::class.java).apply {
                    putExtra("EXTRA_ID", recipe.id)
                    putExtra("EXTRA_TITLE", recipe.title)
                    putExtra("EXTRA_DESCRIPTION", recipe.description)
                    putExtra("EXTRA_IMAGE_URL", recipe.imageUrl)  // Menambahkan URL gambar ke Intent
                }
                startActivity(intent)
            },
            onDeleteClicked = { id ->
                deleteRecipe(id)
            }
        )
        recyclerView.adapter = adapter

        // Inisialisasi Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes")

        // Mengambil data dari Firebase
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeList.clear()
                if (snapshot.exists()) {
                    // Mengecek data di Firebase
                    for (dataSnapshot in snapshot.children) {
                        try {
                            // Mengambil data dan mengonversinya ke dalam objek Recipe
                            val recipe = dataSnapshot.getValue(Recipe::class.java)
                            if (recipe != null) {
                                // Menambahkan resep ke list
                                recipeList.add(recipe)
                            }
                        } catch (e: Exception) {
                            Log.e("Firebase", "Error converting data: ${e.message}")
                        }
                    }
                    // Log untuk mengecek ukuran list data
                    Log.d("LihatResepActivity", "Recipe List Size: ${recipeList.size}")
                    // Memberitahukan adapter bahwa data telah diperbarui
                    if (recipeList.isEmpty()) {
                        Log.d("LihatResepActivity", "No recipes found in Firebase")
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d("LihatResepActivity", "No data found in Firebase")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LihatResepActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteRecipe(recipeId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId)
        ref.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Resep berhasil dihapus", Toast.LENGTH_SHORT).show()
                val index = recipeList.indexOfFirst { it.id == recipeId }
                if (index != -1) {
                    recipeList.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            } else {
                Toast.makeText(this, "Gagal menghapus resep", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
