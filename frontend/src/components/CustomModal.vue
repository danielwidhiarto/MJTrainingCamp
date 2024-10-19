<template>
  <div v-if="visible" class="custom-modal">
    <div class="custom-modal-content">
      <button class="btn-close" @click="closeModal">×</button>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CustomModal',
  props: {
    visible: {
      type: Boolean,
      required: true,
    },
  },
  methods: {
    closeModal() {
      this.$emit('close')
    },
  },
}
</script>

<style scoped>
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

.custom-modal-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 50%;
  position: relative;
}

.btn-close {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}
</style>
