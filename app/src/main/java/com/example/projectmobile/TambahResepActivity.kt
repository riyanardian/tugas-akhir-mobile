package com.example.projectmobile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class TambahResepActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnChooseImage: Button
    private lateinit var btnSaveRecipe: Button
    private lateinit var ivPreviewImage: ImageView

    private var selectedImageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val recipesRef = database.getReference("recipes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_resep)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        btnChooseImage = findViewById(R.id.btnChooseImage)
        btnSaveRecipe = findViewById(R.id.btnSaveRecipe)
        ivPreviewImage = findViewById(R.id.ivPreviewImage)

        // Pilih Gambar
        btnChooseImage.setOnClickListener {
            selectImage()
        }

        // Simpan Resep
        btnSaveRecipe.setOnClickListener {
            saveRecipe()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data?.data != null) {
            selectedImageUri = data.data
            ivPreviewImage.setImageURI(selectedImageUri)
        }
    }

    private fun saveRecipe() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Isi semua kolom dan pilih gambar", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload Gambar ke Firebase Storage
        val imageRef = storage.reference.child("recipe_images/${UUID.randomUUID()}.jpg")
        selectedImageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        // Simpan data ke Firebase Database
                        val recipeId = recipesRef.push().key
                        val recipe = Recipe(
                            title = title,
                            description = description,
                            imageUrl = imageUrl.toString(),
                            timestamp = System.currentTimeMillis()
                        )

                        recipeId?.let {
                            recipesRef.child(it).setValue(recipe)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Resep berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal menyimpan resep", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
