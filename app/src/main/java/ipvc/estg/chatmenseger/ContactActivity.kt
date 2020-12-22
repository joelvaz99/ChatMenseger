package ipvc.estg.chatmenseger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipvc.estg.chatmenseger.ModelClasse.User


class ContactActivity : AppCompatActivity() {

    private lateinit var mAdapter: GroupAdapter<ViewHolder>
    private lateinit var refUsers: DatabaseReference
    private var username: String? = null
    //private val adapter: GroupAdapter<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        val a="CONTACTS"
        supportActionBar?.title = a


        //val mUser = intent.extras?.getParcelable<Contact>(Message.USER_KEY1)!!
       // Toast.makeText(this, "Nome: ${mUser.uuid}", Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, "oi", Toast.LENGTH_SHORT).show()


        //RecyclerView
        val list_contact = findViewById<RecyclerView>(R.id.list_view)
        mAdapter = GroupAdapter<ViewHolder>()
        list_contact.adapter = mAdapter
        list_contact.layoutManager=LinearLayoutManager(this)

        mAdapter.setOnItemClickListener { item, view ->
            val intent = Intent(this@ContactActivity, Chat::class.java)
            val userItem = item as UserItem
            //Toast.makeText(this, "Nome${userItem.name}", Toast.LENGTH_SHORT).show()

            intent.putExtra(USER_KEY, userItem.user)
            startActivity(intent)
        }


        //fetchUsers()
        fetchUser()



    }



    private class UserItem internal constructor(internal val user: User) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            Log.d("Teste", position.toString() + "")
            val txtUsername = viewHolder.itemView.findViewById<TextView>(R.id.txt_username)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.img_photo)


            txtUsername.setText(user.name)
            Picasso.get()
                    .load(user.url)
                    .into(imgPhoto)
        }

        override fun getLayout(): Int {
            return R.layout.user_recycler
        }
    }


    private fun fetchUsers() {

        FirebaseFirestore.getInstance().collection("/users/")
        .addSnapshotListener { snapshot, exception -> exception?.let {
            Log.d("Teste", it.message.toString())
            return@addSnapshotListener
            }
            snapshot?.let {
            for (doc in snapshot) {

                val user = doc.toObject(User::class.java)
                //Listar todos menos o utilizador logado
               if(FirebaseAuth.getInstance().uid != user.uid){
                   mAdapter.add(UserItem(user))
               }

            }
        }

        }

    }

    private fun fetchUser(){

        var id= FirebaseAuth.getInstance().currentUser!!.uid
        refUsers = FirebaseDatabase.getInstance().reference.child("users")

        refUsers.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children)
                {
                    val user: User? = snapshot.getValue(User::class.java)
                    mAdapter.add(UserItem(user!!))

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    companion object {
        val USER_KEY = "user_key"
    }
}