<template>
  <div class="container mt-5">
    <div class="text-center" style="color: #e65100">
      <img src="../assets/logo.jpg" alt="Logo" class="mb-4" />
      <h1 class="h3 mb-3 font-weight-normal">Sign In</h1>
    </div>
    <form @submit.prevent="loginUser">
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
        <a href="#" class="text-muted">Forgot Password?</a>
      </div>
      <button
        type="submit"
        class="btn"
        style="background-color: #557c56; color: #ffffff; width: 100%"
      >
        Sign In
      </button>
    </form>
    <div class="text-center mt-3" style="color: #557c56">
      <p>
        Don't have an account?
        <router-link to="/register" style="color: #e65100"
          >Sign up now!</router-link
        >
      </p>
    </div>
    <div class="blur-overlay"></div>
  </div>
</template>

<script>
import axios from 'axios'
import Swal from 'sweetalert2'

export default {
  name: 'Login',
  data() {
    return {
      email: '',
      password: '',
    }
  },
  methods: {
    async loginUser() {
      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/auth/authenticate',
          {
            email: this.email,
            password: this.password,
          },
        )

        // Assuming the response contains token, role, and userId
        const { token, role, idUser } = response.data

        // Store token in localStorage (or use Vuex for state management)
        localStorage.setItem('token', token)
        localStorage.setItem('role', role)
        localStorage.setItem('idUser', idUser)

        // Show success alert
        Swal.fire({
          title: 'Login Successful!',
          text: 'You have successfully logged in.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          // Redirect based on role
          if (role === 'ROLE_ADMIN') {
            this.$router.push('/admin-dashboard') // Redirect to admin dashboard
          } else if (role === 'ROLE_TRAINER') {
            this.$router.push('/trainer-dashboard') // Redirect to trainer dashboard
          } else {
            this.$router.push('/member-dashboard') // Redirect to user home
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
</style>
