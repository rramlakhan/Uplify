package com.example.uplify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uplify.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        
        binding.ivBackArrowSignUp.setOnClickListener {
            val intent = Intent(applicationContext, WelcomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUpSignUp.setOnClickListener {
            val fullName = binding.etTextFullNameSignUp.text.toString()
            val email = binding.etTextEmailAddressSignUp.text.toString()
            val password = binding.etTextPasswordSignUp.text.toString()
            val confirmPassword = binding.etTextConfirmPasswordSignUp.text.toString()

            if (fullName.isEmpty() && email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()) {
                binding.etTextFullNameSignUp.error = "Full Name is required"
                binding.etTextEmailAddressSignUp.error = "Email Address is required"
                binding.etTextPasswordSignUp.error = "Password is required"
                binding.etTextConfirmPasswordSignUp.error = "Confirm Password is required"
            }
            if (fullName.isEmpty()) binding.etTextFullNameSignUp.error = "Full Name is required"
            if (email.isEmpty()) binding.etTextEmailAddressSignUp.error = "Email Address is required"
            if (password.isEmpty()) binding.etTextPasswordSignUp.error = "Password is required"
            if (confirmPassword.isEmpty()) binding.etTextConfirmPasswordSignUp.error = "Confirm Password is required"

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    signUpUser(fullName, email, password)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Password didn't match",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun signUpUser(fullName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    binding.progressBarSignUp.visibility = View.VISIBLE
                    val user = User(fullName, email)
                    databaseReference.child(firebaseAuth.currentUser?.uid.toString()).setValue(user)

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        task.exception?.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}