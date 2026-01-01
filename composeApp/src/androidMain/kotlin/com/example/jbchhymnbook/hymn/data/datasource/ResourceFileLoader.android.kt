package com.example.jbchhymnbook.hymn.data.datasource

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Global context provider - will be set from Application
object AndroidContextProvider {
    var context: Context? = null
}

actual suspend fun loadResourceFile(path: String): String {
    return withContext(Dispatchers.IO) {
        val context = AndroidContextProvider.context
            ?: throw IllegalStateException("Android Context not initialized.")

        try {
            // If path is "hymn_index.json", we look for it in the flattened structure
            // We search recursively or use the known internal path
            val fullAssetPath = findResourcePath(context, path)
                ?: throw java.io.FileNotFoundException("Could not find $path in assets")

            context.assets.open(fullAssetPath).use { inputStream ->
                inputStream.readBytes().decodeToString()
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to load: $path. Error: ${e.message}", e)
        }
    }
}

// Helper to find the file because the package name is often injected in the middle
private fun findResourcePath(context: Context, relativePath: String): String? {
    val root = "composeResources"
    val assets = context.assets

    // Remove "files/" prefix if present
    val cleanPath = if (relativePath.startsWith("files/")) {
        relativePath.substring(6) // Remove "files/" prefix
    } else {
        relativePath
    }

    // 1. Get the package name folder (e.g., "com.example.app")
    val packageFolders = assets.list(root) ?: return null
    val pkg = packageFolders.firstOrNull() ?: return null

    // 2. Split the user path: "hymns/test/test.xml" or "hymn_index.json"
    // folderPart = "hymns/test" or ""
    // fileName = "test.xml" or "hymn_index.json"
    val folderPart = if (cleanPath.contains("/")) {
        cleanPath.substringBeforeLast("/")
    } else {
        ""
    }
    val fileName = cleanPath.substringAfterLast("/")

    // 3. Construct the directory path inside assets
    // Format: composeResources/[pkg]/files/[user-folders]
    val assetsDirToSearch = if (folderPart.isNotEmpty()) {
        "$root/$pkg/files/$folderPart"
    } else {
        "$root/$pkg/files"
    }

    // 4. Check if the file exists in that specific directory
    return try {
        val filesInDir = assets.list(assetsDirToSearch) ?: emptyArray()
        if (filesInDir.contains(fileName)) {
            "$assetsDirToSearch/$fileName"
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}
