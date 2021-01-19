package ipvc.estg.chatmenseger

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CreatGroupActivity : AppCompatActivity() {


    private var mSelectedUri: Uri? = null
    private lateinit var refGroup: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creat_group)

        val btn_register = findViewById<Button>(R.id.btn_criar)

        val btn_photo = findViewById<Button>(R.id.btn_image)
        supportActionBar?.title = "Creat Group"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        btn_register.setOnClickListener {
            saveGroupInFirebase()

        }
        btn_photo.setOnClickListener {
            selectPhoto()
        }

    }



    private fun saveGroupInFirebase() {


        val edit_name = findViewById<TextView>(R.id.edit_name)
        val edit_descricao = findViewById<TextView>(R.id.descricao)


        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")

        mSelectedUri?.let {
            ref.putFile(it)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        Log.i("Teste", it.toString())

                        val url = it.toString()
                        val name = edit_name.text.toString()
                        val descricao = edit_descricao.text.toString().trim()
                        val uid = FirebaseAuth.getInstance().uid!!

                        refGroup= FirebaseDatabase.getInstance().reference
                        val Groupkey = refGroup.push().key.toString()
                       // refGroup = FirebaseDatabase.getInstance().reference.child("users").child(uid)

                        val groupHashMap = HashMap<String, Any>()

                        groupHashMap["groupId"] = Groupkey
                        groupHashMap["groupTitle"] = name
                        groupHashMap["groupDescription"] = descricao
                        groupHashMap["createdBy"] = uid
                        groupHashMap["url"] = url

                        refGroup.child("group")
                            .child(Groupkey)
                            .setValue(groupHashMap)
                            .addOnCompleteListener {
                                if (it.isSuccessful){

                                    val participantHashMap = HashMap<String, Any>()

                                    participantHashMap["uid"] = uid
                                    participantHashMap["role"] = "Creator"

                                    //Participantes
                                    val refPart = FirebaseDatabase.getInstance().reference.child("group")

                                    refPart.child(Groupkey).child("participants").child(uid)
                                        .setValue(participantHashMap)
                                        .addOnCompleteListener {
                                            Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this@CreatGroupActivity, Message::class.java)
                                            startActivity(intent)

                                        }.addOnFailureListener {
                                            Log.e("teste", it.message, it)
                                        }


                                }
                            }.addOnFailureListener {
                                Log.e("teste", it.message, it)
                            }



                    }
                }
        }
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {

            var  imageView  = findViewById<ImageView>(R.id.imageView)
            val btn_image = findViewById<Button>(R.id.btn_image)

            mSelectedUri = data?.data
            Log.i("Teste", mSelectedUri.toString())

            val bitmap = MediaStore.Images.Media.getBitmap(
                contentResolver,
                mSelectedUri
            )
            imageView.setImageBitmap(bitmap)
            btn_image.alpha = 0f

        }
    }
}