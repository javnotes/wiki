<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :guter="24">
        <a-col :span="8">
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
              v-if="level1.length > 0"
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
              {{ record.sort }} {{ text }}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDel(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称"/>
            </a-form-item>
            <a-form-item>
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
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>

            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutlined/>
                内容预览
              </a-button>
            </a-form-item>

            <a-form-item label="内容">
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <!--显示previewHtml，使用了同一个style wangeditor，即与doc.vue中的样式相同-->
      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>

  <!--  <a-modal-->
  <!--      title="文档表单"-->
  <!--      v-model:visible="modalVisible"-->
  <!--      :confirm-loading="ModalLoading"-->
  <!--      @ok="handleSave"-->
  <!--  >-->
  <!--  </a-modal>-->
</template>
<script lang="ts">
import {defineComponent, onMounted, ref, reactive, toRef, createVNode} from 'vue';
import axios from 'axios';
import {message, Modal} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import {ExclamationCircleOutlined} from '@ant-design/icons-vue';
import E from 'wangeditor'

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

    //因为树选择组件的属性状态，会随当前编辑的节点而变化，所以需要单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];

    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
        slots: {customRender: 'name'}
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
    level1.value = []; // 初始化，避免null.length

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      level1.value = [];
      axios.get("/doc/all/" + route.query.ebookId).then((response) => {
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

        // 初始化父文档下拉框，使其默认显示数据
        treeSelectData.value = Tool.copy(level1.value);
        //为选择树添加一个『无』，treeSelectData 是选项下拉框的数据，所以需要在这里添加
        treeSelectData.value.unshift({id: 0, name: "无"});
      });
    };

    // ----------表单相关----------
    // const doc = ref({}); 空对象
    const doc = ref();
    doc.value = {
      ebookId: route.query.ebookId,
    };
    // const modalVisible = ref(false);
    const modalLoading = ref(false);
    const editor = new E('#content');
    editor.config.zIndex = 0;

    /**
     * (编辑后进行)保存
     */
    const handleSave = () => {
      modalLoading.value = true;
      // 保存富文本内容，通过给doc新增一个content属性，将富文本内容保存到该属性中。
      // 注意：这里的doc是响应式变量，所以可以直接新增属性。这里设置属性名为content，是为了和后端实体属性名保持一致，参数映射。
      // 先清空富文本框，因为富文本框的内容是异步加载的，可能会保存了之前点击的其他文档的内容
      doc.value.content = editor.txt.html();
      axios.post("/doc/save", doc.value).then((response) => {
        modalLoading.value = false;
        const data = response.data;
        if (data.success) {
          // modalVisible.value = false;
          message.success("保存成功");
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
     * 编辑，点击编辑按钮时，doc.value被赋值，然后handleQueryContent()根据doc.value.id去查询文档内容
     **/
    const edit = (record: any) => {
      // 为了避免编辑时，富文本框的内容是上一次点击的文档的内容，所以先清空富文本框
      editor.txt.html("");
      // modalVisible.value = true;
      doc.value = Tool.copy(record);
      handleQueryContent();

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
      editor.txt.html("");
      // modalVisible.value = true;
      // doc.value = {
      //   ebookId: route.query.ebookId
      // };

      treeSelectData.value = Tool.copy(level1.value);
      //为选择树添加一个『无』
      treeSelectData.value.unshift({
        id: 0,
        name: "无",
        disabled: false
      });
    };

    /**
     * 文档内容查询，点击编辑按钮时，查询文档内容
     **/
    const handleQueryContent = () => {
      // 此处注意，得先让doc.value.id有值，才能传给后端
      axios.get("/doc/find-content/" + doc.value.id).then((response) => {
        const data = response.data;
        if (!data.success) {
          message.error(data.message);
          return;
        }
        // 将富文本内容显示到编辑器中
        console.log("data.content", data.content);
        editor.txt.html(data.content);
      });
    };

    const deleteIds: Array<string> = [];
    const deleteNames: Array<string> = [];
    /**
     * 查找整根树枝，获取选中的节点及其子孙节点的id
     */
    const getDeteleIds = (treeSelectData: any, id: any) => {
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          deleteIds.push(id); // 如果是当前节点，则将其id加入到数组中
          deleteNames.push(node.name); // 如果是当前节点，则将其name加入到数组中
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
      deleteIds.length = 0;
      deleteNames.length = 0;
      //level1.value 表格里的数据，treeSelectData是下拉框里的数据
      // console.log("开始比对");
      // console.log(treeSelectData);
      // console.log(level1);
      // console.log(level1.value);
      getDeteleIds(level1.value, id);
      Modal.confirm({
        title: '重要提醒',
        icon: createVNode(ExclamationCircleOutlined),
        content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
        onOk() {
          axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
            const data = response.data;
            if (data.success) { // 删除成功，重新加载当前列表
              handleQuery();
            } else {
              message.error(data.message);
            }
          });
        },
      });
    };

    // ----------------富文本预览--------------
    const drawerVisible = ref(false);
    const previewHtml = ref();
    const handlePreviewContent = () => {
      const html = editor.txt.html();
      previewHtml.value = html;
      drawerVisible.value = true;
    };
    const onDrawerClose = () => {
      drawerVisible.value = false;
    };

    onMounted(() => {
      handleQuery();
      editor.create();
    });
    return {
      param,
      // docs,
      level1,
      columns,
      handleQuery,
      handleSave,
      handleDel,
      loading,

      edit,
      add,

      doc,
      // modalVisible,
      modalLoading,

      treeSelectData,

      drawerVisible,
      previewHtml,
      handlePreviewContent,
      onDrawerClose,

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