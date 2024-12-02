<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Choose Your Plan</h1>
      <p class="welcome-text">
        Get the best membership package tailored for you!
      </p>

      <!-- Loading Spinner -->
      <div v-if="isLoading" class="loading-spinner">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- Membership Plans Section -->
      <div v-else class="card-container">
        <PackageCard
          v-for="plan in membershipPlans"
          :key="plan.idPackage"
          :packageItem="plan"
          buttonType="membership"
          @select="goToPayment"
        />
      </div>

      <!-- Visit Package Section -->
      <div class="visit-package">
        <h2>Buy a Visit Package</h2>
        <div v-if="!isLoading" class="card-container">
          <PackageCard
            v-for="visit in visitPackages"
            :key="visit.idPackage"
            :packageItem="visit"
            buttonType="visit"
            @select="goToPayment"
          />
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Navbar from './Navbar.vue'
import PackageCard from '../PackageCard.vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import { useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'

export default {
  name: 'BuyMembership',
  components: {
    Navbar,
    PackageCard,
  },
  setup() {
    const router = useRouter()
    const membershipPlans = ref([])
    const visitPackages = ref([])
    const isLoading = ref(false)

    // Fetch packages from the API and categorize them
    const fetchPackages = async () => {
      isLoading.value = true
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          Swal.fire(
            'Error',
            'Authentication token not found. Please log in.',
            'error',
          ).then(() => {
            router.push({ name: 'Login' })
          })
          return
        }

        const response = await axios.get(
          'https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/package/get',
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        )
        const packages = response.data

        // Separate membership and visit packages based on their type
        membershipPlans.value = packages.filter(
          packageItem => packageItem.type === 'Membership',
        )
        visitPackages.value = packages.filter(
          packageItem => packageItem.type === 'Visit',
        )
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch packages.', 'error')
      } finally {
        isLoading.value = false
      }
    }

    // Navigate to Payment page with selected package ID
    const goToPayment = idPackage => {
      Swal.fire({
        title: 'Confirm Selection',
        text: 'Do you want to proceed to payment for this package?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Yes, proceed',
        cancelButtonText: 'Cancel',
      }).then(result => {
        if (result.isConfirmed) {
          router.push({ name: 'Payment', params: { idPackage } })
        }
      })
    }

    onMounted(fetchPackages)

    return {
      membershipPlans,
      visitPackages,
      goToPayment,
      isLoading,
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
  justify-content: center; /* Center the cards */
  gap: 20px;
}

/* Visit Package Section */
.visit-package {
  margin-top: 50px;
}

.visit-package h2 {
  font-size: 2rem;
  color: #000;
  margin-bottom: 20px;
}

/* Loading Spinner */
.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px; /* Adjust as needed */
}

.spinner-border {
  width: 3rem;
  height: 3rem;
  color: #ff4500;
}

/* Responsive Styles */
@media (max-width: 992px) {
  .package-card {
    flex: 1 1 calc(33.333% - 20px);
  }
}

@media (max-width: 768px) {
  h1 {
    font-size: 2.5rem;
  }

  .visit-package h2 {
    font-size: 1.75rem;
  }

  .package-card {
    flex: 1 1 calc(50% - 20px);
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 2rem;
  }

  .visit-package h2 {
    font-size: 1.5rem;
  }

  .package-card {
    flex: 1 1 100%;
  }
}
</style>
