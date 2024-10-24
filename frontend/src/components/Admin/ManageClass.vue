<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Classes</h1>

      <!-- Button to Trigger Add Class Modal -->
      <div class="d-flex justify-content-between mb-3">
        <h5></h5>
        <button class="btn add-class-button" @click="isAddModalOpen = true">
          Add New Class
        </button>
      </div>

      <!-- Class List -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white">Class List</div>
        <div class="card-body">
          <table class="table table-hover" v-if="classes.length > 0">
            <thead>
              <tr>
                <th>Class Name</th>
                <th>Date</th>
                <th>Time</th>
                <th>Capacity</th>
                <th>Trainer</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="classItem in classes" :key="classItem.idClass">
                <td>{{ classItem.className }}</td>
                <td>{{ formatDate(classItem.classDate) }}</td>
                <!-- Using the formatDate method -->
                <td>{{ classItem.classTime }}</td>
                <td>{{ classItem.classCapasity }}</td>
                <td>
                  {{
                    classItem.trainerDetail
                      ? classItem.trainerDetail.trainerName
                      : 'No Trainer Assigned'
                  }}
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">
              No classes found. Add a new class to get started!
            </p>
          </div>
        </div>
      </div>

      <!-- Add Class Modal -->
      <CustomModal :visible="isAddModalOpen" @close="isAddModalOpen = false">
        <h5 class="modal-title mb-4 text-center">Add New Class</h5>
        <form @submit.prevent="addClass">
          <div class="mb-3">
            <label for="className" class="form-label">Class Name</label>
            <input
              type="text"
              v-model="newClass.className"
              class="form-control"
              id="className"
              required
              placeholder="Enter class name"
            />
          </div>
          <div class="mb-3">
            <label for="classDate" class="form-label">Class Date</label>
            <input
              type="date"
              v-model="newClass.classDate"
              class="form-control"
              id="classDate"
              required
            />
          </div>
          <div class="mb-3">
            <label for="classTime" class="form-label">Class Time</label>
            <input
              type="time"
              v-model="newClass.classTime"
              class="form-control"
              id="classTime"
              required
            />
          </div>
          <div class="mb-3">
            <label for="classCapasity" class="form-label">Class Capacity</label>
            <input
              type="number"
              v-model="newClass.classCapasity"
              class="form-control"
              id="classCapasity"
              required
              placeholder="Enter class capacity"
            />
          </div>
          <div class="mb-3">
            <label for="classRequirement" class="form-label"
              >Class Requirements</label
            >
            <textarea
              v-model="newClass.classRequirement"
              class="form-control"
              id="classRequirement"
              placeholder="Enter class requirements"
              required
            ></textarea>
          </div>
          <div class="mb-3">
            <label for="idTrainer" class="form-label">Select Trainer</label>
            <select v-model="newClass.idTrainer" class="form-select" required>
              <option value="" disabled>Select a Trainer</option>
              <option
                v-for="trainer in trainers"
                :key="trainer.idTrainer"
                :value="trainer.idTrainer"
              >
                {{ trainer.trainerName }}
              </option>
            </select>
          </div>
          <button type="submit" class="btn add-class-button w-100">
            Add Class
          </button>
        </form>
      </CustomModal>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import Navbar from './Navbar.vue'
import CustomModal from '../CustomModal.vue'
import axios from 'axios'
import Swal from 'sweetalert2'

export default {
  name: 'ManageClass',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      classes: [],
      newClass: {
        classDate: '',
        classTime: '',
        classCapasity: '',
        classRequirement: '',
        idTrainer: '',
        className: '',
      },
      trainers: [],
      token: null,
      isAddModalOpen: false,
    }
  },
  created() {
    this.token = localStorage.getItem('token')

    if (this.token) {
      this.fetchTrainers()
      this.fetchClasses()
    } else {
      Swal.fire('Error', 'Token not found. Please log in.', 'error')
    }
  },
  methods: {
    async fetchTrainers() {
      try {
        const response = await axios.get(
          'http://localhost:8081/api/v1/trainer/getall',
          {
            headers: { Authorization: `Bearer ${this.token}` },
          },
        )
        this.trainers = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch trainers.', 'error')
      }
    },
    async fetchClasses() {
      try {
        const response = await axios.get(
          'http://localhost:8081/api/v1/class/getClasses',
          {
            headers: { Authorization: `Bearer ${this.token}` },
          },
        )
        this.classes = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch classes.', 'error')
      }
    },
    async addClass() {
      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/class/add',
          this.newClass,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        this.newClass = {
          classDate: '',
          classTime: '',
          classCapasity: '',
          classRequirement: '',
          idTrainer: '',
          className: '',
        }

        this.isAddModalOpen = false

        Swal.fire({
          title: 'Class Added!',
          text: 'The new class has been added successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          this.fetchClasses()
        })
      } catch (error) {
        console.error(error)
        Swal.fire('Error!', 'Failed to add the class.', 'error')
      }
    },
    formatDate(date) {
      return dayjs(date).format('dddd, DD MMM YYYY') // Format as Friday, 31 Oct 2024
    },
  },
}
</script>

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  background-color: #f8f9fa;
  border-radius: 16px;
}

h1 {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #ff4500;
}

.card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 1.2rem;
  font-weight: bold;
}

.add-class-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  transition: background-color 0.3s ease;
}

.add-class-button:hover {
  background-color: #e03b00;
}

.table-hover tbody tr:hover {
  background-color: #f0f9f0;
}

/* Modal Styling */
.modal-title {
  color: #ff4500;
  font-weight: bold;
  text-align: center;
}

.form-control {
  border-radius: 8px;
  padding: 12px;
  transition: border-color 0.3s ease;
}

.form-control:focus {
  border-color: #ff4500;
  box-shadow: 0px 0px 5px rgba(255, 69, 0, 0.5);
}

/* Responsive Design */
@media (max-width: 768px) {
  .container {
    padding: 20px;
  }

  h1 {
    font-size: 2rem;
  }

  .card-header {
    font-size: 1.1rem;
  }
}
</style>
