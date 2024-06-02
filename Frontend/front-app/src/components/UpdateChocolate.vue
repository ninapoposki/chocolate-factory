<template>
    <div class="formica" v-if="chocolate">
        <div>
          <label for="chocolateName">Chocolate Name:</label>
          <input type="text" v-model="chocolate.chocolateName" required />
        </div>
        <div>
          <label for="price">Price:</label>
          <input type="number" v-model="chocolate.price" required />
        </div>
        <div>
          <label for="variety">Variety:</label>
          <input type="text" v-model="chocolate.variety" required />
        </div>
        <div>
          <label for="type">Type:</label>
          <input type="text" v-model="chocolate.type" required />
        </div>
        <div>
          <label for="weight">Weight (g):</label>
          <input type="number" v-model="chocolate.weight" required />
        </div>
        <div>
          <label for="description">Description:</label>
          <textarea v-model="chocolate.description" required></textarea>
        </div>
        <div>
          <label for="imageUri">Image URL:</label>
          <input type="text" v-model="chocolate.imageUri" required />
        </div>
        <div>
          <label for="factoryId">Factory:</label>
          <select type="text" v-model="chocolate.factoryId" required />
        </div>

        <div class="button-type">
            <button @click="updateChocolate">Update chocolate</button>
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
      chocolate: null
    };
  },
  created() {
    this.fetchOneChocolate();
  },
  methods: {
    fetchOneChocolate() {
      const id = this.$route.params.id;
      console.log('Chocolate ID:', id); 
      if (!id) {
        console.error('Chocolate ID is missing');
        return;
      }
      axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}`)
        .then(response => {
          console.log('Chocolate data:', response.data); 
          this.chocolate = response.data;
        })
        .catch(error => {
          console.error('Error fetching chocolate:', error);
        });
    },
    updateChocolate() {
      axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${this.chocolate.id}`, this.chocolate)
        .then(response => {
          alert("You have successfully made the changes!");
          this.$router.push('/probica'); //zameni posle sa prikazom jedne
        })
        .catch(error => {
          console.error('Error updating chocolate', error);
        });
    },
    quitClick(){
      this.$router.push('/probica');
    }
    
  }
}

</script>

<style>
h2 {
  text-align: center;
  color: #333;
  font-size: 20px; /* Malo smanjen font za naslov */
}

form {
  display: flex;
  flex-direction: column;
  max-width: 300px; /* Još više smanjena maksimalna širina */
  margin: 20px auto; /* Smanjen razmak od vrha */
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background: #fff; /* Svjetlija pozadina */
  border: 2px solid #42b983; /* Boja na border okvira */
}

form > div {
  margin-bottom: 15px; /* Malo smanjen razmak između polja */
}

label {
  display: block; /* Label ide iznad inputa */
  margin-bottom: 8px; /* Malo smanjen razmak između labela i inputa */
  color: #666;
  font-size: 14px; /* Smanjen font za label */
}

input, textarea, select {
  padding: 10px; /* Malo smanjen padding unutar inputa */
  font-size: 14px; /* Smanjen font unutar inputa */
  border: 1px solid #ccc; /* Tanji border */
  border-radius: 5px; /* Blago zaobljeni uglovi */
  background: #f8f8f8; /* Svjetlija pozadina za input */
}

input:focus, textarea:focus, select:focus {
  border-color: #42b983;
  outline: none;
  background: #fff; /* Promjena pozadine pri fokusu */
}

.button-type {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px; /* Malo smanjen razmak između dugmadi */
  margin-top: 10px;
}

button {
  padding: 8px 16px;
  font-size: 14px; /* Smanjen font za dugme */
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: auto; /* Dugmići će biti širi samo koliko im je potrebno */
  max-width: 80%; /* Maksimalna širina dugmića */
}

button:hover {
  background-color: #369f79;
}

</style>
