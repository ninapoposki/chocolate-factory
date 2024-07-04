<template>
    <div class="background">
      <div class="content">
        <h2>Register New Employee</h2>
        <form @submit.prevent="tryToRegisterUser">
          <div>
            <label for="firstName">First Name:</label>
            <input id="firstName" v-model="registerUser.firstName" required />
          </div>
          <div>
            <label for="lastName">Last Name:</label>
            <input id="lastName" v-model="registerUser.lastName" required />
          </div>
          <div>
            <label for="dateOfBirth">Date of Birth:</label>
            <input id="dateOfBirth" type="date" v-model="registerUser.dateOfBirth" required />
          </div>
          <div>
            <label for="gender">Gender:</label>
            <select id="gender" v-model="registerUser.gender" required>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <div>
            <label for="username">Username:</label>
            <input id="username" v-model="registerUser.username" required />
          </div>
          <div>
            <label for="password">Password:</label>
            <input id="password" type="password" v-model="registerUser.password" required />
          </div>
          <div>
            <label for="confirmPassword">Confirm Password:</label>
            <input id="confirmPassword" type="password" v-model="confirmPassword" required />
          </div>
          <div v-if="!passwordsMatch" class="password-mismatch">
            <p style="color: red;">Passwords do not match!</p>
          </div>
          <button type="submit">Register</button>
        </form>
      </div>
    </div>
  </template>
  

  <script setup>
  import { ref } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import axios from 'axios';
  
  const confirmPassword = ref("");
  const passwordsMatch = ref(true);
  const router = useRouter();
  const route = useRoute(); // Za dobijanje factoryId iz parametara rute
  const registerUser = ref({ username: "", password: "", firstName: "", lastName: "", gender: "MALE", dateOfBirth: null, role: "EMPLOYEE" });
  
  const factoryId = route.params.factoryId; // Dobijanje factoryId iz parametara rute
  
  function tryToRegisterUser() {
    if (registerUser.value.password !== confirmPassword.value) {
      alert("Passwords do not match!");
      confirmPassword.value = "";
      passwordsMatch.value = false;
      return;
    }
  
    const today = new Date();
    const dob = new Date(registerUser.value.dateOfBirth);
    if (dob >= today) {
      alert("Invalid date of birth! Date of birth cannot be today or in the future.");
      return;
    }
  
    axios.get(`http://localhost:8080/WebShopAppREST/rest/users/exists/${registerUser.value.username}`)
      .then(response => {
        if (response.data.exists) {
          alert("Username already exists! Please choose a different username.");
        } else {
          axios.post(`http://localhost:8080/WebShopAppREST/rest/factories/addEmployee/${factoryId}`, registerUser.value)
            .then(response => {
              console.log(response.data);
              registerUser.value.username = "";
              registerUser.value.password = "";
              confirmPassword.value = "";
              registerUser.value.firstName = "";
              registerUser.value.lastName = "";
              registerUser.value.gender = "MALE";
              registerUser.value.dateOfBirth = null;
              passwordsMatch.value = true;
  
              alert(`Welcome! Your account has been successfully created!`);
              router.push('/'); // Navigacija nazad na početnu stranicu ili stranicu sa podacima o menadžeru
            })
            .catch(error => console.log(error));
        }
      })
      .catch(error => console.error(error));
  }
  </script>
  

<style scoped>
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
