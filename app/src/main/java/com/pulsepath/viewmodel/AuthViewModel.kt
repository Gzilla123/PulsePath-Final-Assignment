package com.pulsepath.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()
    private val auth = FirebaseAuth.getInstance()

    fun isLoggedIn() = auth.currentUser != null
}
