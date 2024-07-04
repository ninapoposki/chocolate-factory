<template>
  <div class="container">
      <h1 class="title">Factory Details</h1>
      <div v-if="factory">
          <div class="tables">
              <table class="table1-container">
                  <thead>
                      <tr>
                          <th>Logo</th>
                          <th>Name</th>
                          <th>Location</th>
                          <th>Working Time</th>
                          <th v-if="factory.grade !== 0.0">Grade</th>
                          <th>Status</th>
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
                       
                      </tr>
                  </tbody>
              </table >
                 
              <table class="table2-container">
                  <thead>
                    
                              <tr>
                                  <th>Comment</th>
                                  <th>Grade</th>
                                  <th v-if="userRole === 'MANAGER'">STATUS</th>
                              </tr>

                            </thead>
                            <tbody>
                              <tr v-for="comment in comments" :key="comment.id">

                                <td>{{comment.text}}</td>
                                <td>{{ comment.grade }}</td>
                                
                            
                                  <th v-if="userRole === 'MANAGER'">
                                    <p v-if="comment.isChecked">
                                      {{ comment.isRejected ? 'Accepted' : 'Denied' }}
                                    </p>
                                    <div v-else>
                                      <button @click="acceptComment(comment.id)">Accept</button>
                                      <button @click="denyComment(comment.id)">Deny</button>
                                    </div>
                                  </th>
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
                              <button v-if="canChangeQuantity(chocolate)" @click="changeQuantity(chocolate)">Change Quantity</button>
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
const comments = ref([]);
const texts = ref([]);
const userIds = ref([]);
const updatedComment = ref('');
const showQuantityModal = ref(false);
const selectedChocolate = ref(null);
const selectedChocolateId = ref(null); 
let quantityExceedsStockError = ref(false);
const number = ref(0);

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
function canChangeQuantity(chocolate) {
  const userId = getUserIdFromLocalStorage();
  const factoryId = factory.value ? String(factory.value.id) : null;

  return userRole.value === 'EMPLOYEE' && String(chocolate.factoryId) === factoryId && factory.value.employees.some(employee => employee.id === userId);
}



function acceptComment(id) {
    // Logika za prihvatanje komentara
    console.log(id);
   // comment.isAccepted = true;
   axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/${id}`)
        .then(response => {
            const comment = response.data;
            
            
            
            console.log('Accepted comment:', comment);
            comment.isChecked = true;
            comment.isRejected = true;

                      axios.put(`http://localhost:8080/WebShopAppREST/rest/comments/${id}`, comment)
                  .then(response => {
                    
                      console.log('Updated comment:', comment);

                      const localComment = comments.value.find(c => c.id === id);
                    if (localComment) {
                        localComment.isChecked = true;
                        localComment.isRejected = true;
                    }

                    console.log('factory grade', factory.value.grade);
                    const factoryGrade = factory.value.grade;
          const commentGrade = comment.grade;
                    console.log('comment grade', commentGrade.value);
          const newGrade = (factoryGrade * (number.value) + commentGrade) / (number.value+1);

          // Ažuriranje fabričke ocene
          factory.value.grade = newGrade;

                 /* const updatedFactory = {
                      ...factory.value,  // Kopiramo sve postojeće atribute fabrike
                      grade: newGrade     // Ažuriramo ocenu fabrike
                    };

                    axios.put(`http://localhost:8080/WebShopAppREST/rest/factories/${factory.value.id}`, updatedFactory)
                      .then(response => {
                        console.log('Factory updated successfully with new grade:', response.data);
                      })
                      .catch(error => {
                        console.error('Error updating factory with new grade:', error);
                      });*/

            const patchData = { grade: newGrade };

                  axios.patch(`http://localhost:8080/WebShopAppREST/rest/factories/${factory.value.id}`, patchData)
                      .then(response => {
                          console.log('Factory grade updated successfully:', response.data);
                          // Ovde možete dodati logiku za ažuriranje lokalnog stanja ako je potrebno
                      })
                      .catch(error => {
                          console.error('Error updating factory grade:', error);
                      });

                      
                  })
                  .catch(error => {
                      console.error('Error updating comment', error);
                  });

        })
        .catch(error => {
            console.error('Error accepting comment', error);
        });


      
}

function denyComment(id) {
    // Logika za odbijanje komentara
   // comment.isDenied = true;
    console.log(id);
   // comment.isAccepted = true;
   axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/${id}`)
        .then(response => {
            const comment = response.data;
            
    
            console.log('Denied comment:', comment);
            comment.isChecked = true;
            comment.isRejected = false;

                      axios.put(`http://localhost:8080/WebShopAppREST/rest/comments/${id}`, comment)
                  .then(response => {
                    
                      console.log('Updated comment:', comment);


                      const localComment = comments.value.find(c => c.id === id);
                    if (localComment) {
                        localComment.isChecked = true;
                        localComment.isRejected = false;
                    }
                      // Možete dodati dodatnu logiku ili ažurirati stanje vaše Vue komponente nakon uspešnog ažuriranja
                  })
                  .catch(error => {
                      console.error('Error updating comment', error);
                  });

        })
        .catch(error => {
            console.error('Error accepting comment', error);
        });
}
/*
const fetchNumberOfComments = async() => {
  const id = route.params.id;
    axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/number/${id}`)
        .then(response => {
            number.value = response.data.length; 
            console.log('aa',number.value);// Dodelite dužinu niza odgovoru
        })
        .catch(error => {
            console.error('Error fetching number of comments:', error);
        });
};*/
const fetchNumberOfComments = async () => {
    const id = route.params.id;
    try {
        const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/number/${id}`);
        number.value = response.data.length;
        console.log('Number of comments:', number.value);
    } catch (error) {
        console.error('Error fetching number of comments:', error);
    }
};

onMounted(async() => {
  
  const userId = getUserIdFromLocalStorage();
  console.log(userId);
  await fetchNumberOfComments();
  

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
if(userRole.value == 'CUSTOMER'){
   axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/count/${id}`)
        .then(response => {
            comments.value = response.data;
            texts.value = comments.value.map(comment => comment.text);
            console.log('Komentari:', texts.value);
        })
        .catch(error => {
            console.error('Greška pri preuzimanju komentara:', error);
        });
}
if(userRole.value == 'MANAGER' || userRole.value  == 'ADMINISTRATOR'){
   axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/user/${id}`)
        .then(response => {
            comments.value = response.data;
            texts.value = comments.value.map(comment => comment.text);
            console.log('Komentari:', texts.value);

            userIds.value = comments.value.map(comment => comment.userId);
            fetchUserDetails(userIds.value);
        })
        .catch(error => {
            console.error('Greška pri preuzimanju komentara:', error);
        });
}

      axios.get(``)

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

const fetchUserDetails = (userIds) => {
    userIds.forEach(userId => {
        axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`)
            .then(response => {
                const user = response.data;
                const commentToUpdate = comments.value.find(comment => comment.userId === user.id);
                if (commentToUpdate) {
                    commentToUpdate.userName = user.firstName;
                    commentToUpdate.userLastName = user.lastName;
                }
            })
            .catch(error => {
                console.error(`Error fetching user details for userId ${userId}:`, error);
            });
    });
};

function updateChocolate(chocolate) {
  router.push({ name: 'update', params: { id: chocolate.id } });
}

function changeQuantity(chocolate) {
  const userId = getUserIdFromLocalStorage();
  console.log("User ID:", userId);  // Ispisuje ID korisnika
  axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/employeeFactory/${userId}`)
    .then(response => {
      const factory = response.data;
      console.log("Factory data:", factory);  // Ispisuje podatke o fabrici
      if (!factory) {
        alert('Factory not found.');
        return;
      }
      // Proveravamo da li je trenutni korisnik zaposlenik u fabrici
      const isEmployeeInFactory = factory.employees.some(employee => employee.id === userId);
      console.log("Is employee in factory:", isEmployeeInFactory);  // Ispisuje rezultat provere da li je korisnik zaposlenik u fabrici
      // Proveravamo da li čokolada pripada fabrici
      console.log("Chocolate factoryId:", chocolate.factoryId);  // Ispisuje factoryId čokolade
      console.log("Factory id:", factory.id);  // Ispisuje ID fabrike
      const isChocolateInFactory = String(chocolate.factoryId) === String(factory.id);
      console.log("Is chocolate in factory:", isChocolateInFactory);  // Ispisuje rezultat provere da li čokolada pripada fabrici

      if (isEmployeeInFactory && isChocolateInFactory) {
        router.push({ name: 'changeQuantity', params: { id: chocolate.id } });
      } else {
        alert('You can only change quantity for chocolates that belong to your factory.');
      }
    })
    .catch(error => {
      console.error('Error checking factory chocolate ownership', error);
      alert('Failed to check chocolate ownership.');
    });
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
  width: 100%;
  display: flex;
  justify-content: center;
  margin-left: 1%;
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
.table1-container {
  width: 100%;
  height: 200px;
  border-collapse: collapse;
  margin-top: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  margin: 0 auto;
}
.table2-container {
  width: 100%;
  height: auto; /* Automatska visina */
    max-height: 300px; /* Opciono: maksimalna visina sa skrolom */
    overflow-y: auto;
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
