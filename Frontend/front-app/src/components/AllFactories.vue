<template>
  <div class="background">
    <div class="container content">
      <h1 class="title">FACTORIES</h1>
      <div class="search-bar">
        <input type="text" v-model="searchQuery" placeholder="Search factories..." />
        <div class="search_buttons">
          <button @click="searchFactories">Search</button>
          <button @click="resetFactories">Reset</button>
        </div>
        <div class="sort-bar">
          <label for="sortBy">Sort By:</label>
          <select v-model="sortBy" id="sortBy">
            <option value="factoryname">Factory name</option>
            <option value="location">Location</option>
            <option value="averagegrade">Average grade</option>
          </select>
          <label for="order">Order:</label>
          <select v-model="ascending" id="order">
            <option :value="true">Rastuće</option>
            <option :value="false">Opadajuće</option>
          </select>
          <button @click="sortFactories">Sort</button>
        </div>
        <div class="filter-bar">
          <!-- ovo posle zameni sa pravim podacima -->
          <label for="chocolateType">Chocolate Type:</label>
          <select v-model="chocolateType" id="chocolateType">
            <option value="">All</option>
            <option value="Milk">Milk</option>
            <option value="Dark">Dark</option>
            <option value="White">White</option>
          </select>
          <!-- ovo posle promeni sa podacima pravim  -->
          <label for="chocolateKind">Chocolate Kind:</label>
          <select v-model="chocolateKind" id="chocolateKind">
            <option value="">All</option>
            <option value="Bar">Bar</option>
            <option value="Truffle">Truffle</option>
            <option value="Bonbon">Bonbon</option>
          </select>
          <label for="openOnly">Open Only:</label>
          <input type="checkbox" v-model="openOnly" id="openOnly">
          <button @click="filterFactories">Filter</button>
        </div>
      </div>
      <div class="tables">
        <table class="table-container">
          <thead>
            <tr>
              <th>Logo</th>
              <th>Name</th>
              <th>Location</th>
              <th>Average grade</th>
              <th class="no-header"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="factory in factories" :key="factory.id">
              <td><img :src="factory.logoUri" alt="Factory Logo" class="logo" /></td>
              <td>{{ factory.factoryName }}</td>
              <!-- <td>{{ factory.location.street }} {{ factory.location.streetNumber }}, {{ factory.location.city }} {{ factory.location.postalCode }}</td> -->
              <td>
            <div class="location-info">
              <div class="address">
                <p>{{ factory.location.street }} {{ factory.location.streetNumber }}</p>
                <p>{{ factory.location.city }} {{ factory.location.postalCode }}</p>
                <p>{{ factory.location.height }}, {{ factory.location.width }}</p>
              </div>
              <map-component :initialCoordinates="[factory.location.width, factory.location.height]" :small="true"></map-component>
            </div>
          </td>
              <td>{{ factory.grade }}</td>
              <td class="buttons">
                <button @click="ShowDetails(factory.id)">Show details</button>
                <button class="small-button" @click="AddNewChocolate(factory.id)">Add chocolate</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>


<script>
import axios from 'axios';

export default {
  data() {
    return {
      searchQuery: '',
      factories: [],
      sortBy: 'naziv fabrike',
      ascending: true,
      chocolateType: '',
      chocolateKind: '',
      openOnly: false
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
    searchFactories() {
      axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/search?search=${this.searchQuery}`)
        .then(response => {
          console.log(response);
          if (response.data.length > 0) {
            this.factories = response.data;
          } else {
            console.log('No matching factories found.');
            this.factories = [];
          }
        })
        .catch(error => {
          console.error('Error searching factories', error);
        });
    },
    sortFactories() {
      axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/sort?sortBy=${this.sortBy}&ascending=${this.ascending}`)
        .then(response => {
          console.log(response);
          this.factories = response.data;
        })
        .catch(error => {
          console.error('Error sorting factories', error);
        });
    },
    filterFactories() {
      axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/filter`, {
        params: {
          chocolateType: this.chocolateType,
          chocolateKind: this.chocolateKind,
          openOnly: this.openOnly
        }
      })
      .then(response => {
        console.log(response);
        this.factories = response.data;
      })
      .catch(error => {
        console.error('Error filtering factories', error);
      });
    },
    resetFactories() {
      this.fetchFactories();
      this.sortBy="";
      this.ascending=null;
      this.searchQuery="";
      this.chocolateType="";
      this.chocolateKind="";
      this.openOnly="";
    },
    ShowDetails(id) {
      this.$router.push(`/details/${id}`);
    },
    AddNewChocolate(id) {
      this.$router.push(`/add/${id}`);
    }
  }
}

</script>

<style scoped>
.search-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar input {
  padding: 10px;
  width: 300px;
  border: 2px solid #42b983;
  border-radius: 4px;
  margin-right: 10px;
}

.search-bar button {
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-bar button:hover {
  background-color: #36a372;
}

@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

body {
  font-family: 'Roboto', sans-serif;
}

.background {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.background::before {
  content: "";
  background-image: url('https://cdn2.hauteliving.com/wp-content/uploads/2014/08/macarons.gif');
  background-size: cover;
  opacity: 0.5;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -1;
}

.content {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  max-width: 80%;
  width: 100%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin: auto; /* centriranje */
}
.search-bar{
  margin-right: 10px;
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
}

.title {
  font-size: 1.5em;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.tables {
  width: 100%;
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
  margin-right: 100px;
  border: 3px solid #42b983; /* Zeleni okvir */

}
.location-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.address {
  text-align: center;
  margin-bottom: 10px;
}

.map {
  width: 200px;
  height: 150px;
  border: 1px solid #ddd;
}

th, td {
  border: 1px solid #ddd;
  padding: 12px 15px;
  text-align: left;
}

th.no-header {
  border: none; /* uklanja granicu */
  background-color: transparent; /* uklanja pozadinu */
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
  flex-direction: column; /* Da dugmad budu jedno ispod drugog */
  gap: 10px;
  justify-content: center; /* Centriranje dugmadi */
  align-items: center; /* Centriranje dugmadi */
  border: none;
  background-color: transparent; /* Transparentna pozadina */

}

button {
  padding: 8px 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  width: 120px; /* Širina dugmadi */
}

button:hover {
  background-color: #36a372;
}

.small-button {
  padding: 5px 8px;
  width: 120px; /* Širina dugmadi */
}

.search_buttons {
  display: flex;
  gap: 10px; /* Podesite razmak između dugmića */
}
</style>
