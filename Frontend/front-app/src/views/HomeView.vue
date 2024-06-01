
<template>
  <div class="home">
    <AllFactories/>
  </div>
</template>

<script>
// @ is an alias to /src
import AllFactories from '@/components/AllFactories.vue';

export default {
  name: 'HomeView',
  components: {
    AllFactories
  }
}
</script>
