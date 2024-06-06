<template>
    <div>
      <h2>Add New Chocolate</h2>
      <form @submit.prevent="addChocolate">
        <div>
          <label for="chocolateName">Chocolate Name:</label>
          <input type="text" v-model="newChocolate.chocolateName"  />
        </div>
        <div>
          <label for="price">Price:</label>
          <input type="number" v-model="newChocolate.price"  />
        </div>
        <div>
          <label for="variety">Variety:</label>
          <input type="text" v-model="newChocolate.variety"  />
        </div>
        <div>
          <label for="type">Type:</label>
          <input type="text" v-model="newChocolate.type"  />
        </div>
        <div>
          <label for="weight">Weight (g):</label>
          <input type="number" v-model="newChocolate.weight"  />
        </div>
        <div>
          <label for="description">Description:</label>
          <textarea v-model="newChocolate.description" ></textarea>
        </div>
        <div>
          <label for="imageUri">Image URL:</label>
          <input type="text" v-model="newChocolate.imageUri" />
        </div>
        
        <p>{{error}}</p>
        <button type="submit">Add Chocolate</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute } from 'vue-router';
  const route = useRoute();
  const error = ref('');

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
  const regex = /^\s*[A-Za-z]+(\s+[A-Za-z]+)*\s*$/;
  onMounted(() => {
  const factoryId = route.params.id;
  newChocolate.value.factoryId = factoryId;
});
  
  function addChocolate() {

    event.preventDefault();
  const chocolate = newChocolate.value;
  let errorMessage = '';
  if (!chocolate.chocolateName) {
    errorMessage = "Chocolate name is required.";
  } else if (!regex.test(chocolate.chocolateName)) {
    errorMessage = "Chocolate name must contain only letters.";
  } else if (!chocolate.price) {
    errorMessage = "Price is required.";
  } else if (chocolate.price <= 0) {
    errorMessage = "Price must be greater than zero.";
  } else if (!chocolate.variety) {
    errorMessage = "Variety is required.";
  } else if (!regex.test(chocolate.variety)) {
    errorMessage = "Variety must contain only letters.";
  } else if (!chocolate.type) {
    errorMessage = "Type is required.";
  } else if (!regex.test(chocolate.type)) {
    errorMessage = "Type must contain only letters.";
  } else if (!chocolate.weight) {
    errorMessage = "Weight is required.";
  } else if (chocolate.weight <= 0) {
    errorMessage = "Weight must be greater than zero.";
  } else if (!chocolate.description) {
    errorMessage = "Description is required.";
  } 
  if (errorMessage) {
    error.value = errorMessage;
    return;
  } else {
    error.value = '';
  }
 

    axios.post('http://localhost:8080/WebShopAppREST/rest/chocolates/', newChocolate.value)
      .then(response => {
        console.log(response.data);
        alert("You have successfully added chocolate!");
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
    width: 80%;
  }
  button {
    padding: 10px;
    font-size: 16px;
    background-color: #42b983;
    color: white;
    border: none;
    cursor: pointer;
    margin-left: 10%;
  }
  button:hover {
    background-color: #369f79;
  }
  </style>
  