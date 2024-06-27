<template>
    
  </template>
  
  <script setup>
  import { onMounted } from 'vue';
  import axios from 'axios';
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  
  const logout = async () => {
    try {
      await axios.post('http://localhost:8080/WebShopAppREST/rest/users/logout');
      // Brisanje kolačića na klijent strani
      document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      document.cookie = "userRole=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      document.cookie = "id=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      alert("Successfully logged out");
      // Preusmeravanje na početnu stranu ili bilo koju drugu stranu po želji
      router.push('/');
    } catch (error) {
      console.error("Error during logout:", error);
    }
  };
  
  onMounted(() => {
    logout();
  });
  </script>
  