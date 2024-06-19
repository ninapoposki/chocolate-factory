<template>
    <div>
      <div ref="map" class="map"></div>
      <button @click="confirmLocation">Confirm Location</button>
    </div>
  </template>
  
  <script>
  import 'ol/ol.css';
  import { Map, View } from 'ol';
  import { Tile as TileLayer } from 'ol/layer';
  import { OSM } from 'ol/source';
  import { fromLonLat, toLonLat } from 'ol/proj';
  import { defaults as defaultControls } from 'ol/control';
  import { Feature } from 'ol';
  import { Point } from 'ol/geom';
  import { Vector as VectorLayer } from 'ol/layer';
  import { Vector as VectorSource } from 'ol/source';
  import { Style, Icon } from 'ol/style';
  
  export default {
    props: ['initialCoordinates'],
    data() {
      return {
        map: null,
        marker: null,
        coordinates: this.initialCoordinates || [20.4612, 44.8125] // Default to Novi Sad if no initial coordinates provided
      };
    },
    watch: {
      initialCoordinates(newCoordinates) {
        this.setMarker(newCoordinates);
      }
    },
    mounted() {
      this.initializeMap();
    },
    methods: {
      initializeMap() {
        if (this.map) {
          this.map.setTarget(null);  // Remove the existing map
          this.map = null;
        }
  
        const rasterLayer = new TileLayer({
          source: new OSM()
        });
  
        this.map = new Map({
          target: this.$refs.map,
          layers: [rasterLayer],
          view: new View({
            center: fromLonLat(this.coordinates),
            zoom: 12
          }),
          controls: defaultControls()
        });
  
        this.setMarker(this.coordinates);
  
        this.map.on('click', (event) => {
          const coordinates = event.coordinate;
          this.marker.setGeometry(new Point(coordinates));
          this.coordinates = coordinates;
          this.confirmLocation();
        });
      },
      setMarker(coordinates) {
        if (!this.marker) {
          this.marker = new Feature({
            geometry: new Point(fromLonLat(coordinates))
          });
          this.marker.setStyle(new Style({
            image: new Icon({
              anchor: [0.5, 1],
              src: 'https://openlayers.org/en/latest/examples/data/icon.png'
            })
          }));
  
          const vectorSource = new VectorSource({
            features: [this.marker]
          });
  
          const vectorLayer = new VectorLayer({
            source: vectorSource
          });
  
          this.map.addLayer(vectorLayer);
        } else {
          this.marker.setGeometry(new Point(fromLonLat(coordinates)));
        }
        this.map.getView().setCenter(fromLonLat(coordinates));
      },
      async confirmLocation() {
        const [lon, lat] = toLonLat(this.coordinates);
        this.getAddressFromCoordinates(lon, lat);
      },
      async getAddressFromCoordinates(lon, lat) {
        try {
          const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&addressdetails=1`);
          const data = await response.json();
          const address = data.address;
          const location = {
            city: address.city || address.town || address.village || '',
            country: address.country || '',
            height: lat,
            postalCode: address.postcode || '',
            street: address.road || '',
            streetNumber: address.house_number || '',
            width: lon
          };
          this.$emit('locationSelected', location);
        } catch (error) {
          console.error('Error fetching address:', error);
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .map {
    width: 100%;
    height: 400px;
  }
  </style>
  