<template>
    <div>
      <h2>Add New Chocolate</h2>
      <form @submit.prevent="addChocolate">
        <div>
          <label for="chocolateName">Chocolate Name:</label>
          <input type="text" v-model="newChocolate.chocolateName" required />
        </div>
        <div>
          <label for="price">Price:</label>
          <input type="number" v-model="newChocolate.price" required />
        </div>
        <div>
          <label for="variety">Variety:</label>
          <input type="text" v-model="newChocolate.variety" required />
        </div>
        <div>
          <label for="type">Type:</label>
          <input type="text" v-model="newChocolate.type" required />
        </div>
        <div>
          <label for="weight">Weight (g):</label>
          <input type="number" v-model="newChocolate.weight" required />
        </div>
        <div>
          <label for="description">Description:</label>
          <textarea v-model="newChocolate.description" required></textarea>
        </div>
        <div>
          <label for="imageUri">Image URL:</label>
          <input type="text" v-model="newChocolate.imageUri" required />
        </div>
       
        <button type="submit">Add Chocolate</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute } from 'vue-router';
  const route = useRoute();

  const newChocolate = ref({
    chocolateName: "",
    price: 0,
    variety: "",
    type: "",
    weight: 0,
    description: "",
    imageUri: "",
    factoryId: "",
    numberOfChocolates: 0,
    isOnStock: false
  });
  onMounted(() => {
  const factoryId = route.params.id;
  newChocolate.value.factoryId = factoryId;
});
  
  function addChocolate() {
    axios.post('http://localhost:8080/WebShopAppREST/rest/chocolates/', newChocolate.value)
      .then(response => {
        console.log(response.data);
        // Clear the form after successful submission
        newChocolate.value = {
          chocolateName: "",
          price: 0,
          variety: "",
          type: "",
          weight: 0,
          description: "",
          imageUri: "",
          factoryId: "",
          numberOfChocolates: 0,
          isOnStock: false
        };
      })
      .catch(error => {
        console.log(error);
      });
  }
  </script>
  
  <style scoped>
  h2 {
    margin: 20px 0;
  }
  form {
    display: flex;
    flex-direction: column;
  }
  form > div {
    margin-bottom: 10px;
  }
  label {
    margin-bottom: 5px;
  }
  input, textarea, select {
    padding: 8px;
    font-size: 14px;
    width: 100%;
  }
  button {
    padding: 10px;
    font-size: 16px;
    background-color: #42b983;
    color: white;
    border: none;
    cursor: pointer;
  }
  button:hover {
    background-color: #369f79;
  }
  </style>
  