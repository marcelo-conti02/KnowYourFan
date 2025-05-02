<template>
  <div class="app-container">
    <header class="app-header">
      <img 
        src="/Furia_Esports_logo.png" 
        alt="Furia Esports Logo" 
        class="logo"
      />
      <h1 class="title">Know Your Fan</h1>
      <button class="create-user-button">Criar Usuário</button>
    </header>
    
    <main class="main-content">
      <!-- Conteúdo principal aqui (se necessário) -->
    </main>
  </div>
</template>

<script setup>
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background-color: #001a33; 
}

.title{
  font-family:system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  color: white;
}

.app-header {
  display: flex;
  justify-content: space-between; 
  align-items: center;
  padding: 1rem 2rem;
  background-color: rgba(0, 0, 0, 0.2); 
  border-bottom: 1px solid rgba(255, 255, 255, 0.1); 
}

.logo {
  height: 40px; 
  width: auto;
}

.create-user-button {
  background-color: white; 
  color: black;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}

.main-content {
  padding: 2rem;
  color: white; 
}
</style>