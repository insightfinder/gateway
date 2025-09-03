<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import OpenAI from 'openai'

interface Message {
  role: 'user' | 'assistant'
  content: string
  timestamp: Date
}

interface Config {
  apiKey: string
  model: string
  temperature: number
  apiEndpoint: string
}

// Reactive state
const messages = ref<Message[]>([])
const currentMessage = ref('')
const isLoading = ref(false)
const showConfig = ref(false)
const messagesContainer = ref<HTMLElement>()

// Configuration
const config = reactive<Config>({
  apiKey: localStorage.getItem('openai-api-key') || '',
  model: 'gpt-3.5-turbo',
  temperature: 0.7,
  apiEndpoint: localStorage.getItem('openai-api-endpoint') || 'https://api.openai.com/v1'
})

// Save config to localStorage
const saveConfig = () => {
  localStorage.setItem('openai-api-key', config.apiKey)
  localStorage.setItem('openai-temperature', config.temperature.toString())
  localStorage.setItem('openai-api-endpoint', config.apiEndpoint)
  showConfig.value = false
}

// Load config from localStorage
const loadConfig = () => {
  const savedTemperature = localStorage.getItem('openai-temperature')
  const savedEndpoint = localStorage.getItem('openai-api-endpoint')

  if (savedTemperature) config.temperature = parseFloat(savedTemperature)
  if (savedEndpoint) config.apiEndpoint = savedEndpoint
}

// Initialize config
loadConfig()

// Scroll to bottom of messages
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// Send message to OpenAI API
const sendMessage = async () => {
  if (!currentMessage.value.trim() || !config.apiKey) return

  const userMessage: Message = {
    role: 'user',
    content: currentMessage.value.trim(),
    timestamp: new Date()
  }

  messages.value.push(userMessage)
  const messageContent = currentMessage.value
  currentMessage.value = ''
  isLoading.value = true

  // Create assistant message placeholder
  const assistantMessage: Message = {
    role: 'assistant',
    content: '',
    timestamp: new Date()
  }
  messages.value.push(assistantMessage)

  await scrollToBottom()

  try {
    const openai = new OpenAI({
      apiKey: config.apiKey,
      baseURL: config.apiEndpoint,
      dangerouslyAllowBrowser: true
    })

    const stream = await openai.chat.completions.create({
      model: config.model,
      messages: messages.value.slice(0, -1).map(msg => ({
        role: msg.role,
        content: msg.content
      })),
      temperature: config.temperature,
      stream: true,
    })

    let content = ''
    for await (const chunk of stream) {
      const delta = chunk.choices[0]?.delta?.content
      if (delta) {
        content += delta
        assistantMessage.content = content
        await scrollToBottom()
      }
    }
  } catch (error) {
    console.error('Error calling OpenAI API:', error)
    assistantMessage.content = 'Error: Failed to get response from OpenAI API. Please check your API key and try again.'
  } finally {
    isLoading.value = false
  }
}

// Handle Enter key press
const handleKeyPress = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

// Format timestamp
const formatTime = (date: Date) => {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}
</script>

<template>
  <div class="flex flex-col h-screen max-w-6xl mx-auto bg-base-200">
    <!-- Header -->
    <div class="navbar bg-base-100 border-b">
      <div class="navbar-start">
        <h1 class="text-xl font-bold">🤖 LLM Chat Assistant</h1>
      </div>
      <div class="navbar-end gap-2">
        <div class="tooltip tooltip-bottom" data-tip="Settings">
          <button @click="showConfig = true" class="btn btn-primary btn-sm">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.325.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 0 1 1.37.49l1.296 2.247a1.125 1.125 0 0 1-.26 1.431l-1.003.827c-.293.241-.438.613-.43.992a6.759 6.759 0 0 1 0 .255c-.008.378.137.75.43.991l1.004.827c.424.35.534.955.26 1.43l-1.298 2.247a1.125 1.125 0 0 1-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 0 1-.22.128c-.331.183-.581.495-.644.869l-.213 1.281c-.09.543-.56.94-1.11.94h-2.594c-.55 0-1.019-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 0 1-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 0 1-1.369-.49l-1.297-2.247a1.125 1.125 0 0 1 .26-1.431l1.004-.827c.292-.24.437-.613.43-.991a6.932 6.932 0 0 1 0-.255c.007-.38-.138-.751-.43-.992l-1.004-.827a1.125 1.125 0 0 1-.26-1.43l1.297-2.247a1.125 1.125 0 0 1 1.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.086.22-.128.332-.183.582-.495.644-.869l.214-1.28Z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Configuration Modal -->
    <div v-if="showConfig" class="modal modal-open">
      <div class="modal-box max-w-md">
        <h3 class="font-bold text-lg mb-4">⚙️ Configuration</h3>

        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text font-medium">OpenAI API Key</span>
          </label>
          <input
            v-model="config.apiKey"
            placeholder="sk-..."
            class="input input-bordered w-full"
          />
        </div>

        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text font-medium">API Endpoint</span>
          </label>
          <input
            v-model="config.apiEndpoint"
            placeholder="https://api.openai.com/v1"
            class="input input-bordered w-full"
          />
          <div class="label">
            <span class="label-text-alt text-base-content/70">Custom API endpoint (leave default for OpenAI)</span>
          </div>
        </div>

        <div class="form-control mb-6">
          <label class="label">
            <span class="label-text font-medium">Temperature: {{ config.temperature }}</span>
          </label>
          <input
            v-model.number="config.temperature"
            type="range"
            min="0"
            max="2"
            step="0.1"
            class="range range-primary"
          />
          <div class="w-full flex justify-between text-xs px-2 mt-1">
            <span class="text-base-content/70">Conservative</span>
            <span class="text-base-content/70">Creative</span>
          </div>
        </div>

        <div class="modal-action">
          <button @click="showConfig = false" class="btn btn-ghost">Cancel</button>
          <button @click="saveConfig" class="btn btn-primary">Save Changes</button>
        </div>
      </div>
    </div>

    <!-- Messages Container -->
    <div ref="messagesContainer" class="flex-1 overflow-y-auto p-4 space-y-4 bg-base-100">
      <!-- Empty State -->
      <div v-if="messages.length === 0" class="flex flex-col items-center justify-center h-full text-center">
        <div class="hero-content text-center">
          <div class="max-w-md">
            <div class="text-6xl mb-4">👋</div>
            <h1 class="text-2xl font-bold mb-4">Hello! I'm your AI assistant</h1>
            <p class="text-base-content/70 mb-4">Start a conversation by typing a message below.</p>
            <div v-if="!config.apiKey" class="alert alert-warning">
              <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
              </svg>
              <span>Please configure your OpenAI API key in settings first.</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Messages -->
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="['chat', message.role === 'user' ? 'chat-end' : 'chat-start']"
      >
        <div class="chat-image avatar">
          <div class="w-10 rounded-full bg-primary flex items-center justify-center text-lg">
            {{ message.role === 'user' ? '👤' : '🤖' }}
          </div>
        </div>
        <div class="chat-bubble max-w-md lg:max-w-2xl">
          <div class="whitespace-pre-wrap">{{ message.content }}</div>
        </div>
        <div class="chat-footer opacity-50 text-xs mt-1">
          {{ formatTime(message.timestamp) }}
        </div>
      </div>

      <!-- Loading Indicator -->
      <div v-if="isLoading" class="chat chat-start">
        <div class="chat-image avatar">
          <div class="w-10 rounded-full bg-primary flex items-center justify-center text-lg">
            🤖
          </div>
        </div>
        <div class="chat-bubble">
          <div class="flex items-center space-x-1">
            <div class="flex space-x-1">
              <div class="w-2 h-2 bg-current rounded-full animate-bounce"></div>
              <div class="w-2 h-2 bg-current rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
              <div class="w-2 h-2 bg-current rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
            </div>
            <span class="ml-2 text-sm opacity-70">Thinking...</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Input Area -->
    <div class="p-4 bg-base-100 border-t">
      <div class="flex items-end gap-3">
        <textarea
          v-model="currentMessage"
          @keydown="handleKeyPress"
          placeholder="Type your message here... (Press Enter to send, Shift+Enter for new line)"
          class="textarea textarea-bordered flex-1 resize-none min-h-[2.5rem] max-h-32"
          :disabled="!config.apiKey || isLoading"
          rows="1"
        ></textarea>
        <button
          @click="sendMessage"
          :disabled="!currentMessage.trim() || !config.apiKey || isLoading"
          class="btn btn-primary btn-circle flex-shrink-0"
        >
          <span v-if="isLoading" class="loading loading-spinner loading-sm"></span>
          <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 12 3.269 3.125A59.769 59.769 0 0 1 21.485 12 59.768 59.768 0 0 1 3.27 20.875L5.999 12Zm0 0h7.5" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>
