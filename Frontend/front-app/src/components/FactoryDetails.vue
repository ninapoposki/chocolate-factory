<template>
    <div>
      <h2>Factory Details</h2>
      <div v-if="factory">
        <table>
            <tr>
            <th></th>
            <th>Name</th>
            <th>Location</th>
            <th>Working time</th>
            <th>Grade</th>
            <th>Status</th>


            </tr>
        <tr>
           
            <td>{{ factory.imageUri }}</td>
            <td>{{ factory.factoryName }}</td>
            <td>{{ factory.location.street }} {{ factory.location.streetNumber }}, {{ factory.location.city }} {{ factory.location.postalCode }}</td>
            <td>{{ factory.workingTime }}</td>
            <td>{{ factory.grade }}</td>
            <td>{{ factory.isStatus }}</td>
            
        </tr>

          
        </table>
      </div>
      
      <h2>Chocolates</h2>
    <div v-if="chocolates.length">
      <table>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Price</th>
          <th>Variety</th>
          <th>Type</th>
          <th>Weight</th>
          <th>Description</th>
          <th>Image</th>
        </tr>
        <tr v-for="chocolate in chocolates" :key="chocolate.id">
          <td>{{ chocolate.id }}</td>
          <td>{{ chocolate.chocolateName }}</td>
          <td>{{ chocolate.price }}</td>
          <td>{{ chocolate.variety }}</td>
          <td>{{ chocolate.type }}</td>
          <td>{{ chocolate.weight }}</td>
          <td>{{ chocolate.description }}</td>
          <tr>
            <td><button @click="updateChocolate(chocolate)">Change chocolate</button></td>
            <td><button @click="deleteChocolate(chocolate.id)">Delete chocolate</button></td>
          </tr>
          
      

        </tr>
      </table>
    </div>
    <div v-else>
      No chocolates found for this factory.
    </div>


    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute ,useRouter} from 'vue-router';
  
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

      axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}`).then(response => {
    chocolates.value = response.data;
  });
  });
  function updateChocolate(chocolate) {
  router.push({ name: 'update', params: { id: chocolate.id } });
  }

  function deleteChocolate(chocolateId) {
  axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/${chocolateId}`)
    .then(() => {
      // Uklanja čokoladu iz liste nakon uspešnog brisanja
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
  
  <style scoped>
  h2 {
    margin: 20px 0;
  }
  table {
    width: 100%;
    border-collapse: collapse;
  }
  th, td {
    padding: 10px;
    border: 1px solid #ccc;
  }
  img {
    max-width: 100px;
  }
  .odvojeniDugmici{
    border:0px solid transparent;
  }

  </style>