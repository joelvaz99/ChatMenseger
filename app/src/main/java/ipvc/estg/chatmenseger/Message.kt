package ipvc.estg.chatmenseger

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.firebasenotifications.NotificationData
import com.androiddevs.firebasenotifications.PushNotification
import com.androiddevs.firebasenotifications.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipvc.estg.chatmenseger.ModelClasse.ChatList
import ipvc.estg.chatmenseger.ModelClasse.Contact
import ipvc.estg.chatmenseger.ModelClasse.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




class Message : AppCompatActivity() {
    private var mMe: User? = null

    private lateinit var mAdapter: GroupAdapter<ViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)


        verifiyAuthetication()


        }



    private fun verifiyAuthetication() {

        if (FirebaseAuth.getInstance().uid == null) {
            val intent = Intent(this@Message, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        } else {

            val reference = FirebaseDatabase.getInstance().reference
                    .child("users").child(FirebaseAuth.getInstance().uid.toString())

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val user: User? = p0.getValue(User::class.java)
                    mMe = user

                    val nome = findViewById<TextView>(R.id.nome)
                    val imgPhoto = findViewById<ImageView>(R.id.foto)


                    Picasso.get().load(mMe!!.url).into(imgPhoto)
                    nome.text = mMe!!.name


                     supportActionBar?.title = mMe!!.name


                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })


        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    // Logout
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                verifiyAuthetication()

                true
            }
            R.id.search_contact -> {
                val intent = Intent(this@Message, ContactActivity::class.java)
                startActivity(intent)

                true
            }
            R.id.Group -> {
                val intent = Intent(this@Message, GroupActivity::class.java)
                startActivity(intent)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }


}