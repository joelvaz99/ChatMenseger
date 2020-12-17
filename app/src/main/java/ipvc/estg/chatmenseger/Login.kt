package ipvc.estg.chatmenseger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ir_registro = findViewById<TextView>(R.id.ir_registro)
        ir_registro.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)

        }
    }
}