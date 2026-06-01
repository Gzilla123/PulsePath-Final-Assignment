package com.pulsepath.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.pulsepath.R
import com.pulsepath.databinding.ActivityAuthBinding
import com.pulsepath.databinding.FragmentLoginBinding
import com.pulsepath.databinding.FragmentRegisterBinding
import com.pulsepath.ui.main.MainActivity



class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_container, LoginFragment())
            .commit()
    }


    fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    fun goToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }
}



class LoginFragment : Fragment() {

    private var _b: FragmentLoginBinding? = null
    private val b get() = _b!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentLoginBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        b.btnLogin.setOnClickListener {
            val email    = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            b.progressBar.visibility = View.VISIBLE
            b.btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // Signed in — go to dashboard
                    (requireActivity() as AuthActivity).goToMain()
                }
                .addOnFailureListener { e ->
                    b.progressBar.visibility = View.GONE
                    b.btnLogin.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        e.message ?: "Sign in failed. Check your email and password.",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }


        b.tvRegister.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.auth_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }


        b.tvForgotPassword.setOnClickListener {
            val email = b.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Type your email address in the field above, then tap Forgot password",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Reset link sent to $email. Check your inbox and click the link quickly — it expires in 1 hour.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        e.message ?: "Could not send reset email. Check the address is correct.",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}



class RegisterFragment : Fragment() {

    private var _b: FragmentRegisterBinding? = null
    private val b get() = _b!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentRegisterBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        b.btnRegister.setOnClickListener {
            val name     = b.etName.text.toString().trim()
            val email    = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString()
            val confirm  = b.etConfirmPassword.text.toString()


            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirm) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            b.progressBar.visibility = View.VISIBLE
            b.btnRegister.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    (requireActivity() as AuthActivity).goToOnboarding()
                }
                .addOnFailureListener { e ->
                    b.progressBar.visibility = View.GONE
                    b.btnRegister.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        e.message ?: "Registration failed. Try a different email address.",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }


        b.tvLogin.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
