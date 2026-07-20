<script setup lang="ts">
import * as echarts from 'echarts'
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { KnowledgeTreeNode } from './api/knowledge-api'
const props = defineProps<{ tree: KnowledgeTreeNode[] }>()
const element = ref<HTMLDivElement>(); let chart: echarts.ECharts | undefined
function render(){ if(!element.value)return; chart ??= echarts.init(element.value); chart.setOption({tooltip:{formatter:(p:any)=>`${p.data.name}<br/>知识数：${p.data.value}`},series:[{type:'tree',data:[{name:'医生帮知识库',value:props.tree.reduce((n,item)=>n+item.articleCount,0),children:props.tree.map(node=>({name:node.label,value:node.articleCount,children:node.children.map(child=>({name:child.label,value:child.articleCount,children:child.children.map(leaf=>({name:leaf.label,value:leaf.articleCount}))}))}))}],top:'8%',left:'8%',bottom:'8%',right:'20%',symbolSize:12,label:{position:'left',verticalAlign:'middle',align:'right'},leaves:{label:{position:'right',align:'left'}},expandAndCollapse:true,animationDuration:300} ]}) }
onMounted(render);watch(()=>props.tree,render,{deep:true});onBeforeUnmount(()=>chart?.dispose())
</script><template><div ref="element" class="tree-graph"/></template><style scoped>.tree-graph{height:300px;width:100%}</style>
