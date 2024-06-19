import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

if (!localStorage.getItem('userId')) {
  localStorage.setItem('userId', '-1');
} else {
  // Ako postoji, postavi ga na -1
  localStorage.setItem('userId', '-1');
}
  console.log(localStorage.getItem('userId'));
createApp(App).use(router).mount('#app')
