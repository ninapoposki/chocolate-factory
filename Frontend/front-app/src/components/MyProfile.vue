<template>
  <div class="background">
    <div class="content">
      <div class="user-info">
        <img class="user-image" :src="userImage" alt="User Image">
        <p>{{ user.id }}</p>
        <div style="padding-top: 10px;">
          <strong>User:</strong>
          <template v-if="editable">
            <input v-model="user.firstName" />
          </template>
          <template v-else>{{ user.firstName }}</template>
          <span style="margin: 0 5px;"></span>
          <template v-if="editable">
            <input v-model="user.lastName" />
          </template>
          <template v-else>{{ user.lastName }}</template>
        </div>
        <div style="padding-top: 10px;"><strong>Role:</strong> {{ user.role }}</div>
        <div style="padding-top: 10px;"><strong>Date of birth:</strong>
          <template v-if="editable">
            <input type="date" v-model="user.dateOfBirth" />
          </template>
          <template v-else>{{ formatDate(user.dateOfBirth) }}</template>
        </div>
        <div style="padding-top: 10px;"><strong>Gender:</strong> {{ user.gender }}</div>
        <div style="padding-top: 10px;"><strong>Username:</strong>
          <template v-if="editable">
            <input v-model="user.username" />
          </template>
          <template v-else>{{ user.username }}</template>
        </div>
        <div style="padding-top: 10px; padding-bottom: 10px;"><strong>Password:</strong>
          <template v-if="editable">
            <input type="password" v-model="user.password" />
          </template>
          <template v-else>{{ maskedPassword }}</template>
        </div>
        <button @click="toggleEdit">{{ editable ? 'Save' : 'Edit' }}</button>
      </div>
    </div>

    <div v-if="user.role === 'CUSTOMER'" class="purchase-content"> 
      <h2>YOUR PURCHASES</h2>
      <table>
        <thead>
          <tr>
            <th>Code</th>
            <th>Chocolates</th>
            <th>Quantity</th>
            <th>Date and Time</th>
            <th>Price</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="purchase in purchases" :key="purchase.code">
            <td>{{ purchase.code }}</td>
            <td>{{ getChocolateNames(purchase.chocolates).join(', ') }}</td>
            <td>{{  }}</td>
            <td>{{ formatDate(purchase.dateAndTime) }}</td>
            <td>{{ purchase.price.toFixed(2) }}din</td>
            <td>{{ purchase.status }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="user.role === 'MANAGER' && managerFactory" class="factory-info">
      <h2>YOUR FACTORY</h2>
      <table>
        <thead>
          <tr>
            <th class="narrow-column">Logo</th>
            <th class="narrow-column">Name</th>
            <th class="location-column">Location</th>
            <th class="narrow-column">Working Time</th>
            <th v-if="managerFactory.grade !== 0.0" class="narrow-column">Grade</th>
            <th class="narrow-column">Status</th>
            <th class="narrow-column">Comments</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="narrow-column"><img :src="managerFactory.logoUri" alt="Factory Logo" class="logo" /></td>
            <td class="narrow-column">{{ managerFactory.factoryName }}</td>
            <td class="location-column">
              <div class="location-info">
                <factory-map :initialCoordinates="[managerFactory.location.width, managerFactory.location.height]" :small="true"></factory-map>
                <div class="address">
                  <p>{{ managerFactory.location.street }} {{ managerFactory.location.streetNumber }}</p>
                  <p>{{ managerFactory.location.city }} {{ managerFactory.location.postalCode }}</p>
                  <p>{{ managerFactory.location.height }}, {{ managerFactory.location.width }}</p>
                </div>
              </div>
            </td>
            <td class="narrow-column">{{ managerFactory.workingTime }}</td>
            <td v-if="managerFactory.grade !== 0.0" class="narrow-column">{{ managerFactory.grade }}</td>
            <td class="narrow-column">{{ managerFactory.isStatus ? 'active' : 'inactive' }}</td>
            <td class="narrow-column">None</td>
          </tr>
        </tbody>
      </table>
      <button class="navigate-add-employee" @click="navigateToAddEmployee">Add Employee</button>
    </div>

    <div v-if="user.role === 'EMPLOYEE' && employeeFactory" class="factory-info">
      <h2>YOUR FACTORY</h2>
      <table>
        <thead>
          <tr>
            <th class="narrow-column">Logo</th>
            <th class="narrow-column">Name</th>
            <th class="location-column">Location</th>
            <th class="narrow-column">Working Time</th>
            <th v-if="employeeFactory.grade !== 0.0" class="narrow-column">Grade</th>
            <th class="narrow-column">Status</th>
            <th class="narrow-column">Comments</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="narrow-column"><img :src="employeeFactory.logoUri" alt="Factory Logo" class="logo" /></td>
            <td class="narrow-column">{{ employeeFactory.factoryName }}</td>
            <td class="location-column">
              <div class="location-info">
                <factory-map :initialCoordinates="[employeeFactory.location.width, employeeFactory.location.height]" :small="true"></factory-map>
                <div class="address">
                  <p>{{ employeeFactory.location.street }} {{ employeeFactory.location.streetNumber }}</p>
                  <p>{{ employeeFactory.location.city }} {{ employeeFactory.location.postalCode }}</p>
                  <p>{{ employeeFactory.location.height }}, {{ employeeFactory.location.width }}</p>
                </div>
              </div>
            </td>
            <td class="narrow-column">{{ employeeFactory.workingTime }}</td>
            <td v-if="employeeFactory.grade !== 0.0" class="narrow-column">{{ employeeFactory.grade }}</td>
            <td class="narrow-column">{{ employeeFactory.isStatus ? 'active' : 'inactive' }}</td>
            <td class="narrow-column">None</td>
          </tr>
        </tbody>
      </table>
      <button   class="navigate-add-employee" @click="ShowDetails(employeeFactory.id)">Show details</button>

    </div>
  </div>
</template>

<script setup>
import axios from 'axios';  
import { useRouter } from 'vue-router';
import FactoryMap from '@/components/FactoryMap.vue';

const router = useRouter();
const { ref, computed, onMounted } = require('vue');  
const purchases = ref([]);
const chocolates = ref([]);
const userRole = ref('');
const username = ref('');
const managerFactory = ref(null); 
const employeeFactory = ref(null); // Dodato za fabriku zaposlenog


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
  return new Date(date).toLocaleString('en-US', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};


const getChocolateNames = (chocolateIds) => {
  return chocolateIds.map(id => {
    const chocolate = chocolates.value.find(choco => choco.id === id);
    return chocolate ? chocolate.chocolateName : `Unknown Chocolate (${id})`;
  });
}

const getUserIdFromLocalStorage = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const idCookie = cookies.find(cookie => cookie.startsWith('id='));
  if (idCookie) {
    return idCookie.split('=')[1];
  }
  return null;
};


const fetchUser = () => {
  const userId = getUserIdFromLocalStorage();
  console.log(userId);
  if (userId && userId !== '-1') {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`)
      .then(response => {
        user.value = response.data;  
        if (user.value.role === 'MANAGER') {
          fetchManagerFactory(userId); 
        } else if (user.value.role === 'EMPLOYEE') {
          fetchEmployeeFactory(userId); 
        }
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

const fetchManagerFactory = (userId) => {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/factoriesForManager/${userId}`)
    .then(response => {
      if (response.data.length > 0) {
        managerFactory.value = response.data[0];
      }
    })
    .catch(error => {
      console.error('Error fetching manager factory:', error);
    });
};

const fetchEmployeeFactory = (userId) => {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/employeeFactory/${userId}`)
    .then(response => {
      if (response.data) {
        employeeFactory.value = response.data;
      }
    })
    .catch(error => {
      console.error('Error fetching employee factory:', error);
    });
};

const userImage = 'https://t4.ftcdn.net/jpg/05/50/60/55/360_F_550605549_PaTP81pjaCsrNTnfUaYlUZ8wmPpQSHY8.jpg';
//const maskedPassword = user.value.password.replace(/./g, '•');  // Primer za maskiranje lozinke
const maskedPassword = computed(() => {
  return user.value.password.replace(/./g, '•');
});

const toggleEdit = () => {
  if (editable.value) {
    const userId = getUserIdFromLocalStorage();
    axios.put(`http://localhost:8080/WebShopAppREST/rest/users/update/${userId}`, user.value)
      .then(response => {
        console.log('User updated:', response.data);
        user.value = response.data;  
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



const fetchChocolateInfo = () => {
  return axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates`)
    .then(response => {
      chocolates.value = response.data;  // Pretpostavljamo da API vraća niz objekata sa informacijama o čokoladama
    })
    .catch(error => {
      console.error('Error fetching chocolate info:', error);
    });
};
const getUserRoleFromCookie = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const userRoleCookie = cookies.find(cookie => cookie.startsWith('userRole='));
  if (userRoleCookie) {
    return userRoleCookie.split('=')[1];
  }
  return null;
};

const getUsernameFromCookie = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const usernameCookie = cookies.find(cookie => cookie.startsWith('username='));
  if (usernameCookie) {
    return usernameCookie.split('=')[1];
  }
  return null;
};
const navigateToAddEmployee = () => {
  if (managerFactory.value && managerFactory.value.id) {
    router.push({ name: 'addEmployee', params: { factoryId: managerFactory.value.id } });
  } else {
    alert("Factory ID not found");
  }
};
const ShowDetails = (id) => {
  router.push(`/details/${id}`);

};


onMounted(() => {
  fetchUser();
  fetchPurchasesByUserId(); // Fetch purchases when the component is mounted
  fetchChocolateInfo();
});
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
  background-color: #36a372;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

.navigate-add-employee {
  background-color: #36a372; /* Zelena boja */
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-top:15px;
}

.navigate-add-employee:hover {
  background-color: #36a372; /* Tamnija zelena za hover efekat */
}

.user-info {
  text-align: left; /* Tekst u user-info je poravnat levo */
  padding-left: 20px; /* Dati neki padding za razmak od leve ivice */
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

.factory-info {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  width: 100%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-left: 15px;
  margin-top: 30px;
}

.factory-info table {
  width: 100%;
  border-collapse: collapse;
}

.factory-info th,
.factory-info td {
  border: 1px solid #ddd;
  padding: 8px;
}

.factory-info th {
  background-color: #f2f2f2;
  text-align: left;
}

.narrow-column {
  width: 10%; /* Adjust this width as needed */
}

.location-column {
  width: 40%; /* Adjust this width as needed */
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

.logo {
  max-width: 100px;
  height: auto;
}
</style>

