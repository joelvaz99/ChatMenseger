package ipvc.estg.chatmenseger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import ipvc.estg.chatmenseger.ModelClasse.User

class GroupActivity : AppCompatActivity() {
    private lateinit var mAdapter: GroupAdapter<ViewHolder>

    private lateinit var refGroup: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val a="GROUP"
        supportActionBar?.title = a


        //RecyclerView
        val list_contact = findViewById<RecyclerView>(R.id.list_view)
        mAdapter = GroupAdapter<ViewHolder>()
        list_contact.adapter = mAdapter
        list_contact.layoutManager= LinearLayoutManager(this)
        fetchGroup()
        mAdapter.clear()
        mAdapter.setOnItemClickListener { item, view ->
            val intent = Intent(this@GroupActivity, ChatGroup::class.java)
            val groupItem = item as groupItem
           // Toast.makeText(this, "Nome${groupItem.group.groupTitle}", Toast.LENGTH_SHORT).show()

            intent.putExtra(GROUP_KEY, groupItem.group)
            startActivity(intent)
        }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu2, menu)
        return true
    }

    // Logout
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.Create_Group -> {
                val intent = Intent(this@GroupActivity, CreatGroupActivity::class.java)
                startActivity(intent)

                true
            }



            else -> super.onOptionsItemSelected(item)
        }


    }


    companion object {
        val GROUP_KEY = "group_key"
    }
}