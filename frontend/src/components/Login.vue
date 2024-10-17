<template>
  <div class="container mt-5">
    <!-- Use #F5F5F5 for background -->
    <div class="text-center" style="color: #e65100">
      <!-- Use #FFE5CF for text color -->
      <img src="../assets/logo.jpg" alt="Logo" class="mb-4" />
      <h1 class="h3 mb-3 font-weight-normal">Sign In</h1>
    </div>
    <form>
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
      <!-- Use #557C56 for button color -->
    </form>
    <div class="text-center mt-3" style="color: #557c56">
      <!-- Use #FFE5CF for text color -->
      <p>
        Don't have an account?
        <router-link to="/register" style="color: #e65100"
          >Sign up now!</router-link
        >
      </p>
      <!-- Use #557C56 for link color -->
    </div>
    <div class="blur-overlay"></div>
    <!-- Add a blur overlay -->
  </div>
</template>

<script>
export default {
  name: 'Login',
}
</script>

<style>
.container {
  max-width: 400px; /* Smaller container width */
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 10px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  margin: 0 auto; /* Center the container */
}

@media (max-width: 768px) {
  .container {
    max-width: 90%; /* Make container width responsive */
    padding: 15px;
  }
}

.bg-light {
  position: relative;
  overflow: hidden;
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
