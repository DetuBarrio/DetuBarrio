import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import VCalendar from 'v-calendar';
import 'v-calendar/dist/style.css';

const app = createApp(App)

app.use(VCalendar, {});

app.use(router)

app.mount('#app')
