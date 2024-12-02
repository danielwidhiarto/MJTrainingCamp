<template>
  <div class="container mt-5">
    <div class="text-center" style="color: #ff4500">
      <img src="../assets/logo.jpg" alt="Logo" class="mb-4" />
      <h1 class="h3 mb-3 font-weight-normal">Sign Up</h1>
    </div>
    <form @submit.prevent="registerUser" ref="form" novalidate>
      <!-- Full Name Field -->
      <div class="mb-3">
        <label for="full_name" class="form-label">Full Name</label>
        <input
          type="text"
          class="form-control form-input"
          v-model="name"
          id="full_name"
          placeholder="Enter your full name"
          required
          @input="sanitizeNameDebounced"
          aria-required="true"
          aria-describedby="nameHelp"
        />
        <small id="nameHelp" class="form-text text-muted">
          Please enter your legal full name.
        </small>
        <span v-if="errors.name" class="text-danger">{{ errors.name }}</span>
      </div>

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
        <label for="password" class="form-label"> Password </label>
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
          Must be at least 8 characters and include letters and numbers.
        </small>
        <span v-if="errors.password" class="text-danger">{{
          errors.password
        }}</span>
      </div>

      <!-- Confirm Password Field -->
      <div class="mb-3 position-relative">
        <label for="confirmPassword" class="form-label">
          Confirm Password
        </label>
        <input
          type="password"
          class="form-control form-input"
          v-model="confirmPassword"
          id="confirmPassword"
          placeholder="Confirm your password"
          required
          @input="checkPasswordMatchDebounced"
          aria-required="true"
        />
        <span v-if="passwordMismatch" class="text-danger" aria-live="assertive">
          Passwords do not match.
        </span>
        <span v-if="errors.confirmPassword" class="text-danger">{{
          errors.confirmPassword
        }}</span>
      </div>

      <!-- Phone Number Field -->
      <div class="mb-3">
        <label for="phoneNumber" class="form-label">Phone Number</label>
        <input
          type="tel"
          class="form-control form-input"
          v-model="pNumber"
          id="phoneNumber"
          placeholder="Enter your phone number"
          required
          pattern="\d+"
          title="Please enter digits only"
          @input="validatePhoneNumberDebounced"
          aria-required="true"
          aria-describedby="phoneHelp"
        />
        <small id="phoneHelp" class="form-text text-muted">
          Enter your phone number without any dashes or spaces.
        </small>
        <span v-if="phoneError" class="text-danger">{{ phoneError }}</span>
      </div>

      <!-- Submit Button -->
      <button type="submit" class="btn register-button" :disabled="isLoading">
        <span
          v-if="isLoading"
          class="spinner-border spinner-border-sm"
          role="status"
          aria-hidden="true"
        ></span>
        {{ isLoading ? 'Registering...' : 'Register' }}
      </button>
    </form>
    <div class="text-center mt-3">
      <p class="login-text">
        Already have an account?
        <router-link to="/" class="login-link">Sign in here!</router-link>
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
  name: 'Register',
  data() {
    return {
      name: '',
      email: '',
      password: '',
      confirmPassword: '',
      pNumber: '',
      phoneError: '',
      passwordMismatch: false,
      isLoading: false,
      errors: {}, // Object to hold validation error messages
    }
  },
  computed: {
    hasErrors() {
      return (
        Object.keys(this.errors).length > 0 ||
        this.passwordMismatch ||
        this.phoneError
      )
    },
  },
  methods: {
    // Sanitize inputs using DOMPurify
    sanitizeInput(input) {
      return DOMPurify.sanitize(input)
    },
    sanitizeName() {
      this.name = this.sanitizeInput(this.name)
      if (!this.name.trim()) {
        this.errors.name = 'Full Name is required.'
      } else {
        delete this.errors.name
      }
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
    sanitizePhoneNumber() {
      this.pNumber = this.sanitizeInput(this.pNumber)
      this.validatePhoneNumber()
    },

    // Validate email format
    validateEmailFormat(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return emailRegex.test(email)
    },

    // Validate phone number
    validatePhoneNumber() {
      const phoneRegex = /^\d+$/
      if (!this.pNumber.trim()) {
        this.phoneError = 'Phone number is required.'
      } else if (!phoneRegex.test(this.pNumber)) {
        this.phoneError = 'Phone number must contain digits only.'
      } else {
        this.phoneError = ''
      }
    },
    // Debounced version to optimize performance
    validatePhoneNumberDebounced: debounce(function () {
      this.validatePhoneNumber()
    }, 300),

    // Check if passwords match
    checkPasswordMatch() {
      this.passwordMismatch = this.password !== this.confirmPassword
      if (this.passwordMismatch) {
        this.errors.confirmPassword = 'Passwords do not match.'
      } else {
        delete this.errors.confirmPassword
      }
    },
    // Debounced version
    checkPasswordMatchDebounced: debounce(function () {
      this.checkPasswordMatch()
    }, 300),

    // Handle password input (validation)
    handlePasswordInput() {
      if (!this.password.trim()) {
        this.errors.password = 'Password is required.'
      } else if (this.password.length < 8) {
        this.errors.password = 'Password must be at least 8 characters long.'
      } else if (!/[A-Za-z]/.test(this.password) || !/\d/.test(this.password)) {
        this.errors.password = 'Password must include both letters and numbers.'
      } else {
        delete this.errors.password
      }

      // Re-check password match when password changes
      this.checkPasswordMatch()
    },
    // Debounced version
    handlePasswordInputDebounced: debounce(function () {
      this.handlePasswordInput()
    }, 300),

    // Register user
    async registerUser() {
      // Final validation before submission
      this.sanitizeName()
      this.sanitizeEmail()
      this.sanitizePhoneNumber()
      this.handlePasswordInput()
      this.checkPasswordMatch()

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
          'http://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/auth/register',
          {
            name: this.name,
            email: this.email,
            password: this.password, // Passwords typically aren't sanitized to preserve characters
            pNumber: this.pNumber,
          },
        )
        Swal.fire({
          title: 'Registration Successful!',
          text: 'You have successfully registered.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          this.$router.push('/') // Redirect to login page after success
        })
      } catch (error) {
        const errorMessage =
          error.response?.data?.message ||
          'An error occurred while registering. Please try again later.'

        Swal.fire({
          title:
            errorMessage === 'Email is already in use'
              ? 'Email Already Exists'
              : 'Registration Failed',
          text: errorMessage,
          icon: 'error',
          confirmButtonText: 'OK',
        })
      } finally {
        this.isLoading = false
      }
    },
  },
  mounted() {
    // Initialize Bootstrap tooltips if any (though tooltips are removed)
    // Since tooltips are removed, this section can be omitted or kept for future use
    // const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    // tooltipTriggerList.map(function (tooltipTriggerEl) {
    //   return new bootstrap.Tooltip(tooltipTriggerEl)
    // })
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

.register-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  width: 100%;
  font-size: 1.1rem;
  transition: background-color 0.3s ease;
}

.register-button:hover {
  background-color: #e03b00;
}

.login-text {
  color: #555;
}

.login-link {
  color: #ff4500;
  font-weight: bold;
  text-decoration: none;
}

.login-link:hover {
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
