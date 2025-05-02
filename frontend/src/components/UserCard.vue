<template>
    <div class="user-card">
        <h3>{{ userData.name }}</h3>
        <p>{{ userData.email }}</p>
        
    </div>
</template>

<script setup>
import { defineProps } from 'vue';

defineProps({
    userData: {
        type: Object,
        required: true
    }
});
</script>

<style scoped>
.user-card {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    background: white;
    color: black;
}
</style>