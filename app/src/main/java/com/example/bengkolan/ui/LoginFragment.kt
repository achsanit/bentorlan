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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding?= null
    private val binding get() = _binding!!
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
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
        db = Firebase.firestore

        binding.btnMasuk.setOnClickListener {
            //sebelum login di validasi dlu datanya
            binding.progressLogin.visibility = View.VISIBLE
            validateData()
        }

        binding.btnDaftar.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment2_to_registerFragment)
        }

        binding.tvForget.setOnClickListener {
            resetPassword()
        }

        binding.btnMasukGoogle.setOnClickListener {
            authGoogle()
        }
    }

    fun onBackPressed() {
        Toast.makeText(activity, "Back button disabled", Toast.LENGTH_SHORT)
    }

    private fun authGoogle() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        val signInClient = GoogleSignIn.getClient(requireActivity(), options)
        signInClient.signInIntent.also {
            startActivityForResult(it, REQUEST_CODE_SIGN_IN)
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                    val account = GoogleSignIn.getLastSignedInAccount(context)
                    if (account != null) {
                        val personName = account.displayName
                        val personEmail =account.email

                        val currentUser = firebaseAuth.currentUser

                        if (currentUser!=null) {
                            val data = hashMapOf(
                                "username" to personName,
                                "email" to personEmail
                            )

                            db.collection("users")
                                .document(firebaseAuth.currentUser!!.uid)
                                .set(data)
                        }

                    }

                    startActivity(Intent(activity, MainActivity::class.java))
                    Toast.makeText(requireContext(), "Successfully logged in", Toast.LENGTH_LONG).show()
                }
            } catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
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
                binding.progressLogin.visibility = View.GONE
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
            .addOnFailureListener {
                binding.progressLogin.visibility = View.GONE
                Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val REQUEST_CODE_SIGN_IN = 999
    }
}