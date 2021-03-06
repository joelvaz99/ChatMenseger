package ipvc.estg.chatmenseger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipvc.estg.chatmenseger.ModelClasse.Contact

class Message : AppCompatActivity() {
    private lateinit var mAdapter: GroupAdapter<ViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        val a="MESSAGE"
        supportActionBar?.title = a

        //Recycler
        val list_messages = findViewById<RecyclerView>(R.id.list_messages)
        mAdapter = GroupAdapter()
        list_messages.adapter = mAdapter
        list_messages.layoutManager= LinearLayoutManager(this)
        /*
        mAdapter.setOnItemClickListener { item, view ->
              val intent = Intent(this@Message, Contacts::class.java)
            val contactIem = item as ContactItem
            //Toast.makeText(this, "Nome${userItem.name}", Toast.LENGTH_SHORT).show()

            intent.putExtra(USER_KEY1, contactIem.mContact)
            startActivity(intent)
        }

         */

        verifiyAuthetication()

        fectLastMessage()




    }



    private fun verifiyAuthetication() {

        if (FirebaseAuth.getInstance().uid == null){
            val intent = Intent(this@Message, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
    }

    private fun fectLastMessage() {
        val uid =FirebaseAuth.getInstance().uid.toString()

        FirebaseFirestore.getInstance().collection("/last-messages")
                .document(uid)
                .collection("contacts")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    val changes = querySnapshot?.documentChanges
                    changes?.let {
                        for (doc in it) {
                            when (doc.type) {
                                DocumentChange.Type.ADDED -> {
                                    val contact = doc.document.toObject(Contact::class.java)
                                    // Toast.makeText(this, "Nome: ${contact.username}", Toast.LENGTH_SHORT).show()

                                    mAdapter.add(ContactItem(contact))
                                }
                            }
                        }
                    }
                }


    }


    private inner class ContactItem(internal val mContact:Contact):Item<ViewHolder>(){
        override fun bind(viewHolder: ViewHolder, position: Int) {

            val username = viewHolder.itemView.findViewById<TextView>(R.id.txt_username)
            val txt_last_msg = viewHolder.itemView.findViewById<TextView>(R.id.txt_last_message)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.img_photo)

            username.text= mContact.username
            txt_last_msg.text= mContact.lastMessage

            Picasso.get()
                    .load(mContact.photoUrl)
                    .into(imgPhoto)

        }

        override fun getLayout(): Int {
            return R.layout.item_user_message
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

    companion object {
        val USER_KEY1 = "user_key"
    }


}