<template>
  <div class="container mt-5">
    <div class="text-center" style="color: #ff4500">
      <img src="../assets/logo.jpg" alt="Logo" class="mb-4" />
      <h1 class="h3 mb-3 font-weight-normal">Sign Up</h1>
    </div>
    <form @submit.prevent="registerUser">
      <div class="mb-3">
        <label for="name" class="form-label">Full Name</label>
        <input
          type="text"
          class="form-control form-input"
          v-model="name"
          id="full_name"
          placeholder="Enter your full name"
          required
        />
      </div>
      <div class="mb-3">
        <label for="email" class="form-label">Email address</label>
        <input
          type="email"
          class="form-control form-input"
          v-model="email"
          id="email"
          placeholder="Enter your email"
          required
        />
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <input
          type="password"
          class="form-control form-input"
          v-model="password"
          id="password"
          placeholder="Enter your password"
          required
        />
      </div>
      <div class="mb-3">
        <label for="confirmPassword" class="form-label">Confirm Password</label>
        <input
          type="password"
          class="form-control form-input"
          v-model="confirmPassword"
          id="confirmPassword"
          placeholder="Confirm your password"
          required
          @input="checkPasswordMatch"
        />
        <small v-if="passwordMismatch" class="text-danger"
          >Passwords do not match.</small
        >
      </div>
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
          @input="validatePhoneNumber"
        />
        <small v-if="phoneError" class="text-danger">{{ phoneError }}</small>
      </div>
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
    }
  },
  methods: {
    validatePhoneNumber() {
      const phoneRegex = /^\d+$/
      if (!phoneRegex.test(this.pNumber)) {
        this.phoneError = 'Phone number must contain digits only.'
      } else {
        this.phoneError = ''
      }
    },
    checkPasswordMatch() {
      this.passwordMismatch = this.password !== this.confirmPassword
    },
    validatePassword() {
      const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/
      if (!passwordRegex.test(this.password)) {
        return 'Password must be at least 8 characters long and include both letters and numbers.'
      }
      return ''
    },
    async registerUser() {
      // Validate phone number
      this.validatePhoneNumber()
      if (this.phoneError) {
        Swal.fire({
          title: 'Invalid Phone Number',
          text: this.phoneError,
          icon: 'error',
          confirmButtonText: 'OK',
        })
        return
      }

      // Validate password strength
      const passwordError = this.validatePassword()
      if (passwordError) {
        Swal.fire({
          title: 'Weak Password',
          text: passwordError,
          icon: 'error',
          confirmButtonText: 'OK',
        })
        return
      }

      // Validate password match
      this.checkPasswordMatch()
      if (this.passwordMismatch) {
        Swal.fire({
          title: 'Password Mismatch',
          text: 'Passwords do not match.',
          icon: 'error',
          confirmButtonText: 'OK',
        })
        return
      }

      this.isLoading = true
      try {
        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/auth/register`,
          {
            name: this.name,
            email: this.email,
            password: this.password,
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
