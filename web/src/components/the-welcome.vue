<template>
  <div>
    <a-row>
      <a-col :span="24">
        <a-card>
          <a-row>
            <a-col :span="8">
              <a-statistic title="总阅读量" :value="statistic.viewCount">
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>

            <a-col :span="8">
              <a-statistic title="总点赞量" :value="statistic.voteCount">
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>

            <a-col :span="8">
              <a-statistic title="点赞率" :value="statistic.voteCount / statistic.viewCount * 100"
                           :precision="2"
                           suffix="%"
                           :value-style="{ color: '#cf1322' }">
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>

    <br>
    <a-row :gutter="16">
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col :span="12">
              <a-statistic title="今日阅读" :value="statistic.todayViewCount" style="margin-right: 50px">
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic title="今日点赞" :value="statistic.todayVoteCount">
                <template #suffix>
                  <like-outlined/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col :span="12">
              <a-statistic
                  title="预计今日阅读"
                  :value="statistic.todayViewIncrease"
                  :value-style="{ color: '#0000ff' }"
              >
                <template #suffix>
                  <UserOutlined/>
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic
                  title="预计今日阅读增长"
                  :value="statistic.todayViewIncreaseRateAbs"
                  :precision="2"
                  suffix="%"
                  class="demo-class"
                  :value-style="statistic.todayViewIncreaseRate < 0 ? { color: '#3f8600' } : { color: '#cf1322' }"
              >
                <template #prefix>
                  <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                  <arrow-up-outlined v-if="statistic.todayViewIncreaseRate >= 0"/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>

    <br>
    <a-row :gutter="16">
      <a-col :span="24">
        <div id="main" style="width: 100%;height: 300px;"></div>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
import {defineComponent, ref, onMounted} from 'vue';
import axios from 'axios';

declare let echarts: any;

export default defineComponent({
  name: 'the-welcome',
  setup() {
    const statistic = ref();
    statistic.value = {};
    const getStatistic = () => {
      axios.get('/ebook-snapshot/get-statistic').then((response) => {
        const data = response.data;
        if (data.success) {
          //statisticResp为临时变量，statisticResp[0]：昨日数据，statisticResp[1]：今日数据
          const statisticResp = data.content;
          statistic.value.viewCount = statisticResp[1].viewCount;
          statistic.value.voteCount = statisticResp[1].voteCount;
          statistic.value.todayViewCount = statisticResp[1].viewIncrease;
          statistic.value.todayVoteCount = statisticResp[1].voteIncrease;

          // 先计算当前时刻对应的分钟数，占一天(24*60)的百分比
          const now = new Date();
          const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);
          // console.log(nowRate)
          // todayViewIncrease：今日预计增长量
          statistic.value.todayViewIncrease = parseInt(String(statisticResp[1].viewIncrease / nowRate));
          // todayViewIncreaseRate：今日预计增长率
          statistic.value.todayViewIncreaseRate = (statistic.value.todayViewIncrease - statisticResp[0].viewIncrease) / statisticResp[0].viewIncrease * 100;
          statistic.value.todayViewIncreaseRateAbs = Math.abs(statistic.value.todayViewIncreaseRate);
        }
      });
    };

    /**
     * 初始化30天趋势图,list为后端返回的30天的数据
     * @param list
     */
    const init30DayEcharts = (list: any) => {
      // 基于准备好的dom，初始化echarts实例
      const myChart = echarts.init(document.getElementById('main'));

      const xAxis = [];
      const seriesView = [];
      const seriesVote = [];
      for (let i = 0; i < list.length; i++) {
        const record = list[i];
        xAxis.push(record.date);
        seriesView.push(record.viewIncrease);
        seriesVote.push(record.voteIncrease);
      }

      // 指定图表的配置项和数据
      const option = {
        title: {
          text: '30天趋势图'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总阅读量', '总点赞量']
        },
        grid: {
          left: '1%',
          right: '3%',
          bottom: '3%',
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xAxis
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总阅读量',
            type: 'line',
            // stack: '总量', 不堆叠
            data: seriesView,
            smooth: true
          },
          {
            name: '总点赞量',
            type: 'line',
            // stack: '总量', 不堆叠
            data: seriesVote,
            smooth: true
          }
        ]
      };
      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    };

    const get30DayStatistic = () => {
      axios.get('/ebook-snapshot/get-30-statistic').then((response) => {
        const data = response.data;
        if (data.success) {
          const statisticList = data.content;

          init30DayEcharts(statisticList)
        }
      });
    };

    onMounted(() => {
          getStatistic();
          get30DayStatistic();
        }
    );

    return {
      statistic
    }
  }
});
</script>
