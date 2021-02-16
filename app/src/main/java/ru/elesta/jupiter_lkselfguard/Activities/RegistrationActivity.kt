package ru.elesta.jupiter_lkselfguard.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.imageResource
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.httpManager.LKHttpManager
import java.lang.Thread.sleep

class RegistrationActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    lateinit var registrationLoginTextField: TextView
    lateinit var registrationPasswordTextField: TextView
    lateinit var registrationMailTextField: TextView
    lateinit var registrationPhoneTextField: TextView
    lateinit var personalDataTextView: TextView
    lateinit var userAgreementTextView: TextView
    lateinit var promptView: TextView
    lateinit var eyeImageView: ImageView

    lateinit var submitRegistrationButton: Button

    lateinit var checkboxPersonalData: CheckBox
    lateinit var checkboxUserAgreement: CheckBox

    lateinit var myHandler: Handler
    lateinit var myRunnable: Runnable

    var passwordVisibility = false

    companion object{
        var registrationOk = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val shared = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val darkTheme = shared.getBoolean("dark", true)
        if(darkTheme){
            setTheme(R.style.DarkAppTheme)
        }
        else{
            setTheme(R.style.LightAppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        registrationLoginTextField = findViewById(R.id.registrationLoginTextField)
        registrationPasswordTextField = findViewById(R.id.registrationPasswordTextField)
        registrationMailTextField = findViewById(R.id.registrationMailTextField)
        registrationPhoneTextField = findViewById(R.id.registrationPhoneTextField)
        personalDataTextView = findViewById(R.id.personalDataTextView)
        userAgreementTextView = findViewById(R.id.userAgreementTextView)
        promptView = findViewById(R.id.promptView)
        promptView.visibility = View.GONE
        submitRegistrationButton = findViewById(R.id.submitRegistrationButton)
        registrationPasswordTextField.setOnFocusChangeListener(this)

        checkboxPersonalData = findViewById(R.id.checkboxPersonalData)
        checkboxUserAgreement = findViewById(R.id.checkboxUserAgreement)

        submitRegistrationButton.setOnClickListener(this)

        var firstText = "Я подтверждаю своё согласие на обработку "
        var secondText = SpannableString("персональных данных")
        secondText.setSpan(ForegroundColorSpan(Color.parseColor("#3366BB")), 0, secondText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        personalDataTextView.append(firstText)
        personalDataTextView.append(secondText)
        personalDataTextView.setOnClickListener {
            val url = "http://docs.jupiter8.ru/personal_data_agreement.pdf"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        firstText = "Я подтверждаю свое согласие с условиями "
        secondText = SpannableString("Пользовательского соглашения")
        secondText.setSpan(ForegroundColorSpan(Color.parseColor("#3366BB")), 0, secondText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        userAgreementTextView.append(firstText)
        userAgreementTextView.append(secondText)
        userAgreementTextView.setOnClickListener{
            val url = "http://docs.jupiter8.ru/user_agreement.pdf"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        eyeImageView = findViewById(R.id.eyeImageView)
        if(darkTheme){
            eyeImageView.setImageResource(R.drawable.ic_visibilityon)
        }
        else{
            eyeImageView.setImageResource(R.drawable.ic_visibilityon_black)
        }
        eyeImageView.setOnClickListener{
            if(!passwordVisibility){
                if(darkTheme){
                    eyeImageView.setImageResource(R.drawable.ic_visibilityoff)
                }
                else{
                    eyeImageView.setImageResource(R.drawable.ic_visibilityoff_black)
                }
                passwordVisibility = true
            }
            else{
                if(darkTheme){
                    eyeImageView.setImageResource(R.drawable.ic_visibilityon)
                }
                else{
                    eyeImageView.setImageResource(R.drawable.ic_visibilityon_black)
                }
                passwordVisibility = false
            }
        }
        myHandler = Handler()
        myRunnable = Runnable {
            if(!passwordVisibility){
                registrationPasswordTextField.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
            else {
                registrationPasswordTextField.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            }
            myHandler.postDelayed(myRunnable,100)
        }
        myHandler.post(myRunnable)
    }

    @ExperimentalStdlibApi
    override fun onClick(v: View?) {
        if(!checkboxPersonalData.isChecked || !checkboxUserAgreement.isChecked){
            runOnUiThread {
                Toast.makeText(this, "Подтвердите свое согласие на\nобработку персональных данных\nи с условиями пользовательского соглашения", Toast.LENGTH_SHORT).show()
            }
            return
        }
        if(registrationLoginTextField.text.isEmpty() || registrationPasswordTextField.text.isEmpty() || registrationMailTextField.text.isEmpty() || registrationPhoneTextField.text.isEmpty()){
            runOnUiThread {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val encodedPassword = encodePassword(registrationPasswordTextField.text.toString())

        val json = JSONObject().put("login", registrationLoginTextField.text.toString())
            .put("password", encodedPassword)
            .put("email", registrationMailTextField.text.toString())
            .put("phone", registrationPhoneTextField.text.toString())
            .put("role", "selfGuard").toString()

        LKHttpManager(json, this@RegistrationActivity).registration()
        sleep(1500)
        if (registrationOk) {
            runOnUiThread {
                Toast.makeText(this@RegistrationActivity, "Регистрация завершена успешно", Toast.LENGTH_LONG).show()
            }
            startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
        }

    }

    @ExperimentalStdlibApi
    fun encodePassword(password: String):String{
        val data = password.encodeToByteArray()
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if(hasFocus){
            promptView.visibility = View.VISIBLE
        }
        else{
            promptView.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myHandler.removeCallbacks(myRunnable)
    }
}
