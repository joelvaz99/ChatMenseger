package ipvc.estg.chatmenseger

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Register : AppCompatActivity() {
    private var mSelectedUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btn_register = findViewById<Button>(R.id.btn_register)

        val btn_photo = findViewById<Button>(R.id.btn_image)




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
          Toast.makeText(this,"Nome, Senha ou email Vazio",Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    Toast.makeText(this,"Entrou",Toast.LENGTH_SHORT).show()
                    if(it.isSuccessful){
                        Log.i("Teste","USERID e ${it.result?.user?.uid}")
                        saveUserInFirebase()
                     }
                }.addOnFailureListener {
                    Log.e("teste",it.message,it)
                }
    }

    private fun saveUserInFirebase() {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance()
                .getReference("/images/${filename}")
        mSelectedUri?.let {
            ref.putFile(it)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.i("Teste", it.toString())
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
                    mSelectedUri)
            imageView.setImageBitmap(bitmap)
            btn_image.alpha = 0f

        }
    }



}