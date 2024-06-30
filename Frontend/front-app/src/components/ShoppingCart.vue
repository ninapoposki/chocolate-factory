<template>
  <div class="background">
    <div class="container">
      <h1>Your Shopping Cart</h1>
      <br />
      <div v-if="chocolates.length" class="content">
        <div class="list-container">
          <div class="chocolate-card" v-for="chocolate in chocolates" :key="chocolate.id">
            <div class="chocolate-image">
              <img :src="chocolate.imageUri" alt="Chocolate Image" />
            </div>
            <div class="chocolate-details">
              <div class="chocolate-name">{{ chocolate.chocolateName }}</div>
              <div class="chocolate-type">Type: {{ chocolate.type }}</div>
              <div class="chocolate-variety">Variety: {{ chocolate.variety }}</div>
            </div>
            <div class="chocolate-details">
              <div class="chocolate-weight">Weight: {{ chocolate.weight }}g</div>
              <div class="chocolate-description">Description: {{ chocolate.description }}</div>
            </div>
            <div class="chocolate-details">
              <div class="chocolate-quantity">
                <span>Quantity: </span>
                <template v-if="editableQuantities[chocolate.id]">
                  <input v-model="chocolate.quantity" type="number" min="1" />
                </template>
                <template v-else>
                  <span>{{ chocolate.quantity }}</span>
                </template>
                <button class="small-button" @click="toggleEditQuantity(chocolate)">
                  {{ editableQuantities[chocolate.id] ? 'Save' : 'Change' }}
                </button>
              </div>
            </div>
            <div class="chocolate-actions">
              <div class="chocolate-price">Price: {{ chocolate.price * chocolate.quantity }}din</div>
              <button class="small-button" @click="removeFromCart(chocolate.id)">Remove from Cart</button>
            </div>
          </div>
        </div>
        <div class="total-section">
           <div class="total">Total: {{ totalPrice }} din</div>
          <div class="discount">Discount: -{{ userDiscount }}%</div>
          <hr/>
          <div class="total">Total with discount: {{ discountPrice }} din</div>
          </div>
          <button class="checkout-button" @click="goToCheckout">Go to Checkout</button>
        
      </div>
      <div v-else>
        <p>No chocolates found in your cart.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const chocolates = ref([]);
const userRole = ref('');
const userId = ref(null);
const userName = ref({ firstName: '', lastName: '' });
const carts = ref([]);
const editableQuantities = ref({});
const chocolateId= ref('');
const userPoints = ref(0);
const userDiscount = ref(0);

const getUserIdFromLocalStorage = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const idCookie = cookies.find(cookie => cookie.startsWith('id='));
  if (idCookie) {
    return idCookie.split('=')[1];
  }
  return null;
};

async function fetchChocolates(chocolateIds) {
  const requests = chocolateIds.map((id) => {
    console.log(`Preuzimam čokoladu sa ID: ${id}`);
    chocolateId.value = id;
    return axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/choco/${id}`);
  });

  try {
    const responses = await Promise.all(requests);
    chocolates.value = responses.map((response) => response.data);
    console.log('Preuzete čokolade:', chocolates.value);
  } catch (error) {
    console.error('Greška prilikom preuzimanja čokolada:', error);
  }
}




function generateRandomCode(length = 10) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

async function fetchExistingCodes() {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases`);
    const purchases = response.data;
    return purchases.map(purchase => purchase.code);
  } catch (error) {
    console.error('Greška prilikom preuzimanja kodova:', error);
    return [];
  }
}

async function fetchShoppingCartIds(userId) {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/user/${userId}/ids`);
    return response.data;
    console.log('dajanaaaaaa');
    console.log(response.data); // Očekujemo da ovde bude lista stringova
  } catch (error) {
    console.error('Greška prilikom preuzimanja shoppingCartIds:', error);
    return [];
  }
}


async function fetchShoppingCarts(userId) {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/user/${userId}`);
    return response.data; // Pretpostavljamo da ovo vraća niz korpi za kupovinu
  } catch (error) {
    console.error('Greška prilikom preuzimanja korpi za kupovinu:', error);
    return [];
  }
}
function extractChocolateQuantities(shoppingCarts) {
  const chocolateQuantities = {};
  shoppingCarts.forEach((cart) => {
    if (cart.state) { 
      Object.entries(cart.chocolates).forEach(([chocolateId, quantity]) => {
        if (chocolateQuantities[chocolateId]) {
          chocolateQuantities[chocolateId] += quantity;
        } else {
          chocolateQuantities[chocolateId] = quantity;
        }
      });
    }
  });
  return chocolateQuantities;
}

async function fetchChocolatesWithQuantities(chocolateIds, quantities) {
  const requests = chocolateIds.map((id) => {
    chocolateId.value = id;
    return axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/choco/${id}`);
  });

  try {
    const responses = await Promise.all(requests);
    const chocolates = responses.map((response) => response.data);
    chocolates.forEach(chocolate => {
      chocolate.quantity = quantities[chocolate.id];
    });
    return chocolates;
  } catch (error) {
    console.error('Greška prilikom preuzimanja čokolada:', error);
    return [];
  }
}

async function fetchFactoryId(chocolateId) {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/factory/${chocolateId}`);
    console.log("proba");
    console.log(response.data);
    return response.data; // Assuming the endpoint returns an object with the factoryId
  } catch (error) {
    console.error(`Error fetching factory ID for chocolate ID: ${chocolateId}`, error);
    return null;
  }
}

onMounted(async () => {
  userId.value = getUserIdFromLocalStorage();
  console.log('ID korisnika:', userId.value);

  // Preuzimanje uloge korisnika
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId.value}`);
    userRole.value = response.data.role;
    userName.value.firstName = response.data.firstName;
    userName.value.lastName = response.data.lastName;
    console.log('Uloga korisnika:', userRole.value);
    console.log('Ime korisnika:', userName.value.firstName);
    console.log('Prezime korisnika:', userName.value.lastName);
    userPoints.value = response.data.points;
    console.log('Bodovi korisnika:', userPoints.value);

    //discountPrice.value = calculateDiscount(userPoints);
    //discountPrice.value = calculateDiscount(totalPrice.value, userPoints);
   // console.log('popust', discountPrice.value);
  } catch (error) {
    console.error('Greška prilikom preuzimanja podataka o korisniku', error);
  }
  try {
    const shoppingCarts = await fetchShoppingCarts(userId.value);
    carts.value = shoppingCarts;
    console.log('Preuzete korpe za kupovinu:', shoppingCarts);

    const chocolateQuantities = extractChocolateQuantities(shoppingCarts);
    const chocolateIds = Object.keys(chocolateQuantities);
    console.log('Ekstraktovani ID-jevi čokolada:', chocolateIds);
    console.log('Količine čokolada:', chocolateQuantities);

    chocolates.value = await fetchChocolatesWithQuantities(chocolateIds, chocolateQuantities);
    console.log('Preuzete čokolade sa količinama:', chocolates.value);
  } catch (error) {
    console.error('Greška prilikom preuzimanja korpi za kupovinu i čokolada', error);
  }
});

/*function removeFromCart(chocolateId) {
  chocolates.value = chocolates.value.filter((chocolate) => chocolate.id !== chocolateId);
  console.log(`Uklonjena čokolada sa ID: ${chocolateId}`);
}*/


async function removeFromCart(chocolateId) {
  try {
    const shoppingCarts = carts.value;

    const cartToDelete = shoppingCarts.find(cart =>
      cart.state === true && Object.keys(cart.chocolates).some(key => {
        const id = key.split(':')[0]; // Extract chocolate ID from key (e.g., "19:6|")
        return id === chocolateId.toString();
      })
    );

    if (cartToDelete) {
      await axios.delete(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/${cartToDelete.id}`);
      console.log(`Removed chocolate with ID: ${chocolateId} from cart with ID: ${cartToDelete.id}`);

      chocolates.value = chocolates.value.filter(chocolate => chocolate.id !== chocolateId);
    } else {
      console.log(`No shopping cart found with chocolate ID: ${chocolateId} and state true for user ID: ${userId.value}`);
    }
  } catch (error) {
    console.error(`Error removing chocolate with ID: ${chocolateId}:`, error);
  }
}

// ukupna cena cokolada u korpi
const totalPrice = computed(() => {
  return chocolates.value.reduce((sum, chocolate) => sum + (chocolate.price * chocolate.quantity), 0).toFixed(2);
});
const discountPrice = computed(() => {
  return calculateDiscount(totalPrice.value, userPoints.value);
});

function calculateDiscount(totalPrice, userPoints) {
  let discount = 0;
  if (userPoints > 5000) {
    discount = 0.05; // 5% popust
  } else if (userPoints > 3000) {
    discount = 0.03; // 3% popust
  }
  userDiscount.value = discount*100;
  return (totalPrice * (1 - discount)).toFixed(2); // Vraća numeričku vrednost sa popustom
}
async function goToCheckout() {
  try {
   
   
    const existingCodes = await fetchExistingCodes();
    let purchaseCode;

    do {
      purchaseCode = generateRandomCode();
    } while (existingCodes.includes(purchaseCode));

    const firstChocolateId = chocolates.value.length ? chocolates.value[0].id : null;
    let factoryId = null;

    if (firstChocolateId) {
      factoryId = await fetchFactoryId(firstChocolateId);
      
    }
    
     const shoppingCartIds = await fetchShoppingCartIds(userId.value);
     console.log('Shopping Cart IDs: a', shoppingCartIds);
    
    console.log('totalna cenaaaaa', totalPrice.value);

    const discountedTotalPrice = discountPrice.value;

    console.log('cenaobracnataaaaaaa', discountedTotalPrice);

    const purchase = {
      code: purchaseCode,
      chocolates: shoppingCartIds,
      dateAndTime: new Date(),
      price: discountedTotalPrice,
      factoryId: factoryId,
      customerId: userId.value,
      customerFirstName: userName.value.firstName,
      customerLastName: userName.value.lastName,
      status: 'PROCESSING'
    };

    const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/purchases', purchase);
    console.log('Kupovina kreirana:', response.data);
  

    const user = await fetchUser(userId.value);
    if (user) {
      user.points = user.points + (totalPrice.value / 1000 * 133);
      const updatedUser = await updateUser(userId.value, user);
      console.log('Korisnik ažuriran:', updatedUser);
    }

    router.push('/profile');
  } catch (error) {
    console.error('Greška prilikom kreiranja kupovine:', error);
  }
}
/*
async function goToCheckout() {
  try {
    const existingCodes = await fetchExistingCodes();
    let purchaseCode;

    do {
      purchaseCode = generateRandomCode();
    } while (existingCodes.includes(purchaseCode));

    const firstChocolateId = chocolates.value.length ? chocolates.value[0].id : null;
    let factoryId = null;

    if (firstChocolateId) {
      factoryId = await fetchFactoryId(firstChocolateId);
      console.log(`Factory ID for chocolate ID ${firstChocolateId}: ${factoryId}`);
    }


   
   // const shoppingCartIds = carts.value.map(cart => cart.id);
   const shoppingCartIds = await fetchShoppingCartIds(userId.value); // Koristimo funkciju za preuzimanje shoppingCartIds

    console.log(shoppingCartIds);

    console.log('totalna cenaaaaa', totalPrice);
    const discountedTotalPrice = discountPrice.value;
    //const discountedTotalPrice = calculateDiscount(totalPrice.value, userPoints.value);
    
    console.log('cenaobracnataaaaaaa', discountedTotalPrice);

    const purchase = {
      code: purchaseCode,
      chocolates: shoppingCartIds,
      dateAndTime: new Date(),
      price: discountedTotalPrice,// totalPrice.value,
      factoryId: factoryId,
      customerId: userId.value,
      customerFirstName: userName.value.firstName,
      customerLastName: userName.value.lastName,
      status: 'PROCESSING'
    };

    const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/purchases', purchase);
    console.log('Kupovina kreirana:', response.data);
  
    const user = await fetchUser(userId.value);
    if (user) {
      // Ažuriranje bodova korisnika
      user.points =user.points +(totalPrice.value) /1000 * 133; 
      const updatedUser = await updateUser(userId.value, user);
      console.log('Korisnik ažuriran:', updatedUser);
    }

    router.push('/profile');
  } catch (error) {
    console.error('Greška prilikom kreiranja kupovine:', error);
  }
}


*/
async function fetchUser(userId) {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${userId}`);
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

async function toggleEditQuantity(chocolate) {
  if (editableQuantities.value[chocolate.id]) {
    // Save the new quantity and send it to the server
    try {

      const newQuantity = chocolate.quantity;
      if (newQuantity < 1) {
        alert('Quantity cannot be less than 1.');
        return;
      }
      const originalChocolate = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/choco/${chocolate.id}`);
      const availableQuantity = originalChocolate.data.numberOfChocolates;
      if (newQuantity > availableQuantity) {
        alert(`Quantity cannot be more than available stock (${availableQuantity}).`);
        return;
      }

      const cartId = await fetchCartIdContainingChocolate(chocolate.id, userId.value);
      if (cartId) {
        const OverallPrice = (chocolate.price * newQuantity).toFixed(2);
        await axios.put(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/changeQuantity`, {
          chocolateId: chocolate.id,
          newQuantity: chocolate.quantity,
          cartId: cartId,
          newOverallPrice: OverallPrice
        });
        console.log(`Updated quantity for chocolate ID: ${chocolate.id} to ${chocolate.quantity} in cart ID: ${cartId}`);
      } else {
        console.error(`No cart found containing chocolate ID: ${chocolate.id}`);
      }
    } catch (error) {
      console.error(`Error updating quantity for chocolate ID: ${chocolate.id}:`, error);
    }
  }
  // Toggle the edit mode
  editableQuantities.value[chocolate.id] = !editableQuantities.value[chocolate.id];
}

const discountPercentage = computed(() => {
  if (userPoints.value > 1000) {
    return 10;
  } else if (userPoints.value > 500) {
    return 5;
  } else {
    return 0;
  }
});

async function fetchCartIdContainingChocolate(chocolateId, userId) {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/user/${userId}`);
    const shoppingCarts = response.data;

    for (const cart of shoppingCarts) {
      if (cart.state === true && Object.keys(cart.chocolates).some(key => key.split(':')[0] === chocolateId.toString())) {
        return cart.id;
      }
    }
    return null;
  } catch (error) {
    console.error('Error fetching shopping cart containing chocolate:', error);
    return null;
  }
}
</script>

<style scoped>
.background {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  flex-direction: column;
  padding-top: 20px;
}

.background::before {
  content: '';
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
.discount {
  font-size: 16px;
  color: #ff0000;
  font-size: 1.3rem;
  font-weight: bold;
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  height: 100%;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  box-sizing: border-box; /* Dodato da bi padding bio uračunat u dimenzije */
  overflow: auto; 
}

h1 {
  text-align: center;
  margin: 0;
}

.content {
  width: 100%;
  margin-top: 20px;
}

.list-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
}

.chocolate-card {
  display: flex;
  flex-direction: row;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  width: 100%;
}

.chocolate-image {
  flex: 1;
}

.chocolate-image img {
  width: 100%;
  height: auto;
  max-width: 150px; /* Adjust this value as needed */
}

.chocolate-details {
  flex: 3;
  display: flex;
  flex-direction: column;
  padding: 1rem;
  gap: 0.5rem;
  text-align: left;
}

.chocolate-actions {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  padding: 1rem;
}

.chocolate-name {
  font-size: 1.25rem;
  font-weight: bold;
}

.chocolate-type,
.chocolate-variety,
.chocolate-weight,
.chocolate-description,
.chocolate-price {
  font-size: 1rem;
}

.small-button {
  padding: 0.5rem 1rem;
  background-color: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  align-self: flex-end;
}

.small-button:hover {
  background-color: #42b983;
}

.total-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 500px;
  align-items: flex-end;
}

.total {
  font-size: 1.3rem;
  font-weight: bold;
}

.checkout-button {
  padding: 0.5rem 1rem;
  background-color: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.checkout-button:hover {
  background-color: #42b983;
}
</style>
