package com.chewite.app.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ProfileImageUtils {

    fun saveToDownloadFolder(viewLifecycleOwner: LifecycleOwner, context: Context, uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            val savedUri = saveUriToDownload(context, uri)
            if (savedUri != null) {
                Toast.makeText(context, "다운로드 폴더에 저장됨!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveUriToDownload(
        context: Context,
        uri: Uri,
        displayName: String? = null
    ): Uri? = withContext(Dispatchers.IO) {

        val resolver = context.contentResolver

        // 1) MIME 타입 확인
        val mime = resolver.getType(uri) ?: return@withContext null

        // 2) 확장자 추출
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "jpg"

        // 파일 이름
        val name = displayName ?: "image_${System.currentTimeMillis()}.$ext"

        // 3) MediaStore에 넣을 정보
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.MIME_TYPE, mime)
            // Android 10(Q)부터 필요: "download" 폴더 안으로 지정
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        // 4) 실제로 다운로드 폴더에 엔트리 생성
        val downloadUri = resolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return@withContext null

        // 5) 파일 복사
        resolver.openOutputStream(downloadUri)?.use { output ->
            resolver.openInputStream(uri)?.use { input ->
                input.copyTo(output)
            }
        }

        // 6) 저장된 Uri 반환
        return@withContext downloadUri
    }
}