<template>
  <h1>FABRIKE</h1>
  <div class="tables">
        <table class="table-container">
            <thead>
                <tr>
                    <th>Logo</th>
                    <th>Naziv</th>
                    <th>Lokacija</th>
                    <th>Prosecna ocena</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="factory in factories" :key="factory.id">
                    <td>{{ factory.logoUri }}</td>
                    <td>{{ factory.factoryName }}</td>
                    <td>{{ factory.location.street }} {{ factory.location.streetNumber }}, {{ factory.location.city }} {{ factory.location.postalCode }}</td>
                    <td>{{ factory.grade }}</td>
                </tr>
            </tbody>
        </table>
    </div>

</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            factories: []
        };
    },
    created() {
        this.fetchFactories();
    },
    methods: {
        fetchFactories() {
            axios.get('http://localhost:8080/WebShopAppREST/rest/factories')
                .then(response => {
                    console.log(response);
                    this.factories = response.data.sort((a, b) => {
                        if (a.isStatus === b.isStatus) {
                            return a.id - b.id;
                        }
                        return a.isStatus ? -1 : 1;
                    });
                })
                .catch(error => {
                    console.error('Error fetching factories', error);
                });
        }
    }
}
</script>
