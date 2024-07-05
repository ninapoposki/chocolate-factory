<template>
  <div class="background">
    <div class="content">
      <div class="loginform">
        <h2>Login Form</h2>
        <form @submit.prevent="login">
          <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required v-model="user.username" />
          </div>
          <div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required v-model="user.password" />
          </div>
          <div class="error-message">
            {{ errorMessage }}
          </div>
          <button type="submit" class="login-button">Log in</button>
        </form>
        <div class="register-link">
          <a href="#" @click.prevent="registration">Don't have an account? Register here</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const user = ref({ username: null, password: null });
const errorMessage = ref(null);
const router = useRouter();

function registration() {
  router.push('/register');
}

function login() {
  errorMessage.value = null;

  axios.post('http://localhost:8080/WebShopAppREST/rest/users/login', {
    username: user.value.username,
    password: user.value.password
  })
  .then(response => {
    console.log(response.data);
    const user = response.data;

    if (user && user.id) {
      errorMessage.value = "You successfully logged in.";
      alert('You successfully logged in.');

      // Set cookies or localStorage as needed
      document.cookie = `username=${user.username}; path=/`;
      document.cookie = `userRole=${user.role}; path=/`;
      document.cookie = `id=${user.id}; path=/`;

      console.log('User ID:', user.id);
      router.push('/profile');
    } else {
      errorMessage.value = "Invalid username or password."; // or another appropriate message
    }
  })
  .catch(error => {
    if (error.response && error.response.data.message) {
      if (error.response.data.message === "Invalid password") {
        errorMessage.value = "Invalid password.";
      } else if (error.response.data.message === "User not found") {
        errorMessage.value = "User with this username does not exist.";
      } else if (error.response.status === 403) {
        errorMessage.value = "Your account is blocked and you cannot log in.";
      } else {
        errorMessage.value = "An error occurred. Please try again.";
      }
    } else {
      errorMessage.value = "An error occurred. Please try again.";
    }
    console.error(error);
  });
}

</script>

<style scoped>
.loginform {
  margin-bottom: 700px;
}

.error-message {
  color: red;
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
  height: 350px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 200px;
}
.login-button {
  width: 80px; 
  margin: 10px auto; 
}
</style>
