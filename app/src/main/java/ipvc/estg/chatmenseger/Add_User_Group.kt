package ipvc.estg.chatmenseger

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipvc.estg.chatmenseger.ModelClasse.Group
import ipvc.estg.chatmenseger.ModelClasse.User

class Add_User_Group : AppCompatActivity() {

    private lateinit var mAdapter: GroupAdapter<ViewHolder>
    private lateinit var refUsers: DatabaseReference
    private lateinit var mGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add__user__group)

        //RecyclerView
        val list_contact = findViewById<RecyclerView>(R.id.list_view)
        mAdapter = GroupAdapter<ViewHolder>()
        list_contact.adapter = mAdapter
        list_contact.layoutManager= LinearLayoutManager(this)

        mGroup = intent.extras?.getParcelable<Group>(ChatGroup.GROUP_KEY1)!!


        mAdapter.setOnItemClickListener { item, view ->
            val userItem = item as UserItem
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Participant")
            builder.setMessage("Add this user in this group?")
            builder.setPositiveButton("ADD"){
                dialog, which ->dialog.dismiss()

                //Adicionar User
                val uid = userItem.user.uid

                val addParticipantHashMap = HashMap<String, Any>()

                addParticipantHashMap["role"] = "participant"
                addParticipantHashMap["uid"] = uid
                val refGroup =FirebaseDatabase.getInstance().reference.child("group")
                refGroup.child(mGroup.groupId)
                        .child("participants")
                        .child(userItem.user.uid)
                        .setValue(addParticipantHashMap)
                        .addOnSuccessListener {
                            val intent = Intent(this@Add_User_Group, GroupActivity::class.java)
                            startActivity(intent)


                        }.addOnFailureListener {
                            Log.e("teste", it.message, it)

                        }



            }
            builder.setNegativeButton("CANCEL"){
                dialog, which -> dialog.dismiss()
                val intent = Intent(this@Add_User_Group, GroupActivity::class.java)
                startActivity(intent)

            }

            val  dialog: AlertDialog = builder.create()
            dialog.show()


        }





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


    private fun fetchUser(){

        var id= FirebaseAuth.getInstance().currentUser!!.uid
        refUsers = FirebaseDatabase.getInstance().reference.child("users")

        refUsers.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children)
                {
                    val user: User? = snapshot.getValue(User::class.java)

                    if(user!!.uid != id){
                        mAdapter.add(UserItem(user!!))
                    }


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