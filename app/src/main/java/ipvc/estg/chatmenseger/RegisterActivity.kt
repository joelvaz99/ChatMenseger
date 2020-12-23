package ipvc.estg.chatmenseger

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ipvc.estg.chatmenseger.ModelClasse.User
import java.util.*


class RegisterActivity : AppCompatActivity() {
    private var mSelectedUri: Uri? = null
    private lateinit var refUsers: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val a="REGISTER"
        supportActionBar?.title = a

        val btn_register = findViewById<Button>(R.id.btn_criar)

        val btn_photo = findViewById<Button>(R.id.btn_image)

        val ir_login = findViewById<TextView>(R.id.ir_login)

        ir_login.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }


        btn_register.setOnClickListener {
            createUser()

        }
        btn_photo.setOnClickListener {
            selectPhoto()
        }

    }

    private fun createUser(){
        val edit_name = findViewById<TextView>(R.id.edit_name)
        val edit_email = findViewById<TextView>(R.id.edit_email)
        val edit_password = findViewById<TextView>(R.id.edit_password)

        val name = edit_name.text.toString().trim()
        val email = edit_email.text.toString().trim()
        val password = edit_password.text.toString().trim()

        if( email.isEmpty() || password.isEmpty() || name.isEmpty()){
          Toast.makeText(this, "Nome, Senha ou email Vazio", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    //Toast.makeText(this, "Entrou", Toast.LENGTH_SHORT).show()
                    if(it.isSuccessful){
                        Log.i("Teste", "USERID e ${it.result?.user?.uid}")
                        // saveUserInFirebase()
                        saveUserInFirebaseChat()
                     }
                }.addOnFailureListener {
                    Log.e("teste", it.message, it)
                }
    }

    private fun saveUserInFirebase() {
        val edit_name = findViewById<TextView>(R.id.edit_name)
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")


                mSelectedUri?.let {
                    ref.putFile(it)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            Log.i("Teste", it.toString())

                            val url = it.toString()
                            val name = edit_name.text.toString()
                            val uid = FirebaseAuth.getInstance().uid!!
                            val user = User(uid, name, url)

                    //adicionar a base de dados
                    FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener {

                }
                        .addOnFailureListener {
                            Log.e("Teste", it.message, it)
                        }
                }
            }
        }
    }

    //Chat
    private fun saveUserInFirebaseChat() {
        val edit_name = findViewById<TextView>(R.id.edit_name)
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${filename}")

        mSelectedUri?.let {
            ref.putFile(it)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            Log.i("Teste", it.toString())

                            val url = it.toString()
                            val name = edit_name.text.toString()
                            val uid = FirebaseAuth.getInstance().uid!!

                            refUsers = FirebaseDatabase.getInstance().reference.child("users").child(uid)

                            val userHashMap = HashMap<String, Any>()

                            userHashMap["uid"] = uid
                            userHashMap["name"] = name
                            userHashMap["url"] = url

                            Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show()


                            refUsers.updateChildren(userHashMap)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(this, "Sucesso", Toast.LENGTH_SHORT).show()

                                        }

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