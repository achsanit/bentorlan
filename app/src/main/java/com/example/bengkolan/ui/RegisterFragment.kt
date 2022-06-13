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
        username = binding.etUsername.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        confirmpass = binding.etConfirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(username) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            ||(TextUtils.isEmpty(password)|| TextUtils.isEmpty(confirmpass))||password.length < 6||
                    binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()){
            if (TextUtils.isEmpty(username)){
                binding.wrapUsername.error = "Masukan Username Anda"
            } else{
                binding.wrapUsername.error = null
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                //cek fromat email
                binding.wrapEmail.error = "Format Email Tidak Valid"
            } else{
                //cek fromat email
                binding.wrapEmail.error = null
            }
            if (TextUtils.isEmpty(password)){
                //password kosong
                binding.wrapPassword.error = "Masukan Password Anda"
            } else {
                //password kosong
                binding.wrapPassword.error = null
            }
            if (TextUtils.isEmpty(confirmpass)){
                //password kosong
                binding.wrapConfirmPassword.error = "Masukan Konfirmasi Password Anda"
            } else {
                //password kosong
                binding.wrapConfirmPassword.error = null
            }
            if (password.length < 6){
                binding.wrapPassword.error = "Password Harus Sepanjang 6 Character"
            }else{
                binding.wrapPassword.error = null
            }
            if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
                binding.wrapPassword.error = "Password Tidak Sama"
                binding.wrapConfirmPassword.error = "Password Tidak Sama"
            }else{
                binding.wrapPassword.error = null
                binding.wrapConfirmPassword.error = null
            }

        }else{
            binding.wrapUsername.error = null
            binding.wrapEmail.error = null
            binding.wrapPassword.error = null
            binding.wrapConfirmPassword.error = null
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

//                findNavController().navigate(R.id.action_RegisterFragment_to_loginFragment)
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Sign Up Failed", Toast.LENGTH_SHORT).show()
            }
    }
}