<template>
  <div class="container">
      <h1 class="title">Factory Details</h1>
      <div v-if="factory">
          <div class="tables">
              <table class="table-container">
                  <thead>
                      <tr>
                          <th>Logo</th>
                          <th>Name</th>
                          <th>Location</th>
                          <th>Working Time</th>
                          <th>Grade</th>
                          <th>Status</th>
                      </tr>
                  </thead>
                  <tbody>
                      <tr>
                          <td><img :src="factory.logoUri" alt="Factory Logo" class="logo" /></td>
                          <td>{{ factory.factoryName }}</td>
                          <td>{{ factory.location.street }} {{ factory.location.streetNumber }}, {{ factory.location.city }} {{ factory.location.postalCode }}</td>
                          <td>{{ factory.workingTime }}</td>
                          <td>{{ factory.grade }}</td>
                          <td>{{ factory.isStatus ? 'active' : 'inactive' }}</td>
                      </tr>
                  </tbody>
              </table>
          </div>
      </div>
      
      <h1 class="title">Chocolates</h1>
      <div v-if="chocolates.length">
          <div class="tables">
              <table class="table-container">
                  <thead>
                      <tr>
                          <th>ID</th>
                          <th>Name</th>
                          <th>Price</th>
                          <th>Variety</th>
                          <th>Type</th>
                          <th>Weight</th>
                          <th>Description</th>
                          <th>Image</th>
                          <th>Actions</th>
                      </tr>
                  </thead>
                  <tbody>
                      <tr v-for="chocolate in chocolates" :key="chocolate.id">
                          <td>{{ chocolate.id }}</td>
                          <td>{{ chocolate.chocolateName }}</td>
                          <td>{{ chocolate.price }}</td>
                          <td>{{ chocolate.variety }}</td>
                          <td>{{ chocolate.type }}</td>
                          <td>{{ chocolate.weight }}</td>
                          <td>{{ chocolate.description }}</td>
                          <td><img :src="chocolate.imageUri" alt="Chocolate Image" class="logo" /></td>
                          <td class="buttons">
                              <button @click="updateChocolate(chocolate)">Change chocolate</button>
                              <button class="small-button" @click="deleteChocolate(chocolate.id)">Delete chocolate</button>
                          </td>
                      </tr>
                  </tbody>
              </table>
          </div>
      </div>
      <div v-else>
          <p>No chocolates found for this factory.</p>
      </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const factory = ref(null);
const chocolates = ref([]);

onMounted(() => {
  const id = route.params.id;
  axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${id}`)
      .then(response => {
          factory.value = response.data;
      })
      .catch(error => {
          console.error('Error fetching factory details', error);
      });

  axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}`)
      .then(response => {
          chocolates.value = response.data;
      });
});

function updateChocolate(chocolate) {
  router.push({ name: 'update', params: { id: chocolate.id } });
}

function deleteChocolate(chocolateId) {
  axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/${chocolateId}`)
      .then(() => {
          chocolates.value = chocolates.value.filter(c => c.id !== chocolateId);
          alert('Chocolate deleted successfully');
      })
      .catch(error => {
          console.error('Error deleting chocolate', error);
          alert('Failed to delete chocolate');
      });
}

defineExpose({ updateChocolate });
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

body {
  font-family: 'Roboto', sans-serif;
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.tables {
  width: 80%;
  display: flex;
  justify-content: center;
  margin-left: 10%;
}

.table-container {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  margin: 0 auto; /* Center the table */
}

th, td {
  border: 1px solid #ddd;
  padding: 12px 15px;
  text-align: left;
}
img {
    max-width: 50px;
    max-height: 100px;
}

th {
  background-color: #f5f5f5;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

td {
  background-color: #fff;
  font-size: 0.9em;
  color: #555;
}

.logo {
  max-width: 100px;
  height: auto;
}

.buttons {
  display: flex;
  gap: 10px;
}

button {
  padding: 8px 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #36a372;
}

.small-button {
  padding: 5px 8px; 
}
</style>
