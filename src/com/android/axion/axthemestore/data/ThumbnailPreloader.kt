/*
 * Copyright 2025-2026 AxionOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.axion.axthemestore.data

import android.content.Context
import com.android.axion.axthemestore.data.model.Theme
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object ThumbnailPreloader {
    const val THUMB_PX = 384

    fun preload(context: Context, themes: List<Theme>) {
        if (themes.isEmpty()) return
        val app = context.applicationContext
        val urls = themes
            .flatMap { it.previewImages }
            .distinct()
        for (url in urls) {
            if (url.isEmpty()) continue
            Glide.with(app)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(THUMB_PX, THUMB_PX)
                .preload(THUMB_PX, THUMB_PX)
        }
    }
}
