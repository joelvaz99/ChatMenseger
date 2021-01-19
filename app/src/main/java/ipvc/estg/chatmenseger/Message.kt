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

    private lateinit var mAdapter: GroupAdapter<ViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        val a="MESSAGE"
        supportActionBar?.title = a

        //RecyclerView
        val list_contact = findViewById<RecyclerView>(R.id.list_view2)
        mAdapter = GroupAdapter<ViewHolder>()
        list_contact.adapter = mAdapter
        list_contact.layoutManager= LinearLayoutManager(this)
        mAdapter.clear()


/*
        var id= FirebaseAuth.getInstance().currentUser!!.uid
        val  ref = FirebaseDatabase.getInstance().reference.child("chatList").child(id)

        ref.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                mAdapter.clear()
                for (snapshot in p0.children)
                {
                    val user: ChatList? = snapshot.getValue(ChatList::class.java)

                    for (eachChatList in userschatList!!){
                       // mAdapter.add(ContactActivity.UserItem(user!!))

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

 */

        verifiyAuthetication()

       // fectLastMessage()





    }


    private fun verifiyAuthetication() {

        if (FirebaseAuth.getInstance().uid == null){
            val intent = Intent(this@Message, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
    }
/*
    private fun fectLastMessage() {

        var id= FirebaseAuth.getInstance().currentUser!!.uid
       val  refUsers = FirebaseDatabase.getInstance().reference.child("users")

        refUsers.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                mAdapter.clear()
                for (snapshot in p0.children)
                {
                    val user: User? = snapshot.getValue(User::class.java)

                        for (eachChatList in userschatList!!){
                            mAdapter.add(UserItem(user!!))

                        }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

 */


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