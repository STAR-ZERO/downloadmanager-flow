DEPRECATED
---

This repository no longer maintained.


DownloadManager Flow
---

[ ![Download](https://api.bintray.com/packages/star-zero/maven/downloadmanager-flow/images/download.svg?version=1.0.0) ](https://bintray.com/star-zero/maven/downloadmanager-flow/1.0.0/link)

DownloadManager 🔗 Coroutines Flow

## Usage

```kotlin
scope.launch {
    val request = DownloadManager.Request(/** ... */)
        .setDestinationInExternalFilesDir(/** .. */)

    downloadManager(context, request).collect { state ->
        when (state.status) {
            DownloadManagerState.Status.RUNNING -> {
                // You can get downloaded bytes and total bytes
                val downloadedBytes = state.downloadedBytes
                val totalBytes = state.totalBytes
            }
            DownloadManagerState.Status.SUCCESS -> {
                // Success
            }
        }
    }
}
```

## Download

```
implementation 'com.star_zero:downloadmanager-flow:<latest_version>'
```

## License

```
Copyright 2020 Kenji Abe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
