<template>
  <div class="user-table">
    <h2 class="title">Usuários:</h2>

    <div v-if="users.length === 0" class="empty-message">
      Nenhum usuário registrado
    </div>

    <div v-else class="users-container">
      <UserCard v-for="user in users" :key="user.id" :user-data="user" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import UserCard from './UserCard.vue';
import { onMounted } from 'vue';
import api from '../../api';

const users = ref([]);

// get users
onMounted(async () => {
  try {
    users.value = await api.users.getAll();
  } catch (error) {
    console.error('Erro on fetching users:', error);
  }
});

</script>

<style scoped>
.user-table {
  padding: 20px;
}

.title {
  text-align: left;
  color: white;
  margin-bottom: 20px;
}

.empty-message {
  text-align: left;
  color: white;
  font-style: italic;
}

.users-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
</style>