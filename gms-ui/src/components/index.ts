import { App } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart, LineChart, PieChart, RadarChart } from 'echarts/charts';
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  DataZoomComponent,
  GraphicComponent,
} from 'echarts/components';
import Chart from './chart/index.vue';
import Breadcrumb from './breadcrumb/index.vue';
import PageContainer from './page-container/index.vue';
import ProCard from './pro-card/index.vue';
import ItemIcon from './item-icon/index.vue';
import ItemIdCell from './item-id-cell/index.vue';
import ClientPathConfig from './client-path-config/index.vue';

// Manually introduce ECharts modules to reduce packing size

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  RadarChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  DataZoomComponent,
  GraphicComponent,
]);

export default {
  install(Vue: App) {
    Vue.component('Chart', Chart);
    Vue.component('Breadcrumb', Breadcrumb);
    Vue.component('PageContainer', PageContainer);
    Vue.component('ProCard', ProCard);
    Vue.component('ItemIcon', ItemIcon);
    Vue.component('ItemIdCell', ItemIdCell);
    Vue.component('ClientPathConfig', ClientPathConfig);
  },
};
