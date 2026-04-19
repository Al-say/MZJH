import { ref, watch, nextTick, type Ref } from 'vue'

export function useAutoScroll<T>(dataRef: Ref<T>, scrollContainerRef: Ref<HTMLElement | null>) {
  const isAutoScrollEnabled = ref(true)

  watch(
    dataRef,
    async () => {
      if (!isAutoScrollEnabled.value || !scrollContainerRef.value) return

      await nextTick()
      const el = scrollContainerRef.value
      el.scrollTo({
        top: el.scrollHeight,
        behavior: 'smooth'
      })
    },
    { deep: true }
  )

  return { isAutoScrollEnabled }
}
