<template>
  <div>
    <Navbar />
    <div class="container mt-5">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Manage Classes</h1>
        <!-- Button to trigger modal -->
        <button class="btn btn-primary" @click="isModalVisible = true">
          Add Class
        </button>
      </div>

      <!-- Classes List Section -->
      <div class="card mb-4 shadow-sm">
        <div class="card-body">
          <h2 class="card-title">Class List</h2>
          <ul class="list-group">
            <li
              v-for="(classItem, index) in classes"
              :key="index"
              class="list-group-item d-flex justify-content-between align-items-center"
            >
              <div>
                <strong>{{ classItem.className }}</strong> -
                {{ classItem.classDate }} {{ classItem.classTime }}
                <p>
                  Capacity: {{ classItem.classCapasity }} | Requirements:
                  {{ classItem.classRequirement }}
                </p>
              </div>
            </li>
          </ul>
          <div v-if="!classes.length" class="text-center mt-4">
            <p>No classes available. Add a new class to get started!</p>
          </div>
        </div>
      </div>

      <!-- Add Class Modal (Custom Implementation) -->
      <div v-if="isModalVisible" class="custom-modal">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Add New Class</h5>
              <button
                type="button"
                class="btn-close"
                @click="closeModal"
              ></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="addClass">
                <div class="form-group mb-3">
                  <label for="className">Class Name</label>
                  <input
                    type="text"
                    class="form-control"
                    id="className"
                    v-model="newClass.className"
                    required
                  />
                </div>
                <div class="form-group mb-3">
                  <label for="classDate">Class Date</label>
                  <input
                    type="date"
                    class="form-control"
                    id="classDate"
                    v-model="newClass.classDate"
                    required
                  />
                </div>
                <div class="form-group mb-3">
                  <label for="classTime">Class Time</label>
                  <input
                    type="time"
                    class="form-control"
                    id="classTime"
                    v-model="newClass.classTime"
                    required
                  />
                </div>
                <div class="form-group mb-3">
                  <label for="classCapasity">Class Capacity</label>
                  <input
                    type="number"
                    class="form-control"
                    id="classCapasity"
                    v-model="newClass.classCapasity"
                    required
                  />
                </div>
                <div class="form-group mb-3">
                  <label for="classRequirement">Class Requirements</label>
                  <textarea
                    class="form-control"
                    id="classRequirement"
                    v-model="newClass.classRequirement"
                    required
                  ></textarea>
                </div>
                <div class="form-group mb-3">
                  <label for="idTrainer">Trainer ID</label>
                  <input
                    type="number"
                    class="form-control"
                    id="idTrainer"
                    v-model="newClass.idTrainer"
                    required
                  />
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
                <button
                  type="button"
                  class="btn btn-secondary"
                  @click="closeModal"
                >
                  Cancel
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import Navbar from './Navbar.vue'

export default {
  name: 'ManageClass',
  components: {
    Navbar,
  },
  data() {
    return {
      isModalVisible: false,
      newClass: {
        classDate: '',
        classTime: '',
        classCapasity: 1,
        classRequirement: '',
        idTrainer: 1,
        className: '',
      },
      classes: [],
    }
  },
  mounted() {
    this.fetchClasses() // Fetch classes when the component is mounted
  },
  methods: {
    fetchClasses() {
      const token = localStorage.getItem('authToken')

      axios
        .get('http://localhost:8081/api/v1/class/getClasses', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then(response => {
          this.classes = response.data
        })
        .catch(error => {
          console.error('Error fetching classes:', error)
        })
    },
    addClass() {
      const token = localStorage.getItem('authToken')

      axios
        .post('http://localhost:8081/api/v1/class/add', this.newClass, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then(response => {
          this.classes.push(response.data) // Add the newly created class to the list
          this.closeModal() // Close the modal
        })
        .catch(error => {
          console.error('Error adding class:', error)
        })
    },
    closeModal() {
      this.isModalVisible = false
      this.resetForm()
    },
    resetForm() {
      this.newClass = {
        classDate: '',
        classTime: '',
        classCapasity: 1,
        classRequirement: '',
        idTrainer: 1,
        className: '',
      }
    },
  },
}
</script>

<style scoped>
.container {
  padding: 20px;
}

/* Custom modal styles */
.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-dialog {
  max-width: 500px;
  width: 100%;
}

.modal-content {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-close {
  cursor: pointer;
}
</style>
