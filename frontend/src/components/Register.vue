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
        <label for="phoneNumber" class="form-label">Phone Number</label>
        <input
          type="text"
          class="form-control form-input"
          v-model="pNumber"
          id="phoneNumber"
          placeholder="Enter your phone number"
          required
        />
      </div>
      <button type="submit" class="btn register-button">Register</button>
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
      pNumber: '',
    }
  },
  methods: {
    async registerUser() {
      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/auth/register',
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

/* Responsive adjustments */
@media (max-width: 768px) {
  .container {
    max-width: 90%;
    padding: 30px;
  }
}
</style>
