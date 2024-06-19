import { createRouter, createWebHashHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import AddChocolate from '../views/AddChocolate.vue';
import UpdateChocolate from '../views/UpdateChocolateView.vue';
import FactoryDetails from '@/components/FactoryDetails.vue';
import RegisterUser from '@/views/RegisterUser.vue';
import MyProfile from '@/views/MyProfile.vue';
const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path: "/add/:id",
    name: "add",
    component: AddChocolate,
  },
  {
    path: "/update/:id",
    name: "update",
    component: UpdateChocolate,
  },

  {
    path: "/details/:id",
    name: "details",
    component: FactoryDetails,
  },
  {
    path: "/profile",
    name: "profile",
    component: MyProfile,
  },
  {
    path: "/register",
    name: "register",
    component: RegisterUser,
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
