<template>
    <div class="background">
      <div class="content">
        <h2>Registration Form</h2>
        <form @submit.prevent="TryToregisterUser($event)">
          <div>
            <label for="username">Username:</label>
            <input type="text" v-model="registerUser.username" required />
          </div>
          <div>
            <label for="password">Password:</label>
            <input type="password" v-model="registerUser.password" required />
          </div>
          <div>
           <label for="confirmPassword">Confirm Password:</label>
           <input type="password" v-model="confirmPassword" required />
          </div>
          <div v-if="!passwordsMatch" class="password-mismatch">
            <p style="color: red;">Reenter your password!</p>
          </div>
          <div>
            <label for="firstName">First Name:</label>
            <input type="text" v-model="registerUser.firstName" required />
          </div>
          <div>
            <label for="lastName">Last Name:</label>
            <input type="text" v-model="registerUser.lastName" required />
          </div>
          <div>
            <label for="gender">Gender:</label>
            <select v-model="registerUser.gender" required>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <div>
            <label for="dateOfBirth">Date of Birth:</label>
            <input type="date" v-model="registerUser.dateOfBirth" required />
          </div>
          <button type="submit">Register</button>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
  import axios from 'axios';
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  const confirmPassword = ref("");
  const passwordsMatch = ref(true);
  const router = useRouter();
  const registerUser = ref({ username: "", password: "", firstName: "", lastName: "", gender: "", dateOfBirth: null });
  
  defineProps({
    msg: {
      type: String,
      required: true
    }
  })
  
  onMounted(() => {
    loadProducts();
  })
  
  function loadProducts() {
    axios.get('http://localhost:8080/WebShopAppREST/rest/products/')
      .then(response => {
        console.log(response.data)
      })
      .catch(error => console.log(error));
  }
  
  function addChocolate() {
    router.push('/add');
  }
  
  function TryToregisterUser(event) {
    if (registerUser.value.password !== confirmPassword.value) {
    alert("Passwords do not match!");
    confirmPassword.value = ""; 
    passwordsMatch.value = false;
    return; 
  }

  const today = new Date();
  const dob = new Date(registerUser.value.dateOfBirth);
  if (dob >= today-1) {
    alert("Invalid date of birth! Date of birth cannot be today or in the future.");
    return;
  }

  axios.get(`http://localhost:8080/WebShopAppREST/rest/users/exists/${registerUser.value.username} `)
    .then(response => {
      if (response.data.exists) {
        alert("Username already exists! Please choose a different username.");
      } else {
        // Ako username ne postoji, izvrši registraciju
        axios.post('http://localhost:8080/WebShopAppREST/rest/users/register', registerUser.value)
          .then(response => {
            console.log(response.data);
            registerUser.value.username = "";
            registerUser.value.password = "";
            confirmPassword.value = "";
            registerUser.value.firstName = "";
            registerUser.value.lastName = "";
            registerUser.value.gender = "";
            registerUser.value.dateOfBirth = null;
            passwordsMatch.value = true;
            
            alert(`Welcome ${response.data.firstName}! Your account has been successfully created!`);
          })
          .catch(error => console.log(error));
      }
    })
    .catch(error => console.error(error));
  }
  </script>
  
  <style scoped>
  h3 {
    margin: 40px 0 0;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    display: inline-block;
    margin: 0 10px;
  }
  a {
    color: #42b983;
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
  max-width: 400px;
  width: 100%;
  opacity: 1;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.password-mismatch {
    font-size: 15px;
    height: 15px;
  }
</style>








