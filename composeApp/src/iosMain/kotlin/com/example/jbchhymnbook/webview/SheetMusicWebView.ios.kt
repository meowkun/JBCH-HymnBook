package com.example.jbchhymnbook.webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun SheetMusicWebView(
    musicXml: String,
    modifier: Modifier,
    scale: Float, // Scale factor for music notes
    fontSize: Float // Font size in pixels for lyrics and text
) {
    val base64Xml = remember(musicXml) {
        // Encode to Base64 to prevent character mangling (iOS doesn't have Base64 in stdlib, use platform)
        platform.Foundation.NSString.create(string = musicXml)
            .dataUsingEncoding(platform.Foundation.NSUTF8StringEncoding)
            ?.base64EncodedStringWithOptions(0u)
            ?: ""
    }
    
    UIKitView(
        factory = {
            val config = WKWebViewConfiguration()
            val webView = WKWebView(frame = platform.CoreGraphics.CGRectZero, configuration = config)
            
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <script src="https://cdn.jsdelivr.net/npm/opensheetmusicdisplay@1.8.6/build/opensheetmusicdisplay.min.js"></script>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        html, body { width: 100%; height: 100%; overflow-x: auto; overflow-y: auto; background: white; }
                        #osmd-container { 
                            width: 100vw; 
                            height: auto; 
                            min-height: 100vh;
                            transform-origin: top left;
                        }
                        svg { width: 100% !important; height: auto !important; }
                    </style>
                </head>
                <body>
                    <div id="osmd-container"></div>
                    <script>
                        window.onload = function() {
                            try {
                                // Get screen width for proper scaling
                                const screenWidth = window.innerWidth || document.documentElement.clientWidth || 800;
                                
                                // Decode Base64
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
                                        document.body.innerHTML = "<b>Parser Error:</b> " + err;
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
            
            webView.loadHTMLString(html, baseURL = null)
            webView
        },
        modifier = modifier.fillMaxSize(),
        update = { webView ->
            // Update when musicXml changes
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <script src="https://cdn.jsdelivr.net/npm/opensheetmusicdisplay@1.8.6/build/opensheetmusicdisplay.min.js"></script>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        html, body { width: 100%; height: 100%; overflow-x: auto; overflow-y: auto; background: white; }
                        #osmd-container { 
                            width: 100vw; 
                            height: auto; 
                            min-height: 100vh;
                            transform-origin: top left;
                        }
                        svg { width: 100% !important; height: auto !important; }
                    </style>
                </head>
                <body>
                    <div id="osmd-container"></div>
                    <script>
                        window.onload = function() {
                            try {
                                // Get screen width for proper scaling
                                const screenWidth = window.innerWidth || document.documentElement.clientWidth || 800;
                                
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
                                        document.body.innerHTML = "<b>Parser Error:</b> " + err;
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
            webView.loadHTMLString(html, baseURL = null)
        }
    )
}

