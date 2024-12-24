package com.example.projectmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.projectmobile.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewPagerMenu: ViewPager2
    private lateinit var btnTambahResep: Button
    private lateinit var btnLihatResep: Button
    private val recipes = mutableListOf<Recipe>()
    private lateinit var database: FirebaseDatabase
    private lateinit var recipesRef: DatabaseReference
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Set padding untuk mengatasi insets pada tampilan
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi komponen UI
        viewPagerMenu = findViewById(R.id.viewPagerMenu)
        btnTambahResep = findViewById(R.id.btnTambahResep)
        btnLihatResep = findViewById(R.id.btnLihatResep)
        btnLogout = findViewById(R.id.btnLogout)


        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance()
        recipesRef = database.getReference("recipes")

        // Ambil data resep dari Firebase
        getRecipesFromFirebase()

        // Event listener untuk tombol
        btnTambahResep.setOnClickListener {
            val intent = Intent(this, TambahResepActivity::class.java)
            startActivity(intent)
        }

        btnLihatResep.setOnClickListener {
            val intent = Intent(this, LihatResepActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Fungsi untuk mengambil data resep dari Firebase
    private fun getRecipesFromFirebase() {
        recipesRef.orderByChild("timestamp") // Mengurutkan berdasarkan timestamp
            .limitToLast(5) // Ambil 5 resep terakhir
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    recipes.clear()
                    for (data in snapshot.children) {
                        val recipe = data.getValue(Recipe::class.java)
                        recipe?.let { recipes.add(it) }
                    }
                    // Update adapter untuk ViewPager2 setelah data diterima
                    val adapter = DashboardAdapter(this@DashboardActivity, recipes)
                    viewPagerMenu.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Gagal memuat data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
