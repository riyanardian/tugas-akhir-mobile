import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.R
import com.example.projectmobile.Recipe


class LihatAdaptor(
    private val recipeList: ArrayList<Recipe>, // Konsisten menggunakan ArrayList
    private val onEditClicked: (Recipe) -> Unit,
    private val onDeleteClicked: (String) -> Unit
) : RecyclerView.Adapter<LihatAdaptor.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvDescription)
        private val editButton: Button = itemView.findViewById(R.id.editdata)
        private val deleteButton: Button = itemView.findViewById(R.id.hapusdata)

        fun bind(recipe: Recipe) {
            titleTextView.text = recipe.title
            descriptionTextView.text = recipe.description

            // Handle tombol edit
            editButton.setOnClickListener {
                onEditClicked(recipe)
            }

            // Handle tombol hapus
            deleteButton.setOnClickListener {
                onDeleteClicked(recipe.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resep, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }

    override fun getItemCount(): Int = recipeList.size
}

