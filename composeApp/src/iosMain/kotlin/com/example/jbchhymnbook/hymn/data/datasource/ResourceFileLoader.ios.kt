package com.example.jbchhymnbook.hymn.data.datasource

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.stringWithContentsOfFile

@OptIn(ExperimentalForeignApi::class)
actual suspend fun loadResourceFile(path: String): String {
    return withContext(Dispatchers.Main) {
        try {
            // For iOS, files in composeResources/files/ are in the app bundle
            val bundle = NSBundle.mainBundle
            val resourcePath = bundle.pathForResource(
                path.removeSuffix(".json").removeSuffix(".xml"),
                ofType = path.substringAfterLast(".")
            )
                ?: throw IllegalStateException("Resource not found in bundle: $path")
            
            val content = NSString.stringWithContentsOfFile(
                resourcePath,
                encoding = platform.Foundation.NSUTF8StringEncoding,
                error = null
            ) as? String
            
            content ?: throw IllegalStateException("Failed to read resource file: $path")
        } catch (e: Exception) {
            throw IllegalStateException("Failed to load resource file: $path. Error: ${e.message}", e)
        }
    }
}

