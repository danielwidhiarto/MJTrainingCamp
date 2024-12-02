<template>
  <div class="container mt-5">
    <div class="text-center" style="color: #ff4500">
      <img src="../assets/logo.jpg" alt="Logo" class="mb-4" />
      <h1 class="h3 mb-3 font-weight-normal">Sign In</h1>
    </div>
    <form @submit.prevent="loginUser" ref="form" novalidate>
      <!-- Email Address Field -->
      <div class="mb-3">
        <label for="email" class="form-label">Email address</label>
        <input
          type="email"
          class="form-control form-input"
          v-model="email"
          id="email"
          placeholder="Enter your email"
          required
          @input="sanitizeEmailDebounced"
          aria-required="true"
          aria-describedby="emailHelp"
        />
        <small id="emailHelp" class="form-text text-muted">
          We'll never share your email with anyone else.
        </small>
        <span v-if="errors.email" class="text-danger">{{ errors.email }}</span>
      </div>

      <!-- Password Field -->
      <div class="mb-3 position-relative">
        <label for="password" class="form-label">Password</label>
        <input
          type="password"
          class="form-control form-input"
          v-model="password"
          id="password"
          placeholder="Enter your password"
          required
          @input="handlePasswordInputDebounced"
          aria-required="true"
          aria-describedby="passwordHelp"
        />
        <small id="passwordHelp" class="form-text text-muted">
          Enter your account password.
        </small>
        <span v-if="errors.password" class="text-danger">{{
          errors.password
        }}</span>
      </div>

      <!-- Forgot Password Link -->
      <div class="mb-3 text-end">
        <a href="#" class="forgot-password">Forgot Password?</a>
      </div>

      <!-- Submit Button -->
      <button
        type="submit"
        class="btn login-button"
        :disabled="isLoading || hasErrors"
      >
        <span
          v-if="isLoading"
          class="spinner-border spinner-border-sm"
          role="status"
          aria-hidden="true"
        ></span>
        {{ isLoading ? 'Signing In...' : 'Sign In' }}
      </button>
    </form>
    <div class="text-center mt-3">
      <p class="signup-text">
        Don't have an account?
        <router-link to="/register" class="signup-link"
          >Sign up now!</router-link
        >
      </p>
    </div>
  </div>
</template>
<script>
import axios from 'axios'
import Swal from 'sweetalert2'
import DOMPurify from 'dompurify'
import debounce from 'lodash/debounce'

export default {
  name: 'Login',
  data() {
    return {
      email: '',
      password: '',
      isLoading: false,
      errors: {}, // Object to hold validation error messages
    }
  },
  computed: {
    hasErrors() {
      return Object.keys(this.errors).length > 0
    },
  },
  methods: {
    // Sanitize inputs using DOMPurify
    sanitizeInput(input) {
      return DOMPurify.sanitize(input)
    },
    sanitizeEmail() {
      this.email = this.sanitizeInput(this.email)
      if (!this.email.trim()) {
        this.errors.email = 'Email is required.'
      } else if (!this.validateEmailFormat(this.email)) {
        this.errors.email = 'Please enter a valid email address.'
      } else {
        delete this.errors.email
      }
    },
    // Debounced version to optimize performance
    sanitizeEmailDebounced: debounce(function () {
      this.sanitizeEmail()
    }, 300),

    // Validate email format
    validateEmailFormat(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return emailRegex.test(email)
    },

    // Handle password input (validation)
    handlePasswordInput() {
      if (!this.password.trim()) {
        this.errors.password = 'Password is required.'
      } else {
        delete this.errors.password
      }
    },
    // Debounced version
    handlePasswordInputDebounced: debounce(function () {
      this.handlePasswordInput()
    }, 300),

    // Register user

    async loginUser() {
      // Final validation before submission
      this.sanitizeEmail()
      this.handlePasswordInput()

      // Check for validation errors
      if (this.hasErrors) {
        Swal.fire({
          title: 'Invalid Input',
          text: 'Please correct the errors in the form before submitting.',
          icon: 'error',
          confirmButtonText: 'OK',
        })
        return
      }

      this.isLoading = true
      try {
        const response = await axios.post(
          'http://https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/auth/authenticate',
          {
            email: this.email,
            password: this.password,
          },
        )

        // Destructure the full response
        const { token, role, idUser, name, email, phone, registrationDate } =
          response.data

        // Store each piece of information in localStorage
        localStorage.setItem('token', token)
        localStorage.setItem('role', role)
        localStorage.setItem('idUser', idUser)
        localStorage.setItem('name', name)
        localStorage.setItem('email', email)
        localStorage.setItem('phone', phone)
        localStorage.setItem('registrationDate', registrationDate)

        Swal.fire({
          title: 'Login Successful!',
          text: 'You have successfully logged in.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          if (role === 'ROLE_ADMIN') {
            this.$router.push('/admin/dashboard')
          } else if (role === 'ROLE_TRAINER') {
            this.$router.push('/trainer-dashboard')
          } else {
            this.$router.push('/member/dashboard')
          }
        })
      } catch (error) {
        const errorMessage =
          error.response?.data?.message ||
          'An error occurred while logging in. Please try again later.'

        Swal.fire({
          title: 'Login Failed',
          text: errorMessage,
          icon: 'error',
          confirmButtonText: 'OK',
        })
      } finally {
        this.isLoading = false
      }
    },
  },
}
</script>
<style scoped>
.container {
  max-width: 600px;
  padding: 40px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
  margin: 0 auto;
}

.text-center h1 {
  font-weight: bold;
  color: #ff4500;
}

.form-input {
  border-radius: 8px;
  border: 1px solid #ddd;
  padding: 12px;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  border-color: #ff4500;
  box-shadow: 0px 0px 5px rgba(255, 69, 0, 0.5);
}

.forgot-password {
  color: #ff4500;
  text-decoration: none;
  font-size: 0.9rem;
}

.forgot-password:hover {
  text-decoration: underline;
}

.login-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  width: 100%;
  font-size: 1.1rem;
  transition: background-color 0.3s ease;
}

.login-button:hover {
  background-color: #e03b00;
}

.signup-text {
  color: #555;
}

.signup-link {
  color: #ff4500;
  font-weight: bold;
  text-decoration: none;
}

.signup-link:hover {
  text-decoration: underline;
}

/* Spinner customization */
.spinner-border {
  margin-right: 5px;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .container {
    max-width: 90%;
    padding: 30px;
  }
}
</style>
