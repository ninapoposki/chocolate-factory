<template>
  <div class="map" ref="map"></div>
</template>

<script>
import 'ol/ol.css';
import { Map, View } from 'ol';
import { Tile as TileLayer } from 'ol/layer';
import { BingMaps } from 'ol/source';
import { fromLonLat } from 'ol/proj';
import { Feature } from 'ol';
import { Point } from 'ol/geom';
import { Vector as VectorLayer } from 'ol/layer';
import { Vector as VectorSource } from 'ol/source';
import { Style, Icon } from 'ol/style';

export default {
  props: ['initialCoordinates', 'small'],
  data() {
    return {
      map: null,
      marker: null
    };
  },
  mounted() {
    this.initializeMap();
  },
  methods: {
    initializeMap() {
      if (this.map) {
        this.map.setTarget(null); // Ukloni postojeću mapu
        this.map = null;
      }

      const satelliteLayer = new TileLayer({
        source: new BingMaps({
          key: 'Av51Gi4l7sV11_rD4hSu0Kb9IFkPQxX8iOghLFa4KGXmA9xr01-YKoYdBcUIOMzh', // Ovdje umetnite vaš Bing Maps API ključ
          imagerySet: 'AerialWithLabelsOnDemand' // Satelitski sloj sa etiketama
        })
      });

      this.map = new Map({
        target: this.$refs.map,
        layers: [satelliteLayer],
        view: new View({
          center: fromLonLat(this.initialCoordinates),
          zoom: this.small ? 14 : 12
        }),
        controls: []
      });

      this.setMarker(this.initialCoordinates);
    },
    setMarker(coordinates) {
      const markerCoordinates = fromLonLat(coordinates);
      
      if (!this.marker) {
        this.marker = new Feature({
          geometry: new Point(markerCoordinates)
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
        this.marker.setGeometry(new Point(markerCoordinates));
      }
      this.map.getView().setCenter(markerCoordinates);
    }
  }
};
</script>

<style scoped>
.map {
  width: 100%;
  height: 100%;
}
</style>
