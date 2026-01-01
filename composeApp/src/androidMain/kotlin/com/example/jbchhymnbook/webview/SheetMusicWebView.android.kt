package com.example.jbchhymnbook.webview

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun SheetMusicWebView(
    musicXml: String,
    modifier: Modifier,
    scale: Float, // Scale factor for music notes
    fontSize: Float // Font size in pixels for lyrics and text
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
        }
    }

    DisposableEffect(musicXml) {
        // Encode to Base64 to prevent character mangling
        val base64Xml = android.util.Base64.encodeToString(
            musicXml.toByteArray(Charsets.UTF_8),
            android.util.Base64.NO_WRAP
        )

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <script src="https://cdn.jsdelivr.net/npm/opensheetmusicdisplay@1.8.6/build/opensheetmusicdisplay.min.js"></script>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    html, body { 
                        width: 100%; 
                        height: 100%; 
                        overflow-x: auto; 
                        overflow-y: auto; 
                        background: white;
                        font-size: ${fontSize}px; /* Base font size */
                    }
                    #osmd-container { 
                        width: 100vw; 
                        height: auto; 
                        min-height: 100vh;
                        transform-origin: top left;
                    }
                    svg { 
                        width: 100% !important; 
                        height: auto !important;
                    }
                    /* Control font sizes for text elements in the SVG */
                    svg text {
                        font-size: ${fontSize}px !important;
                    }
                    /* Scale lyrics text */
                    svg .osmd-lyrics {
                        font-size: ${fontSize * 0.9}px !important;
                    }
                    /* Scale title text */
                    svg .osmd-title {
                        font-size: ${fontSize * 1.2}px !important;
                    }
                    /* Scale part names */
                    svg .osmd-part-name {
                        font-size: ${fontSize * 0.8}px !important;
                    }
                </style>
            </head>
            <body>
                <div id="osmd-container"></div>
                <script>
                    window.onload = function() {
                        try {
                            // Get screen width for proper scaling
                            const screenWidth = window.innerWidth || document.documentElement.clientWidth || 800;
                            
                            // Decode Base64 to a Blob to handle larger files safely
                            const b64 = "$base64Xml";
                            const byteCharacters = atob(b64);
                            const byteNumbers = new Array(byteCharacters.length);
                            for (let i = 0; i < byteCharacters.length; i++) {
                                byteNumbers[i] = byteCharacters.charCodeAt(i);
                            }
                            const byteArray = new Uint8Array(byteNumbers);
                            const xmlBlob = new Blob([byteArray], { type: 'application/xml' });
                            
                            const reader = new FileReader();
                            reader.onload = function(e) {
                                const xmlString = e.target.result;
                                
                                const osmd = new opensheetmusicdisplay.OpenSheetMusicDisplay("osmd-container", {
                                    backend: "svg",
                                    autoResize: true,
                                    drawTitle: true,
                                    // Use screen width for page width to fit full screen
                                    pageWidth: screenWidth,
                                    pageHeight: 10000, // Large height to prevent page breaks
                                    drawPartNames: true,
                                    drawComposer: false,
                                    drawLyricist: false,
                                    // Prevent cutting off measures - never create new systems or pages
                                    newSystemMode: 0, // 0 = never, 1 = auto, 2 = always
                                    newPageMode: 0, // 0 = never, 1 = auto, 2 = always
                                    // Scale down to fit more content - configurable
                                    scale: $scale // Scale factor (default 0.6 = 60% of normal size)
                                });

                                osmd.load(xmlString).then(() => {
                                    osmd.Zoom = 0.7;
                                    osmd.render();
                                    
                                    // After rendering, apply font size and scale adjustments
                                    const svg = document.querySelector('#osmd-container svg');
                                    if (svg) {
                                        // Apply font size to all text elements
                                        const textElements = svg.querySelectorAll('text');
                                        textElements.forEach(function(textEl) {
                                            const currentSize = parseFloat(window.getComputedStyle(textEl).fontSize) || ${fontSize};
                                            textEl.style.fontSize = '${fontSize}px';
                                        });
                                        
                                        // Adjust overall scale if needed to fit screen
                                        const svgWidth = svg.getBoundingClientRect().width;
                                        if (svgWidth > screenWidth) {
                                            const scaleFactor = screenWidth / svgWidth;
                                            svg.style.transform = 'scale(' + scaleFactor + ')';
                                            svg.style.transformOrigin = 'top left';
                                        } else {
                                            // Apply the scale parameter as CSS transform
                                            svg.style.transform = 'scale($scale)';
                                            svg.style.transformOrigin = 'top left';
                                        }
                                    }
                                }).catch(err => {
                                    document.body.innerHTML = "<b>Parser Error:</b> " + err + 
                                        "<br><b>End of XML:</b> " + xmlString.slice(-50).replace(/</g, '&lt;');
                                });
                            };
                            reader.readAsText(xmlBlob);

                        } catch (e) {
                            document.body.innerHTML = "<b>JS Error:</b> " + e.message;
                        }
                    };
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL("https://localhost", html, "text/html", "UTF-8", null)
        onDispose { }
    }

    AndroidView(factory = { webView }, modifier = modifier.fillMaxSize())
}