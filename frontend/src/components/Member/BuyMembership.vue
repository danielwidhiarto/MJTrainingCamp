<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Choose Your Plan</h1>
      <p class="welcome-text">
        Get the best membership package tailored for you!
      </p>

      <!-- Membership Plans Section -->
      <div class="card-container">
        <div class="card" v-for="(plan, index) in membershipPlans" :key="index">
          <h2>Rp. {{ plan.price }}</h2>
          <h3>{{ plan.packageName }}</h3>
          <p>{{ plan.description }}</p>
          <button
            class="select-plan-button"
            @click="goToPayment(plan.idPackage)"
          >
            Select Plan
          </button>
        </div>
      </div>

      <!-- Visit Package Section -->
      <div class="visit-package">
        <h2>Buy a Visit Package</h2>
        <div class="card-container">
          <div
            class="visit-card"
            v-for="(visit, index) in visitPackages"
            :key="index"
          >
            <h2>Rp. {{ visit.price }}</h2>
            <h3>{{ visit.packageName }}</h3>
            <p>{{ visit.description }}</p>
            <button
              class="select-visit-button"
              @click="goToPayment(visit.idPackage)"
            >
              Buy Visit Package
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import { useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'

export default {
  name: 'BuyMembership',
  components: {
    Navbar,
  },
  setup() {
    const router = useRouter()
    const membershipPlans = ref([])
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
      router.push({ name: 'Payment', params: { idPackage } })
    }

    onMounted(fetchPackages)

    return {
      membershipPlans,
      visitPackages,
      goToPayment,
    }
  },
}
</script>

<style scoped>
/* General Layout */
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  text-align: center;
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  margin-top: 40px;
}

h1 {
  font-size: 3rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #000; /* Black text to match the logo's accent */
}

.welcome-text {
  font-size: 1.25rem;
  color: #555;
  margin-bottom: 40px;
}

/* Card Containers */
.card-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 20px;
}

/* Card Styling */
.card,
.visit-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  padding: 20px;
  flex: 1 1 calc(25% - 20px);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  text-align: center;
  transition:
    transform 0.4s,
    box-shadow 0.4s;
  border: 2px solid #ff4500; /* Orange-red border to match the logo color */
}

.card:hover,
.visit-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.15);
}

h2 {
  font-size: 2rem;
  color: #000; /* Black to match the logo */
}

h3 {
  font-size: 1.5rem;
  color: #ff4500; /* Orange-red to match the logo */
  margin-bottom: 15px;
}

p {
  font-size: 1rem;
  color: #555;
}

/* Buttons */
.select-plan-button,
.select-visit-button {
  background-color: #ff4500; /* Matching the logo’s red-orange */
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px 16px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s ease;
}

.select-plan-button:hover,
.select-visit-button:hover {
  background-color: #e03b00; /* Slightly darker shade on hover */
}

/* Visit Package Section */
.visit-package {
  margin-top: 50px;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .card,
  .visit-card {
    flex: 1 1 calc(50% - 20px);
  }

  h1 {
    font-size: 2.5rem;
  }
}

@media (max-width: 480px) {
  .card,
  .visit-card {
    flex: 1 1 100%;
  }

  h1 {
    font-size: 2rem;
  }
}
</style>
