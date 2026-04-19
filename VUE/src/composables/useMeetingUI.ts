import { ref, onMounted, onUnmounted } from 'vue'

export function useMeetingUI() {
  const leftPanelCollapsed = ref(false)
  const rightPanelCollapsed = ref(false)
  const isMobile = ref(false)

  const updateResponsiveState = () => {
    isMobile.value = window.innerWidth < 1024
    if (isMobile.value) {
      leftPanelCollapsed.value = true
      rightPanelCollapsed.value = true
    }
  }

  const toggleLeftPanel = () => {
    leftPanelCollapsed.value = !leftPanelCollapsed.value
  }

  const toggleRightPanel = () => {
    rightPanelCollapsed.value = !rightPanelCollapsed.value
  }

  onMounted(() => {
    updateResponsiveState()
    window.addEventListener('resize', updateResponsiveState)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', updateResponsiveState)
  })

  return {
    leftPanelCollapsed,
    rightPanelCollapsed,
    isMobile,
    toggleLeftPanel,
    toggleRightPanel
  }
}
