<template>
  <div class="container">
      <h1 class="title">Factory Details</h1>
      <div v-if="factory">
          <div class="tables">
              <table class="table-container">
                  <thead>
                      <tr>
                          <th>Logo</th>
                          <th>Name</th>
                          <th>Location</th>
                          <th>Working Time</th>
                          <th v-if="factory.grade !== 0.0">Grade</th>
                          <th>Status</th>
                          <th>Comments</th>
                      </tr>
                  </thead>
                  <tbody>
                      <tr>
                          <td><img :src="factory.logoUri" alt="Factory Logo" class="logo" /></td>
                          <td>{{ factory.factoryName }}</td>
                          <td>{{ factory.location.street }} {{ factory.location.streetNumber }}, {{ factory.location.city }} {{ factory.location.postalCode }}</td>
                          <td>{{ factory.workingTime }}</td>
                          <td v-if="factory.grade !== 0.0">{{ factory.grade }}</td>
                          <td>{{ factory.isStatus ? 'active' : 'inactive' }}</td>
                          <td>None</td>
                      </tr>
                  </tbody>
              </table>
          </div>
      </div>
      
      <h1 class="title">Chocolates</h1>
      <div v-if="chocolates.length">
          <div class="tables">
              <table class="table-container">
                  <thead>
                      <tr>
                          <th>ID</th>
                          <th>Name</th>
                          <th>Price</th>
                          <th>Variety</th>
                          <th>Type</th>
                          <th>Weight</th>
                          <th>Description</th>
                          <th>Image</th>
                          <th>Actions</th>
                      </tr>
                  </thead>
                  <tbody>
                      <tr v-for="chocolate in chocolates" :key="chocolate.id">
                          <td>{{ chocolate.id }}</td>
                          <td>{{ chocolate.chocolateName }}</td>
                          <td>{{ chocolate.price }}</td>
                          <td>{{ chocolate.variety }}</td>
                          <td>{{ chocolate.type }}</td>
                          <td>{{ chocolate.weight }}</td>
                          <td>{{ chocolate.description }}</td>
                          <td><img :src="chocolate.imageUri" alt="Chocolate Image" class="logo" /></td>
                          <td class="buttons">

                            <!-- ne nzam da li admin sme da update cokolade radi ? proveri-menadzser sme da doda cokoladu da je brise i apdejtuje  -->
                              <button v-if="userRole === 'MANAGER'" @click="updateChocolate(chocolate)">Change chocolate</button>
                              <button v-if="userRole === 'EMPLOYEE'" @click="changeQuantity(chocolate)">Change Quantity</button>
                              <button v-if="userRole === 'MANAGER'" class="small-button" @click="deleteChocolate(chocolate.id)">Delete chocolate</button>
                              <button class="small-button" v-if="userRole === 'CUSTOMER'" @click="showQuantityDialog(chocolate)">Add to Cart</button>

                          </td>

                    
                      
                      <tr v-if="selectedChocolateId === chocolate.id" :key="chocolate.id + '-modal'">
  <td colspan="9">
    <div title="Enter Quantity" centered>
      <form @submit.prevent="addToCart(chocolate)">
        <div class="form-group">
          <label for="quantity">Quantity:</label>
          <input type="number" id="quantity" v-model="quantity" min="1" required class="form-control">
          <div v-if="quantityExceedsStockError" class="error-message">
              We are sorry! We do not have that amount of chocolates in stock!
            </div>
        </div>
        <button type="submit">Add to Cart</button>
      </form>
    </div>
  </td>
</tr>
</tr>
                  </tbody>
              </table>
          </div>
      </div>
      <div v-else>
          <p>No chocolates found for this factory.</p>
      </div>
      
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import Swal from 'sweetalert2';

const router = useRouter();
const route = useRoute();
const factory = ref(null);
const chocolates = ref([]);
const userRole = ref('');

const showQuantityModal = ref(false);
const selectedChocolate = ref(null);
const selectedChocolateId = ref(null); 
let quantityExceedsStockError = ref(false);

let quantity = ref(1); // Initial quantity value

const getUserRoleFromCookie = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const userRoleCookie = cookies.find(cookie => cookie.startsWith('userRole='));
  if (userRoleCookie) {
    return userRoleCookie.split('=')[1];
  }
  return null;
};
const getUserIdFromLocalStorage = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const idCookie = cookies.find(cookie => cookie.startsWith('id='));
  if (idCookie) {
    return idCookie.split('=')[1];
  }
  return null;
};

onMounted(() => {
  
  const userId = getUserIdFromLocalStorage();
  console.log(userId);
  
  //trenutni korisnik 
 /* axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`)
    .then(response => {
      userRole.value = response.data.role;
    })
    .catch(error => {
      console.error('Error fetching user details', error);
    });
*/
userRole.value = getUserRoleFromCookie();


  const id = route.params.id;
  axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${id}`)
      .then(response => {
          factory.value = response.data;
      })
      .catch(error => {
          console.error('Error fetching factory details', error);
      });

  axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}`)
      .then(response => {
        if (userRole.value === 'CUSTOMER') {
            chocolates.value = response.data.filter(chocolate => chocolate.isOnStock === true);
          } else {
            chocolates.value = response.data;
          }
      });
});

function updateChocolate(chocolate) {
  router.push({ name: 'update', params: { id: chocolate.id } });
}

function changeQuantity(chocolate) {
  router.push({ name: 'changeQuantity', params: { id: chocolate.id } });
}

function deleteChocolate(chocolateId) {
  axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/${chocolateId}`)
      .then(() => {
          chocolates.value = chocolates.value.filter(c => c.id !== chocolateId);
          alert('Chocolate deleted successfully');
      })
      .catch(error => {
          console.error('Error deleting chocolate', error);
          alert('Failed to delete chocolate');
      });
}
function showQuantityDialog(chocolateItem) {
  quantityExceedsStockError.value = false;
  selectedChocolate.value = chocolateItem;  // Use selectedChocolate
  selectedChocolateId.value = chocolateItem.id;
}

function addToCart() {
  
  if (!selectedChocolate.value || !selectedChocolate.value.id) {  // Use selectedChocolate
    console.error('Cannot add to cart: Invalid chocolate item.');
    return;
  }
  if (quantity.value > selectedChocolate.value.numberOfChocolates || !selectedChocolate.value.isOnStock) {
    quantityExceedsStockError.value = true;
    return;
  }

  const userId = getUserIdFromLocalStorage();
  const shoppingCart = {
    customerId: userId,
    chocolates: {
      [selectedChocolate.value.id]: quantity.value  // Use selectedChocolate
    },
    overallPrice: selectedChocolate.value.price * quantity.value  // Use selectedChocolate
  };

  axios.post('http://localhost:8080/WebShopAppREST/rest/shoppingCarts', shoppingCart)
  .then(response => {
        Swal.fire({
            title: 'Chocolate added to cart successfully!',
            showCancelButton: true,
            confirmButtonText: 'Go to Cart',
            cancelButtonText: 'Continue shopping',
            customClass: {
              popup: 'swal-custom-popup',
              confirmButton: 'swal-custom-button',
              cancelButton: 'swal-custom-button'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                router.push('/cart'); // Navigate to the cart page
            }
            selectedChocolateId.value = null; // Reset the selected chocolate ID
        });
    })
    .catch(error => {
      console.error('Error adding chocolate to cart', error);
      alert('Failed to add chocolate to cart');
    });
}

defineExpose({ updateChocolate });
</script>

<style>
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
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.tables {
  width: 80%;
  display: flex;
  justify-content: center;
  margin-left: 10%;
}

.table-container {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  margin: 0 auto;
}

th, td {
  border: 1px solid #ddd;
  padding: 12px 15px;
  text-align: left;
}
img {
    max-width: 50px;
    max-height: 100px;
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

button:hover {
  background-color: #36a372;
}

.small-button {
  padding: 5px 8px; 
  
}
.swal-custom-popup {
  font-size: 0.8em;
}

.swal-custom-button {
  background-color: #42b983 !important;
  color: white !important;
  border-radius: 4px !important;
  padding: 8px 12px !important;
 
}

.swal-custom-button:hover {
  background-color: #36a372 !important;
}
</style>
