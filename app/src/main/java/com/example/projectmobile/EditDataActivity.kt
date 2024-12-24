package com.example.projectmobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditDataActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var imageViewProfile: ImageView
    private lateinit var updateButton: Button
    private lateinit var databaseReference: DatabaseReference
    private var recipeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_data)

        titleEditText = findViewById(R.id.editTextNama)
        descriptionEditText = findViewById(R.id.editTextBahandanAlat)
        updateButton = findViewById(R.id.editdata)

        // Ambil data dari Intent
        recipeId = intent.getStringExtra("EXTRA_ID")
        val title = intent.getStringExtra("EXTRA_TITLE")
        val description = intent.getStringExtra("EXTRA_DESCRIPTION")

        // Isi data ke dalam form
        titleEditText.setText(title)
        descriptionEditText.setText(description)


        // Update data di Firebase saat tombol diklik
        updateButton.setOnClickListener {
            val updatedTitle = titleEditText.text.toString().trim()
            val updatedDescription = descriptionEditText.text.toString().trim()


        }
    }

    private fun updateRecipe(id: String?, title: String, description: String, imageUrl: String?) {
        if (id != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Recipes").child(id)

            val recipe = Recipe(
                title = title,
                description = description,
                imageUrl = imageUrl ?: "", // Tetap gunakan URL gambar yang ada
                timestamp = System.currentTimeMillis() // Update timestamp ke waktu saat ini
            )

            databaseReference.setValue(recipe).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Resep berhasil diupdate", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal mengupdate resep", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
