package com.example.daniel.proyectomoviles.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import com.example.daniel.proyectomoviles.R
import com.example.daniel.proyectomoviles.SignUpActivity
import com.example.daniel.proyectomoviles.http.HttpRequest
import com.example.daniel.proyectomoviles.interfaces.OnNextArrowClickedListener
import kotlinx.android.synthetic.main.fragment_login_fragment.*


class LoginFragment : Fragment() {

    lateinit var mListener: OnNextArrowClickedListener
    lateinit var username: String
   // lateinit var txtView_login_feedback: TextView
    lateinit var imm: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgBtn_frag_login_next.setOnClickListener {
            imm.hideSoftInputFromWindow(editText_login_username.windowToken, 0)
            username = editText_login_username.text.toString()
            HttpRequest.userVerify(username, { error, datos ->
                if(error) {
                    textView_frag_login_feed.text = resources.getString(R.string.sign_up_error_feedback)
                }else {
                    textView_frag_login_feed.text = ""
                    mListener.onNextClicked(username)
                }
            })

        }

        btn_login_sign_up.setOnClickListener{
            launchSignUpActivity()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try{
            mListener = context as OnNextArrowClickedListener
        }catch(e: ClassCastException){
            throw ClassCastException(context.toString() + "must implement OnNextArrowClickedListener")
        }
    }

    fun launchSignUpActivity(){
        val intent = Intent(this.context, SignUpActivity::class.java)
        startActivity(intent)
    }




}
