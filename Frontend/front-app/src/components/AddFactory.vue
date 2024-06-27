<template>
  <div class="background">
    <div class="content">
      <h2>Add New Factory</h2>
      <form @submit.prevent="validateAndSubmit">
        <div class="form-section">
          <label for="factoryName">Factory name:</label>
          <input type="text" v-model="newFactory.factoryName" required />
        </div>
        <div class="form-section">
          <label for="workingTime">Working time:</label>
          <input type="number" v-model="newFactory.workingTime" required />
        </div>
        <div class="form-section">
          <label for="logoUri">Logo URL:</label>
          <input type="text" v-model="newFactory.logoUri" @input="validateLogoUri" required />
          <div v-if="logoUrlValid">
            <img :src="newFactory.logoUri" alt="Preview logo" class="logo-preview" />
          </div>
          <div v-else-if="newFactory.logoUri">
            <p class="error">Invalid logo URI</p>
          </div>
        </div>
        <div class="form-section">
          <label for="locationCity">City:</label>
          <input type="text" v-model="newFactory.location.city" @input="updateMapFromInput" required />
        </div>
        <div class="form-section">
          <label for="locationCountry">Country:</label>
          <input type="text" v-model="newFactory.location.country" @input="updateMapFromInput" required />
        </div>
        <div class="form-section">
          <label for="locationStreet">Street:</label>
          <input type="text" v-model="newFactory.location.street" @input="updateMapFromInput" required />
        </div>
        <div class="form-section">
          <label for="locationStreetNumber">Street Number:</label>
          <input type="text" v-model="newFactory.location.streetNumber" @input="updateMapFromInput" required />
        </div>
        <div class="form-section">
          <label for="locationPostalCode">Postal Code:</label>
          <input type="text" v-model="newFactory.location.postalCode" @input="updateMapFromInput" required />
        </div>
        <map-component ref="mapComponent" :initialCoordinates="[newFactory.location.width || 20.4612, newFactory.location.height || 44.8125]" @locationSelected="setLocation"></map-component>
        
        <div class="form-section" v-if="unassignedManagers.length > 0">
          <label for="manager">Select Manager:</label>
          <select v-model="selectedManagerId">
            <option v-for="manager in unassignedManagers" :key="manager.id" :value="manager.id">
              {{ manager.firstName }} {{ manager.lastName }}
            </option>
          </select>
        </div>
        
        <div v-else>
          <div class="form-section">
            <label for="managerUsername">Manager Username:</label>
            <input type="text" v-model="newManager.username" required />
          </div>
          <div class="form-section">
            <label for="managerPassword">Manager Password:</label>
            <input type="password" v-model="newManager.password" required />
          </div>
          <div class="form-section">
            <label for="managerFirstName">Manager First Name:</label>
            <input type="text" v-model="newManager.firstName" required />
          </div>
          <div class="form-section">
            <label for="managerLastName">Manager Last Name:</label>
            <input type="text" v-model="newManager.lastName" required />
          </div>
          <div class="form-section">
            <label for="managerGender">Manager Gender:</label>
            <select v-model="newManager.gender">
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <div class="form-section">
            <label for="managerDateOfBirth">Manager Date of Birth:</label>
            <input type="date" v-model="newManager.dateOfBirth" required />
          </div>
        </div>
        
        <p>{{ error }}</p>
        <div class="form-section">
          <button type="submit">Add factory</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import MapComponent from './MapComponent.vue';

export default {
  components: {
    MapComponent
  },
  data() {
    return {
      newFactory: {
        factoryName: '',
        workingTime: null,
        logoUri: '',
        location: {
          id: null,
          city: '',
          country: '',
          height: 44.8125,  // Default latitude for Novi Sad
          postalCode: '',
          street: '',
          streetNumber: '',
          width: 20.4612   // Default longitude for Novi Sad
        },
        user: null
      },
      newManager: {
        username: '',
        password: '',
        firstName: '',
        lastName: '',
        gender: 'MALE',
        dateOfBirth: ''
      },
      unassignedManagers: [],
      selectedManagerId: null,
      logoUrlValid: true,
      error: ''
    };
  },
  methods: {
    fetchUnassignedManagers() {
      axios.get('http://localhost:8080/WebShopAppREST/rest/factories/unassignedManagers')
        .then(response => {
          this.unassignedManagers = response.data;
          console.log("Fetched unassigned managers:", this.unassignedManagers); // Log fetched managers
        })
        .catch(error => {
          console.error('Error fetching unassigned managers:', error);
        });
    },
    validateLogoUri() {
      const urlPattern = new RegExp(
        '^(https?:\\/\\/)?' + // validate protocol
        '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.?)+[a-z]{2,}|' + // validate domain name
        '((\\d{1,3}\\.){3}\\d{1,3}))' + // OR validate ip (v4) address
        '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + // validate port and path
        '(\\?[;&a-z\\d%_.~+=-]*)?' + // validate query string
        '(\\#[-a-z\\d_]*)?$', 'i' // validate fragment locator
      );
      this.logoUrlValid = !!this.newFactory.logoUri.match(urlPattern);
    },
    validateAndSubmit() {
      const regex = /^\s*[A-Za-z]+(\s+[A-Za-z]+)*\s*$/;
      const factory = this.newFactory;
      let errorMessage = '';

      if (!factory.factoryName) {
        errorMessage = "Factory name is required.";
      } else if (!regex.test(factory.factoryName)) {
        errorMessage = "Factory name must contain only letters.";
      } else if (factory.workingTime === null || factory.workingTime <= 0) {
        errorMessage = "Working time must be greater than zero.";
      } else if (!factory.logoUri) {
        errorMessage = "Logo URL is required.";
      } else if (!this.logoUrlValid) {
        errorMessage = "Invalid logo URI.";
      } else if (!factory.location.city || !factory.location.country || !factory.location.street || !factory.location.streetNumber || !factory.location.postalCode || factory.location.width === null || factory.location.height === null) {
        errorMessage = "All location fields are required.";
      }

      if (errorMessage) {
        this.error = errorMessage;
        return;
      } else {
        this.error = '';
        this.addFactory();
      }
    },
    addFactory() {
      const factoryData = { ...this.newFactory };

      if (this.selectedManagerId) {
        factoryData.user = { id: this.selectedManagerId };
      } else {
        factoryData.user = this.newManager;
      }

      axios.post('http://localhost:8080/WebShopAppREST/rest/factories/createWithManager', factoryData)
        .then(response => {
          alert("Factory added successfully!");
          this.resetForm();

        })
        .catch(error => {
          console.error('Error adding factory:', error);
          this.error = 'Failed to add factory. Please try again.';
        });
    },
    resetForm() {
      this.newFactory = {
        factoryName: '',
        workingTime: null,
        logoUri: '',
        location: {
          id: null,
          city: '',
          country: '',
          height: 44.8125, //novi sad
          postalCode: '',
          street: '',
          streetNumber: '',
          width: 20.4612   //novi sad
        },
        user: null
      };
      this.newManager = {
        username: '',
        password: '',
        firstName: '',
        lastName: '',
        gender: 'MALE',
        dateOfBirth: ''
      };
      this.selectedManagerId = null;
      this.logoUrlValid = true;
      this.resetMap();
    },
    resetMap() {
      if (this.$refs.mapComponent) {
        this.$refs.mapComponent.initializeMap();
      }
    },
    setLocation(location) {
      this.newFactory.location = location;
    },
    updateMapFromInput() {
      const query = `${this.newFactory.location.street} ${this.newFactory.location.streetNumber}, ${this.newFactory.location.city}, ${this.newFactory.location.country}`;
      this.getCoordinatesFromAddress(query);
    },
    async getCoordinatesFromAddress(query) {
      try {
        const response = await fetch(`https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&addressdetails=1`);
        const data = await response.json();
        if (data.length > 0) {
          const { lat, lon } = data[0];
          this.setLocation({
            ...this.newFactory.location,
            width: parseFloat(lon),
            height: parseFloat(lat)
          });
          this.$refs.mapComponent.coordinates = [lon, lat];
          this.$refs.mapComponent.setMarker([lon, lat]);
        }
      } catch (error) {
        console.error('Error fetching coordinates:', error);
      }
    }
  },
  mounted() {
    this.fetchUnassignedManagers();
    this.resetMap();
  }
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

.error {
  color: red;
}
</style>
