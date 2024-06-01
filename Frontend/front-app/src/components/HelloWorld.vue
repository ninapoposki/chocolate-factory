
  <template>
  <div>
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
  <button @click = "addChocolate()"></button>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import {UseRouter} from 'vue-router'
const users= ref([]);
const router = UseRouter();
const registerUser = ref({username: "", password:"", firstName:"", lastName:"", gender:"", dateOfBirth:null});

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
function addChocolate(){
  router.push('/add');
}

function TryToregisterUser(event){
  axios.post('http://localhost:8080/WebShopAppREST/rest/users/register', registerUser.value)
		.then(response => {
			console.log(response.data)
		})
    .catch(error => console.log(error));
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
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
</style>
