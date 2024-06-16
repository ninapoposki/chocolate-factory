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
      <button type="submit" class="login-button">Log in</button>
    </form>
    <div class="register-link">
      <a href="#" @click.prevent="registration">Don't have an account? Register here</a>
    </div>
    <div class="error-message">
      {{ errorMessage }}
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
  event.preventDefault();

  axios.get('rest/users')
    .then(response => {
      const users = response.data;
      let foundUser = users.find(u => u.username === user.value.username);

      if (foundUser && foundUser.blocked === true) {
        errorMessage.value = "You were blocked by administrator!";
        return;
      }

      if (!user.value.username && !user.value.password) {
        errorMessage.value = "Please enter your username and password";
      } else if (!user.value.username) {
        errorMessage.value = "Please enter your username";
      } else if (!user.value.password) {
        errorMessage.value = "Please enter your password";
      } else if (!foundUser) {
        errorMessage.value = "User with the entered username does not exist";
        user.value.username = "";
        user.value.password = "";
      } else if (foundUser.password !== user.value.password) {
        errorMessage.value = "Incorrect password";
        user.value.password = "";
      }
    })
    .catch(error => {
      console.log(error);
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
