<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Members</h1>

      <!-- Member List Header with Search Bar -->
      <div class="d-flex justify-content-between align-items-center mb-3">
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

      <!-- Member List -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white">Member List</div>
        <div class="card-body">
          <!-- Members Table -->
          <table class="table table-hover" v-if="paginatedMembers.length > 0">
            <thead>
              <tr>
                <th @click="sort('idUser')" class="sortable">
                  User ID
                  <i :class="getSortIcon('idUser')"></i>
                </th>
                <th @click="sort('name')" class="sortable">
                  Name
                  <i :class="getSortIcon('name')"></i>
                </th>
                <th @click="sort('email')" class="sortable">
                  Email
                  <i :class="getSortIcon('email')"></i>
                </th>
                <th @click="sort('phoneNumber')" class="sortable">
                  Phone Number
                  <i :class="getSortIcon('phoneNumber')"></i>
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="member in paginatedMembers" :key="member.idUser">
                <td>{{ member.idUser }}</td>
                <td>{{ member.name }}</td>
                <td>{{ member.email }}</td>
                <td>{{ member.phoneNumber }}</td>
                <td>
                  <button
                    class="btn btn-sm btn-info"
                    @click="openMemberDetailsModal(member)"
                  >
                    <i class="fas fa-eye"></i> View Details
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
      <div v-if="isMemberDetailsModalOpen" class="custom-modal">
        <div class="custom-modal-content">
          <button class="btn-close" @click="closeMemberDetailsModal">
            &times;
          </button>
          <h5 class="modal-title mb-4 text-center">Member Details</h5>
          <div class="member-details">
            <p><strong>User ID:</strong> {{ selectedMember.idUser }}</p>
            <p><strong>Name:</strong> {{ selectedMember.name }}</p>
            <p><strong>Email:</strong> {{ selectedMember.email }}</p>
            <p>
              <strong>Phone Number:</strong> {{ selectedMember.phoneNumber }}
            </p>
            <p>
              <strong>Visits Left:</strong>
              {{
                selectedMember.visitDetails.length > 0
                  ? selectedMember.visitDetails.reduce(
                      (sum, visit) =>
                        sum + (visit.visitNumber - visit.visitUsed),
                      0,
                    )
                  : 'N/A'
              }}
            </p>
            <p><strong>Memberships:</strong></p>
            <ul>
              <li
                v-for="membership in selectedMember.memberships"
                :key="membership.idMembership"
              >
                <p><strong>ID:</strong> {{ membership.idMember }}</p>
                <p>
                  <strong>Duration:</strong>
                  {{ membership.duration }} months
                </p>
                <p>
                  <strong>Start Date:</strong>
                  {{ formatDate(membership.startDate) }}
                </p>
                <p>
                  <strong>End Date:</strong>
                  {{ formatDate(membership.endDate) }}
                </p>
                <p><strong>Price:</strong> ${{ membership.price }}</p>
                <hr />
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'
// Removed CustomModal import since it's not used
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { debounce } from 'lodash'
import dayjs from 'dayjs'
import Swal from 'sweetalert2'

export default {
  name: 'ManageMember',
  components: {
    Navbar,
  },
  setup() {
    // Reactive References
    const members = ref([])
    const searchQuery = ref('')
    const sortKey = ref('idUser')
    const sortAscending = ref(true)
    const currentPage = ref(1)
    const itemsPerPage = ref(5)
    const isMembershipModalOpen = ref(false)
    const selectedMemberships = ref([])
    const isMemberDetailsModalOpen = ref(false)
    const selectedMember = ref({})

    const debouncedSearch = debounce(() => {
      currentPage.value = 1
    }, 300)

    // Fetch Members
    const fetchMembers = async () => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          window.location.href = '/'
          return
        }
        const response = await axios.get(
          'https://mjtrainingcamp.my.id/api/v1/class/getAllUser',
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        members.value = response.data.userData.map(member => ({
          ...member,
          phoneNumber: member.pnumber,
        }))
      } catch (error) {
        console.error('Error fetching members:', error)
        Swal.fire('Error', 'Failed to fetch members.', 'error')
      }
    }

    // Computed Properties
    const filteredMembers = computed(() => {
      const query = searchQuery.value.toLowerCase()
      return members.value.filter(
        member =>
          member.name.toLowerCase().includes(query) ||
          member.email.toLowerCase().includes(query),
      )
    })

    const sortedMembers = computed(() => {
      return [...filteredMembers.value].sort((a, b) => {
        let aVal = a[sortKey.value]
        let bVal = b[sortKey.value]

        // Handle nested properties if any
        if (sortKey.value.includes('.')) {
          const keys = sortKey.value.split('.')
          aVal = keys.reduce((acc, key) => acc && acc[key], a)
          bVal = keys.reduce((acc, key) => acc && acc[key], b)
        }

        // If sorting by visitsLeft, calculate it
        if (sortKey.value === 'visitsLeft') {
          aVal =
            a.visitDetails.length > 0
              ? a.visitDetails.reduce(
                  (sum, visit) => sum + (visit.visitNumber - visit.visitUsed),
                  0,
                )
              : 0
          bVal =
            b.visitDetails.length > 0
              ? b.visitDetails.reduce(
                  (sum, visit) => sum + (visit.visitNumber - visit.visitUsed),
                  0,
                )
              : 0
        }

        // For dates
        if (
          sortKey.value === 'membershipDetail.startDate' ||
          sortKey.value === 'membershipDetail.endDate'
        ) {
          aVal = dayjs(aVal).unix()
          bVal = dayjs(bVal).unix()
        }

        // Convert to lowercase if string
        if (typeof aVal === 'string') aVal = aVal.toLowerCase()
        if (typeof bVal === 'string') bVal = bVal.toLowerCase()

        if (aVal < bVal) return sortAscending.value ? -1 : 1
        if (aVal > bVal) return sortAscending.value ? 1 : -1
        return 0
      })
    })

    const paginatedMembers = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage.value
      const end = start + itemsPerPage.value
      return sortedMembers.value.slice(start, end)
    })

    const totalPages = computed(() =>
      Math.ceil(sortedMembers.value.length / itemsPerPage.value),
    )

    // Sorting Function
    const sort = key => {
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

    // Pagination
    const goToPage = page => {
      if (page < 1 || page > totalPages.value) return
      currentPage.value = page
    }

    // Format Date
    const formatDate = date => {
      return dayjs(date).format('DD MMM YYYY')
    }

    // Membership Modal
    const openMembershipModal = membershipsList => {
      selectedMemberships.value = membershipsList
      isMembershipModalOpen.value = true
    }

    const closeMembershipModal = () => {
      isMembershipModalOpen.value = false
      selectedMemberships.value = []
    }

    // Member Details Modal
    const openMemberDetailsModal = member => {
      selectedMember.value = member
      isMemberDetailsModalOpen.value = true
    }

    const closeMemberDetailsModal = () => {
      isMemberDetailsModalOpen.value = false
      selectedMember.value = {}
    }

    onMounted(fetchMembers)

    return {
      searchQuery,
      sortKey,
      sortAscending,
      currentPage,
      itemsPerPage,
      paginatedMembers,
      totalPages,
      sort,
      getSortIcon,
      goToPage,
      formatDate,
      isMembershipModalOpen,
      selectedMemberships,
      openMembershipModal,
      closeMembershipModal,
      isMemberDetailsModalOpen,
      selectedMember,
      openMemberDetailsModal,
      closeMemberDetailsModal,
      debouncedSearch,
    }
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

.table th {
  cursor: pointer;
}

.sortable:hover {
  color: #ff4500;
}

.btn-info {
  background-color: #17a2b8;
  border: none;
  color: #fff;
  transition: background-color 0.3s ease;
}

.btn-info:hover {
  background-color: #138496;
}

.pagination .page-link {
  color: #ff4500;
}

.pagination .page-item.active .page-link {
  background-color: #ff4500;
  color: #fff;
}

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
  max-height: 80vh;
  overflow-y: auto;
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

.member-details p,
.membership-details p {
  margin-bottom: 0.5rem;
}

.member-details ul,
.membership-details ul {
  list-style-type: disc;
  padding-left: 1.5rem;
}

.member-details ul li,
.membership-details ul li {
  margin-bottom: 0.5rem;
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

  .custom-modal-content {
    padding: 20px;
  }
}
</style>
