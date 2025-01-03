package com.example.projectmobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewPagerMenu: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnTambahResep: CardView
    private lateinit var btnLihatResep: CardView
    private lateinit var btnLogout: Button
    private lateinit var switchDarkMode: Switch // Switch untuk Dark Mode
    private val recipes = mutableListOf<Recipe>()
    private lateinit var database: FirebaseDatabase
    private lateinit var recipesRef: DatabaseReference

    // Handler untuk Auto Slide
    private val handler = Handler(Looper.getMainLooper())
    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            val nextItem = (viewPagerMenu.currentItem + 1) % recipes.size
            viewPagerMenu.currentItem = nextItem
            handler.postDelayed(this, 5000)
        }
    }

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
        tabLayout = findViewById(R.id.indicatorLayout)
        btnTambahResep = findViewById(R.id.cardTambahResep)
        btnLihatResep = findViewById(R.id.cardLihatResep)
        btnLogout = findViewById(R.id.btnLogout)
        switchDarkMode = findViewById(R.id.switchDarkMode) // Inisialisasi switch untuk Dark Mode

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

        // Auto Slide ViewPager
        setupAutoSlide()

        // Setup Dark Mode Switch
        setupDarkModeSwitch()
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

                    // Hubungkan TabLayout dengan ViewPager2
                    TabLayoutMediator(tabLayout, viewPagerMenu) { tab, position ->
                        tab.text = "Tab ${position + 1}" // Ganti dengan nama yang sesuai

                        // Set warna default untuk tab yang tidak aktif
                        tab.view.setBackgroundColor(ContextCompat.getColor(this@DashboardActivity, R.color.light_grey)) // Warna default

                        // Atur warna untuk tab yang aktif
                        viewPagerMenu.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                for (i in 0 until tabLayout.tabCount) {
                                    val tabAt = tabLayout.getTabAt(i)
                                    if (i == position) {
                                        tabAt?.view?.setBackgroundColor(ContextCompat.getColor(this@DashboardActivity, R.color.dark_grey)) // Warna untuk tab aktif
                                    } else {
                                        tabAt?.view?.setBackgroundColor(ContextCompat.getColor(this@DashboardActivity, R.color.light_grey)) // Warna untuk tab non-aktif
                                    }
                                }
                            }
                        })
                    }.attach()

                    // Tampilkan MenuFragment dengan resep pertama jika ada
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

    // Fungsi untuk setup Auto Slide
    private fun setupAutoSlide() {
        handler.postDelayed(autoSlideRunnable, 5000)

        // Hentikan Auto Slide ketika pengguna melakukan scroll manual
        viewPagerMenu.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(autoSlideRunnable)
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    handler.postDelayed(autoSlideRunnable, 5000)
                }
            }
        })
    }

    // Fungsi untuk setup Dark Mode Switch
    private fun setupDarkModeSwitch() {
        // Mengambil state mode saat ini
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        switchDarkMode.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES

        // Mengatur listener untuk switch Dark Mode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Mengaktifkan Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Mengaktifkan Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoSlideRunnable)
    }
}
