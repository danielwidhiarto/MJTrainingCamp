<template>
  <div>
    <Navbar />
    <div class="container mx-auto p-6 mt-6">
      <h1 class="text-center mb-6">Manage Members</h1>

      <!-- Search Bar -->
      <div class="mb-4 d-flex justify-content-between align-items-center">
        <h5>Member List</h5>
        <input
          type="text"
          class="form-control w-50"
          placeholder="Search by name or email..."
          v-model="searchQuery"
          @input="debouncedSearch"
          aria-label="Search Members"
        />
      </div>

      <!-- Members List Table -->
      <div class="card shadow-sm mb-6">
        <div
          class="card-header bg-dark text-white d-flex justify-content-between align-items-center"
        >
          <span>Member List</span>
          <button class="btn btn-secondary btn-sm" @click="toggleSort">
            {{ sortAscending ? 'Sort Descending' : 'Sort Ascending' }}
          </button>
        </div>
        <div class="card-body">
          <table
            class="table table-hover table-responsive"
            v-if="filteredMembers.length > 0"
          >
            <thead class="thead-light">
              <tr>
                <th @click="sortBy('idMember')" class="sortable">
                  Member ID
                  <i :class="getSortIcon('idMember')"></i>
                </th>
                <th @click="sortBy('name')" class="sortable">
                  Name
                  <i :class="getSortIcon('name')"></i>
                </th>
                <th @click="sortBy('email')" class="sortable">
                  Email
                  <i :class="getSortIcon('email')"></i>
                </th>
                <th @click="sortBy('phoneNumber')" class="sortable">
                  Phone Number
                  <i :class="getSortIcon('phoneNumber')"></i>
                </th>
                <th @click="sortBy('membershipStatus')" class="sortable">
                  Membership Status
                  <i :class="getSortIcon('membershipStatus')"></i>
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="member in paginatedMembers"
                :key="member.idMember"
                :class="membershipStatusClass(member.membershipStatus)"
              >
                <td>{{ member.idMember }}</td>
                <td>{{ member.name }}</td>
                <td>{{ member.email }}</td>
                <td>{{ member.phoneNumber }}</td>
                <td>{{ member.membershipStatus }}</td>
                <td>
                  <button
                    class="btn btn-info btn-sm me-2"
                    @click="viewMember(member.idMember)"
                  >
                    <i class="fas fa-eye"></i> View
                  </button>
                  <button
                    class="btn btn-danger btn-sm"
                    @click="confirmDelete(member.idMember)"
                  >
                    <i class="fas fa-trash-alt"></i> Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">No members found.</p>
          </div>

          <!-- Pagination Controls -->
          <nav v-if="totalPages > 1" aria-label="Member list pagination">
            <ul class="pagination justify-content-center mt-4">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button
                  class="page-link"
                  @click="goToPage(currentPage - 1)"
                  aria-label="Previous"
                >
                  <span aria-hidden="true">&laquo;</span>
                </button>
              </li>
              <li
                class="page-item"
                v-for="page in totalPages"
                :key="page"
                :class="{ active: currentPage === page }"
              >
                <button class="page-link" @click="goToPage(page)">
                  {{ page }}
                </button>
              </li>
              <li
                class="page-item"
                :class="{ disabled: currentPage === totalPages }"
              >
                <button
                  class="page-link"
                  @click="goToPage(currentPage + 1)"
                  aria-label="Next"
                >
                  <span aria-hidden="true">&raquo;</span>
                </button>
              </li>
            </ul>
          </nav>
        </div>
      </div>

      <!-- Member Details Modal -->
      <div v-if="isModalOpen" class="custom-modal">
        <div class="custom-modal-content">
          <button class="btn-close" @click="closeModal">&times;</button>
          <h5 class="modal-title mb-4 text-center">Member Details</h5>
          <div v-if="isLoadingDetails" class="modal-loading">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <span>Loading member details...</span>
          </div>
          <div v-else>
            <p><strong>Member ID:</strong> {{ selectedMember.idMember }}</p>
            <p><strong>Name:</strong> {{ selectedMember.name }}</p>
            <p><strong>Email:</strong> {{ selectedMember.email }}</p>
            <p>
              <strong>Phone Number:</strong> {{ selectedMember.phoneNumber }}
            </p>
            <p>
              <strong>Membership Status:</strong>
              {{ selectedMember.membershipStatus }}
            </p>
            <p>
              <strong>Join Date:</strong>
              {{ formatDate(selectedMember.joinDate) }}
            </p>
            <p>
              <strong>Last Active:</strong>
              {{ formatDate(selectedMember.lastActive) }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component
import axios from 'axios'
import Swal from 'sweetalert2'
import { debounce } from 'lodash'
import DOMPurify from 'dompurify'
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'ManageMember',
  components: {
    Navbar,
  },
  setup() {
    const members = ref([])
    const selectedMember = ref({})
    const isModalOpen = ref(false)
    const isLoadingDetails = ref(false)
    const searchQuery = ref('')
    const sortKey = ref('idMember')
    const sortAscending = ref(true)

    // Pagination
    const currentPage = ref(1)
    const itemsPerPage = ref(5)

    // Debounced search
    const debouncedSearch = debounce(() => {
      currentPage.value = 1
    }, 300)

    // Fetch members from API (dummy API endpoint)
    const fetchMembers = async () => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          Swal.fire(
            'Authentication Required',
            'Please log in to manage members.',
            'warning',
          ).then(() => {
            window.location.href = '/login' // Redirect to login
          })
          return
        }

        const response = await axios.get(
          'https://mjtrainingcamp.my.id/api/v1/member/getall',
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        members.value = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch members.', 'error')
      }
    }

    // Computed property for filtered and sorted members
    const filteredMembers = computed(() => {
      let filtered = members.value.filter(member => {
        const query = searchQuery.value.toLowerCase()
        return (
          member.name.toLowerCase().includes(query) ||
          member.email.toLowerCase().includes(query)
        )
      })

      // Sorting
      filtered.sort((a, b) => {
        let aVal = a[sortKey.value]
        let bVal = b[sortKey.value]

        if (typeof aVal === 'string') {
          aVal = aVal.toLowerCase()
          bVal = bVal.toLowerCase()
        }

        if (aVal < bVal) return sortAscending.value ? -1 : 1
        if (aVal > bVal) return sortAscending.value ? 1 : -1
        return 0
      })

      return filtered
    })

    // Pagination calculations
    const totalPages = computed(() =>
      Math.ceil(filteredMembers.value.length / itemsPerPage.value),
    )

    const paginatedMembers = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value
      const end = start + itemsPerPage.value
      return filteredMembers.value.slice(start, end)
    })

    // Sorting function
    const sortBy = key => {
      if (sortKey.value === key) {
        sortAscending.value = !sortAscending.value
      } else {
        sortKey.value = key
        sortAscending.value = true
      }
    }

    const getSortIcon = key => {
      if (sortKey.value !== key) return 'fas fa-sort'
      return sortAscending.value ? 'fas fa-sort-up' : 'fas fa-sort-down'
    }

    // Toggle sort order
    const toggleSort = () => {
      sortAscending.value = !sortAscending.value
    }

    // View member details
    const viewMember = async idMember => {
      isModalOpen.value = true
      isLoadingDetails.value = true
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(
          `https://mjtrainingcamp.my.id/api/v1/member/get?id=${idMember}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        selectedMember.value = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch member details.', 'error')
      } finally {
        isLoadingDetails.value = false
      }
    }

    // Close modal
    const closeModal = () => {
      isModalOpen.value = false
      selectedMember.value = {}
    }

    // Confirm deletion
    const confirmDelete = idMember => {
      Swal.fire({
        title: 'Are you sure?',
        text: 'Do you want to delete this member?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'Cancel',
      }).then(async result => {
        if (result.isConfirmed) {
          await deleteMember(idMember)
        }
      })
    }

    // Delete member
    const deleteMember = async idMember => {
      try {
        const token = localStorage.getItem('token')
        await axios.delete(
          `https://mjtrainingcamp.my.id/api/v1/member/delete/${idMember}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        // Remove member from the list
        members.value = members.value.filter(
          member => member.idMember !== idMember,
        )
        Swal.fire('Deleted!', 'The member has been deleted.', 'success')
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to delete member.', 'error')
      }
    }

    // Assign Bootstrap table classes based on membership status
    const membershipStatusClass = status => {
      if (status === 'Active') return 'table-success'
      if (status === 'Inactive') return 'table-secondary'
      if (status === 'Pending') return 'table-warning'
      return ''
    }

    // Format date using Day.js
    const formatDate = date => {
      return dayjs(date).format('dddd, D MMMM YYYY')
    }

    // Sanitize inputs (if any user-generated content is displayed)
    // Currently, member details are fetched from the API and displayed directly.
    // If you plan to allow editing or input fields, ensure to sanitize them using DOMPurify.

    onMounted(() => {
      fetchMembers()
    })

    return {
      members,
      selectedMember,
      isModalOpen,
      isLoadingDetails,
      searchQuery,
      sortKey,
      sortAscending,
      currentPage,
      itemsPerPage,
      filteredMembers,
      paginatedMembers,
      totalPages,
      sortBy,
      getSortIcon,
      toggleSort,
      viewMember,
      closeModal,
      confirmDelete,
      membershipStatusClass,
      formatDate,
      debouncedSearch,
    }
  },
}
</script>

<style scoped>
/* Container Styling */
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  background-color: #f8f9fa;
  border-radius: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* Heading Styles */
h1 {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #ff4500;
}

/* Card Styling */
.card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
}

.sortable {
  cursor: pointer;
}

.sortable i {
  margin-left: 5px;
  color: #6c757d;
}

/* Table Styling */
.table th,
.table td {
  vertical-align: middle;
}

.table-hover tbody tr:hover {
  background-color: #ffe5d9;
}

.table-responsive {
  overflow-x: auto;
}

/* Modal Styling */
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
  z-index: 1000;
}

.custom-modal-content {
  background-color: #fff;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.btn-close {
  position: absolute;
  top: 15px;
  right: 20px;
  font-size: 1.5rem;
  border: none;
  background: none;
  cursor: pointer;
  color: #333;
}

.btn-close:hover {
  color: #ff4500;
}

.modal-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.modal-loading .spinner-border {
  width: 3rem;
  height: 3rem;
  margin-bottom: 15px;
}

/* Pagination Styling */
.pagination .page-link {
  color: #ff4500;
}

.pagination .page-item.active .page-link {
  background-color: #ff4500;
  border-color: #ff4500;
  color: #fff;
}

.pagination .page-link:hover {
  background-color: #ff4500;
  color: #fff;
}

/* Responsive Design */
@media (max-width: 768px) {
  .container {
    padding: 30px 15px;
  }

  h1 {
    font-size: 2rem;
  }

  .card-header {
    font-size: 1.1rem;
  }

  .custom-modal-content {
    padding: 20px;
  }

  .btn-info,
  .btn-danger {
    padding: 5px 8px;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 1.75rem;
  }

  .custom-modal-content {
    padding: 15px;
  }

  .table th,
  .table td {
    font-size: 0.9rem;
  }

  .btn-info,
  .btn-danger {
    padding: 4px 6px;
    font-size: 0.8rem;
  }

  .pagination .page-link {
    padding: 0.3rem 0.6rem;
    font-size: 0.8rem;
  }
}
</style>
