<template>
  <div class="buy-membership">
    <h1>Buy Membership</h1>
    <p>Choose a membership plan that suits you.</p>
    <div class="membership-plans">
      <div class="plan" v-for="plan in plans" :key="plan.id">
        <h2>{{ plan.name }}</h2>
        <p>{{ plan.description }}</p>
        <p>Price: ${{ plan.price }}</p>
        <button @click="buyPlan(plan.id)">Buy Now</button>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '../Navbar.vue'

export default {
  name: 'BuyMembership',
  data() {
    return {
      plans: [
        {
          id: 1,
          name: 'Basic Plan',
          description: 'Access to basic features.',
          price: 10,
        },
        {
          id: 2,
          name: 'Standard Plan',
          description: 'Access to all features.',
          price: 20,
        },
        {
          id: 3,
          name: 'Premium Plan',
          description: 'Access to all features and premium content.',
          price: 30,
        },
      ],
    }
  },
  methods: {
    buyPlan(planId) {
      // Logic to handle the membership purchase goes here
      alert(`You have purchased the plan with ID: ${planId}`)
    },
  },
}
</script>

<style>
.buy-membership {
  padding: 20px;
}

.membership-plans {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.plan {
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 8px;
}

.plan h2 {
  margin: 0 0 10px;
}
</style>
