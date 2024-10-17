<template>
  <div class="container mt-5">
    <div class="text-center" style="color: #e65100">
      <img src="../assets/logo.jpg" alt="Logo" class="mb-4" />
      <h1 class="h3 mb-3 font-weight-normal">Sign Up</h1>
    </div>
    <form @submit.prevent="registerUser">
      <div class="mb-3">
        <label for="name" class="form-label">Full Name</label>
        <input
          type="text"
          class="form-control"
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
          class="form-control"
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
          class="form-control"
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
          class="form-control"
          v-model="pNumber"
          id="phoneNumber"
          placeholder="Enter your phone number"
          required
        />
      </div>
      <button
        type="submit"
        class="btn"
        style="background-color: #557c56; color: #ffffff; width: 100%"
      >
        Register
      </button>
    </form>
    <div class="text-center mt-3" style="color: #557c56">
      <p>
        Already have an account?
        <router-link to="/" style="color: #e65100">Sign in here!</router-link>
      </p>
    </div>
    <div class="blur-overlay"></div>
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
        // Show success alert on successful registration
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

<style>
.container {
  max-width: 400px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 10px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  margin: 0 auto;
}

@media (max-width: 768px) {
  .container {
    max-width: 90%;
    padding: 15px;
  }
}

.blur-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  z-index: -1;
}
</style>
