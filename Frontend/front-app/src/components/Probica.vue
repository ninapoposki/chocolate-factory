<template>
 <h1>Cokolade</h1>
  <div class="tables">
        <table class="table-container">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Variety</th>
                    <th>Type</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="chocolate in chocolates" :key="chocolate.id">
                    <td>{{ chocolate.chocolateName }}</td>
                    <td>{{ chocolate.price }}</td>
                    <td>{{ chocolate.variety }} </td>
                    <td>{{ chocolate.type }}</td>
                    <td><button @click="izmeniCokoladu(chocolate)">Izmeni cokoladu</button></td>
                </tr>
            </tbody>
        </table> 
    </div>
</template>

<script>
import axios from 'axios';
export default{
    data(){
        return{
            chocolates:[]
        };
    },
    created(){
        this.fetchChocolates();
        return;
    },
    methods:{
        fetchChocolates(){
            axios.get('http://localhost:8080/WebShopAppREST/rest/chocolates').then(response=>{console.log(response.data); this.chocolates=response.data}).
            catch(error=>{console.error("Error fetching chocolates",error);})
        },
        izmeniCokoladu(chocolate){
            this.$router.push({ name: 'update', params:{ id: chocolate.id } });

        }
    }

}


</script>
<style>
.table-container{
    border:1px solid black;
    width: 100%;
}
th{
    border:2px solid gray;
    background-color: rgb(224, 220, 220);
}
td{
    border:1px solid black;

}

</style>