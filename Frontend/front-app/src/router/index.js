import { createRouter, createWebHashHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import AddChocolate from '../views/AddChocolate.vue';
import UpdateChocolate from '../views/UpdateChocolateView.vue';
import Probica from '../views/ProbicaView.vue';

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path: "/add",
    name: "add",
    component: AddChocolate,
  },
  {
    path: "/probica",
    name: "probica",
    component: Probica,
  },
  {
    path: "/update/:id",
    name: "update",
    component: UpdateChocolate,
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
