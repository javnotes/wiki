<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="分类">
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
        <h1>分类管理</h1>
      </div>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="分类表单"
      v-model:visible="modalVisible"
      :confirm-loading="ModalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name"/>
      </a-form-item>
      <a-form-item label="父分类">

        <a-select v-model:value="category.parent" ref="select">
          <a-select-option value="0">无</a-select-option>
          <a-select-option v-for="item in level1" :key="item.id" :value="item.id" :disabled="category.id===item.id">
            {{ item.name }}
          </a-select-option>
        </a-select>

      </a-form-item>

      <a-form-item label="顺序">
        <a-input v-model:value="category.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
import {defineComponent, onMounted, ref, reactive, toRef} from 'vue';
import axios from 'axios';
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'AdminCategory',
  setup() {
    const param = ref();
    param.value = {};

    const categorys = ref();
    const loading = ref(false);

    const columns = [
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '父分类',
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
     * 一级分类树，childen属性就是二级分类
     */
    const level1 = ref(); // 一级分类树

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      categorys.value = [];
      axios.get("/category/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (!data.success) {
          message.error(data.message);
          return;
        }
        categorys.value = data.content;
        console.log("原始数组：", categorys.value);

        level1.value = [];
        // 递归获取所有分类
        level1.value = Tool.array2Tree(categorys.value, 0);
        console.log("树型结构：", level1);
      });
    };

    // ----------表单相关----------
    const category = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    /**
     * 编辑了进行保存
     */
    const handleModalOk = () => {
      modalLoading.value = true;
      axios.post("/category/save", category.value).then((response) => {
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
     * 编辑
     **/
    const edit = (record: any) => {
      modalVisible.value = true;
      category.value = Tool.copy(record);
    };
    /**
     * 新增
     **/
    const add = () => {
      modalVisible.value = true;
      category.value = {};
    };
    /**
     * 删除
     **/
    const handleDel = (id: number) => {
      axios.delete("/category/delete/" + id).then((response) => {
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
      // categorys,
      level1,
      columns,
      handleQuery,
      handleModalOk,
      handleDel,

      edit,
      add,

      category,
      modalVisible,
      modalLoading
    };
  }
});
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