<template>
  <div class="history-class">
    <h1>History Class</h1>
    <p>Your past classes:</p>
    <ul class="history-list">
      <li v-for="historyItem in history" :key="historyItem.id">
        <h2>{{ historyItem.title }}</h2>
        <p>Date: {{ historyItem.date }}</p>
        <p>Status: {{ historyItem.status }}</p>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'HistoryClass',
  data() {
    return {
      history: [
        {
          id: 1,
          title: 'Introduction to Programming',
          date: '2024-01-15',
          status: 'Completed',
        },
        {
          id: 2,
          title: 'Advanced JavaScript',
          date: '2024-02-20',
          status: 'In Progress',
        },
        {
          id: 3,
          title: 'Web Development Fundamentals',
          date: '2024-03-10',
          status: 'Completed',
        },
      ],
    }
  },
}
</script>

<style>
.history-class {
  padding: 20px;
}

.history-list {
  list-style-type: none;
  padding: 0;
}

.history-list li {
  margin-bottom: 20px;
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 8px;
}
</style>
