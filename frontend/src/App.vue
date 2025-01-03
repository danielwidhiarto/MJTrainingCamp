<template>
  <div id="app">
    <router-view class="form-container"></router-view>

    <!-- Floating WhatsApp Icon -->
    <a
      href="https://wa.me/+6287777442022"
      class="whatsapp-icon"
      target="_blank"
      rel="noopener noreferrer"
    >
      <img
        src="https://upload.wikimedia.org/wikipedia/commons/6/6b/WhatsApp.svg"
        alt="WhatsApp"
      />
    </a>
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style>
/* Set the background image for the entire app */
#app {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
}

/* WhatsApp Icon Styling */
.whatsapp-icon {
  position: fixed; /* Keep it fixed to the viewport */
  bottom: 20px; /* Distance from the bottom of the screen */
  right: 20px; /* Distance from the right of the screen */
  width: 60px; /* Icon size */
  height: 60px;
  background-color: #25d366; /* WhatsApp green background */
  border-radius: 50%; /* Circular icon */
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* Subtle shadow for depth */
  z-index: 1000; /* Ensure it stays on top */
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.whatsapp-icon img {
  width: 70%;
  height: 70%;
}

/* Hover effect for the WhatsApp icon */
.whatsapp-icon:hover {
  transform: scale(1.1); /* Slightly enlarge on hover */
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}
</style>
