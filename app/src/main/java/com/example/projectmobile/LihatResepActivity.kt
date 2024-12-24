package com.example.projectmobile

import LihatAdaptor
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LihatResepActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeList: ArrayList<Recipe>
    private lateinit var adapter: LihatAdaptor
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lihat_resep)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi RecyclerView
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
                    putExtra("EXTRA_STEP", recipe.step)
                }
                startActivity(intent)
            },
            onDeleteClicked = { id ->
                deleteRecipe(id)
            }
        )
        recyclerView.adapter = adapter


        // Inisialisasi Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes")

        // Mengambil data dari Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeList.clear()
                for (dataSnapshot in snapshot.children) {
                    try {
                        // Log data yang diterima
                        Log.d("FirebaseData", "Data: ${dataSnapshot.value}")
                        val recipe = dataSnapshot.getValue(Recipe::class.java)
                        if (recipe != null) {
                            recipeList.add(recipe)
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error converting data: ${e.message}")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LihatResepActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteRecipe(recipeId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId)
        ref.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Resep berhasil dihapus", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menghapus resep", Toast.LENGTH_SHORT).show()
            }
        }
    }
}