<template>
  <PageScaffold :show-progress="true" :meteor-count="25">
    <div class="submit min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-blue-900 relative overflow-hidden">
      <div class="container relative z-10">
        <!-- Page Header -->
        <div class="relative z-10 py-12">
          <div class="container mx-auto px-4">
            <div class="text-center">
              <h1 class="text-4xl lg:text-5xl font-bold text-white leading-tight font-serif mb-4">Submit Paper</h1>
              <p class="text-lg text-slate-100">Submit your research paper for publication consideration</p>
            </div>
          </div>
        </div>

        <!-- Submission Form -->
        <div class="relative z-10 pb-20">
          <div class="container mx-auto px-4">
            <div class="max-w-4xl mx-auto">
              <!-- Progress Steps -->
              <div class="mb-8">
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <div :class="['w-10 h-10 rounded-full flex items-center justify-center font-semibold transition-all', 
                                 currentStep >= 1 ? 'bg-cyan-500 text-white' : 'bg-slate-600 text-slate-300']">
                      <span v-if="currentStep > 1">✓</span>
                      <span v-else>1</span>
                    </div>
                    <span :class="['ml-3 font-medium', currentStep >= 1 ? 'text-cyan-400' : 'text-slate-400']">
                      Basic Information
                    </span>
                  </div>
                  <div class="flex-1 h-0.5 mx-4" :class="currentStep >= 2 ? 'bg-cyan-500' : 'bg-slate-600'"></div>
                  <div class="flex items-center">
                    <div :class="['w-10 h-10 rounded-full flex items-center justify-center font-semibold transition-all', 
                                 currentStep >= 2 ? 'bg-cyan-500 text-white' : 'bg-slate-600 text-slate-300']">
                      <span v-if="currentStep > 2">✓</span>
                      <span v-else>2</span>
                    </div>
                    <span :class="['ml-3 font-medium', currentStep >= 2 ? 'text-cyan-400' : 'text-slate-400']">
                      File Upload
                    </span>
                  </div>
                </div>
              </div>

              <div class="smooth-card smooth-card-blue animate-slide-up">
                <!-- Step 1: Basic Information -->
                <div v-if="currentStep === 1" class="space-y-8">
                  <div class="form-section">
                    <h2 class="text-2xl font-bold text-white mb-6">Paper Information</h2>

                    <div class="form-group">
                      <label for="title" class="auth-label block text-white font-semibold mb-3">Paper Title <span class="text-red-400">*</span></label>
                      <input
                        id="title"
                        v-model="form.title"
                        type="text"
                        required
                        placeholder="Enter the title of your paper"
                        maxlength="500"
                        class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40"
                        :class="[fieldErrors.title && 'border-red-500']"
                      />
                      <div class="char-count">{{ form.title.length }}/500</div>
                      <p v-if="fieldErrors.title" class="text-sm text-red-400 mt-1">{{ fieldErrors.title }}</p>
                    </div>

                    <div class="form-group">
                      <label for="abstract" class="space-y-2 text-sm font-semibold text-slate-200">Abstract <span class="text-red-400">*</span></label>
                      <textarea
                        id="abstract"
                        v-model="form.abstract"
                        required
                        rows="6"
                        placeholder="Provide a comprehensive abstract of your paper (250-300 words)"
                        maxlength="2000"
                        class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40 resize-none"
                        :class="[fieldErrors.abstract && 'border-red-500']"
                      ></textarea>
                      <div class="char-count">{{ form.abstract.length }}/2000</div>
                      <p v-if="fieldErrors.abstract" class="text-sm text-red-400 mt-1">{{ fieldErrors.abstract }}</p>
                    </div>

                    <div class="form-group">
                      <label for="keywords" class="space-y-2 text-sm font-semibold text-slate-200">Keywords <span class="text-red-400">*</span></label>
                      <input
                        id="keywords"
                        v-model="form.keywords"
                        type="text"
                        required
                        placeholder="Enter keywords separated by commas"
                        class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40"
                        :class="[fieldErrors.keywords && 'border-red-500']"
                      />
                      <p v-if="fieldErrors.keywords" class="text-sm text-red-400 mt-1">{{ fieldErrors.keywords }}</p>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div class="form-group">
                        <label for="manuscriptType" class="space-y-2 text-sm font-semibold text-slate-200">Manuscript Type <span class="text-red-400">*</span></label>
                        <select id="manuscriptType" v-model="form.manuscriptType" required class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40" :class="[fieldErrors.manuscriptType && 'border-red-500']">
                          <option value="">Please select</option>
                          <option value="RESEARCH_ARTICLE">Research Article</option>
                          <option value="REVIEW">Review</option>
                          <option value="SHORT_COMMUNICATION">Short Communication</option>
                          <option value="CASE_STUDY">Case Study</option>
                          <option value="LETTER">Letter</option>
                        </select>
                        <p v-if="fieldErrors.manuscriptType" class="text-sm text-red-400 mt-1">{{ fieldErrors.manuscriptType }}</p>
                      </div>

                      <div class="form-group">
                        <label for="subjectArea" class="space-y-2 text-sm font-semibold text-slate-200">Subject Area <span class="text-red-400">*</span></label>
                        <div class="relative area-dropdown-container">
                          <div 
                            @click="showAreaDropdown = !showAreaDropdown"
                            class="w-full px-4 py-3 bg-slate-800 border border-slate-600 rounded-lg text-white cursor-pointer hover:border-cyan-500 transition-colors min-h-[48px] flex items-center justify-between"
                            :class="[fieldErrors.subjectArea && 'border-red-500']"
                          >
                            <div class="flex flex-wrap gap-2">
                              <span v-if="!form.subjectArea" class="text-slate-400">
                                Select subject area
                              </span>
                              <span v-else class="text-white">
                                {{ form.subjectArea }}
                              </span>
                            </div>
                            <svg class="w-5 h-5 text-slate-400 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                            </svg>
                          </div>
                          
                          <!-- Dropdown -->
                          <div 
                            v-if="showAreaDropdown"
                            class="absolute z-10 w-full mt-2 bg-slate-800 border border-slate-600 rounded-lg shadow-2xl max-h-96 overflow-y-auto"
                          >
                            <div v-for="subject in subjects" :key="subject.subjectId" class="border-b border-slate-700 last:border-0">
                              <!-- Subject Header (Clickable) -->
                              <div 
                                @click="toggleSubject(subject.subjectId)"
                                class="px-4 py-3 bg-slate-700/50 font-semibold text-amber-300 text-sm cursor-pointer hover:bg-slate-700 transition-colors flex items-center justify-between"
                              >
                                <span>{{ subject.subjectName }}</span>
                                <svg 
                                  class="w-4 h-4 transition-transform duration-200"
                                  :class="{ 'rotate-90': isSubjectExpanded(subject.subjectId) }"
                                  fill="none" 
                                  stroke="currentColor" 
                                  viewBox="0 0 24 24"
                                >
                                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                                </svg>
                              </div>
                              <!-- Areas (Collapsible) -->
                              <div 
                                v-if="isSubjectExpanded(subject.subjectId)"
                                class="p-2"
                              >
                                <div 
                                  v-for="area in getAreasBySubject(subject.subjectId)" 
                                  :key="area.areaId"
                                  @click="selectArea(area)"
                                  class="px-3 py-2 hover:bg-slate-700/50 rounded cursor-pointer transition-colors"
                                >
                                  <span class="text-sm text-slate-300">{{ area.areaName }}</span>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                        <p v-if="fieldErrors.subjectArea" class="text-sm text-red-400 mt-1">{{ fieldErrors.subjectArea }}</p>
                      </div>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                      <div class="form-group">
                        <label for="wordCount" class="space-y-2 text-sm font-semibold text-slate-200">Word Count <span class="text-red-400">*</span></label>
                        <input 
                          id="wordCount" 
                          v-model.number="form.wordCount" 
                          type="number" 
                          min="0" 
                          required 
                          placeholder="e.g., 5200"
                          class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40"
                          :class="[fieldErrors.wordCount && 'border-red-500']"
                        />
                        <p v-if="fieldErrors.wordCount" class="text-sm text-red-400 mt-1">{{ fieldErrors.wordCount }}</p>
                      </div>
                      <div class="form-group">
                        <label for="figureCount" class="space-y-2 text-sm font-semibold text-slate-200">Figures</label>
                        <input 
                          id="figureCount" 
                          v-model.number="form.figureCount" 
                          type="number" 
                          min="0" 
                          placeholder="e.g., 5"
                          class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40"
                        />
                      </div>
                      <div class="form-group">
                        <label for="tableCount" class="space-y-2 text-sm font-semibold text-slate-200">Tables</label>
                        <input 
                          id="tableCount" 
                          v-model.number="form.tableCount" 
                          type="number" 
                          min="0" 
                          placeholder="e.g., 3"
                          class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40"
                        />
                      </div>
                    </div>

                    <div class="form-group">
                      <label for="coverLetter" class="space-y-2 text-sm font-semibold text-slate-200">Cover Letter</label>
                      <textarea
                        id="coverLetter"
                        v-model="form.coverLetter"
                        rows="4"
                        placeholder="Provide a brief cover letter to the editors (optional)"
                        class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40 resize-none"
                      ></textarea>
                    </div>
                  </div>

                  <!-- Academic Commitments -->
                  <div class="form-section">
                    <h2 class="text-2xl font-bold text-white mb-6">Academic Commitments</h2>

                    <div class="space-y-4">
                      <label class="checkbox-label">
                        <input 
                          type="checkbox" 
                          v-model="form.declareOriginal" 
                          required 
                          :class="[fieldErrors.declareOriginal && 'input-warning']"
                        />
                        <span class="checkmark"></span>
                        I declare this submission is original and not under consideration elsewhere. *
                      </label>
                      <label class="checkbox-label">
                        <input 
                          type="checkbox" 
                          v-model="form.declareCorresponding" 
                          required 
                          :class="[fieldErrors.declareCorresponding && 'input-warning']"
                        />
                        <span class="checkmark"></span>
                        I am the corresponding author and responsible for communication with the journal. *
                      </label>
                      <label class="checkbox-label">
                        <input 
                          type="checkbox" 
                          v-model="form.declareNoConflict" 
                          required 
                          :class="[fieldErrors.declareNoConflict && 'input-warning']"
                        />
                        <span class="checkmark"></span>
                        All authors declare no competing interests related to this work. *
                      </label>
                      <p v-if="fieldErrors.general" class="form-help field-hint-warning">{{ fieldErrors.general }}</p>
                    </div>
                  </div>

                  <div class="form-actions">
                    <button 
                      type="button" 
                      @click="handleNextStep" 
                      class="btn btn-primary"
                      :disabled="validatingStep1"
                    >
                      <span v-if="validatingStep1">Validating...</span>
                      <span v-else>Next: Upload Files</span>
                    </button>
                  </div>
                </div>

                <!-- Step 2: File Upload -->
                <div v-if="currentStep === 2" class="space-y-8">
                  <div class="form-section">
                    <h2 class="text-2xl font-bold text-white mb-6">Upload Files</h2>

                    <div class="form-group">
                      <label for="paperFile" class="space-y-2 text-sm font-semibold text-slate-200">Manuscript File <span class="text-red-400">*</span></label>
                      <input
                        id="paperFile"
                        type="file"
                        accept=".pdf,.doc,.docx"
                        @change="handlePaperFileUpload"
                        required
                        class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40"
                        :class="[fieldErrors.paperFile && 'border-red-500']"
                      />
                      <p class="file-help">Accepted formats: PDF, DOC, DOCX (Max size: 20MB)</p>
                      <p v-if="fieldErrors.paperFile" class="text-sm text-red-400 mt-1">{{ fieldErrors.paperFile }}</p>
                      <div v-if="uploadProgress.paperFile > 0" class="mt-2">
                        <div class="w-full bg-slate-700 rounded-full h-2">
                          <div class="bg-cyan-500 h-2 rounded-full transition-all" :style="{ width: uploadProgress.paperFile + '%' }"></div>
                        </div>
                        <p class="text-sm text-slate-300 mt-1">{{ uploadProgress.paperFile }}% uploaded</p>
                      </div>
                      <div v-if="uploadedFiles.paperFile" class="mt-2 text-sm text-green-400 flex items-center">
                        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                        </svg>
                        {{ uploadedFiles.paperFile.originalName }} uploaded successfully
                      </div>
                    </div>

                    <div class="form-group">
                      <label for="supportingFile" class="space-y-2 text-sm font-semibold text-slate-200">Supporting Materials (Optional)</label>
                      <input
                        id="supportingFile"
                        type="file"
                        accept=".pdf,.doc,.docx,.zip"
                        @change="handleSupportingFileUpload"
                        class="w-full rounded-lg border border-slate-600 bg-slate-800 px-4 py-2.5 text-white focus:border-cyan-500 focus:outline-none focus:ring-2 focus:ring-cyan-500/40"
                        :class="[fieldErrors.supportingFile && 'border-red-500']"
                      />
                      <p class="file-help">Accepted formats: PDF, DOC, DOCX, ZIP (Max size: 20MB)</p>
                      <p v-if="fieldErrors.supportingFile" class="text-sm text-red-400 mt-1">{{ fieldErrors.supportingFile }}</p>
                      <div v-if="uploadProgress.supportingFile > 0" class="mt-2">
                        <div class="w-full bg-slate-700 rounded-full h-2">
                          <div class="bg-cyan-500 h-2 rounded-full transition-all" :style="{ width: uploadProgress.supportingFile + '%' }"></div>
                        </div>
                        <p class="text-sm text-slate-300 mt-1">{{ uploadProgress.supportingFile }}% uploaded</p>
                      </div>
                      <div v-if="uploadedFiles.supportingFile" class="mt-2 text-sm text-green-400 flex items-center">
                        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                        </svg>
                        {{ uploadedFiles.supportingFile.originalName }} uploaded successfully
                      </div>
                    </div>
                  </div>

                  <div class="form-actions">
                    <button 
                      type="button" 
                      @click="currentStep = 1" 
                      class="btn btn-outline"
                    >
                      Back to Information
                    </button>
                    <button 
                      type="button" 
                      @click="handleSubmit" 
                      class="btn btn-primary"
                      :disabled="submitting"
                    >
                      <span v-if="submitting">Submitting...</span>
                      <span v-else>Submit Paper</span>
                    </button>
                  </div>
                </div>

                <!-- Success Message -->
                <div v-if="currentStep === 3" class="text-center py-12">
                  <svg class="w-20 h-20 mx-auto text-green-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                  <h2 class="text-3xl font-bold text-white mb-4">Submission Successful!</h2>
                  <p class="text-lg text-slate-300 mb-8">
                    Your paper has been submitted successfully. You will receive a confirmation email shortly.
                  </p>
                  <button @click="resetForm" class="btn btn-primary">
                    Submit Another Paper
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </PageScaffold>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { authApi, apiClient, uploadApi, subjectApi } from '@/api'
import PageScaffold from '@/components/layout/PageScaffold.vue'

const router = useRouter()

const currentStep = ref(1)
const submitting = ref(false)
const validatingStep1 = ref(false)

const form = reactive({
  title: '',
  abstract: '',
  keywords: '',
  manuscriptType: '',
  subjectArea: '',
  wordCount: null,
  figureCount: 0,
  tableCount: 0,
  coverLetter: '',
  declareOriginal: false,
  declareCorresponding: false,
  declareNoConflict: false
})

const fieldErrors = reactive({})

// Subject area dropdown state
const subjects = ref([])
const areas = ref([])
const showAreaDropdown = ref(false)
const expandedSubjects = ref(new Set())

const extractList = (response) => {
  const res = response?.data ?? {}
  const payload = res.data ?? res

  if (Array.isArray(payload)) {
    return payload
  }

  if (Array.isArray(payload?.data)) {
    return payload.data
  }

  if (Array.isArray(payload?.rows)) {
    return payload.rows
  }

  return []
}

const getAreasBySubject = (subjectId) => {
  if (!Array.isArray(areas.value)) {
    return []
  }
  return areas.value.filter(area => area.areaSubjectId === subjectId)
}

const toggleSubject = (subjectId) => {
  if (expandedSubjects.value.has(subjectId)) {
    expandedSubjects.value.delete(subjectId)
  } else {
    expandedSubjects.value.add(subjectId)
  }
}

const isSubjectExpanded = (subjectId) => {
  return expandedSubjects.value.has(subjectId)
}

const selectArea = (area) => {
  form.subjectArea = area.areaName
  showAreaDropdown.value = false
}

const paperFile = ref(null)
const supportingFile = ref(null)
const uploadedFiles = reactive({
  paperFile: null,
  supportingFile: null
})
const uploadProgress = reactive({
  paperFile: 0,
  supportingFile: 0
})

// Create article ID for file upload subdirectory
let articleId = null

// Close dropdown when clicking outside
let clickHandler = null

onMounted(async () => {
  // Already authenticated by router guard
  console.log('Submit page loaded')
  
  // Load subjects and areas
  try {
    const [subjectResponse, areaResponse] = await Promise.all([
      subjectApi.getSubjectList(),
      subjectApi.getAllAreas()
    ])

    subjects.value = extractList(subjectResponse)
    areas.value = extractList(areaResponse)
  } catch (error) {
    console.error('Failed to load subjects and areas:', error)
    subjects.value = []
    areas.value = []
  }
  
  // Close dropdown when clicking outside
  clickHandler = (e) => {
    if (!e.target.closest('.area-dropdown-container')) {
      showAreaDropdown.value = false
    }
  }
  document.addEventListener('click', clickHandler)
})

onUnmounted(() => {
  if (clickHandler) {
    document.removeEventListener('click', clickHandler)
  }
})

const handlePaperFileUpload = (event) => {
  paperFile.value = event.target.files[0]
  uploadedFiles.paperFile = null
  uploadProgress.paperFile = 0
}

const handleSupportingFileUpload = (event) => {
  supportingFile.value = event.target.files[0]
  uploadedFiles.supportingFile = null
  uploadProgress.supportingFile = 0
}

const handleNextStep = async () => {
  // Validate Step 1
  validatingStep1.value = true
  Object.keys(fieldErrors).forEach(k => delete fieldErrors[k])

  if (!form.title) fieldErrors.title = 'Title is required'
  if (!form.abstract) fieldErrors.abstract = 'Abstract is required'
  if (!form.keywords) fieldErrors.keywords = 'Keywords are required'
  if (!form.manuscriptType) fieldErrors.manuscriptType = 'Manuscript type is required'
  if (!form.subjectArea) fieldErrors.subjectArea = 'Subject area is required'
  if (form.wordCount == null || form.wordCount < 0) fieldErrors.wordCount = 'Word count is required'
  if (!form.declareOriginal) fieldErrors.declareOriginal = 'Required'
  if (!form.declareCorresponding) fieldErrors.declareCorresponding = 'Required'
  if (!form.declareNoConflict) fieldErrors.declareNoConflict = 'Required'

  validatingStep1.value = false

  if (Object.keys(fieldErrors).length > 0) {
    scrollToFirstError()
    return
  }

  // Create article first to get articleId
  try {
    const articleData = {
      title: form.title,
      abstract: form.abstract,
      keywords: form.keywords,
      manuscriptType: form.manuscriptType,
      subjectArea: form.subjectArea,
      wordCount: form.wordCount ?? 0,
      figureCount: form.figureCount ?? 0,
      tableCount: form.tableCount ?? 0,
      coverLetter: form.coverLetter || ''
    }

    const response = await apiClient.post('/articles/create', articleData)
    
    if (response && response.code === 200) {
      articleId = response.data.data?.articleId
      
      // Upload files if selected (but don't require them yet)
      if (articleId) {
        if (paperFile.value) {
          await uploadFile(paperFile.value, 'paperFile')
        }
        if (supportingFile.value) {
          await uploadFile(supportingFile.value, 'supportingFile')
        }
      }
      
      currentStep.value = 2
    } else {
      fieldErrors.general = 'Failed to create article. Please try again.'
    }
  } catch (error) {
    console.error('Error creating article:', error)
    fieldErrors.general = 'Failed to create article. Please try again.'
  }
}

const uploadFile = async (file, type) => {
  if (!articleId) return

  try {
    uploadProgress[type] = 10
    
    const response = await uploadApi.uploadFileToSubDir(
      file,
      'articles',
      String(articleId)
    )

    uploadProgress[type] = 100
    uploadedFiles[type] = {
      originalName: file.name,
      fileName: response.data.fileName,
      filePath: response.data.filePath,
      fileSize: response.data.fileSize
    }
  } catch (error) {
    console.error(`Error uploading ${type}:`, error)
    fieldErrors[type] = `Failed to upload file. Please try again.`
    uploadProgress[type] = 0
  }
}

const handleSubmit = async () => {
  // Validate Step 2
  Object.keys(fieldErrors).forEach(k => delete fieldErrors[k])

  if (!uploadedFiles.paperFile) {
    fieldErrors.paperFile = 'Paper file is required'
    scrollToFirstError()
    return
  }

  submitting.value = true

  try {
    // Update article with file information
    const updateData = {
      manuscriptId: articleId,
      files: {
        paper: uploadedFiles.paperFile,
        supporting: uploadedFiles.supportingFile
      }
    }

    const response = await apiClient.post('/articles/update', updateData)

    if (response && response.code === 200) {
      currentStep.value = 3
    } else {
      fieldErrors.general = 'Failed to submit paper. Please try again.'
    }
  } catch (error) {
    console.error('Error submitting paper:', error)
    fieldErrors.general = 'Failed to submit paper. Please try again.'
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  form.title = ''
  form.abstract = ''
  form.keywords = ''
  form.manuscriptType = ''
  form.subjectArea = ''
  form.wordCount = null
  form.figureCount = 0
  form.tableCount = 0
  form.coverLetter = ''
  form.declareOriginal = false
  form.declareCorresponding = false
  form.declareNoConflict = false
  
  paperFile.value = null
  supportingFile.value = null
  uploadedFiles.paperFile = null
  uploadedFiles.supportingFile = null
  uploadProgress.paperFile = 0
  uploadProgress.supportingFile = 0
  articleId = null
  currentStep.value = 1
  Object.keys(fieldErrors).forEach(k => delete fieldErrors[k])
}

const scrollToFirstError = () => {
  const firstKey = Object.keys(fieldErrors)[0]
  if (!firstKey) return
  const el = document.querySelector(`[data-field="${firstKey}"]`)
  if (el && typeof el.scrollIntoView === 'function') {
    el.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}
</script>

<style scoped>
@import '../assets/css/submit.css';
@import '../assets/css/utilities.css';

.submit {
  min-height: 100vh;
}

.progress-step {
  position: relative;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid rgba(148, 163, 184, 0.2);
}

.char-count {
  text-align: right;
  font-size: 0.875rem;
  color: #94a3b8;
  margin-top: 0.25rem;
}

.file-help {
  font-size: 0.875rem;
  color: #94a3b8;
  margin-top: 0.25rem;
}
</style>
