<template>
  <div class="formica" v-if="chocolateCopy">
    <div>
      <label for="chocolateName">Chocolate Name:</label>
      <input type="text" v-model="chocolateCopy.chocolateName" required />
    </div>
    <div>
      <label for="price">Price:</label>
      <input type="number" v-model="chocolateCopy.price" required />
    </div>
    <div>
      <label for="variety">Variety:</label>
      <input type="text" v-model="chocolateCopy.variety" required />
    </div>
    <div>
      <label for="type">Type:</label>
      <input type="text" v-model="chocolateCopy.type" required />
    </div>
    <div>
      <label for="weight">Weight (g):</label>
      <input type="number" v-model="chocolateCopy.weight" required />
    </div>
    <div>
      <label for="description">Description:</label>
      <textarea v-model="chocolateCopy.description" required></textarea>
    </div>
    <div>
      <label for="imageUri">Image URL:</label>
      <input type="text" v-model="chocolateCopy.imageUri" required />
      <div v-if="chocolateCopy.imageUri" class="image-preview-container">
        <img :src="chocolateCopy.imageUri" alt="Preview Image" class="image-preview" />
      </div>
    </div>
    <div>
      <label for="factoryId">Factory:</label>
      <select v-model="chocolateCopy.factoryId" required>
        <option v-if="chocolate && chocolate.factory" :value="chocolate.factory.id">
          {{ chocolate.factory.factoryName }}
        </option>
        <option v-for="factory in factories" :value="factory.id" :key="factory.id">
          {{ factory.factoryName }}
        </option>
      </select>
    </div>
    <p>{{ error }}</p>
    <div class="button-type">
      <button @click="validateAndSubmit">Update chocolate</button>
      <button @click="quitClick">Quit</button>
    </div>
  </div>
  <div v-else>
    <p>Loading...</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'UpdateChocolate',
  data() {
    return {
      chocolate: null,
      chocolateCopy: null,
      factories: [],
      error: ''
    };
  },
  created() {
    this.fetchOneChocolate();
    this.fetchFactories();
  },
  methods: {
    fetchOneChocolate() {
      const id = this.$route.params.id;
      console.log('Chocolate ID:', id);
      if (!id) {
        console.error('Chocolate ID is missing');
        return;
      }
      axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/choco/${id}`)
        .then(response => {
          console.log('Chocolate data:', response.data);
          this.chocolate = response.data;
          this.chocolateCopy = JSON.parse(JSON.stringify(response.data)); // Kreiranje duboke kopije
        })
        .catch(error => {
          console.error('Error fetching chocolate:', error);
        });
    },
    fetchFactories() {
      axios.get('http://localhost:8080/WebShopAppREST/rest/factories')
        .then(response => {
          this.factories = response.data;
        })
        .catch(error => {
          console.error('Error fetching factories:', error);
          this.factories = [];
        });
    },
    validateAndSubmit() {
      const regex = /^\s*[A-Za-z]+(\s+[A-Za-z]+)*\s*$/;
      const chocolate = this.chocolateCopy;
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
        this.error = errorMessage;
        return;
      } else {
        this.error = '';
        this.updateChocolate();
      }
    },
    updateChocolate() {
      axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${this.chocolate.id}`, this.chocolateCopy)
        .then(response => {
          alert("You have successfully made the changes!");
          if (this.chocolateCopy.factoryId) {
            this.$router.push({
              name: 'details',
              params: { id: this.chocolateCopy.factoryId }
            });
          } else {
            console.error('Factory ID is missing');
          }
        })
        .catch(error => {
          console.error('Error updating chocolate', error);
        });
    },
    quitClick() {
      if (this.chocolate.factoryId) {
        this.$router.push({
          name: 'details',
          params: { id: this.chocolate.factoryId }
        });
      } else {
        console.error('Factory ID is missing');
      }
    }
  }
}
</script>

<style>
h2 {
  text-align: center;
  color: #333;
  font-size: 20px;
}

form {
  display: flex;
  flex-direction: column;
  max-width: 300px;
  margin: 20px auto;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background: #fff;
  border: 2px solid #42b983;
}

form > div {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

input, textarea, select {
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background: #f8f8f8;
}

input:focus, textarea:focus, select:focus {
  border-color: #42b983;
  outline: none;
  background: #fff;
}

.button-type {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

button {
  padding: 8px 16px;
  font-size: 14px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: auto;
  max-width: 80%;
}

button:hover {
  background-color: #369f79;
}

.image-preview {
  max-width: 50px;
  margin-top: 10px;
}

.error {
  color: red;
}
</style>
