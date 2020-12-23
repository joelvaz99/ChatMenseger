package ipvc.estg.chatmenseger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipvc.estg.chatmenseger.ModelClasse.Group
import ipvc.estg.chatmenseger.ModelClasse.User

class GroupActivity : AppCompatActivity() {
    private lateinit var mAdapter: GroupAdapter<ViewHolder>

    private lateinit var refGroup: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        //RecyclerView
        val list_contact = findViewById<RecyclerView>(R.id.list_view)
        mAdapter = GroupAdapter<ViewHolder>()
        list_contact.adapter = mAdapter
        list_contact.layoutManager= LinearLayoutManager(this)
        fetchGroup()
    }


    private fun fetchGroup(){

        var id= FirebaseAuth.getInstance().currentUser!!.uid
        refGroup = FirebaseDatabase.getInstance().reference.child("group")

        refGroup.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children)
                {
                    if(snapshot.child("participants").child(id).exists()){
                        val group: Group? = snapshot.getValue(Group::class.java)
                        mAdapter.add(GroupActivity.groupItem(group!!))

                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private class groupItem internal constructor(internal val group: Group) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            Log.d("Teste", position.toString() + "")
            val txtUsername = viewHolder.itemView.findViewById<TextView>(R.id.txt_username)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.img_photo)


            txtUsername.setText(group.groupTitle)
            Picasso.get()
                .load(group.url)
                .into(imgPhoto)
        }

        override fun getLayout(): Int {
            return R.layout.user_recycler
        }
    }
}