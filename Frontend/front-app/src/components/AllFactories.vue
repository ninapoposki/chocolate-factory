<template>
  <div class="container">
    <h1 class="title">FACTORIES</h1>
    <div class="tables">
      <table class="table-container">
        <thead>
          <tr>
            <th>Logo</th>
            <th>Name</th>
            <th>Location</th>
            <th>Average grade</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="factory in factories" :key="factory.id">
            <td><img :src="factory.logoUri" alt="Factory Logo" class="logo" /></td>
            <td>{{ factory.factoryName }}</td>
            <td>{{ factory.location.street }} {{ factory.location.streetNumber }}, {{ factory.location.city }} {{ factory.location.postalCode }}</td>
            <td>{{ factory.grade }}</td>
            <tr class="buttons">
              <td class="button_two"><button @click="ShowDetails(factory.id)">Show details</button></td>
              <td class="button_two"><button class="small-button" @click="AddNewChocolate(factory.id)">Add chocolate</button></td>
            </tr>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      factories: []
    };
  },
  created() {
    this.fetchFactories();
  },
  methods: {
    fetchFactories() {
      axios.get('http://localhost:8080/WebShopAppREST/rest/factories')
        .then(response => {
          console.log(response);
          this.factories = response.data.sort((a, b) => {
            if (a.isStatus === b.isStatus) {
              return a.id - b.id;
            }
            return a.isStatus ? -1 : 1;
          });
        })
        .catch(error => {
          console.error('Error fetching factories', error);
        });
    },
    AddNewChocolate(id) {
      this.$router.push(`/add/${id}`);
    },
    ShowDetails(id) {
      this.$router.push(`/details/${id}`);
    }
  }
}
</script>

<style scoped>
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
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.tables {
  width: 80%;
  display: flex;
  justify-content: center;
}

.table-container {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}


th, td {
  border: 1px solid #ddd;
  padding: 12px 15px;
  text-align: left;
}
.button_two{
  border:0px ;
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
  max-width: 50px;
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
.logo {
max-width: 100px; 
height: auto;
}

button:hover {
  background-color: #36a372;
}

.small-button {
  padding: 5px 8px; 
}
</style>