<template>
    <div class="user-card">
        <h3>Nome: {{ userData.name }}</h3>
        <p>Email: {{ userData.email }}</p>
        <p>Idade: {{ userData.age }}</p> 
        <p>game favorito: {{ fanProfile.favoriteGame }}</p>
    </div>
</template>

<script setup>
import { defineProps, onMounted, ref } from 'vue';
import api from '../../api';

const props = defineProps({
    userData: {
        type: Object,
        required: true
    }
});

const fanProfile = ref({})

// get user profile data
onMounted(async () => {
  try {
    fanProfile.value = await api.fanProfiles.getById(props.userData.id);
  } catch (error) {
    console.error('Erro on fetching profiles:', error);
  }
});
</script>

<style scoped>
.user-card {
    text-align: left;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    background: white;
    color: black;
}
</style>