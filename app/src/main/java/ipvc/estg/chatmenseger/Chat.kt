package ipvc.estg.chatmenseger

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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

class Chat : AppCompatActivity() {

    private lateinit var mAdapter: GroupAdapter<ViewHolder>
     private lateinit var mUser: User
        private var idbloq: String? = null

    private var mMe: User? = null k


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val btn_enviar_msg = findViewById<Button>(R.id.btn_chat)

        btn_enviar_msg.setOnClickListener {
             sendMessage()
        }

        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().uid.toString())
                .get()
                .addOnSuccessListener {
                    mMe = it.toObject(User::class.java)
                    fetchMessages()
                }
        bloquear()


        //Receber nome

        mUser = intent.extras?.getParcelable<User>(ContactActivity.USER_KEY)!!

        Log.i("Teste", "username ${mUser?.name}")

        supportActionBar?.title = mUser?.name

        //RecyclerView
        val list_contact = findViewById<RecyclerView>(R.id.chat_recycler)
        mAdapter = GroupAdapter()
        list_contact.adapter = mAdapter
        list_contact.layoutManager= LinearLayoutManager(this)

    }
    private fun bloquear() {
        val uid =FirebaseAuth.getInstance().uid.toString()

        FirebaseFirestore.getInstance().collection("/bloquear")
                .document(uid)
                .collection("bloquear-user")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    val changes = querySnapshot?.documentChanges
                    changes?.let {
                        for (doc in it) {
                            when (doc.type) {
                                DocumentChange.Type.ADDED -> {
                                    val userblq = doc.document.toObject(userbloqueado::class.java)
                                    Toast.makeText(this, "Nome: ${userblq.name}", Toast.LENGTH_SHORT).show()
                                    idbloq=userblq.uid
                                    //  mAdapter.add(ContactItem(contact))
                                }
                            }
                        }
                    }
                }


    }

    private fun fetchMessages() {
        mMe?.let {
            val fromId = it.uid.toString()
            val toId = mUser.uid.toString()
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        querySnapshot?.documentChanges?.let {
                            for (doc in it) {
                                when(doc.type) {
                                    DocumentChange.Type.ADDED -> {
                                        val message = doc.document.toObject(Message1::class.java)
                                       // Toast.makeText(this, "Nome: ${message.text}", Toast.LENGTH_SHORT).show()

                                        mAdapter.add(MessageItem(message))
                                    }
                                }
                            }
                        }
                    }
        }
    }



    private fun sendMessage() {
        val editChat = findViewById<TextView>(R.id.edit_chat)
        val text = editChat.text.toString()

       editChat.text = null
        val fromId = FirebaseAuth.getInstance().uid.toString()
        val toId = mUser.uid
        val timestamp = System.currentTimeMillis()

       // val message = Message1(text = text, timestamp = timestamp, toId = toId, fromId = fromId)
        val message=Message1(text,timestamp,fromId,toId)
                //Message1(text,timestamp,toId,fromId)

        if(!message.text.isEmpty()){



            //Adicionar para o usuario que vai enviar a mensagem
            FirebaseFirestore.getInstance().collection("/conversations")
                .document(fromId)
                .collection(toId)
                .add(message)
                .addOnSuccessListener {
                    Log.i("Teste",it.id)

                    val contact = Contact(toId,mUser.name,text,mUser.url,message.timestamp)

                    FirebaseFirestore.getInstance().collection("/last-messages")
                            .document(fromId)
                            .collection("contacts")
                            .document(toId)
                            .set(contact)
                }
                .addOnFailureListener {
                    Log.e("Teste", it.message!!)
                }

            if(idbloq == null){
                FirebaseFirestore.getInstance().collection("/conversations")
                        .document(toId)
                        .collection(fromId)
                        .add(message)
                        .addOnSuccessListener {
                            val contact = Contact(toId,mUser.name,text,mUser.url,message.timestamp)

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(toId)
                                    .collection("contacts")
                                    .document(fromId)
                                    .set(contact)
                        }
                        .addOnFailureListener {
                            Log.e("Teste", it.message!!)
                        }


            }






        }


    }

    private inner class MessageItem (private val mMessage: Message1) : Item<ViewHolder>() {

        override fun getLayout(): Int {

            return if (mMessage.fromId == FirebaseAuth.getInstance().uid){
                R.layout.item_from_message
            } else{
                R.layout.item_to_message
            }


        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val txt_msg_from = viewHolder.itemView.findViewById<TextView>(R.id.txt_msg_from)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.img_msg_from)

            if (mMessage.fromId == FirebaseAuth.getInstance().uid) {
                txt_msg_from.text = mMessage.text
                Picasso.get().load(mMe?.url).into(imgPhoto)
            } else {
                val txt_msg = viewHolder.itemView.findViewById<TextView>(R.id.txt_msg)
                val imgPhoto1 = viewHolder.itemView.findViewById<ImageView>(R.id.img_msg)
                txt_msg.text = mMessage.text
                Picasso.get().load(mUser.url).into(imgPhoto1)

            }


        }








    }
}