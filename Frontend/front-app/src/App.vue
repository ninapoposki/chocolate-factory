<template>
  <nav>
    <div class="nav-container">
      <router-link v-if="userRole" to="/logout">Log out</router-link>
    <div class="nav-links">
      <router-link to="/">Factories</router-link> |
      <router-link v-if="!userRole" to="/about">  Log in</router-link>
    </div>
    <div class="nav-profile">
      <router-link to="/register">Register</router-link>  
      <router-link to="/cart" v-if="userRole === 'CUSTOMER'">| 
        <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSzbIO9hbTC8-Zrd_Iv4Rt_noJWyQsHNzhm5eMTpWXPD-00e_QnVFt3FCSsRGAD9RRqGqs&usqp=CAU" alt="profile icon" class="profile-icon">
        My Shopping Cart</router-link>  |   
      <router-link to="/profile">
        <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRU8TAx3eYFRfZCVkJQ7kWqKx-IxoRmxvPUlw&usqp=CAU" alt="profile icon" class="profile-icon">
        My profile
      </router-link>
    </div>
  </div>
  </nav>
  <router-view/>
</template>
<script setup>
/*import { ref, onMounted } from 'vue';


const userRole = ref('');
const getUserRoleFromCookie = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const userRoleCookie = cookies.find(cookie => cookie.startsWith('userRole='));
  if (userRoleCookie) {
    return userRoleCookie.split('=')[1];
  }
  return null;
};
*/
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const userRole = ref('');
const getUserRoleFromCookie = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const userRoleCookie = cookies.find(cookie => cookie.startsWith('userRole='));
  if (userRoleCookie) {
    return userRoleCookie.split('=')[1];
  }
  return null;
};

const router = useRouter();

// Pre-fetch user role before navigating to any route
router.beforeEach((to, from, next) => {
  userRole.value = getUserRoleFromCookie();

  // Check if user is already logged in when navigating to /login
  /*if (userRole.value && to.path === '/about') {
    // Show alert and prevent navigation
    alert('You are already logged in.');
    return; // Prevent navigation
  }
  if (!userRole.value  && to.path === '/logout') {
    // Show alert and prevent navigation
    alert('You are not  logged in.');
    return; // Prevent navigation
  }*/

  next(); // Continue with navigation
});

onMounted(() => {
  userRole.value=getUserRoleFromCookie();
  
});
</script>
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-links {
  display: flex;
  gap: 10px;
  margin: 0 auto;
  margin-left: 45%;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}

.nav-profile {
  display: flex;
  gap: 10px;
  align-items: center;
}

.nav-profile a {
  display: flex;
  align-items: center;
  font-weight: bold;
  color: #2c3e50;
}

.nav-profile .profile-icon {
  margin-right: 5px;
  width: 30px;
  height: 30px;
}
</style>
