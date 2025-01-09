<template>
  <div class="package-card">
    <!-- Package Image -->
    <img
      :src="getImagePath(buttonType)"
      alt="Package Image"
      class="package-image"
      loading="lazy"
    />

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
    /**
     * Returns the appropriate image path based on the package type.
     */
    getImagePath(packageType) {
      if (packageType === 'membership') {
        return new URL('../assets/Membership.png', import.meta.url).href
      } else if (packageType === 'visit') {
        return new URL('../assets/Visit.png', import.meta.url).href
      } else {
        return new URL('../assets/logo.jpg', import.meta.url).href // Fallback placeholder
      }
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
