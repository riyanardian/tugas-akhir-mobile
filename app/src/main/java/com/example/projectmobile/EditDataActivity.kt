package com.example.projectmobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditDataActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var stepEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var databaseReference: DatabaseReference
    private var recipeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_data)

        titleEditText = findViewById(R.id.editTextNama)            // Assuming we're using the same layout IDs
        descriptionEditText = findViewById(R.id.editTextBahandanAlat)
        stepEditText = findViewById(R.id.editTextLangkahLangkah)
        updateButton = findViewById(R.id.editdata)

        //Ambil data dari Intent
        recipeId = intent.getStringExtra("EXTRA_ID")
        val title = intent.getStringExtra("EXTRA_TITLE")
        val description = intent.getStringExtra("EXTRA_DESCRIPTION")
        val step = intent.getStringExtra("EXTRA_STEP")

        // Isi data ke dalam form
        titleEditText.setText(title)
        descriptionEditText.setText(description)
        stepEditText.setText(step)

        // Update data di Firebase saat tombol diklik
        updateButton.setOnClickListener {
            val updatedTitle = titleEditText.text.toString().trim()
            val updatedDescription = descriptionEditText.text.toString().trim()
            val updatedStep = stepEditText.text.toString().trim()

            updateRecipe(recipeId, updatedTitle, updatedDescription, updatedStep)
        }
    }

    private fun updateRecipe(id: String?, title: String, description: String, step: String) {
        if (id != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Recipes").child(id)

            val recipe = Recipe(
                title = title,
                description = description,
                step = step,
                imageUrl = "",  // Maintain existing image URL if needed
                timestamp = System.currentTimeMillis() // Update timestamp to current time
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