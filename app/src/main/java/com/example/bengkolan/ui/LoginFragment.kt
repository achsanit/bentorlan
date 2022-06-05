package com.example.bengkolan.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bengkolan.MainActivity
import com.example.bengkolan.R
import com.example.bengkolan.databinding.FragmentLoginBinding
import com.example.bengkolan.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding?= null
    private val binding get() = _binding!!
    lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.btnMasuk.setOnClickListener {
            //sebelum login di validasi dlu datanya
            validateData()
        }

        binding.btnDaftar.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvForget.setOnClickListener {
            resetPassword()
        }
    }




    fun onBackPressed() {
        Toast.makeText(activity, "Back button disabled", Toast.LENGTH_SHORT)
    }

    private fun resetPassword() {
        email = binding.wrapEmail.editText?.text.toString().trim()
        password = binding.wrapPassword.editText?.text.toString().trim()

        //validasi data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.wrapEmail.error = "Invalid email format"
        }
        else if (TextUtils.isEmpty(password)){
            //pw blank
            binding.wrapPassword.error = "Please enter your password"
        }
        else{
            firebaseResetPass()
        }

    }

    private fun firebaseResetPass() {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(activity, "Email Sent !!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateData() {
        //get data
        email = binding.wrapEmail.editText?.text.toString().trim()
        password = binding.wrapPassword.editText?.text.toString().trim()

        //validasi data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.wrapEmail.error = "Invalid email format"
        }
        else if (TextUtils.isEmpty(password)){
            //pw blank
            binding.wrapPassword.error = "Please enter your password"
        }
        else{
            //data valid
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(Intent(activity, MainActivity::class.java))

            }
            .addOnFailureListener {
                Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser(){
        //if user telah login akan ke main activity
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            startActivity(Intent(activity, MainActivity::class.java))
        }
    }
}