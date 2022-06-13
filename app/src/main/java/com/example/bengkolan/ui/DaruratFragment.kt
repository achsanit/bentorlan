package com.example.bengkolan.ui

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bengkolan.R
import com.example.bengkolan.databinding.FragmentDaruratBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DaruratFragment : Fragment() {
    private var _binding: FragmentDaruratBinding?= null
    private val binding get() = _binding!!
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var result = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaruratBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        checkdata()
    }

    private fun checkdata(){
        val rg_tipe = binding.tipe as RadioGroup

        binding.btnPanggil.setOnClickListener {
            if(rg_tipe.checkedRadioButtonId != -1){
                if (binding.matic.isChecked)
                    result += "Matic"
                else if (binding.bebek.isChecked)
                    result += "Bebek"
                else if (binding.sport.isChecked)
                    result += "Sport"
                else if (binding.lain.isChecked)
                    result += "Lainnya"
            }else{
                result = ""
            }
            if (binding.etNoHP.text.toString().isEmpty()||binding.etAlamat.text.toString().isEmpty()||result == ""
                ||binding.etMerk.text.toString().isEmpty()||binding.etTipe.text.toString().isEmpty()){
                if (binding.etNoHP.text.toString().isEmpty()){
                    binding.wrapNoHP.error = "Mohon Masukan Nomor Handphone Anda"
                }else {
                    binding.wrapNoHP.error = null
                }
                if (binding.etAlamat.text.toString().isEmpty()){
                    //password kosong
                    binding.wrapAlamat.error = "Mohon Masukan Alamat Anda"
                } else {
                    binding.wrapAlamat.error = null
                }
                if(result == "") {
                    Toast.makeText(activity, "Masukan Jenis Motor Anda", Toast.LENGTH_SHORT).show()
                }else {
                    result = ""
                }

                if (binding.etMerk.text.toString().isEmpty()){
                    binding.wrapMerk.error = "Masukan Merk Motor Anda"
                } else{
                    binding.wrapMerk.error = null
                }
                if (binding.etTipe.text.toString().isEmpty()){
                    binding.wrapTipe?.error = "Masukan Merk Motor Anda"
                }
                else{
                    binding.wrapTipe?.error = null
                }

            }
            else{
                binding.wrapMerk.error = null
                binding.wrapAlamat.error = null
                binding.wrapNoHP.error = null
                binding.wrapTipe?.error = null
                sendData()
            }

        }
    }
    private fun sendData() {
        val noHp = binding.wrapNoHP.editText?.text.toString().trim()
        val alamat = binding.wrapAlamat.editText?.text.toString().trim()
        val jenis = result
        val merk = binding.wrapMerk.editText?.text.toString().trim()
        val tipe = binding.wrapTipe.editText?.text.toString().trim()
        val lain = binding.wrapAlam.editText?.text.toString().trim()

        val db = FirebaseFirestore.getInstance()
        val darurat: MutableMap<String, Any> = HashMap()
        darurat["noHP"] = noHp
        darurat["Alamat"] = alamat
        darurat["Jenis"] = jenis
        darurat["Merk"] = merk
        darurat["Tipe"] = tipe
        darurat["Lain"] = lain

        db.collection("darurat").document()
            .set(darurat)
            .addOnSuccessListener {
                Toast.makeText(activity, "record added succesfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_navigation_darurat_to_successFragment)
            }
            .addOnFailureListener{
                Toast.makeText(activity, "record Failed to add", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_navigation_darurat_to_failedFragment)
            }
    }
}
