<template>
  <div>
    <h2>Add New Factory</h2>
    <form @submit.prevent="validateAndSubmit">
      <div>
        <label for="factoryName">Factory name:</label>
        <input type="text" v-model="newFactory.factoryName" required />
      </div>
      <div>
        <label for="workingTime">Working time:</label>
        <input type="number" v-model="newFactory.workingTime" required />
      </div>
      <div>
        <label for="logoUri">Logo URL:</label>
        <input type="text" v-model="newFactory.logoUri" @input="validateLogoUri" required />
        <div v-if="logoUrlValid">
          <img :src="newFactory.logoUri" alt="Preview logo" class="logo-preview" />
        </div>
        <div v-else-if="newFactory.logoUri">
          <p class="error">Invalid logo URI</p>
        </div>
      </div>
      <div>
        <label for="locationCity">City:</label>
        <input type="text" v-model="newFactory.location.city" @input="updateMapFromInput" required />
      </div>
      <div>
        <label for="locationCountry">Country:</label>
        <input type="text" v-model="newFactory.location.country" @input="updateMapFromInput" required />
      </div>
      <div>
        <label for="locationStreet">Street:</label>
        <input type="text" v-model="newFactory.location.street" @input="updateMapFromInput" required />
      </div>
      <div>
        <label for="locationStreetNumber">Street Number:</label>
        <input type="text" v-model="newFactory.location.streetNumber" @input="updateMapFromInput" required />
      </div>
      <div>
        <label for="locationPostalCode">Postal Code:</label>
        <input type="text" v-model="newFactory.location.postalCode" @input="updateMapFromInput" required />
      </div>
      <map-component ref="mapComponent" :initialCoordinates="[newFactory.location.width || 20.4612, newFactory.location.height || 44.8125]" @locationSelected="setLocation"></map-component>
      <p>{{ error }}</p>
      <button type="submit">Add factory</button>
    </form>
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
        }
      },
      logoUrlValid: true,
      error: '',
    };
  },
  methods: {
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
      axios.post('http://localhost:8080/WebShopAppREST/rest/factories', this.newFactory)
        .then(response => {
          alert("Factory added successfully!");
          // Reset the form
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
          height: 44.8125,  // Default latitude for Novi Sad
          postalCode: '',
          street: '',
          streetNumber: '',
          width: 20.4612   // Default longitude for Novi Sad
        }
      };
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
        }
      } catch (error) {
        console.error('Error fetching coordinates:', error);
      }
    }
  },
  mounted() {
    this.resetMap();
  }
};
</script>

<style scoped>
.logo-preview {
  max-width: 100px;
  max-height: 100px;
}
.error {
  color: red;
}
</style>
