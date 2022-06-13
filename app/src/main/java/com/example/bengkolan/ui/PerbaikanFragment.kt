package com.example.bengkolan.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bengkolan.R
import com.example.bengkolan.databinding.FragmentDaruratBinding
import com.example.bengkolan.databinding.FragmentPerbaikanBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class PerbaikanFragment : Fragment() {
    private var _binding: FragmentPerbaikanBinding?= null
    private val binding get() = _binding!!
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var cal = Calendar.getInstance()
    var result = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerbaikanBinding.inflate(inflater, container, false)
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
        binding.tvTanggal.setOnClickListener{
            datePickerDialog()
        }
        binding.tvWaktu.setOnClickListener{
            timePickerDialog()
        }

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
                    binding.wrapTipe.error = "Masukan Merk Motor Anda"
                } else{
                    binding.wrapTipe.error = null
                }
                if (binding.etPerbaikan.text.toString().isEmpty()){
                    binding.wrapPerbaikan?.error = "Masukan Perbaikan ANda"
                } else{
                    binding.wrapPerbaikan?.error = null
                }
                if(binding.tvTanggal.text.toString() == "") {
                    Toast.makeText(activity, "Masukan Tanggal Perbaikan", Toast.LENGTH_SHORT).show()
                }else {
                    result = true.toString()
                }
                if(binding.tvWaktu.text.toString() == "") {
                    Toast.makeText(activity, "Masukan Waaktu Perbaikan", Toast.LENGTH_SHORT).show()
                }else {
                    result = true.toString()
                }

            }
            else{
                binding.wrapMerk.error = null
                binding.wrapAlamat.error = null
                binding.wrapNoHP.error = null
                binding.wrapTipe.error = null
                binding.wrapPerbaikan.error = null
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
        val perbaikan = binding.wrapPerbaikan.editText?.text.toString().trim()
        val tanggal = binding.tvTanggal.text.toString().trim()
        val jam = binding.tvWaktu.text.toString().trim()

        val db = FirebaseFirestore.getInstance()
        val darurat: MutableMap<String, Any> = HashMap()
        darurat["noHP"] = noHp
        darurat["Alamat"] = alamat
        darurat["Jenis"] = jenis
        darurat["Merk"] = merk
        darurat["Tipe"] = tipe
        darurat["Perbaikan"] = perbaikan
        darurat["Tanggal"] = tanggal
        darurat["Jam"] = jam

        db.collection("perbaikan rutin").document()
            .set(darurat)
            .addOnSuccessListener {
                Toast.makeText(activity, "record added succesfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_navigation_perbaikan_to_successFragment)
            }
            .addOnFailureListener{
                Toast.makeText(activity, "record Failed to add", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_navigation_perbaikan_to_failedFragment)
            }
    }

    private fun datePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.tvTanggal.text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(cal.time).toString()
        }
        DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(
            Calendar.MONTH), cal.get(Calendar.MONTH)).show()
    }
    private fun timePickerDialog() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            binding.tvWaktu.text = SimpleDateFormat("HH.mm", Locale.US).format(cal.time).toString()
        }
        TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
            Calendar.MINUTE), true).show()
    }
}