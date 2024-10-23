<template>
  <div class="container mt-5">
    <div class="text-center" style="color: #ff4500">
      <img src="../assets/logo.jpg" alt="Logo" class="mb-4" />
      <h1 class="h3 mb-3 font-weight-normal">Sign In</h1>
    </div>
    <form @submit.prevent="loginUser">
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
      <div class="mb-3 text-end">
        <a href="#" class="forgot-password">Forgot Password?</a>
      </div>
      <button type="submit" class="btn login-button">Sign In</button>
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

        const { token, role, idUser } = response.data

        localStorage.setItem('token', token)
        localStorage.setItem('role', role)
        localStorage.setItem('idUser', idUser)

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

/* Responsive adjustments */
@media (max-width: 768px) {
  .container {
    max-width: 90%;
    padding: 30px;
  }
}
</style>
