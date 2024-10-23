<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Members</h1>

      <!-- Members List Table -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white">Member List</div>
        <div class="card-body">
          <table class="table table-hover">
            <thead>
              <tr>
                <th>Member ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone Number</th>
                <th>Membership Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <!-- Dummy Data -->
              <tr>
                <td>001</td>
                <td>John Doe</td>
                <td>johndoe@example.com</td>
                <td>+62 812 3456 7890</td>
                <td>Active</td>
                <td>
                  <button class="btn btn-sm btn-info">View</button>
                  <button class="btn btn-sm btn-danger">Delete</button>
                </td>
              </tr>
              <tr>
                <td>002</td>
                <td>Jane Smith</td>
                <td>janesmith@example.com</td>
                <td>+62 812 9876 5432</td>
                <td>Inactive</td>
                <td>
                  <button class="btn btn-sm btn-info">View</button>
                  <button class="btn btn-sm btn-danger">Delete</button>
                </td>
              </tr>
              <tr>
                <td>003</td>
                <td>Michael Johnson</td>
                <td>michaelj@example.com</td>
                <td>+62 813 4567 8901</td>
                <td>Pending</td>
                <td>
                  <button class="btn btn-sm btn-info">View</button>
                  <button class="btn btn-sm btn-danger">Delete</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component

export default {
  name: 'ManageMember',
  components: {
    Navbar,
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

.table-hover tbody tr:hover {
  background-color: #f0f9f0;
}

.btn-info {
  background-color: #17a2b8;
  color: #ffffff;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  margin-right: 5px;
  transition: background-color 0.3s ease;
}

.btn-info:hover {
  background-color: #138496;
}

.btn-danger {
  background-color: #dc3545;
  color: #ffffff;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.btn-danger:hover {
  background-color: #c82333;
}
</style>
