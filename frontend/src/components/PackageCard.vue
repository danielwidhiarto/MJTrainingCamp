<template>
  <div class="package-card">
    <!-- Package Image -->
    <img
      v-if="packageItem.imageUrl"
      :src="packageItem.imageUrl"
      alt="Package Image"
      class="package-image"
      loading="lazy"
    />
    <div v-else class="package-placeholder">
      <!-- Placeholder for missing images -->
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="100%"
        height="150"
        fill="#ccc"
        class="bi bi-image"
        viewBox="0 0 16 16"
      >
        <path
          d="M6.002 5.5a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 .5.5v1.5a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5V5.5zM3 5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v1.5a.5.5 0 0 1-.5.5h-7a.5.5 0 0 1-.5-.5V5zm0 3.5a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 .5.5V13a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8.5z"
        />
        <path
          d="M4.502 8a1 1 0 0 1 1-1h6a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1H5.502a1 1 0 0 1-1-1V8z"
        />
      </svg>
    </div>

    <!-- Package Details -->
    <h2>Rp. {{ formattedPrice }}</h2>
    <h3>{{ packageItem.packageName }}</h3>
    <p>{{ packageItem.description }}</p>

    <!-- Action Button -->
    <button
      :class="buttonClass"
      @click="handleSelect"
      :aria-label="buttonLabel"
    >
      {{ buttonText }}
    </button>
  </div>
</template>
<script>
export default {
  name: 'PackageCard',
  props: {
    packageItem: {
      type: Object,
      required: true,
      validator: obj =>
        obj.hasOwnProperty('idPackage') &&
        obj.hasOwnProperty('price') &&
        obj.hasOwnProperty('packageName') &&
        obj.hasOwnProperty('description'),
    },
    buttonType: {
      type: String,
      default: 'membership', // 'membership' or 'visit'
      validator: value => ['membership', 'visit'].includes(value),
    },
  },
  computed: {
    /**
     * Formats the price to include thousand separators.
     * E.g., 1000000 becomes 1.000.000
     */
    formattedPrice() {
      return this.packageItem.price.toLocaleString('id-ID')
    },
    /**
     * Determines the button text based on the package type.
     */
    buttonText() {
      return this.buttonType === 'membership'
        ? 'Select Plan'
        : 'Buy Visit Package'
    },
    /**
     * Determines the CSS class for the button based on the package type.
     */
    buttonClass() {
      return this.buttonType === 'membership'
        ? 'select-plan-button'
        : 'select-visit-button'
    },
    /**
     * Provides a descriptive label for accessibility.
     */
    buttonLabel() {
      return this.buttonType === 'membership'
        ? `Select ${this.packageItem.packageName} Plan`
        : `Buy ${this.packageItem.packageName} Visit Package`
    },
  },
  methods: {
    /**
     * Emits a 'select' event with the package ID when the button is clicked.
     */
    handleSelect() {
      this.$emit('select', this.packageItem.idPackage)
    },
  },
}
</script>
<style scoped>
/* Package Card Container */
.package-card {
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
  border: 2px solid var(--primary-color, #ff4500); /* Orange-red border */
}

.package-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.15);
}

/* Package Image */
.package-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 15px;
}

/* Placeholder for Missing Images */
.package-placeholder {
  width: 100%;
  height: 150px;
  background-color: #f0f0f0;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 15px;
}

.package-placeholder svg {
  width: 50px;
  height: 50px;
}

/* Headings and Text */
h2 {
  font-size: 2rem;
  color: var(--text-color, #000);
  margin-bottom: 10px;
}

h3 {
  font-size: 1.5rem;
  color: var(--primary-color, #ff4500);
  margin-bottom: 15px;
}

p {
  font-size: 1rem;
  color: var(--muted-text, #555);
  flex-grow: 1; /* Ensures description takes available space */
}

/* Action Buttons */
.select-plan-button,
.select-visit-button {
  background-color: var(
    --primary-color,
    #ff4500
  ); /* Matching the logo’s red-orange */
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
  background-color: var(
    --secondary-color,
    #e03b00
  ); /* Slightly darker shade on hover */
}

/* Responsive Adjustments */
@media (max-width: 992px) {
  .package-card {
    flex: 1 1 calc(33.333% - 20px);
  }
}

@media (max-width: 768px) {
  .package-card {
    flex: 1 1 calc(50% - 20px);
  }
}

@media (max-width: 480px) {
  .package-card {
    flex: 1 1 100%;
  }

  h2 {
    font-size: 1.75rem;
  }

  h3 {
    font-size: 1.25rem;
  }

  p {
    font-size: 0.9rem;
  }

  .select-plan-button,
  .select-visit-button {
    padding: 10px 14px;
    font-size: 0.9rem;
  }
}
</style>
