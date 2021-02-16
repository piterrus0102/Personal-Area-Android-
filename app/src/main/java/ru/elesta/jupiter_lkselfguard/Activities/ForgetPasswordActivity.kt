package ru.elesta.jupiter_lkselfguard.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.httpManager.LKHttpManager

class ForgetPasswordActivity : AppCompatActivity(), View.OnClickListener {


    lateinit var forgetPasswordLoginTextField: TextView
    lateinit var forgetPasswordEmailTextField: TextView
    lateinit var forgetButtonAction: Button


    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable

    companion object {
        var rememberPasswordOk = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        forgetPasswordLoginTextField = findViewById(R.id.forgetPasswordLoginTextField)
        forgetPasswordEmailTextField = findViewById(R.id.forgetPasswordEmailTextField)
        forgetButtonAction = findViewById(R.id.forgetButtonAction)
        forgetButtonAction.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        if(forgetPasswordLoginTextField.text.isEmpty() && forgetPasswordEmailTextField.text.isEmpty()){
            runOnUiThread {
                Toast.makeText(this, "Заполните хотя бы одно поле для восстановления пароля", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val json = JSONObject().put("login", forgetPasswordLoginTextField.text.toString())
            .put("email", forgetPasswordEmailTextField.text.toString()).toString()
        myHandler = Handler()
        var send = false
        val startTime = System.currentTimeMillis()
        Log.e("FORGET", "начало")
        myRunnable = Runnable {
            val currentTime = System.currentTimeMillis()
            if(!send) {
                LKHttpManager(json, this@ForgetPasswordActivity).forgetPassword()
            }
            if (rememberPasswordOk) {
                rememberPasswordOk = false
                myHandler.removeCallbacks(myRunnable)
                runOnUiThread {
                    Toast.makeText(this@ForgetPasswordActivity, "Письмо с паролем отправлено вам на почту,\nуказанную при регистрации", Toast.LENGTH_LONG).show()
                }
                startActivity(Intent(this@ForgetPasswordActivity, MainActivity::class.java))
            }
            send = true
            if ((currentTime - startTime) / 1000 < 3) {
                myHandler.postDelayed(myRunnable, 100)
            } else {
                myHandler.removeCallbacks(myRunnable)
                Log.e("FORGET", "прошло больше 3 секунд")
            }
        }
        myHandler.post(myRunnable)
    }
}