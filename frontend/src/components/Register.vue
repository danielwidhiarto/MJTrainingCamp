<template>
  <div class="container mt-5">
    <div class="bg-light p-4 rounded" style="background-color: #f5f5f5">
      <!-- Use #F5F5F5 for background -->
      <div class="text-center" style="color: #ffe5cf">
        <!-- Use #FFE5CF for text color -->
        <img src="../assets/logo.jpg" alt="Logo" class="mb-4" />
        <h1 class="h3 mb-3 font-weight-normal">Register</h1>
      </div>
      <form>
        <div class="mb-3">
          <label for="name" class="form-label">Name</label>
          <input
            type="text"
            class="form-control"
            id="name"
            placeholder="Enter your name"
            required
          />
        </div>
        <div class="mb-3">
          <label for="email" class="form-label">Email address</label>
          <input
            type="email"
            class="form-control"
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
            id="password"
            placeholder="Enter your password"
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
        <!-- Use #557C56 for button color -->
      </form>
      <div class="text-center mt-3" style="color: #ffe5cf">
        <!-- Use #FFE5CF for text color -->
        <p>
          Already have an account?
          <router-link to="/" style="color: #557c56">Login here!</router-link>
        </p>
        <!-- Use #557C56 for link color -->
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
}
</script>

<style>
.container {
  max-width: 400px; /* Set a max width for the form container */
}
</style>
