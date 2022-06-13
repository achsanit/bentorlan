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
import com.example.bengkolan.R

import com.example.bengkolan.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding?= null
    private val binding get() = _binding!!
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var email = ""
    private var password = ""
    private var username = ""
    private var confirmpass = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        binding.btnDaftar.setOnClickListener {
            //validate data
            validateData()
        }
    }


    private fun validateData() {
        username = binding.wrapPassword.editText?.text.toString().trim()
        email = binding.wrapEmail.editText?.text.toString().trim()
        password = binding.wrapPassword.editText?.text.toString().trim()
        confirmpass = binding.wrapConfirmPassword.editText?.text.toString().trim()

        if (TextUtils.isEmpty(username)){
            binding.wrapUsername.error = "Please enter your username"
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //cek fromat email
            binding.wrapEmail.error = "Invalid email format"
        }
        else if (TextUtils.isEmpty(password)){
            //password kosong
            binding.wrapPassword.error = "Please enter your password"
        }
        else if (TextUtils.isEmpty(confirmpass)){
            //password kosong
            binding.wrapConfirmPassword.error = "Please enter your confirm password"
        }
        else if (password.length < 6){
            binding.wrapPassword.error = "Password must atleast 6 character long"
        }
        else if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString())
            Toast.makeText(activity, "your password does not match", Toast.LENGTH_SHORT).show()
        else{
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val data = hashMapOf(
                    "username" to binding.etUsername.text.toString(),
                    "email" to binding.etEmail.text.toString()
                )

                db.collection("users")
                    .document(firebaseAuth.currentUser!!.uid)
                    .set(data)

                findNavController().navigateUp()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Sign Up Failed", Toast.LENGTH_SHORT).show()
            }
    }
}