package ipvc.estg.chatmenseger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipvc.estg.chatmenseger.ModelClasse.Group
import ipvc.estg.chatmenseger.ModelClasse.MessageGroup
import ipvc.estg.chatmenseger.ModelClasse.User
import java.util.*
import kotlin.collections.HashMap

class ChatGroup : AppCompatActivity() {

    private lateinit var mAdapter: GroupAdapter<ViewHolder>
    private lateinit var mGroup: Group
    private lateinit var mUser: Group
    private lateinit var mMe: User
    private var idbloq: String? = null
    private lateinit var refGroup: DatabaseReference

    //private var mMe: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group)

        //RecyclerView
        val list_contact = findViewById<RecyclerView>(R.id.chat_recycler)
        mAdapter = GroupAdapter()
        list_contact.adapter = mAdapter
        list_contact.layoutManager= LinearLayoutManager(this)
        mAdapter.clear()

        //Receber nome

        mGroup = intent.extras?.getParcelable<Group>(GroupActivity.GROUP_KEY)!!
        Toast.makeText(this, "Nome${mGroup.groupTitle}", Toast.LENGTH_SHORT).show()

        val btn_enviar_msg = findViewById<Button>(R.id.btn_chat)

        btn_enviar_msg.setOnClickListener {
            sendMessageDatabase1()
            mAdapter.clear()


        }

        val reference = FirebaseDatabase.getInstance().reference
                .child("users").child(FirebaseAuth.getInstance().uid.toString())

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                val user: User? = p0.getValue(User::class.java)
                mMe = user!!
                mAdapter.clear()
                fetchMessagesDatabase()

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    private fun fetchMessagesDatabase() {
        mMe?.let {
          val regroup = FirebaseDatabase.getInstance().reference.child("group")
                  .child(mGroup.groupId)
                  .child("message")

            regroup.addValueEventListener(object : ValueEventListener
            {

                override fun onDataChange(p0: DataSnapshot) {

                    mAdapter.clear()
                    for (snapshot in p0.children)
                    {
                        val message: MessageGroup? = snapshot.getValue(MessageGroup::class.java)
                        mAdapter.add(MessageItem(message))


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
        val sender = FirebaseAuth.getInstance().uid.toString()
        val timestamp = System.currentTimeMillis().toString()
        refGroup= FirebaseDatabase.getInstance().reference
        val messagekey = refGroup.push().key.toString()


        // val message = Message1(text = text, timestamp = timestamp, toId = toId, fromId = fromId)
        val message= MessageGroup(text,messagekey,sender,timestamp)
        //Message1(text,timestamp,toId,fromId)

        if(!message.message.isEmpty()) {


            val messageHashMap = HashMap<String, Any?>()
            messageHashMap["message"] = text
            messageHashMap["messageId"] = messagekey
            messageHashMap["sender"] = sender
            messageHashMap["timeStamp"] = timestamp


            refGroup.child("group").child(mGroup.groupId)
                .child("message")
                .child(messagekey.toString())
                .setValue(messageHashMap)
                .addOnSuccessListener {

                  //  Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {

                }
        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_group, menu)
        return true
    }

    // Logout
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Create_Group -> {
               val intent = Intent(this@ChatGroup, Add_User_Group::class.java)
                intent.putExtra(GROUP_KEY1,mGroup)
                startActivity(intent)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }



    private inner class MessageItem(private val mMessage: MessageGroup?) : Item<ViewHolder>() {

        override fun getLayout(): Int {

            return if (mMessage!!.sender == FirebaseAuth.getInstance().uid){
                R.layout.item_from_message
            } else{
                R.layout.item_to_message
            }


        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val txt_msg_from = viewHolder.itemView.findViewById<TextView>(R.id.txt_msg_from)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.img_msg_from)

            if (mMessage!!.sender == FirebaseAuth.getInstance().uid) {
                txt_msg_from.text = mMessage.message
                Picasso.get().load(mMe?.url).into(imgPhoto)
            } else {

               val ref = FirebaseDatabase.getInstance().reference.child("users")

                ref.addValueEventListener(object : ValueEventListener
                {
                    override fun onDataChange(p0: DataSnapshot) {
                        for (snapshot in p0.children)
                        {
                            val user: User? = snapshot.getValue(User::class.java)
                            if (user!!.uid == mMessage.sender){
                                val txt_msg = viewHolder.itemView.findViewById<TextView>(R.id.txt_msg)
                                val imgPhoto1 = viewHolder.itemView.findViewById<ImageView>(R.id.img_msg)
                                txt_msg.text = mMessage.message
                                Picasso.get().load(user.url).into(imgPhoto1)
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            }
        }
    }

    companion object {
        val GROUP_KEY1 = "group_key"
    }


}