<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Buy Membership</h1>
      <p>Welcome to your Buy Membership page!</p>

      <!-- Membership Plans Section -->
      <div class="card-container">
        <div class="card" v-for="(plan, index) in membershipPlans" :key="index">
          <h2>{{ plan.price }}</h2>
          <h3>{{ plan.packageName }}</h3>
          <p>{{ plan.description }}</p>
          <button
            class="select-plan-button"
            @click="goToPayment(plan.idPackage)"
          >
            Pilih Plan
          </button>
        </div>
      </div>

      <!-- Visit Package Section -->
      <div class="visit-package">
        <h2>Beli Paket Visit</h2>
        <div class="card-container">
          <div
            class="visit-card"
            v-for="(visit, index) in visitPackages"
            :key="index"
          >
            <h2>{{ visit.price }}</h2>
            <h3>{{ visit.packageName }}</h3>
            <p>{{ visit.description }}</p>
            <button
              class="select-visit-button"
              @click="goToPayment(visit.idPackage)"
            >
              Beli Paket Visit
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component
import axios from 'axios' // Import axios for HTTP requests
import Swal from 'sweetalert2' // Import SweetAlert
import { useRouter } from 'vue-router' // Import useRouter for navigation
import { onMounted, ref } from 'vue' // Import onMounted and ref

export default {
  name: 'BuyMembership',
  components: {
    Navbar,
  },
  setup() {
    const router = useRouter() // Access the router instance
    const membershipPlans = ref([]) // Use ref for reactivity
    const visitPackages = ref([])

    const fetchPackages = async () => {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(
          'http://localhost:8081/api/v1/package/get',
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        )
        const packages = response.data

        membershipPlans.value = packages.filter(
          packageItem => packageItem.type === 'Membership',
        )
        visitPackages.value = packages.filter(
          packageItem => packageItem.type === 'Visit',
        )
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch packages.', 'error')
      }
    }

    const goToPayment = idPackage => {
      router.push({ name: 'Payment', params: { idPackage } }) // Navigate to the payment page with the package ID
    }

    onMounted(fetchPackages) // Fetch packages when the component is mounted

    return {
      membershipPlans,
      visitPackages,
      goToPayment,
    }
  },
}
</script>

<style>
.container {
  padding: 20px;
  max-width: 1200px; /* Max width for larger screens */
  margin: auto; /* Center the container */
}

h1 {
  font-size: 2.5em;
  margin-bottom: 10px;
}

h2 {
  font-size: 1.5em;
  margin: 10px 0;
}

h3 {
  font-size: 1.2em;
  margin: 5px 0;
}

.card-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap; /* Allow cards to wrap in case of small screens */
  gap: 20px; /* Add space between cards */
}

.card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  flex: 1 1 calc(25% - 20px); /* 4 cards in a row with space */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  height: 300px; /* Fixed height for all cards */
  display: flex;
  flex-direction: column; /* Align content vertically */
  justify-content: space-between; /* Space out items */
  text-align: center; /* Center text */
  transition: transform 0.3s; /* Smooth hover effect */
}

.card:hover {
  transform: translateY(-5px); /* Lift effect on hover */
}

.visit-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  height: 300px; /* Fixed height for visit cards */
  display: flex;
  flex-direction: column; /* Align content vertically */
  justify-content: space-between; /* Space out items */
  flex: 1 1 calc(33% - 20px); /* 3 cards in a row with space */
  transition: transform 0.3s; /* Smooth hover effect */
}

.visit-card:hover {
  transform: translateY(-5px); /* Lift effect on hover */
}

.select-plan-button,
.select-visit-button {
  background-color: #557c56; /* Your button color */
  color: #ffe5cf; /* Your text color */
  border: none;
  border-radius: 5px;
  padding: 12px 20px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s; /* Smooth color change */
}

.select-plan-button:hover,
.select-visit-button:hover {
  background-color: #4a6b4b; /* Darker shade on hover */
}

.visit-package {
  margin-top: 40px;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .card {
    flex: 1 1 calc(50% - 20px); /* 2 cards in a row on small screens */
  }

  .visit-card {
    flex: 1 1 calc(50% - 20px); /* 2 visit cards in a row */
  }
}

@media (max-width: 480px) {
  .card {
    flex: 1 1 100%; /* 1 card in a row on extra small screens */
  }

  .visit-card {
    flex: 1 1 100%; /* 1 visit card in a row */
  }

  h1 {
    font-size: 2em; /* Adjust h1 size for smaller screens */
  }
}
</style>
