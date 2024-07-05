<template>
  <div class="background">
    <div class="content">
      <div class="user-info">
        <img class="user-image" :src="userImage" alt="User Image">
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
        <div style="padding-top: 10px;"><strong>Password:</strong>
          <template v-if="editable">
            <input type="password" v-model="user.password" />
          </template>
          <template v-else>{{ maskedPassword }}</template>
        </div>

        <div v-if="user.role === 'CUSTOMER'" style="padding-top: 10px; padding-bottom: 10px;">
    <strong>Your points:</strong> {{ user.points }}
</div>

        <div v-if="user.role === 'CUSTOMER'" style="padding-top: 10px; padding-bottom: 10px;">
         <strong>Your medal:</strong> {{ userPointsStatus }}
         </div>
         <div v-if="user.role === 'CUSTOMER'" style="padding-top: 10px; padding-bottom: 10px;">
         <i>  {{ message }}</i>
         </div>
        <button @click="toggleEdit">{{ editable ? 'Save' : 'Edit' }}</button>
      </div>
    </div>

    <div v-if="user.role === 'CUSTOMER'" class="main-content">
      <div class="purchase-content">
        <h2>YOUR PURCHASES</h2>
        
        <!-- Polja za pretragu i sortiranje -->
        <div class="search-sort">
          <input type="text" v-model="searchFactoryName" placeholder="Search by Factory Name">
          <input type="number" v-model="minPrice" placeholder="Min Price">
          <input type="number" v-model="maxPrice" placeholder="Max Price">
          <input type="date" v-model="startDate" placeholder="Start Date">
          <input type="date" v-model="endDate" placeholder="End Date">
          <div class="sort-order">
            <select v-model="sortBy">
              <option disabled value="">Sort By</option>
              <option value="factoryname">Factory Name</option>
              <option value="price">Price</option>
              <option value="date">Date</option>
            </select>
            <select v-model="ascending">
              <option disabled value="">Order</option>
              <option value="true">Ascending</option>
              <option value="false">Descending</option>
            </select>
          </div>
          <div class="action-buttons">
            <button @click="searchAndSortPurchasesByUser">Search and Sort</button>
            <button @click="resetSearchAndSort">Reset</button>
          </div>
        </div>
        
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
              <td v-if="purchase.chocolateNames">
                <div v-for="chocolateName in purchase.chocolateNames" :key="chocolateName">
                  {{ chocolateName }}
                </div>
              </td>
              <td v-else>N/A</td>
              <td> <div v-for="(quantity, index) in purchase.quantities" :key="index">
    {{ quantity }}<br>
  </div></td>
              <td>{{ formatDate(purchase.dateAndTime) }}</td>
              <td>{{ purchase.price.toFixed(2) }}din</td>
              <td>{{ purchase.status }}</td>
              <td><button v-if="purchase.status === 'PROCESSING'" @click="cancelOrder(purchase.id)">Cancel Order</button>
           
           <button v-if="purchase.status === 'ACCEPTED'" @click="GradeFactory(purchase.id)">Grade Factory</button>
           </td>
            </tr>
          </tbody>
        </table>
      </div>

<!--       
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
            <td>
            <div v-for="(name, index) in purchase.chocolateNames" :key="index">
              {{ name }}<br>
            </div>
          </td>
            <td> <div v-for="(quantity, index) in purchase.quantities" :key="index">
    {{ quantity }}<br>
  </div></td>
            <td>{{ formatDate(purchase.dateAndTime) }}</td>
            <td>{{ purchase.price.toFixed(2) }}din</td>
            <td>{{ purchase.status }}</td>
            <td><button v-if="purchase.status === 'PROCESSING'" @click="cancelOrder(purchase.id)">Cancel Order</button>
           
            <button v-if="purchase.status === 'ACCEPTED'" @click="GradeFactory(purchase.id)">Grade Factory</button>
            </td>
          </tr>
        </tbody>
      </table> -->

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
    
    <h2>FACTORY PURCHASES</h2>
    
    <!-- Polja za pretragu i sortiranje -->
    <div class="search-sort">
      <input type="number" v-model="minPrice" placeholder="Min Price">
      <input type="number" v-model="maxPrice" placeholder="Max Price">
      <input type="date" v-model="startDate" placeholder="Start Date">
      <input type="date" v-model="endDate" placeholder="End Date">
      <div class="sort-order">
        <select v-model="sortBy">
          <option disabled value="">Sort By</option>
          <option value="price">Price</option>
          <option value="date">Date</option>
        </select>
        <select v-model="ascending">
          <option disabled value="">Order</option>
          <option value="true">Ascending</option>
          <option value="false">Descending</option>
        </select>
      </div>
      <div class="action-buttons">
        <button @click="searchAndSortFactoryPurchases">Search and Sort</button>
        <button @click="resetSearchAndSort">Reset</button>
      </div>
    </div>
    
    <table>
      <thead>
        <tr>
          <th>Code</th>
          <th>Chocolates</th>
          <th>Quantity</th>
          <th>Date and Time</th>
          <th>Price</th>
          <th>Status</th>
          <th>Customer</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="purchase in factoryPurchases" :key="purchase.id">
          <td>{{ purchase.code }}</td>
          <td v-if="purchase.chocolateNames">
            <div v-for="chocolateName in purchase.chocolateNames" :key="chocolateName">
              {{ chocolateName }}
            </div>
          </td>
          <td v-else>N/A</td>
          <td> <div v-for="(quantity, index) in purchase.chocolateQuantities" :key="index">
    {{ quantity }}<br>
  </div></td>
          <td>{{ formatDate(purchase.dateAndTime) }}</td>
          <td>{{ purchase.price.toFixed(2) }}din</td>
          <td>{{ purchase.status }}</td>
          <td>{{ purchase.customerFirstName }}  {{ purchase.customerLastName }}</td>
          <td>
            <button v-if="purchase.status === 'PROCESSING'" @click="toggleChangeRequest(purchase)">Change Request</button>
            <div v-if="purchase.showChangeRequest">
              <select v-model="purchase.newStatus">
                <option value="ACCEPTED">Accept</option>
                <option value="DECLINED">Decline</option>
              </select>
              <div v-if="purchase.newStatus === 'DECLINED'">
                <input type="text" v-model="purchase.commentText" placeholder="Enter comment" />
              </div>
              <button @click="submitChangeRequest(purchase)">Submit</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
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
    <button class="navigate-add-employee" @click="ShowDetails(employeeFactory.id)">Show details</button>
  </div>


    <div v-if="user.role === 'ADMINISTRATOR'" class="main-content">
      <div class="admin-content">
        <h2>REGISTERED USERS</h2>
        <div class="actions">
            <button @click="showSuspiciousUsers">See Suspicious Users</button>
          </div>
        <div class="search-sort">
          <input type="text" v-model="searchTerm" placeholder="Search">
          <select v-model="filterRole">
            <option value="">All Roles</option>
            <option value="MANAGER">Manager</option>
            <option value="EMPLOYEE">Employee</option>
            <option value="CUSTOMER">Customer</option>
          </select>
          <select v-model="filterType">
            <option value="">All Types</option>
            <option value="gold">Gold</option>
            <option value="silver">Silver</option>
            <option value="bronze">Bronze</option>
          </select>
          <select v-model="sortBy">
            <option disabled value="">Sort By</option>
            <option value="firstname">First Name</option>
            <option value="lastname">Last Name</option>
            <option value="username">Username</option>
            <option value="points">Points</option>
          </select>
          <select v-model="ascending">
            <option value="true">Ascending</option>
            <option value="false">Descending</option>
          </select>
          <button @click="searchSortFilterUsers">Search and Sort</button>
          <button @click="resetFilters">Reset</button>
        </div>
        <table>
          <thead>
            <tr>
              <th>Username</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Gender</th>
              <th>Date of Birth</th>
              <th>Role</th>
              <th>Points</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="adminUser in adminUsers" :key="adminUser.username">
              <td>{{ adminUser.username }}</td>
              <td>{{ adminUser.firstName }}</td>
              <td>{{ adminUser.lastName }}</td>
              <td>{{ adminUser.gender }}</td>
              <td>{{ formatDate(adminUser.dateOfBirth) }}</td>
              <td>{{ adminUser.role }}</td>
              <td>{{ adminUser.points }}</td>
             
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  <div v-if="showModal" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h2>Suspicious Users</h2>
        <table>
          <thead>
            <tr>
              <th>Username</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="suspiciousUser in suspiciousUsers" :key="suspiciousUser.username">
              <td>{{ suspiciousUser.username }}</td>
              <td>{{ suspiciousUser.firstName }}</td>
              <td>{{ suspiciousUser.lastName }}</td>
              <td>
                <button v-if="suspiciousUser.activity === 'ACTIVE'" @click="confirmBlockUser(suspiciousUser.id)">Block</button>
                <span v-else class="blocked-user">Blocked user</span>
              </td>
            </tr>
          </tbody>
        </table>
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
const adminUsers = ref([]);
const suspiciousUsers = ref([]);
const showModal = ref(false);

const user = ref({
  id: null,
  firstName: '',
  lastName: '',
  role: '',
  dateOfBirth: '',
  username: '',
  password: '',
  points: ''
});

const fetchAdminUsers = () => {
  axios.get('http://localhost:8080/WebShopAppREST/rest/users/withoutAdm')
    .then(response => {
      adminUsers.value = response.data;
    })
    .catch(error => {
      console.error('Error fetching admin users:', error);
    });
};

const showSuspiciousUsers = () => {
  axios.get('http://localhost:8080/WebShopAppREST/rest/purchases/suspicious')
    .then(response => {
      suspiciousUsers.value = response.data;
      showModal.value = true;
    })
    .catch(error => {
      console.error('Error fetching suspicious users:', error);
    });
};

const closeModal = () => {
  showModal.value = false;
};

const confirmBlockUser = (userId) => {
  if (confirm("Are you sure you want to block this user?")) {
    blockUser(userId);
  }
};

const blockUser = (userId) => {
  axios.put(`http://localhost:8080/WebShopAppREST/rest/users/deacUser/${userId}`)
    .then(response => {
      alert("User has been blocked.");
      closeModal();
      // Dodajte kratko kašnjenje pre osvežavanja liste kako bi modal bio sigurno zatvoren
      setTimeout(() => {
        fetchAdminUsers(); // Refresh the user list
      }, 500);
    })
    .catch(error => {
      console.error('Error blocking user:', error);
    });
};

onMounted(() => {
  fetchAdminUsers();
});

// Vaš postojeći kod
const userRole = ref('');
const username = ref('');
const managerFactory = ref(null); 
const employeeFactory = ref(null);
const searchFactoryName = ref('');
const minPrice = ref(null);
const maxPrice = ref(null);
const startDate = ref('');
const endDate = ref('');
const sortBy = ref(''); 
const ascending = ref(true);
const searchTerm = ref('');
const filterRole = ref('');
const filterType = ref('');
const editable = ref(false);
const factoryPurchases = ref([]);

const userPointsStatus = computed(() => {
  const points = user.value.points;
  if (points < 3000) {
    return 'BRONZE';
  } else if (points >= 3000 && points < 5000) {
    return 'SILVER';
  } else {
    return 'GOLDEN';
  }
});

const message = computed(() => {
  const points = user.value.points;
  if (points < 3000) {
    return 'Make more purchases to get 3000 points and get -3% sale';
  } else if (points >= 3000 && points < 5000) {
    return 'Currently you have -3% sale.Make more purchases to get 5000 points and get -5% sale';
  } else {
    return 'You are our golden user and you have -5% sale for any item';
  }
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


function GradeFactory(id) {
  router.push(`/grade/${id}`);
}


const getChocolateQuantitiesFromCartIds = async (cartIds) => {
  const quantities = [];

  for (const id of cartIds) {
    try {
      const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/${id}`);
      const chocolates = response.data.chocolates;
      const quantity = Object.values(chocolates)[0]; // Preuzimanje vrednosti iz mape chocolates
      quantities.push(quantity);
    } catch (error) {
      console.error(`Error fetching quantity for shopping cart ID ${id}:`, error);
    }
  }

  return quantities;
};




const getChocolateNamesFromCartIds = async (cartIds) => {
  const names = [];


  for (const cartId of cartIds) {
    try {
      const cartResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/${cartId}`);
      const cartData = cartResponse.data;

      for (const chocolateId of Object.keys(cartData.chocolates)) {
        try {
          const chocolateResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/choco/${chocolateId}`);
          names.push(chocolateResponse.data.chocolateName);
        } catch (error) {
          console.error(`Error fetching chocolate with ID: ${chocolateId}`, error);
          names.push(`Unknown Chocolate (${chocolateId})`);
        }
      }
    } catch (error) {
      console.error(`Error fetching shopping cart with ID: ${cartId}`, error);
    }
  }

  return names;
};

const searchSortFilterUsers = () => {
  const params = {
    searchTerm: searchTerm.value,
    sortBy: sortBy.value,
    ascending: ascending.value,
    role: filterRole.value,
    type: filterType.value
  };

  axios.get('http://localhost:8080/WebShopAppREST/rest/users/searchSortFilter', { params })
    .then(response => {
      adminUsers.value = response.data;
    })
    .catch(error => {
      console.error('Error fetching filtered and sorted users:', error);
    });
};

const resetFilters = () => {
  searchTerm.value = '';
  filterRole.value = '';
  filterType.value = '';
  sortBy.value = '';
  ascending.value = true;
  fetchAdminUsers(); // Reset to initial data
};

const fetchChocolateNames = async (chocolateIds) => {
  const chocolateNames = await Promise.all(chocolateIds.map(async (id) => {
    try {
      const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/choco/${id}`);
      return response.data.chocolateName;  // Pretpostavljam da odgovor sadrži polje 'name'
    } catch (error) {
      console.error('Error fetching chocolate name:', error);
      return `Unknown Chocolate (${id})`;
    }
  }));
  return chocolateNames;
};

const searchAndSortFactoryPurchases = async () => {
  const factoryId = managerFactory.value.id;
  const params = {};

  if (minPrice.value !== null) params.minPrice = minPrice.value;
  if (maxPrice.value !== null) params.maxPrice = maxPrice.value;
  if (startDate.value) params.startDate = new Date(startDate.value).toISOString();
  if (endDate.value) params.endDate = new Date(endDate.value).toISOString();
  if (sortBy.value) params.sortBy = sortBy.value;
  params.ascending = ascending.value;

  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/search/${factoryId}`, { params });
    const purchaseData = response.data;
    const updatedPurchases = await Promise.all(purchaseData.map(async (purchase) => {
      const names = await getChocolateNamesFromCartIds(purchase.chocolates);
      return { ...purchase, chocolateNames: names };
    }));
    factoryPurchases.value = updatedPurchases;
  } catch (error) {
    console.error('Error fetching factory purchases:', error);
  }
};

const searchAndSortPurchasesByUser = async () => {
  const userId = user.value.id;
  const params = {};

  if (searchFactoryName.value) params.factoryName = searchFactoryName.value;
  if (minPrice.value !== null) params.minPrice = minPrice.value;
  if (maxPrice.value !== null) params.maxPrice = maxPrice.value;
  if (startDate.value) params.startDate = new Date(startDate.value).toISOString();
  if (endDate.value) params.endDate = new Date(endDate.value).toISOString();
  if (sortBy.value) params.sortBy = sortBy.value;
  params.ascending = ascending.value;

  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/searchTwo/${userId}`, { params });
    const purchaseData = response.data;
    const updatedPurchases = await Promise.all(purchaseData.map(async (purchase) => {
      const names = await getChocolateNamesFromCartIds(purchase.chocolates);
      return { ...purchase, chocolateNames: names };
    }));
    purchases.value = updatedPurchases;
  } catch (error) {
    console.error('Error fetching user purchases:', error);
  }
};

const resetSearchAndSort = () => {
  searchFactoryName.value = '';
  minPrice.value = null;
  maxPrice.value = null;
  startDate.value = '';
  endDate.value = '';
  sortBy.value = '';
  ascending.value = true;

  if (user.value.role === 'CUSTOMER') {
    fetchPurchasesByUserId(); //user.value.id
  } else if (user.value.role === 'MANAGER') {
    fetchFactoryPurchases(managerFactory.value.id);
  }
};

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
  if (userId && userId !== '-1') {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`)
      .then(response => {
        user.value = response.data;  
        if (user.value.role === 'MANAGER') {
          fetchManagerFactory(userId); 
        } else if (user.value.role === 'EMPLOYEE') {
          fetchEmployeeFactory(userId); 
        } else if (user.value.role === 'CUSTOMER') {
          fetchPurchasesByUserId(); //userId
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
        fetchFactoryPurchases(managerFactory.value.id);
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


const fetchFactoryPurchases = (factoryId) => {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/factory/${factoryId}`)
    .then(async (response) => {
      const purchaseData = response.data;
      const updatedPurchases = await Promise.all(purchaseData.map(async (purchase) => {
        const names = await getChocolateNamesFromCartIds(purchase.chocolates);
        const quantities = await getChocolateQuantitiesFromCartIds(purchase.chocolates); // Poziv funkcije za količine
        return { 
          ...purchase, 
          chocolateNames: names, 
          chocolateQuantities: quantities // Dodavanje količina u kupovinu
        };
      }));
      factoryPurchases.value = updatedPurchases;
      console.log('Updated factory purchases:', updatedPurchases); // Dodato logovanje
    })
    .catch(error => {
      console.error('Error fetching factory purchases:', error);
    });
};


/*

const fetchFactoryPurchases = (factoryId) => {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/factory/${factoryId}`)
    .then(async (response) => {
      const purchaseData = response.data;
      const updatedPurchases = await Promise.all(purchaseData.map(async (purchase) => {
        const names = await getChocolateNamesFromCartIds(purchase.chocolates);
        return { ...purchase, chocolateNames: names };
      }));
      factoryPurchases.value = updatedPurchases;
      console.log('Updated factory purchases:', updatedPurchases); // Dodato logovanje
    })
    .catch(error => {
      console.error('Error fetching factory purchases:', error);
    });
};
*/
const userImage = 'https://t4.ftcdn.net/jpg/05/50/60/55/360_F_550605549_PaTP81pjaCsrNTnfUaYlUZ8wmPpQSHY8.jpg';

const maskedPassword = computed(() => {
  return user.value.password ? user.value.password.replace(/./g, '•') : '';
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

const fetchShoppingCartById = async (cartIds) => {
  try {
    for (const cartId of cartIds) {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/${cartId}`);
    if (response.data && response.data.items) {
      const quantities = response.data.items.map(item => item.quantity);
      return quantities; 
    }
  }
  } catch (error) {
    console.error(`Error fetching shopping cart with ID: ${cartId}`, error);
    return null;
  }
};
/* DAJNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
const fetchPurchasesByUserId = () => {
  const userId = getUserIdFromLocalStorage();
  if (userId && userId !== '-1') {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/user/${userId}`)
      .then(response => {
        const purchaseData = response.data;
        Promise.all(purchaseData.map(purchase => {
          return getChocolateNamesFromCartIds(purchase.chocolates).then(names => {
            purchase.chocolateNames = names;
            return purchase;
          });




        })).then(updatedPurchases => {
          purchases.value = updatedPurchases;
        });
      })
      .catch(error => {
        console.error('Error fetching purchases:', error);
      });
  }
};
*/
const fetchPurchasesByUserId = () => {
  const userId = getUserIdFromLocalStorage();
  if (userId && userId !== '-1') {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/user/${userId}`)
      .then(response => {
        const purchaseData = response.data;
        Promise.all(purchaseData.map(purchase => {
          return Promise.all([
            getChocolateNamesFromCartIds(purchase.chocolates),
            getChocolateQuantitiesFromCartIds(purchase.chocolates)
          ]).then(([names, quantities]) => {
            purchase.chocolateNames = names;
            purchase.quantities = quantities;
            return purchase;
          });
        })).then(updatedPurchases => {
          purchases.value = updatedPurchases;
        });
      })
      .catch(error => {
        console.error('Error fetching purchases:', error);
      });
  }
};

const fetchChocolateInfo = () => {
  return axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates`)
    .then(response => {
      chocolates.value = response.data;
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

// New methods and variables
const toggleChangeRequest = (purchase) => {
  purchase.showChangeRequest = !purchase.showChangeRequest;
  if (!purchase.showChangeRequest) {
    purchase.newStatus = null;
    purchase.commentText = '';
  }
};

const submitChangeRequest = (purchase) => {
  const requestData = {
    status: purchase.newStatus,
  };

  if(purchase.newStatus === 'ACCEPTED'){
      
      fetchAndUpdateChocolates(purchase.chocolates);
  }

  if (purchase.newStatus === 'DECLINED') {
    requestData.commentText = purchase.commentText;
    requestData.userId = getUserIdFromLocalStorage();
  }

  axios.put(`http://localhost:8080/WebShopAppREST/rest/purchases/status/${purchase.id}`, requestData)
    .then(response => {
      console.log('Purchase status updated:', response.data);
      purchase.status = response.data.status;
      purchase.showChangeRequest = false; // Close the change request form
    })
    .catch(error => {
      console.error('Error updating purchase status:', error);
    });
};

const cancelOrder = async (purchaseId) => {
  try {
    const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/purchases/cancel/${purchaseId}`);
    console.log('Order cancelled successfully:', response.data);
    
    // Ažurirajte status kupovine u lokalnoj listi
    const purchase = purchases.value.find(p => p.id === purchaseId);
    if (purchase) {
      purchase.status = 'CANCELLED';
    }

    // Preuzmite korisnika za ažuriranje
    const userResponse = await fetchUserForUpdate(purchase.customerId);
    console.log('Fetched user:', user);

    if (userResponse) {
      // Ažuriranje bodova korisnika
      userResponse.points = userResponse.points - (purchase.price / 1000 * 133 * 4);
      if(userResponse.points<0){
        userResponse.points = 0;
      }
      const updatedUser = await updateUser(purchase.customerId, userResponse);
      console.log('Updated user:', updatedUser);

      user.value.points = updatedUser.points;
    }
  } catch (error) {
    console.error('Error cancelling order:', error);
  }
};

async function fetchAndUpdateChocolates(cartIds) {
  for (const cartId of cartIds) {
    console.log('dajana lista idjeva ', cartIds.value)
    try {
      const cartResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/${cartId}`);
      const cartData = cartResponse.data;

      for (const [chocolateId, quantity] of Object.entries(cartData.chocolates)) {
        try {
          const chocolateResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/choco/${chocolateId}`);
          const chocolateData = chocolateResponse.data;
          const newQuantity = chocolateData.numberOfChocolates - quantity;
         console.log('new iaOnStock', newQuantity);
         let newIsOnStock = true;
          if (newQuantity === 0) {
            newIsOnStock = false;
          }
          // Create a new chocolate object with quantity
          const chocolateWithQuantity = {
            ...chocolateData,
             isOnStock: newIsOnStock,
            numberOfChocolates: newQuantity
          };

          // Send a PUT request to update the chocolate
          await axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${chocolateId}`, chocolateWithQuantity);
        } catch (error) {
          console.error(`Error fetching or updating chocolate with ID: ${chocolateId}`, error);
        }
      }
    } catch (error) {
      console.error(`Error fetching shopping cart with ID: ${cartId}`, error);
    }
  }
}





async function fetchUserForUpdate(userId) {
  try {
    console.log('Fetching user with ID:', userId);
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`);
    console.log('Fetched user data:', response.data);
    return response.data;
  } catch (error) {
    console.error(`Error fetching user with ID: ${userId}`, error);
    return null;
  }
}

async function updateUser(userId, updatedUser) {
  try {
    const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/users/update/${userId}`, updatedUser);
    return response.data;
  } catch (error) {
    console.error(`Error updating user with ID: ${userId}`, error);
    return null;
  }
}

onMounted(() => {
  fetchUser();
  fetchPurchasesByUserId(); // Fetch purchases when the component is mounted
  fetchChocolateInfo();
  fetchAdminUsers();
});
</script>



<style scoped>
.background {
  position: relative;
  width: 100%;
  min-height: 100vh; /* Ensure the background covers the full height */
  display: flex;
  justify-content: center;
  align-items: flex-start;
  overflow: hidden; 
}

.background::before {
  content: "";
  background-image: url('https://cdn2.hauteliving.com/wp-content/uploads/2014/08/macarons.gif');
  background-size: cover;
  background-position: center;
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
  background-color: #36a372;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 15px;
}

.navigate-add-employee:hover {
  background-color: #2b8659;
}

.user-info {
  text-align: left;
  padding-left: 20px;
}

.purchase-content {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  width: 100%;
  max-width: 900px; /* Ograničena širina */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  margin-right: auto;
  margin-top: 30px;
}

.search-sort {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
  width: 100%;
  max-width: 800px; /* Ograničena širina */
  margin-left: auto;
  margin-right: auto;
}

.search-sort input,
.search-sort select {
  padding: 5px;
  border-radius: 5px;
  border: 1px solid #ddd;
  flex: 1 1 120px; /* Prilagođavanje širine */
}

.search-sort .sort-order select {
  margin-right: 10px;
}

.search-sort .action-buttons button {
  margin-right: 10px;
}

.search-sort button {
  padding: 5px 10px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  background-color: #36a372;
  color: white;
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
  max-width: 900px; /* Ograničena širina */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  margin-right: auto;
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
  width: 10%;
}

.location-column {
  width: 40%;
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

.factory-info select,
.factory-info input[type="text"] {
  margin-top: 5px;
  padding: 5px;
  width: calc(100% - 10px);
  box-sizing: border-box;
}

.factory-info button {
  margin-top: 5px;
  padding: 5px;
  background-color: #36a372;
  color: white;
  border: none;
  cursor: pointer;
}

.factory-info button:hover {
  background-color: #2b8659;
}

.admin-content {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  width: 100%;
  max-width: 900px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  margin-right: auto;
  margin-top: 30px;
}

.admin-content table {
  width: 100%;
  border-collapse: collapse;
}

.admin-content th,
.admin-content td {
  border: 1px solid #ddd;
  padding: 8px;
}

.admin-content th {
  background-color: #f2f2f2;
  text-align: left;
}

.modal {
  display: flex;
  align-items: center;
  justify-content: center;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
  background-color: #fefefe;
  margin: auto;
  padding: 20px;
  border: 1px solid #888;
  width: 80%;
  border-radius: 10px;
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
.blocked-user{
  color:pink;
}
</style>

