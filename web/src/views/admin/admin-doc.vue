<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="文档">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>

      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删除后不可恢复，确认删除?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDel(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
      <div class="about">
        <h1>文档管理</h1>
      </div>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="文档表单"
      v-model:visible="modalVisible"
      :confirm-loading="ModalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name"/>
      </a-form-item>
      <a-form-item label="父文档">
        <a-tree-select
            v-model:value="doc.parent"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="treeSelectData"
            placeholder="请选择父文档"
            tree-default-expand-all
            :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
        >
        </a-tree-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
import {defineComponent, onMounted, ref, reactive, toRef} from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";

export default defineComponent({
  name: 'AdminDoc',
  setup() {
    const route = useRoute();
    console.log("route", route);
    console.log("route.query", route.query);
    console.log("route.query.ebookId", route.query.ebookId);


    const param = ref();
    param.value = {};

    const docs = ref();
    const loading = ref(false);

    const columns = [
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '父文档',
        key: 'parent',
        dataIndex: 'parent'
      },
      {
        title: '顺序',
        key: 'sort',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];

    /**
     * 一级文档树，childen属性就是二级文档
     */
    const level1 = ref(); // 一级文档树

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      level1.value = [];
      axios.get("/doc/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (!data.success) {
          message.error(data.message);
          return;
        }
        docs.value = data.content;
        console.log("原始数组：", docs.value);

        level1.value = [];
        // 递归获取所有文档
        level1.value = Tool.array2Tree(docs.value, 0);
        console.log("树型结构：", level1);
      });
    };

    // ----------表单相关----------
    //因为树选择组件的属性状态，会随当前编辑的节点而变化，所以需要单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];
    const doc = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    /**
     * 编辑了进行保存
     */
    const handleModalOk = () => {
      modalLoading.value = true;
      axios.post("/doc/save", doc.value).then((response) => {
        modalLoading.value = false;
        const data = response.data;
        if (data.success) {
          modalVisible.value = false;

          // 重新加载当前页
          handleQuery();
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 因为如果是编辑，则需要将当前节点及其子孙节点置为 disabled
     * 即当前节点及其子孙节点作为一个整体
     */
    const setDisabled = (treeSelectData: any, id: any) => {
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) { // 如果是当前节点，则置为 disabled
          node.disabled = true;
          const children = node.children; // 获取子节点
          if (Tool.isNotEmpty(children)) { // 如果有子节点，则递归
            for (let j = 0; j < children.length; j++) { //树的同一个层级的节点，id是不会重复的
              setDisabled(children, children[j].id);
            }
          }
        } else { // 如果不是当前节点，则递归
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisabled(children, id);
          }
        }
      }
    };

    /**
     * 编辑
     **/
    const edit = (record: any) => {
      modalVisible.value = true;
      doc.value = Tool.copy(record);

      //不能选择当前节点及其所有子孙接一单，所以需要将这些节点置为 disabled
      //因为作为父节点，会使树断枝
      treeSelectData.value = Tool.copy(level1.value);
      setDisabled(treeSelectData.value, record.id);

      //为选择树添加一个『无』，treeSelectData 是选项下拉框的数据，所以需要在这里添加
      treeSelectData.value.unshift({
        id: 0,
        name: "无",
        disabled: false
      });
    };
    /**
     * 新增
     **/
    const add = () => {
      modalVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId
      };

      treeSelectData.value = Tool.copy(level1.value);
      //为选择树添加一个『无』
      treeSelectData.value.unshift({
        id: 0,
        name: "无",
        disabled: false
      });
    };

    const ids: Array<string> = [];

    /**
     * 查找整根树枝，获取选中的节点及其子孙节点的id
     */
    const getDeteleIds = (treeSelectData: any, id: any) => {
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          ids.push(id); // 如果是当前节点，则将其id加入到数组中
          const children = node.children; // 获取子节点
          if (Tool.isNotEmpty(children)) { // 如果有子节点，则递归
            for (let j = 0; j < children.length; j++) { //树的同一个层级的节点，id是不会重复的
              getDeteleIds(children, children[j].id);
            }
          }
        } else { // 如果不是当前节点，则递归
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeteleIds(children, id);
          }
        }
      }
    };

    /**
     * 删除
     **/
    const handleDel = (id: number) => {
      //每次删除前，都要清空数组，否则会累加，导致删除的数据不正确，因为是全局变量，所以每次都要清空
      ids.length = 0;
      //level1.value 表格里的数据，treeSelectData是下拉框里的数据
      console.log("开始比对");
      console.log(treeSelectData);
      console.log(level1);
      console.log(level1.value);
      getDeteleIds(level1.value, id);
      axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
        const data = response.data;
        if (data.success) { // 删除成功，重新加载当前列表
          handleQuery();
        }
      });
    };


    onMounted(() => {
      handleQuery();
    });
    return {
      param,
      // docs,
      level1,
      columns,
      handleQuery,
      handleModalOk,
      handleDel,

      edit,
      add,

      doc,
      modalVisible,
      modalLoading,

      treeSelectData
    };
  }
})
;
</script>

<!--<style scoped>-->
<!--.ant-avatar {-->
<!--  width: 50px;-->
<!--  height: 50px;-->
<!--  line-height: 50;-->
<!--  border-radius: 50%;-->
<!--  border-radius: 8%;-->
<!--  margin: 5px 0;-->
<!--}-->
<!--</style>-->