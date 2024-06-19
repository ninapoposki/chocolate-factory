<template>
  <div class="background">
    <div class="content">
      <div class="user-info">
        <img class="user-image" :src="userImage" alt="User Image">
        <p>{{ user.id }}</p>
        <div style="padding-top: 10px;"><strong>User:</strong> <template v-if="editable"><input v-model="user.firstName" /></template><template v-else>{{ user.firstName }}</template>
          <span style="margin: 0 5px;"></span>
          <template v-if="editable"><input v-model="user.lastName" /></template><template v-else>{{ user.lastName }}</template>
        </div>
        <div style="padding-top: 10px;"><strong>Role:</strong> {{ user.role }}</div>
        <div style="padding-top: 10px;"><strong>Date of birth:</strong> <template v-if="editable"><input type="date" v-model="user.dateOfBirth" /></template><template v-else>{{ formatDate(user.dateOfBirth) }}</template></div>
        <div style="padding-top: 10px;"><strong>Gender:</strong> {{ user.gender }}</div>
        <div style="padding-top: 10px;"><strong>Username:</strong> <template v-if="editable"><input v-model="user.username" /></template><template v-else>{{ user.username }}</template></div>
        <div style="padding-top: 10px; padding-bottom: 10px;"><strong>Password:</strong> <template v-if="editable"><input type="password" v-model="user.password" /></template><template v-else>{{ maskedPassword }}</template></div>
        <button @click="toggleEdit">{{ editable ? 'Save' : 'Edit' }}</button>
      </div>
    </div>

    <div class="purchase-content"> 
      <h2>YOUR PURCHASES</h2>
      <table>
        <thead>
          <tr>
            <th>Code</th>
            <th>Chocolates</th>
            <th>Factory ID</th>
            <th>Date and Time</th>
            <th>Price</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="purchase in purchases" :key="purchase.code">
            <td>{{ purchase.code }}</td>
            <td>{{ purchase.chocolates.join(', ') }}</td>
            <td>{{ purchase.factoryId }}</td>
            <td>{{ formatDate(purchase.dateAndTime) }}</td>
            <td>${{ purchase.price.toFixed(2) }}</td>
            <td>{{ purchase.status }}</td>
          </tr>
        </tbody>
      </table>
    </div>

  </div>
</template>

<script setup>
import axios from 'axios';  // Pravilan import Axios-a
import { useRouter } from 'vue-router';
const router = useRouter();
const { ref, computed,onMounted } = require('vue');  // Vue 3 komponente koriste `require` za uvoz
const purchases = ref([]);

const editable = ref(false);
const user = ref({
  id: null,
  firstName: '',
  lastName: '',
  role: '',
  dateOfBirth: '',
  username: '',
  password: ''
});

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US');
};

const getUserIdFromLocalStorage = () => {
 return localStorage.getItem('userId');
 
};

const fetchUser = () => {
  const userId = getUserIdFromLocalStorage();
  console.log(userId);
  if (userId && userId !== '-1') {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`)
      .then(response => {
        user.value = response.data;  // Postavljanje podataka korisnika u user objekt
      })
      .catch(error => {
        console.error('Error fetching user:', error);
      });
  } else {
    console.error('No userId found in LocalStorage');
    alert('You are not logged in! Redirecting to log in page');
    router.push('/about');
  }
};

const userImage = 'https://t4.ftcdn.net/jpg/05/50/60/55/360_F_550605549_PaTP81pjaCsrNTnfUaYlUZ8wmPpQSHY8.jpg';
//const maskedPassword = user.value.password.replace(/./g, '•');  // Primer za maskiranje lozinke
const maskedPassword = computed(() => {
  return user.value.password.replace(/./g, '•');
});


const toggleEdit = () => {
  if (editable.value) {
    // Implementacija logike za čuvanje promena (npr. slanje na server ili u Vuex store)
    const userId = getUserIdFromLocalStorage();
    axios.put(`http://localhost:8080/WebShopAppREST/rest/users/update/${userId}`, user.value)
      .then(response => {
        console.log('User updated:', response.data);
        user.value = response.data;  // Ažuriranje korisnika sa odgovorom sa servera
      })
      .catch(error => {
        console.error('Error updating user:', error);
      });
  }
  editable.value = !editable.value;
};

const fetchPurchasesByUserId = () => {
  const userId = getUserIdFromLocalStorage();
  if (userId && userId !== '-1') {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/user/${userId}`)
      .then(response => {
        purchases.value = response.data;
      })
      .catch(error => {
        console.error('Error fetching purchases:', error);
      });
  }
};
onMounted(() => {
  fetchUser();
  fetchPurchasesByUserId(); // Fetch purchases when the component is mounted
});
//onMounted(fetchUser);  // Poziv funkcije za dohvat korisnika iz LocalStorage-a pri montiranju komponente
</script>

<style scoped>
.background {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: flex-start; 
  align-items: flex-start;
}

.background::before {
  content: "";
  background-image: url('https://cdn2.hauteliving.com/wp-content/uploads/2014/08/macarons.gif');
  background-size: cover;
  opacity: 0.5; /* Prilagodite ovo prema vašim potrebama */
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -1; /* Postavljanje da bude ispod sadržaja */
}

.content {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  width: 500px; 
  height: 530px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-left: 15px;
  margin-top: 30px;
}

.user-info {
  text-align: center;
}

.user-image {
  width: 250px;
  height: auto;
  border-radius: 50%; 
  margin-bottom: 10px;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}
.user-info {
  text-align: left; /* Izmena: Tekst u user-info je poravnat levo */
  padding-left: 20px; /* Dodato: Dati neki padding za razmak od leve ivice */
}


.purchase-content {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  width: 100%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-left: 15px;
  margin-top: 30px;
}

.purchase-info {
  margin-bottom: 20px;
}

.purchase-content table {
  width: 100%;
  border-collapse: collapse;
}

.purchase-content th,
.purchase-content td {
  border: 1px solid #ddd;
  padding: 8px;
}

.purchase-content th {
  background-color: #f2f2f2;
  text-align: left;
}

</style>
