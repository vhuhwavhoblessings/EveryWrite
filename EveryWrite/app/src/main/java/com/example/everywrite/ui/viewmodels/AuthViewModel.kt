package com.example.everywrite.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.everywrite.data.User
import com.example.everywrite.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _signupError = MutableStateFlow<String?>(null)
    val signupError: StateFlow<String?> = _signupError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            val emailTaken = userRepository.isEmailTaken("demo@everywrite.com")
            if (!emailTaken) {
                userRepository.register(
                    User(
                        email = "demo@everywrite.com",
                        username = "demo",
                        password = "demo123"
                    )
                )
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginError.value = "Please fill in all fields"
            return
        }

        _isLoading.value = true
        _loginError.value = null

        viewModelScope.launch {
            try {
                val user = userRepository.login(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    _loginError.value = "Invalid email or password"
                }
            } catch (e: Exception) {
                _loginError.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(email: String, username: String, password: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            _signupError.value = "Please fill in all fields"
            return
        }

        if (password != confirmPassword) {
            _signupError.value = "Passwords don't match"
            return
        }

        if (password.length < 6) {
            _signupError.value = "Password must be at least 6 characters"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _signupError.value = "Please enter a valid email address"
            return
        }

        _isLoading.value = true
        _signupError.value = null

        viewModelScope.launch {
            try {
                val emailTaken = userRepository.isEmailTaken(email)
                val usernameTaken = userRepository.isUsernameTaken(username)

                when {
                    emailTaken -> _signupError.value = "Email is already registered"
                    usernameTaken -> _signupError.value = "Username is already taken"
                    else -> {
                        val user = User(
                            email = email,
                            username = username,
                            password = password
                        )
                        val success = userRepository.register(user)
                        if (success) {
                            _currentUser.value = user
                            _isLoggedIn.value = true
                            onSuccess()
                        } else {
                            _signupError.value = "Registration failed"
                        }
                    }
                }
            } catch (e: Exception) {
                _signupError.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
    }

    fun clearErrors() {
        _loginError.value = null
        _signupError.value = null
    }
}