<template>
  <nav>
    <div class="nav-container">
      <router-link to="/logout">Log out</router-link>
    <div class="nav-links">
      <router-link to="/">Factories</router-link> |
      <router-link to="/about">Log in</router-link>
    </div>
    <div class="nav-profile">
      <router-link to="/register">Register</router-link>  | 
      <router-link to="/cart">
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
