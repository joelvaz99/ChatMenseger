package ipvc.estg.chatmenseger

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.BlockedNumberContract.unblock
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipvc.estg.chatmenseger.ModelClasse.Contact
import ipvc.estg.chatmenseger.ModelClasse.Message1
import ipvc.estg.chatmenseger.ModelClasse.User
import ipvc.estg.chatmenseger.ModelClasse.userbloqueado
import android.content.Context as Context1

class Chat : AppCompatActivity() {
1
    private lateinit var mAdapter: GroupAdapter<ViewHolder>
     private lateinit var mUser: User
        private var idbloq: String? = null
    private lateinit var refUsers: DatabaseReference

    private var mMe: User? = null 


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val btn_enviar_msg = findViewById<Button>(R.id.btn_chat)

        btn_enviar_msg.setOnClickListener {
            sendMessageDatabase1()
            mAdapter.clear()

        }



        val reference = FirebaseDatabase.getInstance().reference
                .child("users").child(FirebaseAuth.getInstance().uid.toString())

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                mAdapter.clear()
                val user: User? = p0.getValue(User::class.java)
                mMe = user
                fetchMessagesDatabase()


            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })




        //Receber nome

        mUser = intent.extras?.getParcelable<User>(ContactActivity.USER_KEY)!!
        Toast.makeText(this, "Nome${mUser.name}", Toast.LENGTH_SHORT).show()


       // Log.i("Teste", "username ${mUser?.name}")

        supportActionBar?.title = mUser?.name

        //RecyclerView
        val list_contact = findViewById<RecyclerView>(R.id.chat_recycler)
        mAdapter = GroupAdapter()
        list_contact.adapter = mAdapter
        list_contact.layoutManager= LinearLayoutManager(this)
        mAdapter.clear()


    }

    private fun fetchMessagesDatabase() {
        mMe?.let {
            val fromId = FirebaseAuth.getInstance().currentUser!!.uid.toString()
            val toId = mUser.uid.toString()

            refUsers = FirebaseDatabase.getInstance().reference.child("chats")

            refUsers.addValueEventListener(object : ValueEventListener
            {

                override fun onDataChange(p0: DataSnapshot) {

                    mAdapter.clear()
                    for (snapshot in p0.children)
                    {
                        val message: Message1? = snapshot.getValue(Message1::class.java)

                       if(message!!.receiver.equals(fromId) && message.sender.equals(toId) || message.receiver.equals(toId) && message.sender.equals(fromId)){
                           mAdapter.add(MessageItem(message!!))

                       }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        }
    }


    private fun sendMessageDatabase1() {
        val editChat = findViewById<TextView>(R.id.edit_chat)
        val text = editChat.text.toString()

        editChat.text = null
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = mUser.uid
        val timestamp = System.currentTimeMillis().toString()

        // val message = Message1(text = text, timestamp = timestamp, toId = toId, fromId = fromId)
       val message=Message1(text,timestamp,fromId,toId)
        //Message1(text,timestamp,toId,fromId)

        if(!message.message.isEmpty()){


            refUsers= FirebaseDatabase.getInstance().reference
            val messagekey = refUsers.push().key.toString()

            val messageHashMap = HashMap<String,Any?>()
            messageHashMap["sender"] = fromId
            messageHashMap["message"] = text
            messageHashMap["receiver"] = toId
            messageHashMap["messageId"] = messagekey

            refUsers.child("chats")
                .child(messagekey.toString())
                .setValue(messageHashMap)
                .addOnCompleteListener{
                    if (it.isSuccessful)
                    {

                        val chatListReference = FirebaseDatabase.getInstance()
                            .reference
                            .child("chatList")
                            .child(fromId)
                            .child(toId)

                        chatListReference.addListenerForSingleValueEvent(object : ValueEventListener
                        {
                            override fun onDataChange(p0: DataSnapshot) {


                                if (!p0.exists())
                                {
                                    chatListReference.child("id").setValue(toId)
                                }

                                val chatListReceiveRef = FirebaseDatabase.getInstance()
                                    .reference
                                    .child("chatList")
                                    .child(toId)
                                    .child(fromId)

                                chatListReceiveRef.child("id").setValue(fromId)


                            }

                            override fun onCancelled(p0: DatabaseError) {
                            }

                        } )       }
                }
        }


    }


    private inner class MessageItem (private val mMessage: Message1) : Item<ViewHolder>() {

        override fun getLayout(): Int {

            return if (mMessage.sender == FirebaseAuth.getInstance().uid){
                R.layout.item_from_message
            } else{
                R.layout.item_to_message
            }


        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val txt_msg_from = viewHolder.itemView.findViewById<TextView>(R.id.txt_msg_from)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.img_msg_from)

            if (mMessage.sender == FirebaseAuth.getInstance().uid) {
                txt_msg_from.text = mMessage.message
                Picasso.get().load(mMe?.url).into(imgPhoto)
            } else {
                val txt_msg = viewHolder.itemView.findViewById<TextView>(R.id.txt_msg)
                val imgPhoto1 = viewHolder.itemView.findViewById<ImageView>(R.id.img_msg)
                txt_msg.text = mMessage.message
                Picasso.get().load(mUser.url).into(imgPhoto1)

            }


        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_user, menu)
        return true
    }

    // Logout
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Block -> {
                mAdapter.clear()
                block()
                mAdapter.clear()
                true
            }

            R.id.UnBlock -> {
                mAdapter.clear()
                unBlock()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }

    private fun unBlock() {
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("users")

        ref.child(id).child("blockusers").orderByChild("uid").equalTo(mUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        mAdapter.clear()
                        for (snapshot in p0.children){
                            if (snapshot.exists()){
                                snapshot.ref.removeValue()
                                        .addOnSuccessListener {

                                           // Toast.makeText(this, "sucesso", Toast.LENGTH_SHORT).show()

                                        }.addOnFailureListener {

                                        }

                            }
                        }

                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

    }

    private fun block() {
        mAdapter.clear()
        val blockHashMap = HashMap<String, Any>()
        val id = FirebaseAuth.getInstance().currentUser!!.uid

        blockHashMap["uid"] = mUser.uid

        val ref = FirebaseDatabase.getInstance().reference.child("users")
        ref.child(id).child("blockusers").child(mUser.uid).setValue(blockHashMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "sucesso", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {

                }
    }



}