package ipvc.estg.chatmenseger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ir_registro = findViewById<TextView>(R.id.ir_login)
        val login = findViewById<Button>(R.id.btn_criar)
        val a="LOGIN"
        supportActionBar?.title = a


        ir_registro.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {

            signIn()
        }
    }

    private fun signIn(){

        val edit_email = findViewById<TextView>(R.id.edit_email)
        val edit_password = findViewById<TextView>(R.id.edit_password)

        val email = edit_email.text.toString().trim()
        val password = edit_password.text.toString().trim()

        if( email.isEmpty() || password.isEmpty() ){
            Toast.makeText(this,"Senha ou email Vazio", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {

                if(it.isSuccessful){
                    val intent = Intent(this@LoginActivity, Message::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    Log.i("Teste","USERID e ${it.result?.user?.uid}")
                    // 321@gmail.com pass->1234567

                }
            }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                Log.e("Teste",it.message,it)
            }
    }
}