package ru.elesta.jupiter_lkselfguard.helpers

import android.app.Activity
import ru.elesta.jupiter_lkselfguard.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ru.elesta.jupiter_lkselfguard.dataClasses.UserClass


class UserAdapter(context: Context, users: ArrayList<UserClass>, loginTextView: TextView, passwordTextView: TextView, app: AppCompatActivity) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private val users: ArrayList<UserClass>
    private val context: Context

    private val loginTextView: TextView
    private val passwordTextView: TextView
    private val app: AppCompatActivity

    init {
        this.app = app
        this.context = context
        this.users = users
        this.inflater = LayoutInflater.from(context)
        this.loginTextView = loginTextView
        this.passwordTextView = passwordTextView
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val userLogin: TextView

        init {
            userLogin = view.findViewById(R.id.userLogin)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.user_list, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user  = users.toList()
            val r = user.get(position)
            holder.userLogin.text = r.login
            holder.userLogin.setOnClickListener {
                loginTextView.text = r.login
                passwordTextView.text = r.password
                val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(app.getCurrentFocus()?.getWindowToken(), 0)
            }
    }
}