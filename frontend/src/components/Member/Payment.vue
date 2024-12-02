<template>
  <div>
    <LazyNavbar />
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-10 col-lg-8">
          <div class="card shadow-lg p-4 payment-card">
            <h1 class="text-center mb-4 page-title">Complete Your Payment</h1>

            <div v-if="packageDetails">
              <!-- Package Overview Section -->
              <section class="package-overview mb-4">
                <h2 class="section-title">Package Overview</h2>
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <div class="info-box">
                      <strong>Package Name:</strong>
                      <span>{{ packageDetails.packageName }}</span>
                    </div>
                  </div>
                  <div class="col-md-6 mb-3">
                    <div class="info-box">
                      <strong>Price:</strong>
                      <span>Rp {{ packageDetails.price }}</span>
                    </div>
                  </div>
                </div>

                <!-- Membership Specific Details -->
                <div
                  v-if="packageDetails.type === 'Membership'"
                  class="membership-details"
                >
                  <div class="row">
                    <div class="col-md-4 mb-3">
                      <div class="info-box">
                        <strong>Duration:</strong>
                        <span>{{ packageDetails.duration }} month(s)</span>
                      </div>
                    </div>
                    <div class="col-md-4 mb-3">
                      <div class="info-box">
                        <strong>Start Date:</strong>
                        <input
                          type="date"
                          id="startDate"
                          class="form-control"
                          v-model="startDate"
                          :min="minStartDate"
                          @change="updateEndDate"
                          required
                        />
                      </div>
                    </div>
                    <div class="col-md-4 mb-3">
                      <div class="info-box">
                        <strong>End Date:</strong>
                        <span>{{ endDate }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Visit Package Specific Details -->
                <div
                  v-if="packageDetails.type === 'Visit'"
                  class="visit-details"
                >
                  <div class="row">
                    <div class="col-md-6 mb-3">
                      <div class="info-box">
                        <strong>Number of Visits:</strong>
                        <span>{{ packageDetails.visitNumber }}</span>
                      </div>
                    </div>
                    <!-- Add other visit package-specific details here if needed -->
                  </div>
                </div>
              </section>
              <!-- End of Package Overview Section -->

              <hr />

              <!-- Payment Method Selection Section -->
              <section class="payment-method mb-4">
                <h2 class="section-title">Select Payment Method</h2>
                <div class="d-flex justify-content-around my-3">
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="paymentMethod"
                      id="qris"
                      value="QRIS"
                      v-model="selectedPaymentMethod"
                      aria-labelledby="qris-label"
                    />
                    <label class="form-check-label" for="qris" id="qris-label">
                      QRIS
                    </label>
                  </div>
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="paymentMethod"
                      id="bank"
                      value="Bank Transfer"
                      v-model="selectedPaymentMethod"
                      aria-labelledby="bank-label"
                    />
                    <label class="form-check-label" for="bank" id="bank-label">
                      Bank Transfer
                    </label>
                  </div>
                </div>

                <!-- QRIS Payment Details -->
                <div
                  v-if="selectedPaymentMethod === 'QRIS'"
                  class="payment-details"
                >
                  <h5>Scan QR Code to Pay</h5>
                  <img
                    src="../../assets/qris.png"
                    alt="QR Code"
                    class="img-fluid qris-img mt-3"
                    loading="lazy"
                  />
                </div>

                <!-- Bank Transfer Payment Details -->
                <div
                  v-if="selectedPaymentMethod === 'Bank Transfer'"
                  class="payment-details"
                >
                  <h5>Bank Transfer Details</h5>
                  <ul class="list-unstyled mt-3">
                    <li><strong>Bank:</strong> BCA</li>
                    <li><strong>Account Number:</strong> 0661749406</li>
                    <li><strong>Account Name:</strong> Your Company Name</li>
                  </ul>
                </div>
              </section>
              <!-- End of Payment Method Selection Section -->

              <hr />

              <!-- Payment Proof Upload Section -->
              <section class="payment-proof mb-4">
                <h2 class="section-title">Upload Payment Proof</h2>
                <div class="form-group">
                  <label for="paymentProof" class="form-label visually-hidden"
                    >Upload Payment Proof</label
                  >
                  <input
                    type="file"
                    id="paymentProof"
                    class="form-control"
                    @change="handleFileUpload"
                    aria-label="Upload Payment Proof"
                    accept="image/*"
                  />
                </div>

                <!-- Image Preview -->
                <div
                  v-if="paymentProofPreview"
                  class="image-preview mt-3 text-center"
                >
                  <h5>Preview of Uploaded Payment Proof:</h5>
                  <img
                    :src="paymentProofPreview"
                    alt="Payment Proof Preview"
                    class="img-fluid preview-img mt-2"
                  />
                </div>
              </section>
              <!-- End of Payment Proof Upload Section -->

              <!-- Total Payment and Proceed Button Section -->
              <section
                class="total-payment d-flex justify-content-between align-items-center mt-4"
              >
                <h4>Total Payment: Rp {{ packageDetails.price }}</h4>
                <button
                  class="btn proceed-button"
                  :disabled="
                    !paymentProof ||
                    (packageDetails.type === 'Membership' && !startDate)
                  "
                  @click="processPayment"
                >
                  Proceed to Payment
                </button>
              </section>
              <!-- End of Total Payment and Proceed Button Section -->
            </div>

            <!-- Loading State -->
            <div v-else class="text-center mt-5">
              <p>Loading package details...</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { onMounted, ref, defineAsyncComponent, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import dayjs from 'dayjs'

const LazyNavbar = defineAsyncComponent(() => import('./Navbar.vue'))

export default {
  name: 'PaymentPage',
  components: { LazyNavbar },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const packageDetails = ref(null)
    const selectedPaymentMethod = ref('')
    const paymentProof = ref(null)
    const paymentProofPreview = ref(null)
    const startDate = ref('')
    const endDate = ref('')
    const visitNumber = ref(0)

    const minStartDate = ref(dayjs().format('YYYY-MM-DD')) // Minimum selectable date is today

    let previousObjectURL = null // To keep track of the previous object URL

    const fetchPackageDetails = async () => {
      const idPackage = route.params.idPackage
      const token = localStorage.getItem('token')

      if (!token) {
        Swal.fire(
          'Error',
          'Authentication token is missing. Please log in again.',
          'error',
        )
        router.push({ name: 'Login' })
        return
      }

      try {
        const response = await axios.get(
          `https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/package/get?id=${idPackage}`,
          { headers: { Authorization: `Bearer ${token}` } },
        )
        packageDetails.value = response.data

        if (packageDetails.value.type === 'Membership') {
          // Wait for user to select start date
        }

        // If the package is a VisitPackage, you might want to set visitNumber here
        if (packageDetails.value.type === 'Visit') {
          visitNumber.value = packageDetails.value.visitNumber || 1
        }
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to load package details.', 'error')
      }
    }

    const calculateEndDate = start => {
      return dayjs(start)
        .add(packageDetails.value.duration, 'month')
        .format('YYYY-MM-DD')
    }

    const updateEndDate = () => {
      if (startDate.value) {
        endDate.value = calculateEndDate(startDate.value)
      } else {
        endDate.value = ''
      }
    }

    const handleFileUpload = event => {
      const file = event.target.files[0]
      if (file) {
        // Revoke the previous object URL if it exists
        if (previousObjectURL) {
          URL.revokeObjectURL(previousObjectURL)
        }

        paymentProof.value = file
        paymentProofPreview.value = URL.createObjectURL(file)
        previousObjectURL = paymentProofPreview.value

        Swal.fire(
          'File Uploaded',
          'Payment proof uploaded successfully.',
          'success',
        )
      }
    }

    const processPayment = async () => {
      if (!selectedPaymentMethod.value) {
        Swal.fire('Error', 'Please select a payment method.', 'error')
        return
      }

      if (packageDetails.value.type === 'Membership' && !startDate.value) {
        Swal.fire(
          'Error',
          'Please select a start date for your membership.',
          'error',
        )
        return
      }

      const token = localStorage.getItem('token')
      const formData = new FormData()
      formData.append('buktiTransfer', paymentProof.value)
      formData.append('paymentMethod', selectedPaymentMethod.value)

      if (packageDetails.value.type === 'Membership') {
        formData.append('startDate', startDate.value)
        formData.append('endDate', endDate.value)
        formData.append('duration', packageDetails.value.duration)
      }

      if (packageDetails.value.type === 'Visit') {
        formData.append('visitNumber', visitNumber.value)
      }

      formData.append('price', packageDetails.value.price)
      formData.append('transactionPrice', packageDetails.value.price)
      formData.append('token', token)

      try {
        let response
        if (packageDetails.value.type === 'Membership') {
          response = await axios.post(
            'https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/membership/buy',
            formData,
            {
              headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'multipart/form-data',
              },
            },
          )
        } else if (packageDetails.value.type === 'Visit') {
          response = await axios.post(
            'https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/visitpackage/buy',
            formData,
            {
              headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'multipart/form-data',
              },
            },
          )
        } else {
          throw new Error('Unknown package type')
        }

        Swal.fire('Payment', 'Payment completed.', 'success').then(() => {
          router.push({ name: 'MemberDashboard' })
        })
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to process payment.', 'error')
      }
    }

    onMounted(() => {
      fetchPackageDetails()
    })

    onBeforeUnmount(() => {
      // Revoke the object URL to free up memory
      if (previousObjectURL) {
        URL.revokeObjectURL(previousObjectURL)
      }
    })

    return {
      packageDetails,
      selectedPaymentMethod,
      paymentProof,
      paymentProofPreview,
      startDate,
      endDate,
      visitNumber,
      handleFileUpload,
      processPayment,
      minStartDate,
      updateEndDate,
    }
  },
}
</script>

<style scoped>
/* General container styling */
.container {
  background-color: #f8f9fa;
  border-radius: 12px;
}

/* Payment Card Styling */
.payment-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Page Title Styling */
.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #000; /* Black color for consistency */
}

/* Section Titles */
.section-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: #000; /* Black color to match page title */
  margin-bottom: 20px;
  border-bottom: 2px solid #dee2e6;
  padding-bottom: 10px;
}

/* Info Box Styling */
.info-box {
  background-color: #f0f0f0; /* Light grey background to differentiate sections */
  padding: 15px;
  border-radius: 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.info-box strong {
  font-size: 1.1rem;
  color: #000; /* Black for labels */
}

.info-box span {
  font-size: 1rem;
  color: #555; /* Dark grey for values */
  margin-top: 5px;
}

/* Payment Method Styling */
.payment-method .form-check {
  font-size: 1.1rem;
}

.payment-method .form-check-label {
  margin-left: 5px;
  color: #000; /* Black labels */
}

/* QRIS Image Styling */
.qris-img {
  max-width: 250px;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  padding: 10px;
  background-color: #f8f9fa;
}

/* Payment Details Styling */
.payment-details h5 {
  font-size: 1.25rem;
  color: #000; /* Black color */
}

.payment-details ul {
  font-size: 1rem;
  color: #555; /* Dark grey for text */
}

.payment-details ul li {
  margin-bottom: 8px;
}

/* Payment Proof Upload Styling */
.payment-proof .form-control[type='file'] {
  border: 2px dashed #ced4da;
  border-radius: 8px;
  padding: 12px;
  transition: border-color 0.3s;
  cursor: pointer;
  background-color: #f8f9fa;
}

.payment-proof .form-control[type='file']:hover {
  border-color: #ff4500; /* Highlight border with accent color on hover */
}

/* Image Preview Styling */
.image-preview h5 {
  font-size: 1.1rem;
  color: #000; /* Black color */
}

.preview-img {
  max-width: 100%;
  height: auto;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  padding: 5px;
  margin-top: 10px;
}

/* Total Payment and Button Styling */
.total-payment {
  background-color: #f0f0f0; /* Light grey background */
  padding: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-payment h4 {
  font-size: 1.5rem;
  color: #000; /* Black color */
}

.proceed-button {
  background-color: #ff4500; /* Matching accent color */
  color: #ffffff; /* White text */
  padding: 10px 25px;
  font-size: 1.1rem;
  border: none;
  border-radius: 8px;
  transition: background-color 0.3s;
  cursor: pointer;
}

.proceed-button:hover {
  background-color: #e03b00; /* Darker shade on hover */
}

.proceed-button:disabled {
  background-color: #ccc; /* Disabled button color */
  cursor: not-allowed;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .info-box strong {
    font-size: 1rem;
  }

  .info-box span {
    font-size: 0.95rem;
  }

  .payment-method .form-check {
    font-size: 1rem;
  }

  .qris-img {
    max-width: 200px;
  }

  .proceed-button {
    padding: 8px 20px;
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 1.75rem;
  }

  .section-title {
    font-size: 1.25rem;
  }

  .info-box {
    padding: 10px;
  }

  .info-box strong {
    font-size: 0.95rem;
  }

  .info-box span {
    font-size: 0.9rem;
  }

  .payment-method .form-check {
    font-size: 0.95rem;
  }

  .qris-img {
    max-width: 150px;
  }

  .proceed-button {
    padding: 6px 15px;
    font-size: 0.95rem;
  }
}
</style>
