<template>
  <div class="view-class">
    <h1>View Class</h1>
    <p>Here are the classes available for you:</p>
    <ul class="class-list">
      <li v-for="classItem in classes" :key="classItem.id">
        <h2>{{ classItem.title }}</h2>
        <p>{{ classItem.description }}</p>
        <button @click="viewClassDetails(classItem.id)">View Details</button>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'ViewClass',
  data() {
    return {
      classes: [
        {
          id: 1,
          title: 'Introduction to Programming',
          description: 'Learn the basics of programming.',
        },
        {
          id: 2,
          title: 'Advanced JavaScript',
          description: 'Deep dive into JavaScript.',
        },
        {
          id: 3,
          title: 'Web Development Fundamentals',
          description: 'Learn how to build websites.',
        },
      ],
    }
  },
  methods: {
    viewClassDetails(classId) {
      // Logic to view class details goes here
      alert(`Viewing details for class with ID: ${classId}`)
    },
  },
}
</script>

<style>
.view-class {
  padding: 20px;
}

.class-list {
  list-style-type: none;
  padding: 0;
}

.class-list li {
  margin-bottom: 20px;
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 8px;
}
</style>
