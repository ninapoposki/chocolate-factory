<template>
  <div class="background">
    <div class="container content">
      <h1 class="title">FACTORIES</h1>
      <div class="search-bar">
        <input type="text" v-model="searchQuery" placeholder="Search factories..." />
        <div class="search-buttons">
          <button @click="searchFactories">Search</button>
          <button class="reset-button" @click="resetFactories">Reset</button>
        </div>
      </div>
      <div class="filters">
        <div class="sort-bar">
          <label for="sortBy">Sort By:</label>
          <select v-model="sortBy" id="sortBy">
            <option value="factoryname">Factory name</option>
            <option value="location">Location</option>
            <option value="averagegrade">Average grade</option>
          </select>
          <label for="order">Order:</label>
          <select v-model="ascending" id="order">
            <option :value="true">Ascending</option>
            <option :value="false">Descending</option>
          </select>
          <button @click="sortFactories">Sort</button>
        </div>
        <div class="filter-bar">
          <label for="chocolateType">Chocolate Type:</label>
          <select v-model="chocolateType" id="chocolateType" class="narrow-select">
            <option value="">All</option>
            <option v-for="type in chocolateTypes" :key="type" :value="type">{{ type }}</option>
          </select>
          <label for="chocolateKind">Chocolate Kind:</label>
          <select v-model="chocolateKind" id="chocolateKind">
            <option value="">All</option>
            <option v-for="kind in chocolateVarieties" :key="kind" :value="kind">{{ kind }}</option>
          </select>
          <label for="openOnly">Open Only:</label>
          <input type="checkbox" v-model="openOnly" id="openOnly">
          <button @click="filterFactories">Filter</button>
        </div>
      </div>
      <div v-if="userRole === 'ADMINISTRATOR'" class="add-button">
        <button @click="addNewFactory">Add new</button>
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
              <td>
                <div class="location-info">
                  <factory-map :initialCoordinates="[factory.location.width, factory.location.height]" :small="true"></factory-map>
                  <div class="address">
                    <p>{{ factory.location.street }} {{ factory.location.streetNumber }}</p>
                    <p>{{ factory.location.city }} {{ factory.location.postalCode }}</p>
                    <p>{{ factory.location.height }}, {{ factory.location.width }}</p>
                  </div>
                </div>
              </td>
              <td>{{ factory.grade }}</td>
              <td class="buttons">
                <button @click="ShowDetails(factory.id)">Show details</button>
                <button  v-if="userRole === 'MANAGER'" class="small-button" @click="AddNewChocolate(factory.id)">Add chocolate</button>
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
import FactoryMap from '@/components/FactoryMap.vue';

export default {
  components: {
    FactoryMap
  },
  data() {
    return {
      searchQuery: '',
      factories: [],
      sortBy: '',
      ascending: null,
      chocolateType: '',
      chocolateKind: '',
      openOnly: false,
      userRole: '', 
      chocolateTypes: [], 
      chocolateVarieties: []
    };
  },
  created() {
    this.fetchFactories();
    this.fetchChocolateTypes();
    this.fetchChocolateVarieties();
    this.getUserRole(); 
  },
  methods: {
    fetchFactories() {
      axios.get('http://localhost:8080/WebShopAppREST/rest/factories')
        .then(response => {
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
    fetchChocolateTypes() {
      axios.get('http://localhost:8080/WebShopAppREST/rest/chocolates/types')
        .then(response => {
          this.chocolateTypes = response.data;
        })
        .catch(error => {
          console.error('Error fetching chocolate types', error);
        });
    },
    fetchChocolateVarieties() {
      axios.get('http://localhost:8080/WebShopAppREST/rest/chocolates/varieties')
        .then(response => {
          this.chocolateVarieties = response.data;
        })
        .catch(error => {
          console.error('Error fetching chocolate varieties', error);
        });
    },
    getUserRole() {
      const userId = this.getUserIdFromLocalStorage();
      axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`)
        .then(response => {
          this.userRole = response.data.role;
        })
        .catch(error => {
          console.error('Error fetching user role', error);
        });
    },
    getUserIdFromLocalStorage() {
      return localStorage.getItem('userId');
    },
    searchFactories() {
      axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/search?search=${this.searchQuery}`)
        .then(response => {
          if (response.data.length > 0) {
            this.factories = response.data;
          } else {
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
          this.factories = response.data;
        })
        .catch(error => {
          console.error('Error sorting factories', error);
        });
    },
    filterFactories() {
      console.log("Filtering with params:", {
    chocolateType: this.chocolateType,
    chocolateKind: this.chocolateKind,
    openOnly: this.openOnly
  });

      axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/filter`, {
        params: {
          chocolateType: this.chocolateType,
          chocolateVariety: this.chocolateKind, 
          openOnly: this.openOnly
        }
      })
      .then(response => {
        this.factories = response.data;
      })
      .catch(error => {
        console.error('Error filtering factories', error);
      });
    },
    resetFactories() {
      this.fetchFactories();
      this.sortBy = '';
      this.ascending = null;
      this.searchQuery = '';
      this.chocolateType = '';
      this.chocolateKind = '';
      this.openOnly = '';
    },
    ShowDetails(id) {
      this.$router.push(`/details/${id}`);
    },
    AddNewChocolate(id) {
      this.$router.push(`/add/${id}`);
    },
    addNewFactory() {
      this.$router.push({ name: 'addF' }); 
    }
  }
};
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
.add-button {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.add-button button {
  padding: 10px 20px;
  background-color: rosybrown;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-button button:hover {
  background-color: #36a372;
}

@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

body {
  font-family: 'Roboto', sans-serif;
  height: 100vh; 
  margin: 0; 
}

.background {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden; 
}

.background::before {
  content: "";
  background-image: url('https://cdn2.hauteliving.com/wp-content/uploads/2014/08/macarons.gif');
  background-size: cover;
  background-position: center; /* Center the background image */
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
  margin: auto; 
}

.search-bar {
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

.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-right: 290px;
  margin-bottom: 20px;
  width: 100%;
  max-width: 800px;
}

.sort-bar,
.filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}
.narrow-select {
  width: 100px; 
}


.sort-bar {
  flex: 1;
  justify-content: flex-start; 
  margin-right: 30px;
}

.filter-bar {
  flex: 2;
  justify-content: flex-end;
}

.tables {
  width: 100%;
  display: flex;
  justify-content: center;
}

.table-container {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  margin-right: 100px;
}

.location-info {
  display: flex;
  align-items: center; 
}

.address {
  text-align: left; 
  margin-left: 10px; 
}

.map {
  width: 150px;
  height: 100px;
  border: 1px solid #ddd;
}

th, td {
  border: 1px solid #ddd;
  padding: 12px 15px;
  text-align: left;
}

th.no-header {
  border: none; 
  background-color: transparent; 
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
  flex-direction: column; 
  gap: 10px;
  justify-content: center; 
  align-items: center; 
  border: none;
  background-color: transparent; 
}

button {
  padding: 8px 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  width: 120px; 
}

button:hover {
  background-color: #36a372;
}

.small-button {
  padding: 5px 8px;
  width: 120px; 
}

.search-buttons {
  display: flex;
  gap: 20px; 
}
.filters input,
.filters select,
.filters button {
  border: 2px solid #42b983; 
  border-radius: 4px;
  padding: 10px;
}
</style>
