<template>
  <a-layout>
    <a-layout-concent :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <h2 v-if="level1.length === 0" style="text-align:center">＿|￣|● 未查询到相关文档</h2>
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="level1.length > 0"
              :tree-data="level1"
              @select="onSelect"
              :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              :defaultExpandAll="true"
              :defaultSelectedKeys="defaultSelectedKeys"
          >
          </a-tree>
        </a-col>
        <a-col :span="18">
          <!--普通的html，与wangEditor无关-->
          <div class="wangeditor" :innerHTML="html"></div>
        </a-col>
      </a-row>
    </a-layout-concent>
  </a-layout>
</template>


<script lang="ts">
import {defineComponent, onMounted, ref, createVNode} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";

export default defineComponent({
  name: 'Doc',
  setup() {
    const route = useRoute();
    const docs = ref();
    const html = ref();
    //defaultSelectedKeys是一个响应式变量，数组
    const defaultSelectedKeys = ref();
    defaultSelectedKeys.value = [];
    // 当前选中的文档
    const doc = ref();
    doc.value = {};

    /**
     * level1：一级文档树，children属性就是二级文档
     * [{
     *   id: "",
     *   name: "",
     *   children: [{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     */
    const level1 = ref(); // 一级文档树，children属性就是二级文档
    level1.value = [];

    /**
     * 内容查询
     **/
    const handleQueryContent = (id: number) => {
      axios.get("/doc/find-content/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          html.value = data.content;
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 数据查询
     * 从请求路径中获取ebookId，route.query.ebookId
     * 先通过handleQueryContent(先定义再使用)获取到文档内容，再通过handleQuery获取到文档树
     **/
    const handleQuery = () => {
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
        const data = response.data;
        if (data.success) {
          docs.value = data.content;

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);

          // 判空：如果level1不为空，就把第一个元素的id赋值给defaultSelectedKeys，再调用handleQueryContent，加载内容
          if (Tool.isNotEmpty(level1)) {
            // 选中第一个节点
            defaultSelectedKeys.value = [level1.value[0].id];
            // 根据节点去加载内容
            handleQueryContent(level1.value[0].id);
          }
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 选中某一节点时，加载该节点的文档信息
     * @param selectedKeys 当前选中的节点，数组，树形组件支持多选
     * @param info
     */
    const onSelect = (selectedKeys: any, info: any) => {
      console.log('selected', selectedKeys, info);
      if (Tool.isNotEmpty(selectedKeys)) {
        // 选中某一节点时，加载该节点的文档信息
        // doc.value = info.selectedNodes[0].props;
        // 加载内容
        handleQueryContent(selectedKeys[0]);
      }
    };

    onMounted(() => {
      handleQuery();
    });

    return {
      level1,
      html,
      onSelect,
      defaultSelectedKeys
    }
  }
});
</script>


<style>
/* wangeditor默认样式, 参照: http://www.wangeditor.com/doc/pages/02-%E5%86%85%E5%AE%B9%E5%A4%84%E7%90%86/03-%E8%8E%B7%E5%8F%96html.html */
/* table 样式 */
.wangeditor table {
  border-top: 1px solid #ccc;
  border-left: 1px solid #ccc;
}

.wangeditor table td,
.wangeditor table th {
  border-bottom: 1px solid #ccc;
  border-right: 1px solid #ccc;
  padding: 3px 5px;
}

.wangeditor table th {
  border-bottom: 2px solid #ccc;
  text-align: center;
}

/* blockquote 样式 */
.wangeditor blockquote {
  display: block;
  border-left: 8px solid #d0e5f2;
  padding: 5px 10px;
  margin: 10px 0;
  line-height: 1.4;
  font-size: 100%;
  background-color: #f1f1f1;
}

/* code 样式 */
.wangeditor code {
  display: inline-block;
  *display: inline;
  *zoom: 1;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}

.wangeditor pre code {
  display: block;
}

/* ul ol 样式 */
.wangeditor ul, ol {
  margin: 10px 0 10px 20px;
}

/* 和antdv p冲突，覆盖掉 */
.wangeditor blockquote p {
  font-family: "YouYuan";
  margin: 20px 10px !important;
  font-size: 16px !important;
  font-weight: 600;
}
</style>
