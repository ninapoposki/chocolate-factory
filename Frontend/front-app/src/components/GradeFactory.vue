<template>
    <div class="background">
      <div class="content">
        <h2>GRADE FACTORY</h2>
        
      <div v-if="factory">
        <h3>{{ factory.factoryName }}</h3>
        <img :src="factory.logoUri" alt="Factory Logo" class="logo-preview" />
      </div>
        <form @submit.prevent="gradeFactory">
          <div class="form-section">
            <label for="rating">Rating:</label>
            <select v-model="rating" required>
              <option value="" disabled>Select rating</option>
              <option v-for="i in 10" :key="i" :value="i">{{ i }}</option>
            </select>
          </div>
          <div class="form-section">
            <label for="comment">Comment:</label>
            <textarea v-model="comment" rows="5" required></textarea>
          </div>
          <div class="form-section">
            <button type="submit">Grade Factory</button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { useRoute, useRouter } from 'vue-router';
  
  const route = useRoute();
  const router = useRouter();
  const rating = ref('');
  const comment = ref('');
  const purchase = ref(null);
  const factory= ref(null);
  const commentCount = ref(0);
  const id = route.params.id;
  const comments= ref([]);
 
  onMounted(async () => {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/purchaseId/${id}`);
    purchase.value = response.data; 

    const factoryId = purchase.value.factoryId;
    const factoryResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}`);
    factory.value = factoryResponse.data;
    
      /* const commentsResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/comments/count/${factoryId}`);
    comments.value = commentsResponse.data;
    commentCount.value= comments.value.length;
    console.log('Komentari:', commentCount.value);*/
  } catch (error) {
    console.error('Error fetching purchase details:', error);
  }
});


const gradeFactory = async () => {
  const userId = getUserIdFromLocalStorage();
  const role = getUserRoleFromCookie();
  const factoryId = purchase.value.factoryId; // Static factory ID for now

  const payload = {
    text: comment.value,
    grade: parseFloat(rating.value),
    factoryId,
    userId,
    //rejection: false, // Assuming default rejection is false
    role
  };

  try {
    const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/comments', payload);
    console.log('Factory graded successfully:', response.data);
    router.push('/details'); // Redirect to home or another route after grading
  } catch (error) {
    console.error('Error grading factory:', error);
  }
};


const getUserIdFromLocalStorage = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const idCookie = cookies.find(cookie => cookie.startsWith('id='));
  if (idCookie) {
    return idCookie.split('=')[1];
  }
  return null;
};
const getUserRoleFromCookie = () => {
  const cookies = document.cookie.split(';').map(cookie => cookie.trim());
  const userRoleCookie = cookies.find(cookie => cookie.startsWith('userRole='));
  if (userRoleCookie) {
    return userRoleCookie.split('=')[1];
  }
  return null;
};
  </script>
  
  <style scoped>
 
  
.background {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden; 
}

.background::before {
  content: "";
  background-image: url('https://cdn2.hauteliving.com/wp-content/uploads/2014/08/macarons.gif');
  background-size: cover;
  background-position: center; /* Center the background image */
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
    max-width: 600px;
    width: 100%;
    opacity: 1;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 200px;
  }
  
  form {
    display: flex;
    flex-direction: column;
  }
  
  .form-section {
    margin-bottom: 10px;
  }
  
  .logo-preview {
    max-width: 100px;
    max-height: 100px;
  }
  
  button {
    background-color: #42b983;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  button:hover {
    background-color: #36a372;
  }
  </style>
  