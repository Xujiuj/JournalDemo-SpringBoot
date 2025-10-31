import apiClient from './client'

/**
 * 上传单个文件
 */
export const uploadSingleFile = async (file, category = 'default') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('category', category)

  const response = await apiClient.post('/api/upload/single', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })

  return response.data
}

/**
 * 批量上传文件
 */
export const uploadBatchFiles = async (files, category = 'default') => {
  const formData = new FormData()
  files.forEach((file) => {
    formData.append('files', file)
  })
  formData.append('category', category)

  const response = await apiClient.post('/api/upload/batch', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })

  return response.data
}

/**
 * 上传文件到子目录
 */
export const uploadFileToSubDir = async (file, category, subDirectory) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('category', category)
  formData.append('subDirectory', subDirectory)

  const response = await apiClient.post('/api/upload/subdir', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })

  return response.data
}

/**
 * 获取上传根目录
 */
export const getUploadRoot = async () => {
  const response = await apiClient.get('/api/upload/root')
  return response.data
}

/**
 * 获取文件下载URL
 */
export const getFileDownloadUrl = (filePath) => {
  return `${import.meta.env.VITE_API_BASE_URL}/api/upload/download/${filePath}`
}

/**
 * 删除文件
 */
export const deleteFile = async (filePath) => {
  const response = await apiClient.delete(`/api/upload/${filePath}`)
  return response.data
}

export default {
  uploadSingleFile,
  uploadBatchFiles,
  uploadFileToSubDir,
  getUploadRoot,
  getFileDownloadUrl,
  deleteFile
}

