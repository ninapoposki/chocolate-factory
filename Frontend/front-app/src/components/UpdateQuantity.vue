<template>
  <div class="background">
    <div class="content">
      <h2>Change Chocolate Quantity</h2>
      <form @submit.prevent="updateQuantity">
        <!-- Ostali inputi -->
        <div class="form-section">
          <label for="quantity">Quantity:</label>
          <input type="number" v-model="quantity" required />
        </div>
        <div class="form-section">
          <button type="submit">Update Quantity</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const chocolate = ref({
  id: '',
  chocolateName: '',
  price: '',
  variety: '',
  type: '',
  weight: '',
  description: '',
  imageUri: ''
});
const quantity = ref(0);

const id = route.params.id;

onMounted(() => {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/choco/${id}`)
    .then(response => {
      Object.assign(chocolate.value, response.data);
      quantity.value = response.data.numberOfChocolates;
    })
    .catch(error => {
      console.error('Error fetching chocolate details', error);
    });
});

const updateQuantity = () => {
  console.log("Quantity to update:", quantity.value); // Provera vrednosti
  console.log("Type of quantity:", typeof quantity.value); // Provera tipa podatka
  axios.patch(`http://localhost:8080/WebShopAppREST/rest/chocolates/quantity/${id}`, 
    { numberOfChocolates: quantity.value },
    {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      console.log(response.data); // Log response to confirm update
      alert('Quantity updated successfully');
      router.push(`/details/${chocolate.value.factoryId}`);
    })
    .catch(error => {
      console.error('Error updating quantity', error);
      alert('Failed to update quantity');
    });
};
</script>

<style scoped>
.background {
  position: relative;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px 0;
  background-image: url('https://cdn2.hauteliving.com/wp-content/uploads/2014/08/macarons.gif');
  background-size: cover;
  background-repeat: no-repeat;
}

.content {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  max-width: 600px;
  width: 100%;
  opacity: 1;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

form {
  display: flex;
  flex-direction: column;
}

.form-section {
  margin-bottom: 10px;
}

.logo-preview {
  max-width: 100px;
  max-height: 100px;
}

button {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #36a372;
}
</style>
