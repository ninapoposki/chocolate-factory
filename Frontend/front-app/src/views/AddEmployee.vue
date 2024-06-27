<template>
      <AddEmployee/>
  </template>
  
  <script>
  // @ is an alias to /src
  import AddEmployee from '@/components/AddEmployee.vue';
  
  export default {
    name: 'AddEmployee',
    components: {
      AddEmployee
    }
  }
  </script>
  