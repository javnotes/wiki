<template>
  <a-layout-footer style="text-align: center">
    Ant の 电子书 <span v-show="user.id">，欢迎：{{ user.name }}</span>
  </a-layout-footer>
</template>

<script lang="ts">
import {computed, defineComponent, onMounted} from 'vue';
import store from '@/store';
import {Tool} from '@/util/tool';
import {notification} from "ant-design-vue";

export default defineComponent({
  name: 'the-footer',
  setup() {
    const user = computed(() => store.state.user);

    let websocket: any;
    let token: any;
    // 连接成功
    const onOpen = () => {
      console.log('WebSocket连接成功，状态码：', websocket.readyState)
    };
    // 收到消息的回调
    const onMessage = (event: any) => {
      console.log('WebSocket收到消息：', event.data);
      notification['info']({
        message: '收到消息',
        description: event.data,
      });
    };
    // 连接错误
    const onError = () => {
      console.log('WebSocket连接错误，状态码：', websocket.readyState)
    };
    // 连接关闭的回调
    const onClose = () => {
      console.log('WebSocket连接关闭，状态码：', websocket.readyState)
    };
    // 初始化, 连接成功, 收到消息的回调, 连接错误, 连接关闭的回调
    const initWebSocket = () => {
      // 连接成功
      websocket.onopen = onOpen;
      // 收到消息的回调
      websocket.onmessage = onMessage;
      // 连接错误
      websocket.onerror = onError;
      // 连接关闭的回调
      websocket.onclose = onClose;
    };

    // 页面加载完成后执行
    onMounted(() => {
      // WebSocket, 浏览器支持 WebSocket, 连接地址：ws://, 连接成功, 收到消息的回调, 连接错误, 连接关闭的回调
      if ('WebSocket' in window) {
        // 生成随机字符串
        token = Tool.uuid(10);
        // 连接地址：ws://127.0.0.1:8880/ws/xxx
        websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + token);
        initWebSocket()

        // 关闭
        // websocket.close();
      } else {
        alert('当前浏览器 不支持')
      }
    });

    return {
      user,
    };
  }
});
</script>
