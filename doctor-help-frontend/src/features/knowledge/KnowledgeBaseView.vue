<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { CurrentUser } from '../auth/api/auth-api'
import { createKnowledgeArticle, createKnowledgeCategory, fetchKnowledgeArticles, fetchKnowledgeTree, likeKnowledgeArticle, type KnowledgeArticle, type KnowledgeTreeNode } from './api/knowledge-api'

const props = defineProps<{ currentUser: CurrentUser }>()
const articles = ref<KnowledgeArticle[]>([])
const tree = ref<KnowledgeTreeNode[]>([])
const titleKeyword = ref('')
const levelOne = ref('全部')
const levelTwo = ref('全部')
const levelThree = ref('全部')
const currentPage = ref(1)
const pageSize = 12
const selected = ref<KnowledgeArticle>()
const detailVisible = ref(false)
const dialogVisible = ref(false)
const categoryDialogVisible = ref(false)
const articleFormRef = ref<FormInstance>()
const categoryFormRef = ref<FormInstance>()
const form = reactive({ categoryPath: [] as string[], title: '', content: '' })
const categoryForm = reactive({ parentPath: [] as string[], label: '' })
const cascaderProps = { value: 'label', label: 'label', children: 'children', checkStrictly: true, emitPath: true }
const articleRules: FormRules = {
  categoryPath: [{ required: true, type: 'array', min: 1, message: '请选择分类', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }, { max: 100, message: '标题不能超过 100 个字符', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }, { max: 5000, message: '正文不能超过 5000 个字符', trigger: 'blur' }],
}
const categoryRules: FormRules = {
  label: [{ required: true, message: '请输入层级名称', trigger: 'blur' }, { max: 30, message: '层级名称不能超过 30 个字符', trigger: 'blur' }],
}

function articlePath(article: KnowledgeArticle): string[] { return article.categoryPath ?? [] }
const levelOneOptions = computed(() => ['全部', ...tree.value.map((node) => node.label)])
const levelTwoOptions = computed(() => {
  const root = tree.value.find((node) => node.label === levelOne.value)
  return ['全部', ...(root?.children.map((node) => node.label) ?? [])]
})
const levelThreeOptions = computed(() => {
  const root = tree.value.find((node) => node.label === levelOne.value)
  const child = root?.children.find((node) => node.label === levelTwo.value)
  return ['全部', ...(child?.children.map((node) => node.label) ?? [])]
})
const filteredArticles = computed(() => articles.value.filter((article) => {
  const path = articlePath(article)
  return (levelOne.value === '全部' || path[0] === levelOne.value)
    && (levelTwo.value === '全部' || path[1] === levelTwo.value)
    && (levelThree.value === '全部' || path[2] === levelThree.value)
}))
const topArticles = computed(() => [...articles.value].sort((a, b) => b.likes - a.likes).slice(0, 3))
const pagedArticles = computed(() => filteredArticles.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize))

function resetDescendants() { levelTwo.value = '全部'; levelThree.value = '全部'; currentPage.value = 1 }
function resetThirdLevel() { levelThree.value = '全部'; currentPage.value = 1 }
function resetArticleForm() { Object.assign(form, { categoryPath: [], title: '', content: '' }); articleFormRef.value?.clearValidate() }
function openArticleDialog() { resetArticleForm(); dialogVisible.value = true }
function openArticleDetail(article: KnowledgeArticle) { selected.value = article; detailVisible.value = true }
function openCategoryDialog() { Object.assign(categoryForm, { parentPath: [...form.categoryPath], label: '' }); categoryDialogVisible.value = true; categoryFormRef.value?.clearValidate() }
async function load() { [articles.value, tree.value] = await Promise.all([fetchKnowledgeArticles(titleKeyword.value), fetchKnowledgeTree()]) }
async function searchByTitle() { currentPage.value = 1; await load() }
async function clearTitleSearch() { titleKeyword.value = ''; await searchByTitle() }
async function like(article: KnowledgeArticle) {
  if (article.likedByCurrentUser) return
  const updated = await likeKnowledgeArticle(article.id)
  articles.value = articles.value.map((item) => item.id === updated.id ? updated : item)
  if (selected.value?.id === updated.id) selected.value = updated
}
async function create() {
  if (!articleFormRef.value || !(await articleFormRef.value.validate().catch(() => false))) return
  const created = await createKnowledgeArticle(form)
  articles.value = [created, ...articles.value]
  dialogVisible.value = false
  ElMessage.success('知识已新增')
}
async function createCategoryLevel() {
  if (!categoryFormRef.value || !(await categoryFormRef.value.validate().catch(() => false))) return
  const parentPath = [...categoryForm.parentPath]
  const created = await createKnowledgeCategory(categoryForm)
  tree.value = await fetchKnowledgeTree()
  form.categoryPath = [...parentPath, created.label]
  categoryDialogVisible.value = false
  ElMessage.success('分类层级已新增')
}
onMounted(load)
</script>

<template>
  <main class="page">
    <header class="page-header"><div><p class="eyebrow">院内知识库</p><h1>检验、操作与通用经验</h1><p>演示数据；真实内容应经医院审核。</p></div><el-button type="primary" @click="openArticleDialog">新增知识</el-button></header>
    <section class="summary"><article><h2>热门知识</h2><ol><li v-for="item in topArticles" :key="item.id">{{ item.title }} <strong>{{ item.likes }} 赞</strong></li></ol></article><article><h2>检索知识</h2><p>可按标题模糊匹配，并与知识层级筛选组合使用。</p><div class="title-search"><el-input v-model="titleKeyword" clearable placeholder="输入标题关键词" @keyup.enter="searchByTitle" @clear="clearTitleSearch" /><el-button type="primary" @click="searchByTitle">搜索</el-button></div><h2 class="filter-title">层级检索</h2><div class="filters"><el-select v-model="levelOne" @change="resetDescendants"><el-option v-for="item in levelOneOptions" :key="item" :label="item" :value="item" /></el-select><el-select v-model="levelTwo" :disabled="levelOne === '全部'" @change="resetThirdLevel"><el-option v-for="item in levelTwoOptions" :key="item" :label="item" :value="item" /></el-select><el-select v-model="levelThree" :disabled="levelTwo === '全部'"><el-option v-for="item in levelThreeOptions" :key="item" :label="item" :value="item" /></el-select></div></article></section>
    <section class="grid"><article v-for="item in pagedArticles" :key="item.id" class="card" :class="{ 'card--liked': item.likedByCurrentUser }" @click="openArticleDetail(item)"><el-tag>{{ articlePath(item).join(' / ') }}</el-tag><h2>{{ item.title }}</h2><p>{{ item.content }}</p><footer><span>提供者：{{ item.provider }}</span><el-button link :class="{ 'like-button--liked': item.likedByCurrentUser }" :disabled="item.likedByCurrentUser" @click.stop="like(item)">👍 {{ item.likes }}</el-button></footer></article></section>
    <el-pagination v-if="filteredArticles.length > pageSize" v-model:current-page="currentPage" :page-size="pageSize" :total="filteredArticles.length" layout="total, prev, pager, next" class="pagination" />
    <el-drawer v-model="detailVisible" title="知识详情" size="480px" @closed="selected = undefined"><template v-if="selected"><el-tag>{{ articlePath(selected).join(' / ') }}</el-tag><h2>{{ selected.title }}</h2><p class="content">{{ selected.content }}</p><p>提供者：{{ selected.provider }}</p><el-button :class="{ 'like-button--liked': selected.likedByCurrentUser }" :disabled="selected.likedByCurrentUser" @click="like(selected)">👍 {{ selected.likes }}</el-button></template></el-drawer>
    <el-dialog v-model="dialogVisible" title="新增知识" @closed="resetArticleForm"><el-form ref="articleFormRef" :model="form" :rules="articleRules" label-position="top"><el-form-item label="分类" prop="categoryPath"><el-cascader v-model="form.categoryPath" :options="tree" :props="cascaderProps" clearable placeholder="请选择知识分类层级" class="full-width" /><el-button link type="primary" @click="openCategoryDialog">新增分类层级</el-button></el-form-item><el-form-item label="标题" prop="title"><el-input v-model="form.title" maxlength="100" show-word-limit /></el-form-item><el-form-item label="正文" prop="content"><el-input v-model="form.content" type="textarea" :rows="8" maxlength="5000" show-word-limit /></el-form-item><el-form-item label="提供者"><el-input :model-value="props.currentUser.name" disabled /></el-form-item></el-form><template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="create">保存</el-button></template></el-dialog>
    <el-dialog v-model="categoryDialogVisible" title="新增分类层级"><el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-position="top"><el-form-item label="父级分类"><el-cascader v-model="categoryForm.parentPath" :options="tree" :props="cascaderProps" clearable placeholder="留空则新增一级分类" class="full-width" /></el-form-item><el-form-item label="层级名称" prop="label"><el-input v-model="categoryForm.label" maxlength="30" show-word-limit /></el-form-item></el-form><template #footer><el-button @click="categoryDialogVisible = false">取消</el-button><el-button type="primary" @click="createCategoryLevel">新增层级</el-button></template></el-dialog>
  </main>
</template>

<style scoped>
.page{padding:28px 32px}.page-header{display:flex;justify-content:space-between;gap:12px;align-items:flex-start}.eyebrow{margin:0 0 8px;color:#2f80ed;font-weight:600}h1,h2{margin:0;color:#1d2939}h1{font-size:28px}h2{font-size:18px;margin-top:12px}.page-header p,.summary p{color:#667085}.summary,.grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px;margin-top:24px}.summary article,.card{padding:20px;background:#fff;border:1px solid #e7edf5;border-radius:12px}.title-search,.filters{display:flex;gap:10px}.title-search{margin-top:14px}.title-search :deep(.el-input){flex:1}.filter-title{margin-top:20px}.filters{margin-top:12px}.filters :deep(.el-select){flex:1}.grid{grid-template-columns:repeat(3,minmax(0,1fr));margin-top:16px}.card{cursor:pointer}.card--liked{border-color:#b8d8c5;background:#f5fbf7}.card p{min-height:52px;color:#475467}.card footer{display:flex;justify-content:space-between;color:#98a2b3;font-size:12px}.pagination{justify-content:flex-end;margin-top:18px}.content{color:#475467;line-height:1.8}.like-button--liked{color:#98a2b3!important}.full-width{width:100%}@media(max-width:1000px){.summary,.grid{grid-template-columns:1fr}.filters,.title-search,.page-header{flex-direction:column}.filters :deep(.el-select){width:100%}}
</style>
